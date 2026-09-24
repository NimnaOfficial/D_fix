import re

file_path = "cloudflare-backend/worker.js"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Replace all occurrences of t.is_active with u.is_active
content = re.sub(r"t\.is_active", "u.is_active", content)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Fixed is_active SQL bugs.")

