import re

with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace(
    'await env.DB.prepare(UPDATE technicians SET specialization = ? WHERE user_id = ?)',
    'await env.DB.prepare(UPDATE technicians SET specialization = ? WHERE user_id = ?)'
)

with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
    f.write(content)
