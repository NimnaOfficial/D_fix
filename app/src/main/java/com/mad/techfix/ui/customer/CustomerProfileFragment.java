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

    private TextInputEditText etFirstName, etLastName, etEmail, etPhone, etCity;
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
        
        etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Pre-fill fields initially from local session
        etFirstName.setText(sessionManager.getUserName());
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
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Failed to load latest profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String firstName = etFirstName.getText() != null ? etFirstName.getText().toString().trim() : "";
        String lastName = etLastName.getText() != null ? etLastName.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String city = etCity.getText() != null ? etCity.getText().toString().trim() : "";

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(phone)) {
            Toast.makeText(getContext(), "Name and phone are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> profileData = new HashMap<>();
        profileData.put("first_name", firstName);
        profileData.put("last_name", lastName);
        profileData.put("phone", phone);
        profileData.put("city", city);

        String token = sessionManager.getBearerToken();
        RetrofitClient.getApiService().updateProfile(token, profileData).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Object>> call, @NonNull Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    // Update session manager too
                    User updatedUser = new User();
                    updatedUser.setId(sessionManager.getUserId());
                    updatedUser.setFirst_name(firstName);
                    updatedUser.setLast_name(lastName);
                    updatedUser.setEmail(sessionManager.getUserEmail());
                    updatedUser.setPhone(phone);
                    updatedUser.setRole("CUSTOMER");
                    updatedUser.setCity(city);
                    sessionManager.saveAuthSession(sessionManager.getAuthToken(), updatedUser);
                } else {
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Object>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updatePassword() {
        String currentPassword = etCurrentPassword.getText() != null ? etCurrentPassword.getText().toString() : "";
        String newPassword = etNewPassword.getText() != null ? etNewPassword.getText().toString() : "";

        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword)) {
            Toast.makeText(getContext(), "Both password fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (newPassword.length() < 8) {
            Toast.makeText(getContext(), "New password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> passwordData = new HashMap<>();
        passwordData.put("current_password", currentPassword);
        passwordData.put("new_password", newPassword);

        String token = sessionManager.getBearerToken();
        RetrofitClient.getApiService().updatePassword(token, passwordData).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Object>> call, @NonNull Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show();
                    etCurrentPassword.setText("");
                    etNewPassword.setText("");
                } else {
                    Toast.makeText(getContext(), "Failed to update password. Check your current password.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Object>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
