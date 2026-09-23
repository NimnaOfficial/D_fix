with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    lines = f.readlines()
for i, line in enumerate(lines):
    if 'path === "/api/appointments" && request.method === "GET"' in line:
        for j in range(40, 100):
            if i+j < len(lines):
                print(lines[i+j].strip('\n'))
        break
