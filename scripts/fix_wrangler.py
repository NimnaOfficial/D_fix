import re

with open('cloudflare-backend/wrangler.toml', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('[[r2_buckets]]', '# [[r2_buckets]]')
content = content.replace('binding = "BUCKET"', '# binding = "BUCKET"')
content = content.replace('bucket_name = "techfix-images"', '# bucket_name = "techfix-images"')

with open('cloudflare-backend/wrangler.toml', 'w', encoding='utf-8') as f:
    f.write(content)
