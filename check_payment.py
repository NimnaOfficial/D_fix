with open('app/src/main/java/com/mad/techfix/ui/history/RepairHistoryDetailFragment.java', 'r', encoding='utf-8') as f:
    lines = f.readlines()
for i, line in enumerate(lines):
    if 'View btnPayment' in line:
        for j in range(-5, 20):
            if i+j < len(lines):
                print(lines[i+j].strip('\n'))
        break
