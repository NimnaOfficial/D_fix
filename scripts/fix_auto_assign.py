import re

with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    content = f.read()

# Replace the inner join block for auto-assignment
target = r"SELECT t\.id FROM technicians t\s+INNER JOIN technician_services ts ON ts\.technician_id = t\.id\s+WHERE t\.branch_id = \? AND t\.availability_status = 'AVAILABLE' AND ts\.service_id = \?\s+LIMIT 1"
replacement = r"SELECT t.id FROM technicians t WHERE t.branch_id = ? AND t.availability_status = 'AVAILABLE' LIMIT 1"

content = re.sub(target, replacement, content)

# Also update the bind() that follows it.
# It used to be .bind(finalBranchId, service_id).first();
# Now it should be .bind(finalBranchId).first();
target2 = r"\.bind\(finalBranchId, service_id\)\.first\(\);"
replacement2 = r".bind(finalBranchId).first();"

content = re.sub(target2, replacement2, content)

with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
    f.write(content)
