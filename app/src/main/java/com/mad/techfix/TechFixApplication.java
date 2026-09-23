package com.mad.techfix;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import com.mad.techfix.data.SessionManager;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class TechFixApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        try {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "dfe75vzmj");
            config.put("api_key", "817913768236419");
            config.put("api_secret", "ub-QavDHXEPX3vXIfE2fxuLMdNU");
            MediaManager.init(this, config);
        } catch (Exception e) {
            // MediaManager already initialized or error
        }
    }
}
