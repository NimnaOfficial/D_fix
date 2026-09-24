
import base64
with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    c = f.read()

bad1 = 'let availableTech = await env.DB.prepare(            SELECT t.id \\n              FROM technicians t \\n              INNER JOIN technician_services ts ON t.id = ts.technician_id\\n              WHERE t.branch_id = ? AND ts.service_id = ? AND t.availability_status = \\'AVAILABLE\\'\\n              LIMIT 1\\n          ).bind(branch_id, service_id).first();'

good1 = 'let availableTech = await env.DB.prepare(SELECT t.id FROM technicians t INNER JOIN technician_services ts ON t.id = ts.technician_id WHERE t.branch_id = ? AND ts.service_id = ? AND t.availability_status = \\'AVAILABLE\\' LIMIT 1).bind(branch_id, service_id).first();'

bad2 = 'availableTech = await env.DB.prepare(                SELECT id \\n                  FROM technicians \\n                  WHERE branch_id = ? AND availability_status = \\'AVAILABLE\\'\\n                  LIMIT 1\\n              ).bind(branch_id).first();'

good2 = 'availableTech = await env.DB.prepare(SELECT id FROM technicians WHERE branch_id = ? AND availability_status = \\'AVAILABLE\\' LIMIT 1).bind(branch_id).first();'

c = c.replace(bad1, good1).replace(bad2, good2)

with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
    f.write(c)
print('Done!')

