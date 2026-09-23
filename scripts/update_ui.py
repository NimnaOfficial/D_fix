import re

# Update BookingReviewFragment.java
path1 = 'app/src/main/java/com/mad/techfix/ui/customer/booking/BookingReviewFragment.java'
with open(path1, 'r', encoding='utf-8') as f:
    content1 = f.read()

content1 = content1.replace(
    'String appointmentNumber =',
    'String appointmentId = data.containsKey("id") ? String.valueOf(data.get("id")) : "";\n                            String appointmentNumber ='
)

content1 = content1.replace(
    '''openConfirmationScreen(
                                    appointmentNumber,
                                    appointmentStatus
                            );''',
    '''openConfirmationScreen(
                                    appointmentId,
                                    appointmentNumber,
                                    appointmentStatus
                            );'''
)

content1 = content1.replace(
    '''private void openConfirmationScreen(
            String appointmentNumber,
            String appointmentStatus
    ) {''',
    '''private void openConfirmationScreen(
            String appointmentId,
            String appointmentNumber,
            String appointmentStatus
    ) {'''
)

content1 = content1.replace(
    '''BookingConfirmationFragment
                        .newInstance(
                                appointmentNumber,''',
    '''BookingConfirmationFragment
                        .newInstance(
                                appointmentId,
                                appointmentNumber,'''
)

with open(path1, 'w', encoding='utf-8') as f:
    f.write(content1)


# Update BookingConfirmationFragment.java
path2 = 'app/src/main/java/com/mad/techfix/ui/customer/booking/BookingConfirmationFragment.java'
with open(path2, 'r', encoding='utf-8') as f:
    content2 = f.read()

content2 = content2.replace(
    'private static final String ARG_APPOINTMENT_NUMBER =',
    'private static final String ARG_APPOINTMENT_ID = "appointment_id";\n    private static final String ARG_APPOINTMENT_NUMBER ='
)

content2 = content2.replace(
    '''private String appointmentNumber;''',
    '''private String appointmentId;\n    private String appointmentNumber;'''
)

content2 = content2.replace(
    '''public static BookingConfirmationFragment newInstance(
            String appointmentNumber,''',
    '''public static BookingConfirmationFragment newInstance(
            String appointmentId,
            String appointmentNumber,'''
)

content2 = content2.replace(
    '''args.putString(
                ARG_APPOINTMENT_NUMBER,
                appointmentNumber
        );''',
    '''args.putString(ARG_APPOINTMENT_ID, appointmentId);\n        args.putString(
                ARG_APPOINTMENT_NUMBER,
                appointmentNumber
        );'''
)

content2 = content2.replace(
    '''appointmentNumber =
                args.getString(
                        ARG_APPOINTMENT_NUMBER
                );''',
    '''appointmentId = args.getString(ARG_APPOINTMENT_ID);\n        appointmentNumber =
                args.getString(
                        ARG_APPOINTMENT_NUMBER
                );'''
)

content2 = content2.replace(
    '''private MaterialButton btnHome;''',
    '''private MaterialButton btnHome;\n    private MaterialButton btnUploadPhoto;'''
)

content2 = content2.replace(
    '''btnHome =
                view.findViewById(
                        R.id.btn_confirmation_home
                );''',
    '''btnHome =
                view.findViewById(
                        R.id.btn_confirmation_home
                );\n        btnUploadPhoto = view.findViewById(R.id.btn_upload_photo);'''
)

content2 = content2.replace(
    '''btnHome.setOnClickListener(
                v -> returnHome()
        );''',
    '''btnHome.setOnClickListener(
                v -> returnHome()
        );\n        if (btnUploadPhoto != null) {\n            btnUploadPhoto.setOnClickListener(v -> openCamera());\n        }'''
)

camera_import = """
    private void openCamera() {
        if (appointmentId != null && !appointmentId.isEmpty()) {
            com.mad.techfix.ui.camera.CameraFragment cameraFragment = new com.mad.techfix.ui.camera.CameraFragment();
            Bundle args = new Bundle();
            args.putString("appointment_id", appointmentId);
            cameraFragment.setArguments(args);
            getParentFragmentManager().beginTransaction()
                .replace(getId(), cameraFragment)
                .addToBackStack(null)
                .commit();
        } else {
            android.widget.Toast.makeText(requireContext(), "Appointment ID not found", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
}
"""
content2 = content2.replace('}', camera_import, 1)

with open(path2, 'w', encoding='utf-8') as f:
    f.write(content2)

print("Java files updated")
