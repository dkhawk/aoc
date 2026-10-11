#!/usr/bin/env python3
"""
fetch_problems.py
Downloads Advent of Code problem statements locally into the solution's directory:
  app/src/main/kotlin/com/sphericalchickens/aoc<year>/day<XX>/PROBLEM.md
and mirrors to:
  problems/<year>/day<XX>.md

Both locations are gitignored so problem text is never committed to GitHub.
Uses AOC_SESSION_COOKIE from .env to fetch Part 2 if completed or unlocked.
"""

import os
import sys
import re
import time
import argparse
import urllib.request
import urllib.error
from html.parser import HTMLParser

class AoCHTMLToMarkdown(HTMLParser):
    def __init__(self):
        super().__init__()
        self.markdown = []
        self.tag_stack = []
        self.in_code = False
        self.in_pre = False
        self.current_href = ""

    def handle_starttag(self, tag, attrs):
        self.tag_stack.append(tag)
        if tag == "h2":
            self.markdown.append("\n\n## ")
        elif tag == "p":
            self.markdown.append("\n\n")
        elif tag == "code":
            self.in_code = True
            if self.tag_stack and len(self.tag_stack) >= 2 and self.tag_stack[-2] == "pre":
                self.in_pre = True
                self.markdown.append("\n```\n")
            else:
                self.markdown.append("`")
        elif tag == "em":
            self.markdown.append("**")
        elif tag == "pre":
            pass
        elif tag == "li":
            self.markdown.append("\n- ")
        elif tag == "a":
            attrs_dict = dict(attrs)
            self.current_href = attrs_dict.get("href", "")
            self.markdown.append("[")

    def handle_endtag(self, tag):
        if self.tag_stack and self.tag_stack[-1] == tag:
            self.tag_stack.pop()
        if tag == "h2":
            self.markdown.append("\n\n")
        elif tag == "code":
            self.in_code = False
            if self.in_pre:
                self.in_pre = False
                self.markdown.append("\n```\n")
            else:
                self.markdown.append("`")
        elif tag == "em":
            self.markdown.append("**")
        elif tag == "a":
            href = getattr(self, "current_href", "")
            self.markdown.append(f"]({href})")

    def handle_data(self, data):
        self.markdown.append(data)

def parse_html_to_markdown(html_content):
    parser = AoCHTMLToMarkdown()
    articles = re.findall(r'<article class="day-desc">(.*?)</article>', html_content, re.DOTALL)
    if not articles:
        return html_content
    
    md_out = []
    for article in articles:
        parser = AoCHTMLToMarkdown()
        parser.feed(article)
        text = "".join(parser.markdown).strip()
        # Clean redundant empty lines
        text = re.sub(r'\n{3,}', '\n\n', text)
        md_out.append(text)
    
    return "\n\n---\n\n".join(md_out) + "\n"

def get_session_cookie():
    env_file = os.path.join(os.path.dirname(__file__), "..", ".env")
    if os.path.exists(env_file):
        with open(env_file, "r") as f:
            for line in f:
                if line.startswith("AOC_SESSION_COOKIE="):
                    return line.strip().split("=", 1)[1].strip('"\'')
    return None

def max_days_for_year(year):
    return 12 if int(year) >= 2025 else 25

def is_day_unfinished(repo_root, year, day):
    dp = f"{int(day):02d}"
    kt_path = os.path.join(repo_root, "app", "src", "main", "kotlin", "com", "sphericalchickens", f"aoc{year}", f"day{dp}", f"Day{dp}.kt")
    if not os.path.exists(kt_path):
        return True
    with open(kt_path, "r", encoding="utf-8") as f:
        content = f.read()
    if "Placeholder template" in content or "// TODO: Implement Part 1" in content:
        return True
    return False

def get_problem_file_path(repo_root, year, day):
    dp = f"{int(day):02d}"
    return os.path.join(repo_root, "app", "src", "main", "kotlin", "com", "sphericalchickens", f"aoc{year}", f"day{dp}", "PROBLEM.md")

