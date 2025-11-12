package com.example.vocabularybuilder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.vocabularybuilder.data.model.User;
import com.example.vocabularybuilder.databinding.ActivityRegisterBinding;
import com.example.vocabularybuilder.viewmodel.UserViewModel;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private UserViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        // Set the click listener for the navigation icon (ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding.buttonRegister.setOnClickListener(v -> registerUser());

        binding.textGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        //
        // --- IMPROVEMENT (CRITICAL BUILD FIX): ---
        // Get text using a ternary operator. This initializes the variable
        // in one line, making it "effectively final" and safe to use in the lambda.
        //
        String name = (binding.registerName.getText() != null) ?
                binding.registerName.getText().toString().trim() : "";

        String username = (binding.registerUsername.getText() != null) ?
                binding.registerUsername.getText().toString().trim() : "";

        String password = (binding.registerPassword.getText() != null) ?
                binding.registerPassword.getText().toString().trim() : "";

        String confirmPassword = (binding.registerConfirmPassword.getText() != null) ?
                binding.registerConfirmPassword.getText().toString().trim() : "";

        // Validate input fields - This logic is excellent
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user already exists - This async logic is perfect
        // The 'username' variable is now effectively final and can be used here.
        mUserViewModel.findByUsername(username).thenAccept(existingUser -> {
            runOnUiThread(() -> {
                if (existingUser != null) {
                    Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create new user and save to database
                // The 'name', 'username', and 'password' variables are also effectively final.
                User newUser = new User(name, username, password);
                mUserViewModel.register(newUser).thenAccept(insertedId -> {
                    runOnUiThread(() -> {
                        if (insertedId != null && insertedId > 0) {
                            Toast.makeText(this, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show();
                            // Navigate to login activity
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish(); // Close register activity
                        } else {
                            Toast.makeText(this, "Registration failed (username might be taken)", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).exceptionally(throwable -> { // --- IMPROVEMENT: Cleaned up lambda ---
                    runOnUiThread(() -> Toast.makeText(this, "Registration failed: " + throwable.getMessage(), Toast.LENGTH_LONG).show());
                    return null;
                });
            });
        }).exceptionally(throwable -> { // --- IMPROVEMENT: Cleaned up lambda ---
            runOnUiThread(() -> Toast.makeText(this, "Error checking username: " + throwable.getMessage(), Toast.LENGTH_LONG).show());
            return null;
        });
    }
}