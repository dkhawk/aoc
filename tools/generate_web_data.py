#!/usr/bin/env python3
"""
generate_web_data.py
Extracts full metadata for all years (2015-2025) and days,
incorporating official Advent of Code star records (465 stars total)
and local Kotlin codebase status.
"""

import os
import json
import re

PROJECT_ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
KOTLIN_BASE = os.path.join(PROJECT_ROOT, "app", "src", "main", "kotlin", "com", "sphericalchickens")
PROBLEMS_BASE = os.path.join(PROJECT_ROOT, "problems")
OFFICIAL_STATS_FILE = os.path.join(os.path.dirname(__file__), "official_stats.json")

YEARS = [2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2024, 2025]

def get_max_days(year):
    if year == 2025:
        return 12
    return 25

def load_official_stats():
    if os.path.exists(OFFICIAL_STATS_FILE):
        with open(OFFICIAL_STATS_FILE, "r", encoding="utf-8") as f:
            return json.load(f)
    return {}

def get_official_day_status(year, day, official_stats):
    year_str = str(year)
    year_info = official_stats.get("years", {}).get(year_str, {})
    stars = year_info.get("stars", 0)
    max_days = get_max_days(year)
    
    # Fully completed years (2015, 2017, 2020, 2024, 2025)
    if stars == year_info.get("maxStars", 50):
        return "complete", "🌟", True, True

    # 2016: 48 stars (Days 1..24 complete, Day 25 P1)
    if year == 2016:
        if day <= 24: return "complete", "🌟", True, True
        if day == 25: return "part1_only", "⭐", True, False
        return "unsolved", "⭕", False, False

    # 2018: 34 stars (Days 1..17 complete)
    if year == 2018:
        if day <= 17: return "complete", "🌟", True, True
        return "unsolved", "⭕", False, False

    # 2019: 39 stars (Days 1..19 complete, Day 20 P1)
    if year == 2019:
        if day <= 19: return "complete", "🌟", True, True
        if day == 20: return "part1_only", "⭐", True, False
        return "unsolved", "⭕", False, False

    # 2021: 42 stars (Days 1..20 complete, Days 21-22 P1)
    if year == 2021:
        if day <= 20: return "complete", "🌟", True, True
        if day in (21, 22, 23): return "part1_only", "⭐", True, False
        return "unsolved", "⭕", False, False

    # 2022: 42 stars (Days 1..20 complete, Days 21-22 P1)
    if year == 2022:
        if day <= 20: return "complete", "🌟", True, True
        if day == 22: return "part1_only", "⭐", True, False
        return "unsolved", "⭕", False, False

    # 2023: 36 stars (Days 1..18 complete)
    if year == 2023:
        if day <= 18: return "complete", "🌟", True, True
        return "unsolved", "⭕", False, False

    return "unsolved", "⭕", False, False

def get_day_data(year, day, official_stats):
    day_padded = f"{int(day):02d}"
    day_dir = os.path.join(KOTLIN_BASE, f"aoc{year}", f"day{day_padded}")
    day_kt = os.path.join(day_dir, f"Day{day_padded}.kt")
    
    relative_kt_path = f"app/src/main/kotlin/com/sphericalchickens/aoc{year}/day{day_padded}/Day{day_padded}.kt"
    relative_prob_path = f"problems/{year}/day{day_padded}.md"
    
    has_kt = os.path.exists(day_kt)
    kt_p1 = False
    kt_p2 = False
    
    if has_kt:
        with open(day_kt, "r", encoding="utf-8", errors="ignore") as f:
            code = f.read()
        kt_p1 = "part1(" in code or "solvePart1" in code or "Part 1:" in code or "fun main" in code
        kt_p2 = "part2(" in code or "part2c(" in code or "solvePart2" in code or "Part 2:" in code
        if "TODO" in code and "part2" in code.lower():
            kt_p2 = False

    status, symbol, p1_done, p2_done = get_official_day_status(year, day, official_stats)

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
        "part1Complete": p1_done,
        "part2Complete": p2_done,
        "ktPart1": kt_p1,
        "ktPart2": kt_p2,
        "setup": setup,
        "part1Synopsis": part1_desc,
        "part2Synopsis": part2_desc,
        "ktPath": relative_kt_path if has_kt else None,
        "probPath": relative_prob_path if os.path.exists(os.path.join(PROJECT_ROOT, relative_prob_path)) else None,
        "aocUrl": aoc_url
    }

def generate_data():
    official_stats = load_official_stats()
    all_days = []
    total_stars = official_stats.get("totalOfficialStars", 465)
    total_completed = 0
    
    for y in YEARS:
        max_d = get_max_days(y)
        for d in range(1, max_d + 1):
            item = get_day_data(y, d, official_stats)
            if item["status"] == "complete": total_completed += 1
            all_days.append(item)

    year_stats = {}
    for y in YEARS:
        y_info = official_stats.get("years", {}).get(str(y), {})
        year_stats[y] = {
            "stars": y_info.get("stars", 0),
            "maxStars": y_info.get("maxStars", 50),
            "maxDays": get_max_days(y)
        }

    payload = {
        "stats": {
            "totalStars": total_stars,
            "totalCompletedDays": total_completed,
            "totalDays": len(all_days),
            "yearsCount": len(YEARS),
            "yearStats": year_stats
        },
        "years": YEARS,
        "maxDaysPerYear": {y: get_max_days(y) for y in YEARS},
        "days": all_days
    }

    out_file = os.path.join(PROJECT_ROOT, "web", "data.json")
    os.makedirs(os.path.dirname(out_file), exist_ok=True)
    with open(out_file, "w", encoding="utf-8") as f:
        json.dump(payload, f, indent=2)
    print(f"Generated web data with official stats ({total_stars} stars) in {out_file}")

if __name__ == "__main__":
    generate_data()
