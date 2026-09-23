with open('app/src/main/java/com/mad/techfix/ui/technician/TechnicianRepairDetailFragment.java', 'r', encoding='utf-8') as f:
    lines = f.readlines()
for i, line in enumerate(lines):
    if 'void onViewCreated' in line:
        for j in range(100):
            if i+j < len(lines):
                print(lines[i+j].strip('\n'))
        break
