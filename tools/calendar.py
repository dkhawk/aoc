#!/usr/bin/env python3
"""
calendar.py
Master Calendar Generator & Interactive Problem Synopsis Viewer for Advent of Code.

Usage:
  python3 tools/calendar.py                     -> Interactive Calendar Dashboard & Synopsis Viewer
  python3 tools/calendar.py update-readme       -> Regenerate root README.md Master Calendar
  python3 tools/calendar.py <year> <day>         -> View synopsis & status for specific year/day
"""

import os
import sys
import re

PROJECT_ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
KOTLIN_BASE = os.path.join(PROJECT_ROOT, "app", "src", "main", "kotlin", "com", "sphericalchickens")
PROBLEMS_BASE = os.path.join(PROJECT_ROOT, "problems")

YEARS = [2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2024, 2025]

def get_max_days(year):
    if year == 2025:
        return 12
    return 25

def get_day_status(year, day):
    day_padded = f"{int(day):02d}"
    day_dir = os.path.join(KOTLIN_BASE, f"aoc{year}", f"day{day_padded}")
    day_kt = os.path.join(day_dir, f"Day{day_padded}.kt")
    
    if not os.path.exists(day_kt):
        return {"status": "unsolved", "part1": False, "part2": False, "symbol": "⭕"}
    
    with open(day_kt, "r", encoding="utf-8", errors="ignore") as f:
        code = f.read()
    
    has_part1 = "part1(" in code or "solvePart1" in code or "Part 1:" in code or "fun main" in code
    has_part2 = "part2(" in code or "part2c(" in code or "solvePart2" in code or "Part 2:" in code
    
    if "TODO" in code or "not implemented" in code.lower():
        if "part2" in code.lower() and "TODO" in code:
            has_part2 = False

    if has_part1 and has_part2:
        return {"status": "complete", "part1": True, "part2": True, "symbol": "🌟"}
    elif has_part1:
        return {"status": "part1_only", "part1": True, "part2": False, "symbol": "⭐"}
    else:
        return {"status": "in_progress", "part1": False, "part2": False, "symbol": "🛠️"}

def get_day_synopsis(year, day):
    day_padded = f"{int(day):02d}"
    
    readme_path = os.path.join(KOTLIN_BASE, f"aoc{year}", f"day{day_padded}", "README.md")
    if os.path.exists(readme_path):
        with open(readme_path, "r", encoding="utf-8", errors="ignore") as f:
            content = f.read()
            return extract_synopsis_from_markdown(content)
            
    prob_path = os.path.join(PROBLEMS_BASE, str(year), f"day{day_padded}.md")
    if os.path.exists(prob_path):
        with open(prob_path, "r", encoding="utf-8", errors="ignore") as f:
            content = f.read()
            return extract_synopsis_from_markdown(content)
            
    return {"title": f"Day {day}", "setup": "No problem description available locally. Run tools/fetch_problems.py to download.", "part1": "", "part2": ""}

def extract_synopsis_from_markdown(content):
    lines = content.splitlines()
    title = "Unknown Day"
    setup_lines = []
    part1_lines = []
    part2_lines = []
    
    current_sec = "setup"
    
    for line in lines:
        if line.startswith("# ") or line.startswith("## --- Day"):
            m = re.search(r"Day \d+:\s*(.*?)(---|#|$)", line)
            if m:
                title = m.group(1).strip()
            continue
        elif "## Setup" in line or "--- Day" in line:
            current_sec = "setup"
            continue
        elif "## Part 1" in line or "--- Part One ---" in line:
            current_sec = "part1"
            continue
        elif "## Part 2" in line or "--- Part Two ---" in line:
            current_sec = "part2"
            continue
        elif "## Solution" in line or "## Gallery" in line:
            break
            
        if current_sec == "setup":
            setup_lines.append(line)
        elif current_sec == "part1":
            part1_lines.append(line)
        elif current_sec == "part2":
            part2_lines.append(line)
            
    return {
        "title": title,
        "setup": "\n".join(setup_lines).strip()[:500] or "Problem statement available in local record.",
        "part1": "\n".join(part1_lines).strip()[:400],
        "part2": "\n".join(part2_lines).strip()[:400]
    }

def print_dashboard():
    print("=========================================================================")
    print("                  🎄 ADVENT OF CODE MASTER CALENDAR 🎄                   ")
    print("=========================================================================")
    print("Legend:  🌟 Both Parts Complete   ⭐ Part 1 Complete   🛠️ In Progress   ⭕ Unsolved\n")
    
    header = f"{'Year':<6} | " + " ".join(f"{d:2d}" for d in range(1, 26))
    print(header)
    print("-" * len(header))
    
    for y in YEARS:
        max_d = get_max_days(y)
        row = f"{y:<6} | "
        symbols = []
        for d in range(1, 26):
            if d <= max_d:
                st = get_day_status(y, d)
                symbols.append(st["symbol"])
            else:
                symbols.append("  ")
        print(row + "  ".join(symbols))
    print("-" * len(header))

