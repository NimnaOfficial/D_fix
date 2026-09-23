import re

with open('app/src/main/res/layout/fragment_repair_booking.xml', 'r', encoding='utf-8') as f:
    content = f.read()

# Remove the "Branch Section" entirely
pattern = r'<!-- Branch Section -->[\s\S]*?(?=<!-- Selected Summary -->)'
content = re.sub(pattern, '', content)

# Remove "Branch: Not selected" textview (if it still exists in the summary)
pattern_tv = r'<TextView\s+android:id="@+id/tv_selected_branch"[\s\S]*?/>'
content = re.sub(pattern_tv, '', content)

# But wait, looking at the previous grep, it was at line 233. Let's see what it is.
# We also want to replace "3. Select Branch" if it wasn't caught by the above regex (it should be).

with open('app/src/main/res/layout/fragment_repair_booking.xml', 'w', encoding='utf-8') as f:
    f.write(content)
