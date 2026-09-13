#!/usr/bin/env python3
"""
fetch_problems.py
Downloads Advent of Code problem statements locally (to problems/<year>/day<day>.md).
Uses AOC_SESSION_COOKIE from .env if available to fetch Part 2 if completed.
"""

import os
import sys
import re
import time
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

    def handle_starttag(self, tag, attrs):
        self.tag_stack.append(tag)
        if tag == "h2":
            self.markdown.append("\n## ")
        elif tag == "p":
            self.markdown.append("\n\n")
        elif tag == "code":
            self.in_code = True
            if self.tag_stack and self.tag_stack[-2] == "pre":
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
    # Extract <article class="day-desc"> contents
    articles = re.findall(r'<article class="day-desc">(.*?)</article>', html_content, re.DOTALL)
    if not articles:
        return html_content
    
    md_out = []
    for article in articles:
        parser = AoCHTMLToMarkdown()
        parser.feed(article)
        md_out.append("".join(parser.markdown).strip())
    
    return "\n\n---\n\n".join(md_out)

def get_session_cookie():
    env_file = os.path.join(os.path.dirname(__file__), "..", ".env")
    if os.path.exists(env_file):
        with open(env_file, "r") as f:
            for line in f:
                if line.startswith("AOC_SESSION_COOKIE="):
                    return line.strip().split("=", 1)[1].strip('"\'')
    return None

def fetch_problem(year, day, delay=1):
    day_padded = f"{int(day):02d}"
    out_dir = os.path.join(os.path.dirname(__file__), "..", "problems", str(year))
    os.makedirs(out_dir, exist_ok=True)
    out_file = os.path.join(out_dir, f"day{day_padded}.md")

    cookie = get_session_cookie()
    url = f"https://adventofcode.com/{year}/day/{day}"
    req = urllib.request.Request(url, headers={"User-Agent": "AoC-Problem-Fetcher-Bot"})
    if cookie:
        req.add_header("Cookie", f"session={cookie}")

    try:
        print(f"Fetching {year} Day {day} from {url}...")
        with urllib.request.urlopen(req) as resp:
            html = resp.read().decode("utf-8")
            md = parse_html_to_markdown(html)
            with open(out_file, "w", encoding="utf-8") as f:
                f.write(md)
            print(f"Saved to {out_file}")
            time.sleep(delay)
            return True
    except Exception as e:
        print(f"Error fetching {year} Day {day}: {e}")
        return False

def main():
    if len(sys.argv) < 2:
        print("Usage: python3 fetch_problems.py <year> [start_day] [end_day]")
        sys.exit(1)

    year = sys.argv[1]
    start_day = int(sys.argv[2]) if len(sys.argv) > 2 else 1
    end_day = int(sys.argv[3]) if len(sys.argv) > 3 else (25 if len(sys.argv) <= 2 else start_day)

    for day in range(start_day, end_day + 1):
        fetch_problem(year, day)

if __name__ == "__main__":
    main()