def fetch_problem(year, day, delay=1, verbose=True):
    repo_root = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
    dp = f"{int(day):02d}"

    solution_dir = os.path.join(repo_root, "app", "src", "main", "kotlin", "com", "sphericalchickens", f"aoc{year}", f"day{dp}")
    os.makedirs(solution_dir, exist_ok=True)
    solution_problem_file = os.path.join(solution_dir, "PROBLEM.md")

    legacy_problems_dir = os.path.join(repo_root, "problems", str(year))
    os.makedirs(legacy_problems_dir, exist_ok=True)
    legacy_problem_file = os.path.join(legacy_problems_dir, f"day{dp}.md")

    cookie = get_session_cookie()
    url = f"https://adventofcode.com/{year}/day/{day}"
    headers = {
        "User-Agent": "github.com/dkhawk/aoc by Dale Hawkins (AoC-Problem-Fetcher)"
    }
    req = urllib.request.Request(url, headers=headers)
    if cookie:
        req.add_header("Cookie", f"session={cookie}")

    try:
        if verbose:
            print(f"📥 Fetching AoC {year} Day {day} from {url}...")
        with urllib.request.urlopen(req) as resp:
            html = resp.read().decode("utf-8")
            md = parse_html_to_markdown(html)
            
            with open(solution_problem_file, "w", encoding="utf-8") as f:
                f.write(md)
            with open(legacy_problem_file, "w", encoding="utf-8") as f:
                f.write(md)
                
            if verbose:
                print(f"   ✅ Saved problem text to {solution_problem_file}")
            if delay > 0:
                time.sleep(delay)
            return True
    except urllib.error.HTTPError as e:
        if verbose:
            print(f"   ❌ HTTP {e.code} fetching {year} Day {day}: {e.reason}")
        return False
    except Exception as e:
        if verbose:
            print(f"   ❌ Error fetching {year} Day {day}: {e}")
        return False

def main():
    parser = argparse.ArgumentParser(
        description="Fetch Advent of Code problem statements into local solution directories (PROBLEM.md)."
    )
    parser.add_argument("pos_year", nargs="?", type=int, help="Target year (e.g. 2016)")
    parser.add_argument("pos_start_day", nargs="?", type=int, help="Start day (e.g. 1)")
    parser.add_argument("pos_end_day", nargs="?", type=int, help="End day (e.g. 25)")
    parser.add_argument("--year", "-y", type=int, help="Target year")
    parser.add_argument("--day", "-d", type=int, help="Target day")
    parser.add_argument("--unfinished", "-u", action="store_true", help="Fetch problem text for all unfinished/placeholder days")
    parser.add_argument("--missing", "-m", action="store_true", help="Fetch problem text for days missing PROBLEM.md")
    parser.add_argument("--delay", type=float, default=1.0, help="Delay between HTTP requests in seconds (default: 1.0)")

    args = parser.parse_args()
    repo_root = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))

    # Determine target year
    year = args.year or args.pos_year

    if args.unfinished:
        years_to_check = [year] if year else [2015, 2016, 2018, 2019, 2021, 2022, 2023, 2024, 2025]
        count = 0
        for y in years_to_check:
            max_d = max_days_for_year(y)
            for d in range(1, max_d + 1):
                if is_day_unfinished(repo_root, y, d):
                    prob_file = get_problem_file_path(repo_root, y, d)
                    if not os.path.exists(prob_file):
                        fetch_problem(y, d, delay=args.delay)
                        count += 1
                    else:
                        print(f"⏩ AoC {y} Day {d:02d} already has PROBLEM.md")
        print(f"\n🎉 Done! Fetched problem statements for {count} unfinished day(s).")
        return

    if args.missing:
        years_to_check = [year] if year else [2015, 2016, 2018, 2019, 2021, 2022, 2023, 2024, 2025]
        count = 0
        for y in years_to_check:
            max_d = max_days_for_year(y)
            for d in range(1, max_d + 1):
                prob_file = get_problem_file_path(repo_root, y, d)
                if not os.path.exists(prob_file):
                    fetch_problem(y, d, delay=args.delay)
                    count += 1
                else:
                    print(f"⏩ AoC {y} Day {d:02d} already has PROBLEM.md")
        print(f"\n🎉 Done! Fetched {count} missing problem statements.")
        return

    # Specific day/year
    if not year:
        parser.print_help()
        sys.exit(1)

    max_d = max_days_for_year(year)
    if args.day:
        start_d = args.day
        end_d = args.day
    elif args.pos_start_day:
        start_d = args.pos_start_day
        end_d = args.pos_end_day if args.pos_end_day else (args.pos_start_day if len(sys.argv) > 3 else max_d)
    else:
        start_d = 1
        end_d = max_d

    start_d = max(1, min(start_d, max_d))
    end_d = max(1, min(end_d, max_d))

    for d in range(start_d, end_d + 1):
        fetch_problem(year, d, delay=args.delay)

if __name__ == "__main__":
    main()
