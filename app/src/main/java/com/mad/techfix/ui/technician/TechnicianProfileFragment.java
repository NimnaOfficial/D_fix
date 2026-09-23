package com.mad.techfix.ui.technician;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.AuthResponse;
import com.mad.techfix.models.User;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.ui.auth.LoginActivity;
import com.mad.techfix.utils.TokenManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TechnicianProfileFragment extends Fragment {

    private TextInputEditText etFirstName, etLastName, etPhone, etSpecialization;
    private TextInputEditText etCurrentPassword, etNewPassword;
    private MaterialButton btnSaveProfile, btnChangePassword, btnLogout;
    
    private ApiService apiService;
    private SessionManager sessionManager;
    private TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_technician_profile, container, false);

        sessionManager = new SessionManager(requireContext());
        tokenManager = new TokenManager(requireContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);

        initViews(view);
        loadUserData();

        return view;
    }

    private void initViews(View view) {
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etPhone = view.findViewById(R.id.etPhone);
        etSpecialization = view.findViewById(R.id.etSpecialization);
        
        etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);

        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        btnSaveProfile.setOnClickListener(v -> updateProfile());
        btnChangePassword.setOnClickListener(v -> changePassword());
        btnLogout.setOnClickListener(v -> performLogout());
    }

    private void loadUserData() {
        String token = "Bearer " + tokenManager.getToken();
        apiService.getMe(token).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    User user = response.body().getUser();
                    etFirstName.setText(user.getFirst_name());
                    etLastName.setText(user.getLast_name());
                    etPhone.setText(user.getPhone());
                    if (user.getSpecialization() != null) {
                        etSpecialization.setText(user.getSpecialization());
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String fName = etFirstName.getText().toString().trim();
        String lName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String spec = etSpecialization.getText().toString().trim();

        if (fName.isEmpty() || lName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(requireContext(), "Name and phone are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> request = new HashMap<>();
        request.put("first_name", fName);
        request.put("last_name", lName);
        request.put("phone", phone);
        request.put("specialization", spec);
        
        apiService.updateProfile("Bearer " + tokenManager.getToken(), request).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePassword() {
        String currentPass = etCurrentPassword.getText().toString();
        String newPass = etNewPassword.getText().toString();
        
        if (newPass.length() < 8) {
            etNewPassword.setError("Min 8 chars");
            return;
        }
        
        Map<String, String> request = new HashMap<>();
        request.put("current_password", currentPass);
        request.put("new_password", newPass);
        
        apiService.updatePassword("Bearer " + tokenManager.getToken(), request).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Password changed!", Toast.LENGTH_SHORT).show();
                    etCurrentPassword.setText("");
                    etNewPassword.setText("");
                } else {
                    Toast.makeText(requireContext(), "Incorrect current password", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performLogout() {
        sessionManager.clearSession();
        tokenManager.clear();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
