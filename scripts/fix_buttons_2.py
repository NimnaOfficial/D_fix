import re

# Patch TechnicianRepairDetailFragment.java
file2 = "app/src/main/java/com/mad/techfix/ui/technician/TechnicianRepairDetailFragment.java"
with open(file2, "r", encoding="utf-8") as f:
    content2 = f.read()

pattern2 = r"(btnUpdateRepairStatus\s*\.setEnabled\(\s*!finished\s*\);)"
repl2 = r"""\1

        if (btnMessages != null) {
            btnMessages.setVisibility(finished ? android.view.View.GONE : android.view.View.VISIBLE);
        }
"""
content2 = re.sub(pattern2, repl2, content2)
with open(file2, "w", encoding="utf-8") as f:
    f.write(content2)


# Patch CustomerAppointmentDetailBottomSheet.java
file1 = "app/src/main/java/com/mad/techfix/ui/customer/booking/CustomerAppointmentDetailBottomSheet.java"
with open(file1, "r", encoding="utf-8") as f:
    content1 = f.read()

pattern1 = r"(btnCancelAppointment\.setEnabled\(\s*cancellable\s*\);)"
repl1 = r"""\1

        boolean finished = normalized.equals("COMPLETED") || normalized.equals("CANCELLED");
        if (btnMessages != null) {
            btnMessages.setVisibility(finished ? android.view.View.GONE : android.view.View.VISIBLE);
        }
"""
content1 = re.sub(pattern1, repl1, content1)
with open(file1, "w", encoding="utf-8") as f:
    f.write(content1)

print("Buttons fixed correctly.")

