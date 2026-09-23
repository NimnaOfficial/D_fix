import re

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
    '''private MaterialButton btnHome;\n    private com.google.android.material.button.MaterialButton btnUploadPhoto;'''
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
            android.os.Bundle args = new android.os.Bundle();
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

idx = content2.rfind('}')
if idx != -1:
    content2 = content2[:idx] + camera_import + content2[idx+1:]
    with open(path2, 'w', encoding='utf-8') as f:
        f.write(content2)

print("BookingConfirmationFragment updated")
