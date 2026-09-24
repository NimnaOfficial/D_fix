import re

file_path = "cloudflare-backend/worker.js"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

pattern = r"(`SELECT u\.id, u\.first_name, u\.last_name, u\.email, u\.phone, u\.role, u\.profile_image_url, c\.city, c\.address, c\.loyalty_points FROM users u LEFT JOIN customers c ON u\.id = c\.user_id WHERE u\.id = \? LIMIT 1`,)"

repl = r"`SELECT u.id, u.first_name, u.last_name, u.email, u.phone, u.role, u.profile_image_url, c.city, c.address, c.loyalty_points, t.specialization, t.employee_code, t.branch_id FROM users u LEFT JOIN customers c ON u.id = c.user_id LEFT JOIN technicians t ON u.id = t.user_id WHERE u.id = ? LIMIT 1`,"

content = re.sub(pattern, repl, content)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Fixed /api/auth/me query.")

