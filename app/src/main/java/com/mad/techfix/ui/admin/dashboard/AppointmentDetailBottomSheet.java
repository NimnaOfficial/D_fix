package com.mad.techfix.ui.admin.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.mad.techfix.R;
import com.mad.techfix.ui.admin.assignment.AssignTechnicianBottomSheet;
import com.mad.techfix.viewmodel.AdminViewModel;
import androidx.lifecycle.ViewModelProvider;
import android.widget.Toast;

public class AppointmentDetailBottomSheet extends BottomSheetDialogFragment {

    private String id, number, status, date, time, customer, branch;
    private String serviceId, serviceName, branchName;
    private String technicianName, technicianStatus;

    public static AppointmentDetailBottomSheet newInstance(String id, String number, String status, String date, String time, String customer, String branch) {
        return newInstance(id, number, status, date, time, customer, branch, null, null, null, null, null);
    }

    public static AppointmentDetailBottomSheet newInstance(String id, String number, String status, String date, String time, String customer, String branch, String serviceId, String serviceName, String branchName, String techName, String techStatus) {
        AppointmentDetailBottomSheet fragment = new AppointmentDetailBottomSheet();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("number", number);
        args.putString("status", status);
        args.putString("date", date);
        args.putString("time", time);
        args.putString("customer", customer);
        args.putString("branch", branch);
        args.putString("serviceId", serviceId);
        args.putString("serviceName", serviceName);
        args.putString("branchName", branchName);
        args.putString("techName", techName);
        args.putString("techStatus", techStatus);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_appointment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            id = getArguments().getString("id");
            number = getArguments().getString("number");
            status = getArguments().getString("status");
            date = getArguments().getString("date");
            time = getArguments().getString("time");
            customer = getArguments().getString("customer");
            branch = getArguments().getString("branch");
            serviceId = getArguments().getString("serviceId");
            serviceName = getArguments().getString("serviceName");
            branchName = getArguments().getString("branchName");
            technicianName = getArguments().getString("techName");
            technicianStatus = getArguments().getString("techStatus");
        }

        TextView tvNumber = view.findViewById(R.id.tv_detail_number);
        TextView tvStatus = view.findViewById(R.id.tv_detail_status);
        TextView tvDateTime = view.findViewById(R.id.tv_detail_datetime);
        TextView tvCustomer = view.findViewById(R.id.tv_detail_customer);
        TextView tvBranch = view.findViewById(R.id.tv_detail_branch);
        
        View llTechInfo = view.findViewById(R.id.ll_tech_info);
        TextView tvTechName = view.findViewById(R.id.tv_detail_technician);
        TextView tvTechStatus = view.findViewById(R.id.tv_detail_tech_status);
        
        if (technicianName != null && !technicianName.isEmpty()) {
            llTechInfo.setVisibility(View.VISIBLE);
            tvTechName.setText(technicianName);
            tvTechStatus.setText(technicianStatus != null ? technicianStatus : "UNKNOWN");
        }
        MaterialButton btnAction = view.findViewById(R.id.btn_action);
        MaterialButton btnSuspend = view.findViewById(R.id.btn_suspend);
        MaterialButton btnResume = view.findViewById(R.id.btn_resume);
        MaterialButton btnRemove = view.findViewById(R.id.btn_remove);
        AdminViewModel adminViewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        tvNumber.setText(number != null ? number : "N/A");
        tvStatus.setText(status != null ? status : "N/A");
        tvDateTime.setText((date != null ? date : "") + " " + (time != null ? time : ""));
        tvCustomer.setText(customer != null ? customer : "N/A");
        tvBranch.setText(branchName != null && !branchName.isEmpty() ? branchName : (branch != null ? branch : "N/A"));

        if ("SUSPENDED".equalsIgnoreCase(status)) {
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

        if ("REQUESTED".equalsIgnoreCase(status) || "CONFIRMED".equalsIgnoreCase(status)) {
            btnAction.setText("Assign Technician");
            btnAction.setVisibility(View.VISIBLE);
        } else {
            btnAction.setVisibility(View.GONE);
        }

        btnAction.setOnClickListener(v -> {
            dismiss();
            AssignTechnicianBottomSheet assignSheet = AssignTechnicianBottomSheet.newInstance(
                    id,
                    branch,
                    number,
                    serviceName != null ? serviceName : "Service",
                    branchName != null ? branchName : "Branch"
            );
            assignSheet.show(getParentFragmentManager(), "AssignBottomSheet");
        });
    }
}

