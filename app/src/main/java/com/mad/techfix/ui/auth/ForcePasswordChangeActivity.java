package com.mad.techfix.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.ui.technician.TechnicianActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForcePasswordChangeActivity extends AppCompatActivity {

    private TextInputEditText etNewPassword, etConfirmPassword;
    private MaterialButton btnChangePassword;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_force_password_change);

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getClient().create(ApiService.class);

        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (newPassword.length() < 8) {
            etNewPassword.setError("Password must be at least 8 characters");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }
        if (newPassword.equals("TechFix123!")) {
            etNewPassword.setError("Cannot reuse the default password");
            return;
        }

        btnChangePassword.setEnabled(false);

        Map<String, String> request = new HashMap<>();
        request.put("current_password", "TechFix123!");
        request.put("new_password", newPassword);
        String authHeader = "Bearer " + new com.mad.techfix.utils.TokenManager(this).getToken();

        apiService.updatePassword(authHeader, request).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ForcePasswordChangeActivity.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForcePasswordChangeActivity.this, TechnicianActivity.class));
                    finish();
                } else {
                    Toast.makeText(ForcePasswordChangeActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                    btnChangePassword.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Toast.makeText(ForcePasswordChangeActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                btnChangePassword.setEnabled(true);
            }
        });
    }
}
