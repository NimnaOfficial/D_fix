package com.mad.techfix.ui.auth;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mad.techfix.R;
import com.mad.techfix.models.AuthResponse;
import com.mad.techfix.models.RegisterRequest;
import com.mad.techfix.network.RetrofitClient;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilTitle, tilFirstName, tilLastName, tilEmail;
    private TextInputLayout tilCountryCode, tilPhone, tilCity, tilAddress;
    private TextInputLayout tilPassword, tilConfirmPassword;

    private AutoCompleteTextView actTitle, actCountryCode, actCity;
    private TextInputEditText etFirstName, etLastName, etEmail, etPhone, etPassword, etConfirmPassword, etAddress;
    private CheckBox cbTerms;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;
    private TextView tvLogin;
    
    private LocationManager locationManager;
    private LocationListener locationListener;

    private static final String[] TITLES = {"Mr.", "Mrs.", "Ms.", "Dr.", "Eng."};
    private static final String[] COUNTRY_CODES = {"+94 (LK)", "+1 (US)", "+44 (UK)", "+61 (AU)", "+971 (UAE)"};
    private static final String[] CITIES = {
            "Colombo", "Galle", "Kandy", "Gampaha", "Negombo",
            "Matara", "Kurunegala", "Kalutara", "Jaffna", "Ratnapura",
            "Batticaloa", "Anuradhapura", "Badulla", "Trincomalee", "Nuwara Eliya"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        setupDropdowns();
        setupListeners();
    }

    private void initializeViews() {
        tilTitle = findViewById(R.id.tilTitle);
        tilFirstName = findViewById(R.id.tilFirstName);
        tilLastName = findViewById(R.id.tilLastName);
        tilEmail = findViewById(R.id.tilEmail);
        tilCountryCode = findViewById(R.id.tilCountryCode);
        tilPhone = findViewById(R.id.tilPhone);
        tilCity = findViewById(R.id.tilCity);
        tilAddress = findViewById(R.id.tilAddress);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        actTitle = findViewById(R.id.actTitle);
        actCountryCode = findViewById(R.id.actCountryCode);
        actCity = findViewById(R.id.actCity);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        cbTerms = findViewById(R.id.cbTerms);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void setupDropdowns() {
        ArrayAdapter<String> titleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, TITLES);
        actTitle.setAdapter(titleAdapter);
        actTitle.setText(TITLES[0], false);

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, COUNTRY_CODES);
        actCountryCode.setAdapter(countryAdapter);
        actCountryCode.setText(COUNTRY_CODES[0], false);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, CITIES);
        actCity.setAdapter(cityAdapter);
        actCity.setText(CITIES[0], false);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> performRegister());
        tvLogin.setOnClickListener(v -> finish());
        
        if (tilAddress != null) {
            tilAddress.setEndIconOnClickListener(v -> fetchLocation());
        }
    }
    
    private void fetchLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        // Check if GPS or Network is enabled
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        if (!isGpsEnabled && !isNetworkEnabled) {
            Toast.makeText(this, "Please enable Location Services", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }

        Toast.makeText(this, "Fetching location... please wait", Toast.LENGTH_SHORT).show();
        
        try {
            Location lastKnownGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location lastKnownNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            
            Location bestLastKnown = null;
            if (lastKnownGps != null && lastKnownNet != null) {
                bestLastKnown = (lastKnownGps.getTime() > lastKnownNet.getTime()) ? lastKnownGps : lastKnownNet;
            } else if (lastKnownGps != null) {
                bestLastKnown = lastKnownGps;
            } else if (lastKnownNet != null) {
                bestLastKnown = lastKnownNet;
            }
            
            // Only use last known if it is recent (within 5 minutes)
            if (bestLastKnown != null && (System.currentTimeMillis() - bestLastKnown.getTime()) < 5 * 60 * 1000) {
                reverseGeocode(bestLastKnown);
                return;
            }
            
            // Otherwise, request fresh update
            String provider = isGpsEnabled ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;
            
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    locationManager.removeUpdates(this);
                    reverseGeocode(location);
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(@NonNull String provider) {}
                @Override
                public void onProviderDisabled(@NonNull String provider) {}
            };
            
            locationManager.requestSingleUpdate(provider, locationListener, Looper.getMainLooper());
            
        } catch (SecurityException e) {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error starting location service", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void reverseGeocode(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                // Try to get a nicely formatted address
                String fullAddress = address.getAddressLine(0);
                if (fullAddress == null || fullAddress.isEmpty()) {
                    fullAddress = address.getLocality() + ", " + address.getAdminArea();
                }
                
                if (etAddress != null) {
                    etAddress.setText(fullAddress);
                    Toast.makeText(this, "Location autofilled!", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Fallback to coordinates if Geocoder fails to find an address
                String coordStr = "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude();
                etAddress.setText(coordStr);
                Toast.makeText(this, "Address not found. Used coordinates instead.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("RegisterActivity", "Geocoder error: " + e.getMessage());
            // Geocoder service might not be available on this device/emulator
            String coordStr = "Lat: " + String.format(Locale.US, "%.5f", location.getLatitude()) + 
                              ", Lng: " + String.format(Locale.US, "%.5f", location.getLongitude());
            if (etAddress != null) {
                etAddress.setText(coordStr);
                Toast.makeText(this, "Maps service unavailable. Used coordinates.", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
        } else {
            Toast.makeText(this, "Permission required to fetch GPS location", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private void performRegister() {
        // Clear previous error states
        tilFirstName.setError(null);
        tilLastName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
        tilCity.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String firstName = etFirstName.getText() != null ? etFirstName.getText().toString().trim() : "";
        String lastName = etLastName.getText() != null ? etLastName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String rawPhone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String selectedCity = actCity.getText() != null ? actCity.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

        boolean hasError = false;

        // 1. Validate First Name
        if (TextUtils.isEmpty(firstName)) {
            tilFirstName.setError("First name is required");
            hasError = true;
        } else if (firstName.length() < 2) {
            tilFirstName.setError("First name must be at least 2 characters");
            hasError = true;
        } else if (!firstName.matches("^[a-zA-Z\\s]+$")) {
            tilFirstName.setError("First name must contain letters only");
            hasError = true;
        }

        // 2. Validate Last Name
        if (TextUtils.isEmpty(lastName)) {
            tilLastName.setError("Last name is required");
            hasError = true;
        } else if (lastName.length() < 2) {
            tilLastName.setError("Last name must be at least 2 characters");
            hasError = true;
        } else if (!lastName.matches("^[a-zA-Z\\s]+$")) {
            tilLastName.setError("Last name must contain letters only");
            hasError = true;
        }

        // 3. Validate Email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email address is required");
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email format (e.g. user@domain.com)");
            hasError = true;
        }

        // 4. Validate Numeric Phone Number (9-10 digits)
        if (TextUtils.isEmpty(rawPhone)) {
            tilPhone.setError("Phone number is required");
            hasError = true;
        } else if (!rawPhone.matches("^[0-9]{9,10}$")) {
            tilPhone.setError("Enter a valid 9 or 10-digit numeric phone number");
            hasError = true;
        }

        // 5. Validate Customer City Selection
        if (TextUtils.isEmpty(selectedCity)) {
            tilCity.setError("Please select your residential city");
            hasError = true;
        }

        // 6. Validate Password Strength (Min 8 chars, letter + digit)
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            hasError = true;
        } else if (password.length() < 8) {
            tilPassword.setError("Password must contain at least 8 characters");
            hasError = true;
        } else if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d).+$")) {
            tilPassword.setError("Password must contain both letters and digits");
            hasError = true;
        }

        // 7. Validate Confirm Password Match
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Please confirm your password");
            hasError = true;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            hasError = true;
        }

        // 8. Validate Terms Acceptance
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please agree to the Terms of Service to continue", Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        if (hasError) return;

        setLoading(true);

        // Registration is strictly for CUSTOMER accounts with selected Customer City and Address
        final RegisterRequest request = new RegisterRequest(firstName, lastName, email, password, rawPhone, selectedCity, address, "CUSTOMER");

        RetrofitClient.getApiService().registerUser(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this,
                            "Welcome, " + firstName + "! Customer account created successfully.",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("registered_email", email);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Registration failed";
                    try {
                        if (response.errorBody() != null) {
                            String errStr = response.errorBody().string();
                            JSONObject json = new JSONObject(errStr);
                            if (json.has("message")) {
                                errorMsg = json.getString("message");
                            }
                        }
                    } catch (Exception ignored) {}

                    if (response.code() == 409) {
                        tilEmail.setError("This email is already registered in the system");
                    }
                    Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, "Network Connection Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            btnRegister.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btnRegister.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }
}
