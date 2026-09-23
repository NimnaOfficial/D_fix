import re

with open('app/src/main/java/com/mad/techfix/ui/history/RepairHistoryDetailFragment.java', 'r', encoding='utf-8') as f:
    content = f.read()

target = r'View btnPayment = view\.findViewById\(R\.id\.btn_payment\);'

replacement = '''View btnPayment = view.findViewById(R.id.btn_payment);
        com.mad.techfix.data.SessionManager sessionManager = new com.mad.techfix.data.SessionManager(requireContext());
        if (!"CUSTOMER".equalsIgnoreCase(sessionManager.getUserRole())) {
            btnPayment.setVisibility(View.GONE);
        }'''

if target in content:
    content = content.replace(target, replacement)
    with open('app/src/main/java/com/mad/techfix/ui/history/RepairHistoryDetailFragment.java', 'w', encoding='utf-8') as f:
        f.write(content)
    print("Success fixing payment button visibility")
else:
    print("Failed to find btnPayment declaration")
