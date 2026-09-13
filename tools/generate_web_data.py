#!/usr/bin/env python3
"""
generate_web_data.py
Scans the aoc repository to extract full metadata for all years (2015-2025) and days,
respecting year-specific day limits (e.g. 12 days for 2025).
"""

import os
import json
import re

PROJECT_ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
KOTLIN_BASE = os.path.join(PROJECT_ROOT, "app", "src", "main", "kotlin", "com", "sphericalchickens")
PROBLEMS_BASE = os.path.join(PROJECT_ROOT, "problems")

YEARS = [2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2024, 2025]

def get_max_days(year):
    if year == 2025:
        return 12
    return 25

def get_day_data(year, day):
    day_padded = f"{int(day):02d}"
    day_dir = os.path.join(KOTLIN_BASE, f"aoc{year}", f"day{day_padded}")
    day_kt = os.path.join(day_dir, f"Day{day_padded}.kt")
    
    relative_kt_path = f"app/src/main/kotlin/com/sphericalchickens/aoc{year}/day{day_padded}/Day{day_padded}.kt"
    relative_prob_path = f"problems/{year}/day{day_padded}.md"
    
    has_kt = os.path.exists(day_kt)
    has_part1 = False
    has_part2 = False
    
    if has_kt:
        with open(day_kt, "r", encoding="utf-8", errors="ignore") as f:
            code = f.read()
        has_part1 = "part1(" in code or "solvePart1" in code or "Part 1:" in code or "fun main" in code
        has_part2 = "part2(" in code or "part2c(" in code or "solvePart2" in code or "Part 2:" in code
        if "TODO" in code and "part2" in code.lower():
            has_part2 = False

    status = "unsolved"
    symbol = "⭕"
    if has_part1 and has_part2:
        status = "complete"
        symbol = "🌟"
    elif has_part1:
        status = "part1_only"
        symbol = "⭐"
    elif has_kt:
        status = "in_progress"
        symbol = "🛠️"

    title = f"Day {day}"
    setup = "Problem description pending. Run `python3 tools/fetch_problems.py` to download local records."
    part1_desc = ""
    part2_desc = ""

    readme_path = os.path.join(day_dir, "README.md")
    prob_path = os.path.join(PROBLEMS_BASE, str(year), f"day{day_padded}.md")
    
    source_md = None
    if os.path.exists(readme_path):
        source_md = readme_path
    elif os.path.exists(prob_path):
        source_md = prob_path

    if source_md:
        with open(source_md, "r", encoding="utf-8", errors="ignore") as f:
            content = f.read()
        lines = content.splitlines()
        setup_lines = []
        p1_lines = []
        p2_lines = []
        curr = "setup"
        for line in lines:
            if "Day " in line and (line.startswith("#") or line.startswith("##")):
                m = re.search(r"Day \d+:\s*(.*?)(---|#|$)", line)
                if m:
                    title = m.group(1).strip()
            elif "## Setup" in line or "--- Day" in line:
                curr = "setup"
            elif "## Part 1" in line or "--- Part One ---" in line:
                curr = "part1"
            elif "## Part 2" in line or "--- Part Two ---" in line:
                curr = "part2"
            elif "## Solution" in line or "## Gallery" in line:
                break
            else:
                if curr == "setup":
                    setup_lines.append(line)
                elif curr == "part1":
                    p1_lines.append(line)
                elif curr == "part2":
                    p2_lines.append(line)

        if setup_lines: setup = "\n".join(setup_lines).strip()
        if p1_lines: part1_desc = "\n".join(p1_lines).strip()
        if p2_lines: part2_desc = "\n".join(p2_lines).strip()

    aoc_url = f"https://adventofcode.com/{year}/day/{day}"

    return {
        "year": year,
        "day": day,
        "dayPadded": day_padded,
        "title": title,
        "status": status,
        "symbol": symbol,
        "part1Complete": has_part1,
        "part2Complete": has_part2,
        "setup": setup,
        "part1Synopsis": part1_desc,
        "part2Synopsis": part2_desc,
        "ktPath": relative_kt_path if has_kt else None,
        "probPath": relative_prob_path if os.path.exists(os.path.join(PROJECT_ROOT, relative_prob_path)) else None,
        "aocUrl": aoc_url
    }

def generate_data():
    all_days = []
    total_stars = 0
    total_completed = 0
    
    for y in YEARS:
        max_d = get_max_days(y)
        for d in range(1, max_d + 1):
            item = get_day_data(y, d)
            if item["part1Complete"]: total_stars += 1
            if item["part2Complete"]: total_stars += 1
            if item["status"] == "complete": total_completed += 1
            all_days.append(item)

    payload = {
        "stats": {
            "totalStars": total_stars,
            "totalCompletedDays": total_completed,
            "totalDays": len(all_days),
            "yearsCount": len(YEARS)
        },
        "years": YEARS,
        "maxDaysPerYear": {y: get_max_days(y) for y in YEARS},
        "days": all_days
    }

    out_file = os.path.join(PROJECT_ROOT, "web", "data.json")
    os.makedirs(os.path.dirname(out_file), exist_ok=True)
    with open(out_file, "w", encoding="utf-8") as f:
        json.dump(payload, f, indent=2)
    print(f"Generated web data in {out_file}")

if __name__ == "__main__":
    generate_data()
