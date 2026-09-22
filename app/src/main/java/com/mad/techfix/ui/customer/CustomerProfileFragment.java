package com.mad.techfix.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.ui.auth.LoginActivity;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerProfileFragment extends Fragment {

    private TextInputEditText etFirstName, etLastName, etEmail, etPhone, etCity, etAddress;
    private TextInputEditText etCurrentPassword, etNewPassword;
    private MaterialButton btnUpdateProfile, btnChangePassword, btnLogout;
    private TextView tvUserEmail;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_profile, container, false);
        
        sessionManager = new SessionManager(requireContext());
        
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etCity = view.findViewById(R.id.etCity);
        etAddress = view.findViewById(R.id.etAddress);
        
        etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Pre-fill fields initially from local session
        String fullName = sessionManager.getUserName();
        if (fullName != null) {
            String[] parts = fullName.split(" ");
            etFirstName.setText(parts[0]);
            if (parts.length > 1) {
                etLastName.setText(fullName.substring(parts[0].length()).trim());
            }
        }
        
        etPhone.setText(sessionManager.getUserPhone());
        etEmail.setText(sessionManager.getUserEmail());
        tvUserEmail.setText(sessionManager.getUserEmail());

        // Fetch latest details from API
        loadProfileData();

        btnUpdateProfile.setOnClickListener(v -> updateProfile());
        btnChangePassword.setOnClickListener(v -> updatePassword());

        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void loadProfileData() {
        String token = sessionManager.getBearerToken();
        RetrofitClient.getApiService().getMe(token).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    User user = response.body().getUser();
                    etFirstName.setText(user.getFirst_name());
                    etLastName.setText(user.getLast_name());
                    etPhone.setText(user.getPhone());
                    etEmail.setText(user.getEmail());
                    tvUserEmail.setText(user.getEmail());
                    
                    if(user.getCity() != null) {
                        etCity.setText(user.getCity());
                    }
                    if(user.getAddress() != null) {
                        etAddress.setText(user.getAddress());
                    }
                    
                    // Update cache
                    sessionManager.saveAuthSession(sessionManager.getAuthToken(), user);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateProfile() {
        String firstName = etFirstName.getText() != null ? etFirstName.getText().toString().trim() : "";
        String lastName = etLastName.getText() != null ? etLastName.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String city = etCity.getText() != null ? etCity.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(phone)) {
            Toast.makeText(getContext(), "First Name, Last Name, and Phone are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> profileData = new HashMap<>();
        profileData.put("first_name", firstName);
        profileData.put("last_name", lastName);
        profileData.put("phone", phone);
        profileData.put("city", city);
        profileData.put("address", address);

        String token = sessionManager.getBearerToken();
        btnUpdateProfile.setEnabled(false);

        RetrofitClient.getApiService().updateProfile(token, profileData).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Object>> call, @NonNull Response<ApiResponse<Object>> response) {
                btnUpdateProfile.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    
                    // Reload profile data to refresh UI and update session manager
                    loadProfileData();
                } else {
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Object>> call, @NonNull Throwable t) {
                btnUpdateProfile.setEnabled(true);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePassword() {
        String currentPassword = etCurrentPassword.getText() != null ? etCurrentPassword.getText().toString() : "";
        String newPassword = etNewPassword.getText() != null ? etNewPassword.getText().toString() : "";

        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword)) {
            Toast.makeText(getContext(), "Please enter both current and new passwords", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (newPassword.length() < 8) {
            Toast.makeText(getContext(), "New password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> passData = new HashMap<>();
        passData.put("current_password", currentPassword);
        passData.put("new_password", newPassword);

        String token = sessionManager.getBearerToken();
        btnChangePassword.setEnabled(false);

        RetrofitClient.getApiService().updatePassword(token, passData).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Object>> call, @NonNull Response<ApiResponse<Object>> response) {
                btnChangePassword.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Password changed successfully!", Toast.LENGTH_SHORT).show();
                    etCurrentPassword.setText("");
                    etNewPassword.setText("");
                } else {
                    Toast.makeText(getContext(), "Failed to change password. Check your current password.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Object>> call, @NonNull Throwable t) {
                btnChangePassword.setEnabled(true);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
