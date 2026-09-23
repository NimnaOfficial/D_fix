import re

with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    content = f.read()

# Fix the bind statement
target = r"\.bind\(branch_id, service_id\)\s*\.first\(\);"
replacement = r".bind(branch_id).first();"
content = re.sub(target, replacement, content)

with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
    f.write(content)
