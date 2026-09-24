
import re
with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    c = f.read()

c = re.sub(
    r'let availableTech = await env\.DB\.prepare\(.*?LIMIT 1\s*\\\)\.bind\(branch_id, service_id\)\.first\(\);',
    'let availableTech = await env.DB.prepare(\\n            SELECT t.id \n            FROM technicians t \n            INNER JOIN technician_services ts ON t.id = ts.technician_id\n            WHERE t.branch_id = ? AND ts.service_id = ? AND t.availability_status = \'AVAILABLE\'\n            LIMIT 1\n        \).bind(branch_id, service_id).first();',
    c, flags=re.DOTALL
)

c = re.sub(
    r'availableTech = await env\.DB\.prepare\(.*?LIMIT 1\s*\\\)\.bind\(branch_id\)\.first\(\);',
    'availableTech = await env.DB.prepare(\\n                SELECT id \n                FROM technicians \n                WHERE branch_id = ? AND availability_status = \'AVAILABLE\'\n                LIMIT 1\n            \).bind(branch_id).first();',
    c, flags=re.DOTALL
)

with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
    f.write(c)

