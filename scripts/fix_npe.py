import re

with open('app/src/main/java/com/mad/techfix/ui/technician/TechnicianRepairDetailFragment.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Fix the missing bindViews
target_bind = r'btnViewRepairHistory =[\s\S]*?R\.id\.btn_view_repair_history_technician[\s\S]*?\);'

replacement_bind = '''btnViewRepairHistory =
                view.findViewById(
                        R.id.btn_view_repair_history_technician
                );

        btnMessages =
                view.findViewById(
                        R.id.btn_messages
                );'''

if re.search(target_bind, content):
    content = re.sub(target_bind, replacement_bind, content)
    with open('app/src/main/java/com/mad/techfix/ui/technician/TechnicianRepairDetailFragment.java', 'w', encoding='utf-8') as f:
        f.write(content)
    print("Success fixing NPE in TechnicianRepairDetailFragment")
else:
    print("Failed to find target in TechnicianRepairDetailFragment")
