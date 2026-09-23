import re

with open('app/src/main/java/com/mad/techfix/ui/customer/booking/RepairBookingFragment.java', 'r', encoding='utf-8') as f:
    content = f.read()

pattern = r'if \(false\)\s*\{\s*Toast\.makeText\(\s*requireContext\(\),\s*"Please select a branch",\s*Toast\.LENGTH_SHORT\s*\)\.show\(\);\s*return false;\s*\}'
content = re.sub(pattern, '', content)

with open('app/src/main/java/com/mad/techfix/ui/customer/booking/RepairBookingFragment.java', 'w', encoding='utf-8') as f:
    f.write(content)
