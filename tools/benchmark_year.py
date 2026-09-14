#!/usr/bin/env python3
"""
benchmark_year.py
Benchmarks all solved days for a given Advent of Code year (e.g., 2025),
extracting Part 1, Part 2, and Total execution runtimes, and updates
both the year's main README.md and individual day README files.

Usage:
  python3 tools/benchmark_year.py <YEAR> [--runs 3] [--update]

Example:
  python3 tools/benchmark_year.py 2025 --update
"""

import os
import sys
import re
import argparse
import subprocess

PROJECT_ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
KOTLIN_BASE = os.path.join(PROJECT_ROOT, "app", "src", "main", "kotlin", "com", "sphericalchickens")
PROBLEMS_BASE = os.path.join(PROJECT_ROOT, "problems")

def get_max_days(year):
    return 12 if year == 2025 else 25

def to_ms(val, unit):
    v = float(val)
    if unit == "s": return v * 1000.0
    if unit == "ms": return v
    if unit == "µs": return v / 1000.0
    if unit == "ns": return v / 1000000.0
    return v

def format_ms(ms):
    if ms is None or ms == 0.0:
        return "N/A" if ms is None else "<1ms"
    if ms < 0.1:
        return "<1ms"
    if ms < 10.0:
        return f"{ms:.1f}ms"
    return f"{round(ms)}ms"

def get_day_title(year, day):
    day_padded = f"{int(day):02d}"
    year_readme = os.path.join(KOTLIN_BASE, f"aoc{year}", "README.md")
    if os.path.exists(year_readme):
        with open(year_readme, "r", encoding="utf-8") as f:
            for line in f:
                m = re.match(r"^\|\s*" + day_padded + r"\s*\|\s*(.*?)\s*\|", line)
                if m and m.group(1) != "Title":
                    return m.group(1).strip()

    readme_path = os.path.join(KOTLIN_BASE, f"aoc{year}", f"day{day_padded}", "README.md")
    prob_path = os.path.join(PROBLEMS_BASE, str(year), f"day{day_padded}.md")
    
    source = readme_path if os.path.exists(readme_path) else (prob_path if os.path.exists(prob_path) else None)
    if source:
        with open(source, "r", encoding="utf-8", errors="ignore") as f:
            for line in f:
                m = re.search(r"Day \d+:\s*(.*?)(---|#|$)", line)
                if m:
                    return m.group(1).strip()
    return f"Day {day}"

def run_day_benchmark(year, day, runs=3):
    day_padded = f"{int(day):02d}"
    cls = f"com.sphericalchickens.aoc{year}.day{day_padded}.Day{day_padded}Kt"
    cmd = ["./gradlew", "run", f"-PmainClass={cls}"]
    
    p1_times = []
    p2_times = []
    
    for _ in range(runs):
        res = subprocess.run(cmd, cwd=PROJECT_ROOT, capture_output=True, text=True)
        out = res.stdout
        
        p1_m = re.search(r"Part 1 runtime:\s*([\d\.]+)\s*(ms|µs|s|ns)", out)
        p2_m = re.search(r"Part 2 runtime:\s*([\d\.]+)\s*(ms|µs|s|ns)", out)
        
        if p1_m:
            p1_times.append(to_ms(p1_m.group(1), p1_m.group(2)))
        if p2_m:
            p2_times.append(to_ms(p2_m.group(1), p2_m.group(2)))
            
    avg_p1 = (sum(p1_times) / len(p1_times)) if p1_times else None
    avg_p2 = (sum(p2_times) / len(p2_times)) if p2_times else None
    
    total = ((avg_p1 or 0.0) + (avg_p2 or 0.0)) if (avg_p1 is not None or avg_p2 is not None) else None
    return avg_p1, avg_p2, total

