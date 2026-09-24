const fs = require("fs");
let xml = fs.readFileSync("app/src/main/res/layout/bottom_sheet_appointment_detail.xml", "utf8");

xml = xml.replace(/<com\.google\.android\.material\.button\.MaterialButton[\s\S]*?id="\@\+id\/btn_action"[\s\S]*?\/>/,
    `<LinearLayout
        android:id="@+id/ll_manager_actions"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">

        <com.google.android.material.button.MaterialButton
            android:id="@+id/btn_action"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="Assign Technician"
            app:cornerRadius="12dp"
            app:icon="@drawable/ic_technician" />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/btn_suspend"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="Suspend Appointment"
            android:layout_marginTop="8dp"
            android:visibility="gone"
            app:backgroundTint="#FF9800"
            app:cornerRadius="12dp" />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/btn_resume"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="Resume Appointment"
            android:layout_marginTop="8dp"
            android:visibility="gone"
            app:backgroundTint="#4CAF50"
            app:cornerRadius="12dp" />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/btn_remove"
            style="@style/Widget.MaterialComponents.Button.OutlinedButton"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="Remove Appointment"
            android:textColor="@color/error_red"
            app:strokeColor="@color/error_red"
            android:layout_marginTop="8dp"
            app:cornerRadius="12dp" />
    </LinearLayout>`);

fs.writeFileSync("app/src/main/res/layout/bottom_sheet_appointment_detail.xml", xml);
console.log("Layout updated");

