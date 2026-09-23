with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    lines = f.readlines()
for i, line in enumerate(lines):
    if 'path.startsWith("/api/appointments/")' in line and '"/cancel"' in line:
        for j in range(-5, 25):
            if i+j < len(lines):
                print(lines[i+j].strip('\n'))
        break
