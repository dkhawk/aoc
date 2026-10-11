#!/usr/bin/env python3
"""
submit_answer.py
Submits puzzle answers to Advent of Code and verifies the result.

Usage:
  # Submit an answer directly:
  python3 tools/submit_answer.py --year 2016 --day 18 --part 1 --answer 198

  # Run the Kotlin solution via Gradle, extract the answer, and submit after confirmation:
  python3 tools/submit_answer.py --year 2016 --day 18 --part 1 --run

  # Skip interactive confirmation:
  python3 tools/submit_answer.py --year 2016 --day 18 --part 1 --answer 198 --force
"""

import os
import sys
import re
import time
import argparse
import subprocess
import urllib.request
import urllib.error
import urllib.parse
from html.parser import HTMLParser

# Import fetch_problem from fetch_problems if available
try:
    from fetch_problems import fetch_problem, get_session_cookie
except ImportError:
    # Add tools/ to path
    tools_dir = os.path.dirname(os.path.abspath(__file__))
    if tools_dir not in sys.path:
        sys.path.insert(0, tools_dir)
    from fetch_problems import fetch_problem, get_session_cookie

class TextExtractor(HTMLParser):
    def __init__(self):
        super().__init__()
        self.text = []

    def handle_data(self, data):
        self.text.append(data)

def extract_article_text(html):
    articles = re.findall(r'<article>(.*?)</article>', html, re.DOTALL)
    if not articles:
        articles = re.findall(r'<main>(.*?)</main>', html, re.DOTALL)
    
    raw = "\n".join(articles) if articles else html
    parser = TextExtractor()
    parser.feed(raw)
    cleaned = "".join(parser.text).strip()
    return re.sub(r'\s+', ' ', cleaned)

def run_solution_for_part(year, day, part):
    dp = f"{int(day):02d}"
    main_class = f"com.sphericalchickens.aoc{year}.day{dp}.Day{dp}Kt"
    repo_root = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
    
    print(f"⚙️  Running {main_class} via Gradle...")
    cmd = ["./gradlew", "run", f"-PmainClass={main_class}", "--quiet"]
    try:
        proc = subprocess.run(
            cmd,
            cwd=repo_root,
            capture_output=True,
            text=True,
            check=False
        )
    except Exception as e:
        print(f"❌ Failed to run Gradle: {e}")
        return None

    stdout = proc.stdout
    if proc.returncode != 0:
        print(f"❌ Gradle execution failed (exit code {proc.returncode}):")
        if proc.stderr:
            print(proc.stderr)
        return None

    # Print output from solution
    print("--- Program Output ---")
    for line in stdout.splitlines():
        if "Part 1" in line or "Part 2" in line or "Test" in line or "🎁" in line or "🎀" in line:
            print(f"  {line}")
    print("----------------------")

    # Match Part 1 or Part 2
    if part == 1:
        match = re.search(r'(?:🎁\s*)?Part 1:\s*([^\r\n]+)', stdout)
    else:
        match = re.search(r'(?:🎀\s*)?Part 2:\s*([^\r\n]+)', stdout)

    if match:
        answer = match.group(1).strip()
        return answer
    else:
        print(f"❌ Could not find output for Part {part} in solution output.")
        return None

def update_official_stats(year, day, part):
    stats_file = os.path.join(os.path.dirname(__file__), "official_stats.json")
    if not os.path.exists(stats_file):
        return
    try:
        import json
        with open(stats_file, "r") as f:
            stats = json.load(f)
        
        y_str = str(year)
        d_str = str(int(day))
        if "years" in stats and y_str in stats["years"]:
            y_data = stats["years"][y_str]
            day_stars = y_data.get("dayStars", {})
            current_stars = day_stars.get(d_str, 0)
            if part == 1 and current_stars < 1:
                day_stars[d_str] = 1
                y_data["stars"] = sum(day_stars.values())
                stats["totalOfficialStars"] = sum(y["stars"] for y in stats["years"].values())
            elif part == 2 and current_stars < 2:
                day_stars[d_str] = 2
                y_data["stars"] = sum(day_stars.values())
                stats["totalOfficialStars"] = sum(y["stars"] for y in stats["years"].values())
            
            with open(stats_file, "w") as f:
                json.dump(stats, f, indent=2)
            print(f"📊 Updated {stats_file} (AoC {year} Day {day}: {day_stars.get(d_str)} stars)")
    except Exception as e:
        print(f"⚠️ Could not update official_stats.json: {e}")

