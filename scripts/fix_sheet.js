const fs = require("fs");
let java = fs.readFileSync("app/src/main/java/com/mad/techfix/ui/admin/dashboard/AppointmentDetailBottomSheet.java", "utf8");

java = java.replace(
    /import com\.mad\.techfix\.ui\.admin\.assignment\.AssignTechnicianBottomSheet;/,
    `import com.mad.techfix.ui.admin.assignment.AssignTechnicianBottomSheet;
import com.mad.techfix.viewmodel.AdminViewModel;
import androidx.lifecycle.ViewModelProvider;
import android.widget.Toast;`
);

java = java.replace(
    /MaterialButton btnAction = view\.findViewById\(R\.id\.btn_action\);/,
    `MaterialButton btnAction = view.findViewById(R.id.btn_action);
        MaterialButton btnSuspend = view.findViewById(R.id.btn_suspend);
        MaterialButton btnResume = view.findViewById(R.id.btn_resume);
        MaterialButton btnRemove = view.findViewById(R.id.btn_remove);
        AdminViewModel adminViewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);`
);

java = java.replace(
    /if \("REQUESTED"\.equalsIgnoreCase\(status\) \|\| "CONFIRMED"\.equalsIgnoreCase\(status\)\) \{/,
    `if ("SUSPENDED".equalsIgnoreCase(status)) {
            btnResume.setVisibility(View.VISIBLE);
            btnSuspend.setVisibility(View.GONE);
            btnAction.setVisibility(View.GONE);
        } else if ("REQUESTED".equalsIgnoreCase(status) || "CONFIRMED".equalsIgnoreCase(status) || "ASSIGNED".equalsIgnoreCase(status) || "DIAGNOSING".equalsIgnoreCase(status) || "REPAIRING".equalsIgnoreCase(status)) {
            btnSuspend.setVisibility(View.VISIBLE);
            btnResume.setVisibility(View.GONE);
        }
        
        btnSuspend.setOnClickListener(v -> {
            adminViewModel.suspendAppointment(id);
            dismiss();
            Toast.makeText(getContext(), "Suspending appointment...", Toast.LENGTH_SHORT).show();
        });

        btnResume.setOnClickListener(v -> {
            adminViewModel.resumeAppointment(id);
            dismiss();
            Toast.makeText(getContext(), "Resuming appointment...", Toast.LENGTH_SHORT).show();
        });

        btnRemove.setOnClickListener(v -> {
            adminViewModel.removeAppointment(id);
            dismiss();
            Toast.makeText(getContext(), "Removing appointment...", Toast.LENGTH_SHORT).show();
        });

        if ("REQUESTED".equalsIgnoreCase(status) || "CONFIRMED".equalsIgnoreCase(status)) {`
);

fs.writeFileSync("app/src/main/java/com/mad/techfix/ui/admin/dashboard/AppointmentDetailBottomSheet.java", java);
console.log("Java updated");

