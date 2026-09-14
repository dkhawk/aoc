#!/usr/bin/env python3
import os
import sys
import time
import urllib.request
import urllib.error
from datetime import datetime, timezone, timedelta

# Advent of Code releases puzzles at midnight Eastern Time (UTC-5)
AOC_TZ = timezone(timedelta(hours=-5))

def get_session_cookie():
    cookie = os.environ.get("AOC_SESSION_COOKIE")
    if cookie:
        return cookie
    env_file = os.path.join(os.path.dirname(__file__), "..", ".env")
    if os.path.exists(env_file):
        with open(env_file, "r", encoding="utf-8") as f:
            for line in f:
                if line.startswith("AOC_SESSION_COOKIE="):
                    return line.strip().split("=", 1)[1].strip('"\'')
    return None

def fetch_input(year, day, cookie, force=False, delay=0.5, retries=0):
    day_padded = f"{int(day):02d}"
    out_dir = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "resources", f"aoc{year}")
    os.makedirs(out_dir, exist_ok=True)
    out_file = os.path.join(out_dir, f"day{day_padded}_input.txt")

    if os.path.exists(out_file) and not force:
        print(f"Skipping {year} Day {day_padded} (already exists at {out_file})")
        return True

    url = f"https://adventofcode.com/{year}/day/{day}/input"
    req = urllib.request.Request(
        url,
        headers={
            "User-Agent": "AoC-Input-Fetcher/1.0 (github.com/dkhawk/aoc)",
            "Cookie": f"session={cookie}"
        }
    )

    attempt = 0
    while attempt <= retries:
        attempt += 1
        try:
            print(f"Fetching {year} Day {day_padded} from {url}...")
            with urllib.request.urlopen(req) as resp:
                data = resp.read()
                with open(out_file, "wb") as f:
                    f.write(data)
                print(f"  ➜ Saved to {out_file} ({len(data)} bytes)")
                time.sleep(delay)
                return True
        except urllib.error.HTTPError as e:
            if e.code == 404:
                if attempt <= retries:
                    print(f"  ➜ Not yet available (HTTP 404), retrying in 2s... (attempt {attempt}/{retries})")
                    time.sleep(2)
                    continue
                print(f"  ➜ {year} Day {day_padded} not found (HTTP 404 - not released yet)")
            else:
                print(f"  ➜ Error fetching {year} Day {day_padded}: {e}")
            return False
        except Exception as e:
            print(f"  ➜ Error fetching {year} Day {day_padded}: {e}")
            return False

def get_current_aoc_time():
    return datetime.now(AOC_TZ)

def print_help():
    print("""Advent of Code Input Fetcher

Usage:
  python3 tools/fetch_inputs.py [--all-missing] [--force]
      Checks all years (2015..current) and downloads any missing inputs.

  python3 tools/fetch_inputs.py --today [--force] [--wait]
      Downloads today's input based on Advent of Code time (midnight US Eastern).
      Use --wait to retry if run right around release time.

  python3 tools/fetch_inputs.py --latest [--force]
      Detects the latest active year/day and grabs the latest available input.

  python3 tools/fetch_inputs.py <year> [start_day] [end_day] [--force]
      Fetches inputs for a specific year and day range (e.g. 2024 1 25).
""")

def max_days_for_year(year):
    # Starting in 2025, Advent of Code was reduced from 25 to 12 days
    return 12 if year >= 2025 else 25

def main():
    if "-h" in sys.argv or "--help" in sys.argv:
        print_help()
        sys.exit(0)

    cookie = get_session_cookie()
    if not cookie:
        print("Error: AOC_SESSION_COOKIE not found in environment or .env file.")
        sys.exit(1)

    force = "--force" in sys.argv
    wait = "--wait" in sys.argv
    retries = 5 if wait else 0
    args = [a for a in sys.argv[1:] if a not in ("--force", "--wait")]

    now_est = get_current_aoc_time()

    if "--today" in args:
        if now_est.month == 12 and 1 <= now_est.day <= 25:
            print(f"AoC Time: {now_est.strftime('%Y-%m-%d %H:%M:%S EST')} -> Fetching {now_est.year} Day {now_est.day}")
            fetch_input(now_est.year, now_est.day, cookie, force=force, retries=retries)
        else:
            print(f"AoC Time: {now_est.strftime('%Y-%m-%d %H:%M:%S EST')} (Not currently December 1–25).")
            print("Use python3 tools/fetch_inputs.py <year> <day> to fetch a specific date, or --all-missing.")
        return

    if "--latest" in args:
        # Check current year down to 2015
        target_year = now_est.year if now_est.month == 12 else (now_est.year if now_est.year <= 2025 else 2025)
        year_cap = max_days_for_year(target_year)
        max_day = min(now_est.day, year_cap) if (target_year == now_est.year and now_est.month == 12) else year_cap
        print(f"Checking for latest inputs in {target_year} up to day {max_day}...")
        for d in range(1, max_day + 1):
            fetch_input(target_year, d, cookie, force=force)
        return

    if not args or args[0] == "--all-missing":
        current_year = now_est.year
        max_year = current_year if now_est.month == 12 else current_year - 1
        years = list(range(2015, max_year + 1))
        print(f"Scanning for missing inputs across years {years[0]}–{years[-1]}...")
        for y in years:
            year_cap = max_days_for_year(y)
            max_day = min(now_est.day, year_cap) if (y == now_est.year and now_est.month == 12) else year_cap
            for d in range(1, max_day + 1):
                fetch_input(y, d, cookie, force=force)
    else:
        try:
            year = int(args[0])
        except ValueError:
            print(f"Unknown argument: {args[0]}")
            print_help()
            sys.exit(1)

        year_cap = max_days_for_year(year)
        start_day = int(args[1]) if len(args) > 1 else 1
        end_day = int(args[2]) if len(args) > 2 else (year_cap if len(args) <= 1 else start_day)
        end_day = min(end_day, year_cap)
        for d in range(start_day, end_day + 1):
            fetch_input(year, d, cookie, force=force, retries=retries)

if __name__ == "__main__":
    main()