def display_day_info(year, day):
    st = get_day_status(year, day)
    syn = get_day_synopsis(year, day)
    day_padded = f"{int(day):02d}"
    aoc_url = f"https://adventofcode.com/{year}/day/{day}"
    
    print("\n" + "="*70)
    print(f" 🎅 ADVENT OF CODE {year} - DAY {day}: {syn['title']} {st['symbol']}")
    print("="*70)
    print(f"Status:       Part 1 {'✅ Complete' if st['part1'] else '❌ Pending'} | Part 2 {'✅ Complete' if st['part2'] else '❌ Pending'}")
    print(f"Solution File: app/src/main/kotlin/com/sphericalchickens/aoc{year}/day{day_padded}/Day{day_padded}.kt")
    print(f"AoC Problem:  {aoc_url}")
    print("\n--- 📖 PROBLEM SYNOPSIS ---")
    print(syn["setup"])
    if syn["part1"]:
        print("\n--- Part 1 Objective ---")
        print(syn["part1"])
    if syn["part2"]:
        print("\n--- Part 2 Objective ---")
        print(syn["part2"])
    print(f"\n🔗 Open in browser: {aoc_url}")
    print("="*70 + "\n")

def generate_readme_calendar():
    readme_file = os.path.join(PROJECT_ROOT, "README.md")
    
    grid_rows = []
    grid_rows.append("| Year | " + " | ".join(f"{d}" for d in range(1, 26)) + " |")
    grid_rows.append("|:---:| " + " | ".join(":---:" for _ in range(25)) + " |")
    
    total_stars = 0
    
    for y in YEARS:
        row = [f"**[{y}](app/src/main/kotlin/com/sphericalchickens/aoc{y}/README.md)**"]
        max_d = get_max_days(y)
        for d in range(1, 26):
            if d <= max_d:
                st = get_day_status(y, d)
                if st["part1"]: total_stars += 1
                if st["part2"]: total_stars += 1
                day_padded = f"{d:02d}"
                kt_file = f"app/src/main/kotlin/com/sphericalchickens/aoc{y}/day{day_padded}/Day{day_padded}.kt"
                if os.path.exists(os.path.join(PROJECT_ROOT, kt_file)):
                    row.append(f"[{st['symbol']}]({kt_file})")
                else:
                    row.append(st['symbol'])
            else:
                row.append("—")
        grid_rows.append("| " + " | ".join(row) + " |")
        
    calendar_md = "\n".join(grid_rows)
    
    content = f"""# advent-of-code [🐔](spherical_chickens.md)

Welcome to the unified **Advent of Code** multi-year repository (2015–2025).

Total Stars Collected: ⭐ **{total_stars}**

## Master Calendar

Legend: 🌟 Both Parts Complete | ⭐ Part 1 Complete | 🛠️ In Progress | ⭕ Pending

{calendar_md}

---

## Web Calendar & Interactive Synopsis Viewer

You can use the built-in Web Application or CLI calendar tool to inspect any problem, view synopses, and open direct problem links:

```bash
# Launch interactive Web Calendar Application
python3 tools/web_calendar.py

# Display interactive CLI dashboard
python3 tools/calendar.py
```

## Local Problem Statement Record

Problem descriptions and input files are downloaded locally for offline reference:
- Local problem records: `problems/<YEAR>/day<DAY>.md` (Gitignored)
- Input files: `app/src/main/resources/aoc<YEAR>/day<DAY>_input.txt` (Gitignored)

To download problem statements for a year:
```bash
python3 tools/fetch_problems.py <YEAR>
```

## Running Solutions

This project uses Gradle:
- Run all checks: `./gradlew check`
- Build project: `./gradlew build`
"""
    with open(readme_file, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"Updated Master Calendar in {readme_file}")

def main():
    if len(sys.argv) == 2 and sys.argv[1] == "update-readme":
        generate_readme_calendar()
        return

    if len(sys.argv) == 3:
        try:
            y = int(sys.argv[1])
            d = int(sys.argv[2])
            display_day_info(y, d)
            return
        except ValueError:
            pass

    print_dashboard()
    print("\nType a Year and Day to inspect (e.g. '2025 1' or '2023 14') or 'q' to quit:")
    try:
        while True:
            cmd = input("aoc-calendar> ").strip()
            if not cmd or cmd.lower() in ("q", "exit", "quit"):
                break
            parts = cmd.split()
            if len(parts) == 2 and parts[0].isdigit() and parts[1].isdigit():
                display_day_info(int(parts[0]), int(parts[1]))
            else:
                print("Invalid format. Usage: <year> <day> (e.g. '2025 1')")
    except (KeyboardInterrupt, EOFError):
        print("\nGoodbye!")

if __name__ == "__main__":
    main()
