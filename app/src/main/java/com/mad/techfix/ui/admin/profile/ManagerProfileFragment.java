package com.mad.techfix.ui.admin.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mad.techfix.R;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.User;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.ui.auth.LoginActivity;
import com.mad.techfix.data.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerProfileFragment extends Fragment {

    private TextInputEditText etEmail, etFirstName, etLastName, etCurrentPassword, etNewPassword;
    private MaterialButton btnUpdateProfile, btnUpdatePassword, btnLogout;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manager_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        sessionManager = new SessionManager(requireContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);

        etEmail = view.findViewById(R.id.et_profile_email);
        etFirstName = view.findViewById(R.id.et_profile_first_name);
        etLastName = view.findViewById(R.id.et_profile_last_name);
        etCurrentPassword = view.findViewById(R.id.et_current_password);
        etNewPassword = view.findViewById(R.id.et_new_password);
        
        btnUpdateProfile = view.findViewById(R.id.btn_update_profile);
        btnUpdatePassword = view.findViewById(R.id.btn_update_password);
        btnLogout = view.findViewById(R.id.btn_logout);
        progressBar = view.findViewById(R.id.progress_profile);

        loadUserData();

        btnUpdateProfile.setOnClickListener(v -> updateProfile());
        btnUpdatePassword.setOnClickListener(v -> updatePassword());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadUserData() {
        etEmail.setText(sessionManager.getUserEmail());
        String fullName = sessionManager.getUserName();
        if (fullName != null) {
            String[] parts = fullName.split(" ");
            if (parts.length > 0) etFirstName.setText(parts[0]);
            if (parts.length > 1) etLastName.setText(parts[1]);
        }
    }

    private void updateProfile() {
        String fName = etFirstName.getText().toString().trim();
        String lName = etLastName.getText().toString().trim();

        if (fName.isEmpty() || lName.isEmpty()) {
            Toast.makeText(getContext(), "First and Last name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnUpdateProfile.setEnabled(false);

        Map<String, String> data = new HashMap<>();
        data.put("first_name", fName);
        data.put("last_name", lName);

        String token = "Bearer " + sessionManager.getBearerToken();
        apiService.updateProfile(token, data).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                progressBar.setVisibility(View.GONE);
                btnUpdateProfile.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    // Update session
                    sessionManager.saveUserSession(
                        sessionManager.getUserId(), 
                        sessionManager.getUserEmail(), 
                        sessionManager.getUserRole(), 
                        sessionManager.getBearerToken(), 
                        fName + " " + lName, 
                        sessionManager.getUserPhone()
                    );
                } else {
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnUpdateProfile.setEnabled(true);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePassword() {
        String currentPass = etCurrentPassword.getText().toString().trim();
        String newPass = etNewPassword.getText().toString().trim();

        if (currentPass.isEmpty() || newPass.isEmpty()) {
            Toast.makeText(getContext(), "Both password fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPass.length() < 8) {
            Toast.makeText(getContext(), "New password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnUpdatePassword.setEnabled(false);

        Map<String, String> data = new HashMap<>();
        data.put("current_password", currentPass);
        data.put("new_password", newPass);

        String token = "Bearer " + sessionManager.getBearerToken();
        apiService.updatePassword(token, data).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                progressBar.setVisibility(View.GONE);
                btnUpdatePassword.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show();
                    etCurrentPassword.setText("");
                    etNewPassword.setText("");
                } else {
                    Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnUpdatePassword.setEnabled(true);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        sessionManager.clearSession();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}

