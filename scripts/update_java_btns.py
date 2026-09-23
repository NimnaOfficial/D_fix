import re

# Update CustomerAppointmentDetailBottomSheet.java
path1 = 'app/src/main/java/com/mad/techfix/ui/customer/booking/CustomerAppointmentDetailBottomSheet.java'
with open(path1, 'r', encoding='utf-8') as f:
    content1 = f.read()

content1 = content1.replace(
    'private MaterialButton btnCancelAppointment;',
    'private MaterialButton btnCancelAppointment;\n    private MaterialButton btnMessages;'
)

content1 = content1.replace(
    '''btnCancelAppointment =
                view.findViewById(
                        R.id.btn_cancel_appointment
                );''',
    '''btnCancelAppointment =
                view.findViewById(
                        R.id.btn_cancel_appointment
                );\n        btnMessages = view.findViewById(R.id.btn_messages);'''
)

content1 = content1.replace(
    '''btnCancelAppointment.setOnClickListener(
                v -> confirmCancelAppointment()
        );''',
    '''btnCancelAppointment.setOnClickListener(
                v -> confirmCancelAppointment()
        );\n        if (btnMessages != null) {\n            btnMessages.setOnClickListener(v -> {\n                android.content.Intent intent = new android.content.Intent(requireContext(), com.mad.techfix.ui.messages.MessagesActivity.class);\n                intent.putExtra("appointment_id", appointmentId);\n                startActivity(intent);\n            });\n        }'''
)
with open(path1, 'w', encoding='utf-8') as f:
    f.write(content1)

# Update TechnicianRepairDetailFragment.java
path2 = 'app/src/main/java/com/mad/techfix/ui/technician/TechnicianRepairDetailFragment.java'
with open(path2, 'r', encoding='utf-8') as f:
    content2 = f.read()

content2 = content2.replace(
    'private MaterialButton btnViewRepairHistory;',
    'private MaterialButton btnViewRepairHistory;\n    private MaterialButton btnMessages;'
)

content2 = content2.replace(
    '''btnViewRepairHistory = view.findViewById(R.id.btn_view_repair_history_technician);''',
    '''btnViewRepairHistory = view.findViewById(R.id.btn_view_repair_history_technician);\n        btnMessages = view.findViewById(R.id.btn_messages);'''
)

content2 = content2.replace(
    '''btnViewRepairHistory.setOnClickListener(v -> openRepairHistory());''',
    '''btnViewRepairHistory.setOnClickListener(v -> openRepairHistory());\n        if (btnMessages != null) {\n            btnMessages.setOnClickListener(v -> {\n                android.content.Intent intent = new android.content.Intent(requireContext(), com.mad.techfix.ui.messages.MessagesActivity.class);\n                intent.putExtra("appointment_id", appointmentId);\n                startActivity(intent);\n            });\n        }'''
)
with open(path2, 'w', encoding='utf-8') as f:
    f.write(content2)

print("Java button wireups updated")