def update_individual_readme(year, day, p1_ms, p2_ms, total_ms):
    day_padded = f"{int(day):02d}"
    readme_path = os.path.join(KOTLIN_BASE, f"aoc{year}", f"day{day_padded}", "README.md")
    if not os.path.exists(readme_path):
        return

    with open(readme_path, "r", encoding="utf-8") as f:
        content = f.read()

    p1_str = format_ms(p1_ms)
    p2_str = format_ms(p2_ms)
    tot_str = format_ms(total_ms)

    perf_table = f"## Performance\n\n| Part | Runtime |\n|:---:|:---:|\n| Part 1 | {p1_str} |\n| Part 2 | {p2_str} |\n| **Total** | **{tot_str}** |\n"

    if "## Performance" in content:
        content = re.sub(r"## Performance\n\n\| Part \| Runtime \|.*?(?=\n<img|\n#|\Z)", perf_table, content, flags=re.DOTALL)
    else:
        if "<img " in content:
            content = content.replace("<img ", f"{perf_table}\n<img ")
        else:
            content += f"\n\n{perf_table}"

    with open(readme_path, "w", encoding="utf-8") as f:
        f.write(content)

def update_year_readme(year, metrics):
    year_dir = os.path.join(KOTLIN_BASE, f"aoc{year}")
    readme_path = os.path.join(year_dir, "README.md")
    if not os.path.exists(readme_path):
        return

    table_rows = [
        "## Solutions",
        "",
        "| Day | Title | Part 1 | Part 2 | Total | Links |",
        "|:---:|:---|:---:|:---:|:---:|:---|"
    ]

    for day, title, p1_ms, p2_ms, tot_ms in metrics:
        dp = f"{day:02d}"
        p1_str = format_ms(p1_ms)
        p2_str = format_ms(p2_ms)
        tot_str = format_ms(tot_ms)
        row = f"| {dp} | {title} | {p1_str} | {p2_str} | {tot_str} | [Readme](day{dp}/README.md) / [Code](day{dp}/Day{dp}.kt) |"
        table_rows.append(row)

    new_table_sec = "\n".join(table_rows)

    with open(readme_path, "r", encoding="utf-8") as f:
        content = f.read()

    if "## Solutions" in content:
        content = re.sub(r"## Solutions\n\n\| Day \| Title \|.*?(?=\n## Gallery|\n#|\Z)", new_table_sec + "\n", content, flags=re.DOTALL)
        with open(readme_path, "w", encoding="utf-8") as f:
            f.write(content)

def main():
    parser = argparse.ArgumentParser(description="Benchmark Advent of Code year solutions.")
    parser.add_argument("year", type=int, help="Year to benchmark (e.g. 2025)")
    parser.add_argument("--runs", type=int, default=3, help="Number of benchmark iterations per day (default 3)")
    parser.add_argument("--update", action="store_true", help="Update year README.md and individual day READMEs")

    args = parser.parse_args()
    year = args.year
    max_days = get_max_days(year)
    
    print("=" * 75)
    print(f" ⏱️ BENCHMARKING ADVENT OF CODE {year} ({args.runs} runs per day)")
    print("=" * 75)

    metrics = []

    for d in range(1, max_days + 1):
        dp = f"{d:02d}"
        kt_path = os.path.join(KOTLIN_BASE, f"aoc{year}", f"day{dp}", f"Day{dp}.kt")
        if not os.path.exists(kt_path):
            continue

        title = get_day_title(year, d)
        with open(kt_path, "r", encoding="utf-8") as f:
            kt_code = f.read()
        is_placeholder = "// TODO: Implement" in kt_code or "Placeholder template" in kt_code or "Placeholder solution" in kt_code

        if is_placeholder:
            print(f"Skipping Day {dp}: {title} (Placeholder template)...")
            p1_ms, p2_ms, tot_ms = None, None, None
        else:
            print(f"Benchmarking Day {dp}: {title}...")
            p1_ms, p2_ms, tot_ms = run_day_benchmark(year, d, runs=args.runs)
        
        p1_str = format_ms(p1_ms)
        p2_str = format_ms(p2_ms)
        tot_str = format_ms(tot_ms)
        
        print(f"  ➜ P1: {p1_str:<7} | P2: {p2_str:<7} | Total: {tot_str}")
        metrics.append((d, title, p1_ms, p2_ms, tot_ms))

        if args.update:
            update_individual_readme(year, d, p1_ms, p2_ms, tot_ms)

    if args.update:
        update_year_readme(year, metrics)
        print("\n✅ Successfully updated year README.md and day README files!")

    print("=" * 75)

if __name__ == "__main__":
    main()
