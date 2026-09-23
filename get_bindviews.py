with open('app/src/main/java/com/mad/techfix/ui/technician/TechnicianRepairDetailFragment.java', 'r', encoding='utf-8') as f:
    lines = f.readlines()
for i, line in enumerate(lines):
    if 'void bindViews' in line:
        for j in range(150):
            if i+j < len(lines):
                print(lines[i+j].strip('\n'))
        break
