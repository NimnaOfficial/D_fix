import re

with open('cloudflare-backend/worker.js', 'r') as f:
    content = f.read()

# Replace profile updatedUser query to include specialization
content = content.replace(
    'u.profile_image_url, c.city, c.address FROM users u LEFT JOIN customers c ON u.id = c.user_id \\nWHERE u.id = ?',
    'u.profile_image_url, c.city, c.address, t.specialization FROM users u LEFT JOIN customers c ON u.id = c.user_id LEFT JOIN technicians t ON u.id = t.user_id \\nWHERE u.id = ?'
)

# And if it is a single line without newline
content = content.replace(
    'u.profile_image_url, c.city, c.address FROM users u LEFT JOIN customers c ON u.id = c.user_id \\r\\nWHERE u.id = ?',
    'u.profile_image_url, c.city, c.address, t.specialization FROM users u LEFT JOIN customers c ON u.id = c.user_id LEFT JOIN technicians t ON u.id = t.user_id \\r\\nWHERE u.id = ?'
)

# Wait let's just use regex
content = re.sub(
    r'SELECT u\.id(.*?)c\.address FROM users u LEFT JOIN customers c ON u\.id = c\.user_id\s*WHERE u\.id = \?',
    r'SELECT u.id\g<1>c.address, t.specialization FROM users u LEFT JOIN customers c ON u.id = c.user_id LEFT JOIN technicians t ON u.id = t.user_id WHERE u.id = ?',
    content,
    flags=re.DOTALL
)

# Add logic block
block_to_add = '''        }
        
        if (user.role === "TECHNICIAN" && specialization) {
            await env.DB.prepare("UPDATE technicians SET specialization = ? WHERE user_id = ?").bind(specialization.trim(), user.id).run();
        }
        
        const updatedUser ='''

content = content.replace('        }\\n        \\n        const updatedUser =', block_to_add)
content = content.replace('        }\\r\\n        \\r\\n        const updatedUser =', block_to_add)

with open('cloudflare-backend/worker.js', 'w') as f:
    f.write(content)
