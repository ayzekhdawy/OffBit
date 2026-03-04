package com.offbit.offbit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.offbit.offbit.model.UserProfile;
import com.offbit.offbit.utils.UserPreferences;
import com.offbit.offbit.utils.SecurityUtils;

import java.security.KeyPair;

public class WelcomeActivity extends AppCompatActivity {

    private TextInputEditText displayNameEditText;
    private TextInputLayout displayNameInputLayout;
    private Button continueButton;
    private Toolbar toolbar;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initViews();
        setupToolbar();
        setupClickListeners();
        
        userPreferences = new UserPreferences(this);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        displayNameEditText = findViewById(R.id.displayNameEditText);
        displayNameInputLayout = findViewById(R.id.displayNameInputLayout);
        continueButton = findViewById(R.id.continueButton);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Welcome to OffBit");
        }
    }

    private void setupClickListeners() {
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserProfile();
            }
        });
    }

    private void createUserProfile() {
        String displayName = displayNameEditText.getText().toString().trim();
        
        if (displayName.isEmpty()) {
            displayNameInputLayout.setError("Please enter a display name");
            return;
        }
        
        if (displayName.length() < 3) {
            displayNameInputLayout.setError("Display name must be at least 3 characters");
            return;
        }
        
        // Generate cryptographic keys using Android Keystore
        KeyPair keyPair = SecurityUtils.generateAndStoreKeyPair(this);
        if (keyPair == null) {
            Toast.makeText(this, "Error creating cryptographic keys", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create user profile with public key
        String publicKeyBase64 = SecurityUtils.publicKeyToBase64(keyPair.getPublic());
        UserProfile userProfile = new UserProfile(displayName, publicKeyBase64);
        userPreferences.saveUserProfile(userProfile);
        
        // Navigate to main activity
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
