const fs = require("fs");

// 1. Update Layout
let xml = fs.readFileSync("app/src/main/res/layout/bottom_sheet_appointment_detail.xml", "utf8");
let techLayout = `    <LinearLayout
        android:id="@+id/ll_tech_info"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:visibility="gone"
        android:layout_marginBottom="24dp">
        
        <View
            android:layout_width="match_parent"
            android:layout_height="1dp"
            android:background="@color/app_divider"
            android:layout_marginBottom="12dp" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_marginBottom="12dp">
            <TextView
                android:layout_width="120dp"
                android:layout_height="wrap_content"
                android:text="Technician"
                android:textColor="@color/app_text_secondary" />
            <TextView
                android:id="@+id/tv_detail_technician"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="N/A"
                android:textStyle="bold"
                android:textColor="@color/app_text_primary" />
        </LinearLayout>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal">
            <TextView
                android:layout_width="120dp"
                android:layout_height="wrap_content"
                android:text="Current Activity"
                android:textColor="@color/app_text_secondary" />
            <TextView
                android:id="@+id/tv_detail_tech_status"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="AVAILABLE"
                android:textStyle="bold"
                android:textColor="@color/app_text_primary" />
        </LinearLayout>
    </LinearLayout>

    <LinearLayout
        android:id="@+id/ll_manager_actions"`;

xml = xml.replace(/<LinearLayout\s+android:id="\@\+id\/ll_manager_actions"/, techLayout);
fs.writeFileSync("app/src/main/res/layout/bottom_sheet_appointment_detail.xml", xml);

// 2. Update BottomSheet class
let sheet = fs.readFileSync("app/src/main/java/com/mad/techfix/ui/admin/dashboard/AppointmentDetailBottomSheet.java", "utf8");

sheet = sheet.replace(
    /private String serviceId, serviceName, branchName;/,
    "private String serviceId, serviceName, branchName;\n    private String technicianName, technicianStatus;"
);

sheet = sheet.replace(
    /public static AppointmentDetailBottomSheet newInstance\(String id, String number, String status, String date, String time, String customer, String branch, String serviceId, String serviceName, String branchName\) \{/,
    `public static AppointmentDetailBottomSheet newInstance(String id, String number, String status, String date, String time, String customer, String branch, String serviceId, String serviceName, String branchName, String techName, String techStatus) {`
);

sheet = sheet.replace(
    /args\.putString\("branchName", branchName\);/,
    `args.putString("branchName", branchName);\n        args.putString("techName", techName);\n        args.putString("techStatus", techStatus);`
);

sheet = sheet.replace(
    /branchName = getArguments\(\)\.getString\("branchName"\);/,
    `branchName = getArguments().getString("branchName");\n            technicianName = getArguments().getString("techName");\n            technicianStatus = getArguments().getString("techStatus");`
);

let bindLogic = `TextView tvBranch = view.findViewById(R.id.tv_detail_branch);
        
        View llTechInfo = view.findViewById(R.id.ll_tech_info);
        TextView tvTechName = view.findViewById(R.id.tv_detail_technician);
        TextView tvTechStatus = view.findViewById(R.id.tv_detail_tech_status);
        
        if (technicianName != null && !technicianName.isEmpty()) {
            llTechInfo.setVisibility(View.VISIBLE);
            tvTechName.setText(technicianName);
            tvTechStatus.setText(technicianStatus != null ? technicianStatus : "UNKNOWN");
        }`;
sheet = sheet.replace(/TextView tvBranch = view\.findViewById\(R\.id\.tv_detail_branch\);/, bindLogic);

fs.writeFileSync("app/src/main/java/com/mad/techfix/ui/admin/dashboard/AppointmentDetailBottomSheet.java", sheet);

// 3. Update AppointmentsFragment to pass these 2 new arguments
let frag = fs.readFileSync("app/src/main/java/com/mad/techfix/ui/admin/appointments/AppointmentsFragment.java", "utf8");

frag = frag.replace(
    /appointment\.getService_name\(\),\s*appointment\.getBranch_name\(\)\s*\);/g,
    `appointment.getService_name(),
                      appointment.getBranch_name(),
                      appointment.getTechnician_name(),
                      appointment.getTechnician_status()
              );`
);

fs.writeFileSync("app/src/main/java/com/mad/techfix/ui/admin/appointments/AppointmentsFragment.java", frag);

console.log("Updated Android app with technician info");

