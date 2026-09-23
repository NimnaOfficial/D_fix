import re

with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    content = f.read()

# Add specialization logic
content = re.sub(
    r'(if\s*\(user\.role === \'CUSTOMER\'\)\s*\{[\s\S]*?\}\s*)\n\s*const updatedUser =',
    r'\1\n        if (user.role === \'TECHNICIAN\' && typeof specialization !== \'undefined\') {\n            await env.DB.prepare(UPDATE technicians SET specialization = ? WHERE user_id = ?).bind(specialization.trim(), user.id).run();\n        }\n\n        const updatedUser =',
    content
)

content = re.sub(
    r'c\.address FROM users u LEFT JOIN customers c ON u\.id = c\.user_id\s*WHERE u\.id = \?',
    r'c.address, t.specialization FROM users u LEFT JOIN customers c ON u.id = c.user_id LEFT JOIN technicians t ON u.id = t.user_id WHERE u.id = ?',
    content
)

with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
    f.write(content)
