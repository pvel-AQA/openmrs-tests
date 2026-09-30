import json
import sys
import os

HISTORY_PATH = "allure-history/last-history/history.json"
LAST_N = int(os.environ.get("STABLE_LAST_N", "5"))
MIN_STABLE_PERCENT = float(os.environ.get("MIN_STABLE_PERCENT", "0"))

if not os.path.exists(HISTORY_PATH):
    print(f"::warning::history.json not found at {HISTORY_PATH}, skipping stability check")
    with open(os.environ.get("GITHUB_OUTPUT", "/dev/null"), "a") as out:
        out.write("stable_percent=0\n")
        out.write("stable_count=0\n")
        out.write("flaky_count=0\n")
        out.write("always_failed_count=0\n")
        out.write("skipped_count=0\n")
    sys.exit(0)

with open(HISTORY_PATH, encoding="utf-8") as f:
    history = json.load(f)

stable = 0
flaky = 0
always_failed = 0
skipped = 0

for hist_id, entry in history.items():
    items = entry.get("items", [])
    if not items:
        continue

    last_items = items[-LAST_N:]
    statuses = [i.get("status") for i in last_items]

    if all(s == "skipped" for s in statuses):
        skipped += 1
        continue

    has_passed = any(s == "passed" for s in statuses)
    has_failed = any(s in ("failed", "broken") for s in statuses)

    if all(s == "passed" for s in statuses):
        stable += 1
    elif has_passed and has_failed:
        flaky += 1
    else:
        always_failed += 1

denominator = stable + flaky + always_failed
percent = (stable * 100 / denominator) if denominator else 0.0

print(f"Total tests in history: {len(history)}")
print(f"Stable:                 {stable}")
print(f"Flaky:                  {flaky}")
print(f"Always failed:          {always_failed}")
print(f"Skipped (excluded):     {skipped}")
print(f"Stable Automated %:     {percent:.2f}%   ({stable} / {denominator})")

with open(os.environ.get("GITHUB_OUTPUT", "/dev/null"), "a") as out:
    out.write(f"stable_percent={percent:.2f}\n")
    out.write(f"stable_count={stable}\n")
    out.write(f"flaky_count={flaky}\n")
    out.write(f"always_failed_count={always_failed}\n")
    out.write(f"skipped_count={skipped}\n")

if MIN_STABLE_PERCENT > 0 and percent < MIN_STABLE_PERCENT:
    print(f"::error::Stable % {percent:.2f} is below threshold {MIN_STABLE_PERCENT}")
    sys.exit(1)
