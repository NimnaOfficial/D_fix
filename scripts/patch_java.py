import re

with open('app/src/main/java/com/mad/techfix/ui/camera/CameraFragment.java', 'r', encoding='utf-8') as f:
    content = f.read()

new_content = '''        btnUpload.setOnClickListener(v -> uploadImage());
        btnBookAppointment.setOnClickListener(v -> openBooking());

        View btnCloseCamera = view.findViewById(R.id.btn_close_camera);
        if (btnCloseCamera != null) {
            btnCloseCamera.setOnClickListener(v -> {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                } else if (getActivity() != null) {
                    getActivity().finish();
                }
            });
        }

        if (getArguments() != null && getArguments().getBoolean("return_url_only", false)) {
            btnBookAppointment.setVisibility(View.GONE);
            view.findViewById(R.id.et_appointment_id).setVisibility(View.GONE);
        }'''

content = content.replace('        btnUpload.setOnClickListener(v -> uploadImage());\n        btnBookAppointment.setOnClickListener(v -> openBooking());', new_content)

with open('app/src/main/java/com/mad/techfix/ui/camera/CameraFragment.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Patched Java!")