def submit_answer(year, day, part, answer, force=False):
    cookie = get_session_cookie()
    if not cookie:
        print("❌ Error: AOC_SESSION_COOKIE is not set in .env")
        print("Please add AOC_SESSION_COOKIE=\"your_cookie_here\" to .env")
        return False

    answer_str = str(answer).strip()
    if not answer_str or answer_str in ("0", "-1", "N/A", "TODO"):
        print(f"⚠️ Warning: Answer '{answer_str}' looks like a placeholder or default value.")
        if not force:
            confirm = input("Are you sure you want to submit this? [y/N]: ").strip().lower()
            if confirm != "y":
                print("Submission cancelled.")
                return False

    if not force:
        print(f"\n🚀 Ready to Submit:")
        print(f"   • Year:   {year}")
        print(f"   • Day:    {day}")
        print(f"   • Part:   {part}")
        print(f"   • Answer: {answer_str}")
        confirm = input("\nSubmit to Advent of Code? [y/N]: ").strip().lower()
        if confirm != "y":
            print("Submission cancelled.")
            return False

    url = f"https://adventofcode.com/{year}/day/{day}/answer"
    payload = {
        "level": str(part),
        "answer": answer_str
    }
    data = urllib.parse.urlencode(payload).encode("utf-8")
    
    headers = {
        "User-Agent": "github.com/dkhawk/aoc by Dale Hawkins (AoC-Answer-Submitter)",
        "Cookie": f"session={cookie}",
        "Content-Type": "application/x-www-form-urlencoded"
    }
    req = urllib.request.Request(url, data=data, headers=headers, method="POST")

    print(f"\n📡 Submitting answer '{answer_str}' to {url}...")
    try:
        with urllib.request.urlopen(req) as resp:
            html = resp.read().decode("utf-8")
            response_text = extract_article_text(html)

            print("\n" + "=" * 60)
            if "That's the right answer!" in response_text:
                print("🎉 THAT'S THE RIGHT ANSWER! ⭐")
                print(f"   {response_text}")
                print("=" * 60)
                
                # Automatically fetch the updated problem description (which now includes Part 2 or completion)
                print("\n🔄 Refreshing PROBLEM.md with updated puzzle text...")
                fetch_problem(year, day, delay=0, verbose=True)

                # Update official stats if tracking
                update_official_stats(year, day, part)
                return True

            elif "Did you already complete it?" in response_text or "You don't seem to be solving the right level" in response_text:
                print("ℹ️  ALREADY COMPLETED / WRONG LEVEL")
                print(f"   {response_text}")
                print("=" * 60)
                print("\n🔄 Refreshing PROBLEM.md...")
                fetch_problem(year, day, delay=0, verbose=True)
                return True

            elif "That's not the right answer; your answer is too high" in response_text:
                print("📈 WRONG ANSWER: Your answer is TOO HIGH.")
                print(f"   {response_text}")
                print("=" * 60)
                return False

            elif "That's not the right answer; your answer is too low" in response_text:
                print("📉 WRONG ANSWER: Your answer is TOO LOW.")
                print(f"   {response_text}")
                print("=" * 60)
                return False

            elif "That's not the right answer" in response_text:
                print("❌ WRONG ANSWER: Incorrect.")
                print(f"   {response_text}")
                print("=" * 60)
                return False

            elif "You gave an answer too recently" in response_text:
                print("⏳ RATE LIMITED / COOLDOWN:")
                print(f"   {response_text}")
                print("=" * 60)
                return False

            else:
                print(f"❓ Unexpected response:\n{response_text}")
                print("=" * 60)
                return False

    except urllib.error.HTTPError as e:
        print(f"❌ HTTP Error {e.code}: {e.reason}")
        return False
    except Exception as e:
        print(f"❌ Request failed: {e}")
        return False

def main():
    parser = argparse.ArgumentParser(description="Submit and verify an Advent of Code answer.")
    parser.add_argument("--year", "-y", type=int, required=True, help="Puzzle year (e.g. 2016)")
    parser.add_argument("--day", "-d", type=int, required=True, help="Puzzle day (e.g. 18)")
    parser.add_argument("--part", "-p", type=int, choices=[1, 2], default=1, help="Part (1 or 2, default: 1)")
    parser.add_argument("--answer", "-a", type=str, help="Answer string to submit")
    parser.add_argument("--run", "-r", action="store_true", help="Run Kotlin solution via Gradle to extract the answer")
    parser.add_argument("--force", "-f", action="store_true", help="Submit without confirmation prompt")

    args = parser.parse_args()

    answer = args.answer
    if args.run:
        answer = run_solution_for_part(args.year, args.day, args.part)
        if answer is None:
            sys.exit(1)

    if not answer:
        print("❌ Error: Must specify --answer <value> or use --run to execute the code.")
        sys.exit(1)

    success = submit_answer(args.year, args.day, args.part, answer, force=args.force)
    sys.exit(0 if success else 1)

if __name__ == "__main__":
    main()
