package com.example.vocabularybuilder;

import android.content.Intent;
import android.os.Bundle;
// IMPROVEMENT 1: Removed unused Button, EditText, TextView imports
import android.widget.Toast;

import androidx.annotation.NonNull; // Import NonNull
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

// IMPROVEMENT 1: Import ViewBinding
import com.example.vocabularybuilder.databinding.ActivityLoginBinding;
// IMPROVEMENT 2: Removed unused User model import
import com.example.vocabularybuilder.viewmodel.UserViewModel;

public class LoginActivity extends AppCompatActivity {

    // IMPROVEMENT 1: Removed old view fields
    private UserViewModel mUserViewModel;

    // IMPROVEMENT 1: Use ViewBinding
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // IMPROVEMENT 1: Inflate layout using ViewBinding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // IMPROVEMENT 1: Remove all findViewById calls

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // IMPROVEMENT 1: Set listeners using the binding object
        binding.buttonLogin.setOnClickListener(v -> loginUser());

        binding.textGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish(); // Close login activity
        });
    }

    private void loginUser() {
        // IMPROVEMENT 1: Get text from the binding object
        // Note: The TextInputEditText is inside the layout, so we get text from binding.loginUsername
        String username = "";
        if (binding.loginUsername.getText() != null) {
            username = binding.loginUsername.getText().toString().trim();
        }

        String password = "";
        if (binding.loginPassword.getText() != null) {
            password = binding.loginPassword.getText().toString().trim();
        }

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();

        //
        // IMPROVEMENT 2 (CRITICAL LOGIC FIX):
        // Call the new, secure 'login' method from the ViewModel.
        // This *both* checks the credentials and saves the session.
        //
        mUserViewModel.login(username, password).thenAccept(user -> {
            runOnUiThread(() -> {
                if (user != null) {
                    // Success! The repository has already saved the login state.
                    Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Failure (user was null, bad credentials)
                    Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                }
            });
        }).exceptionally(throwable -> {
            // Handle any database or network errors
            runOnUiThread(() -> {
                Toast.makeText(this, "Error during login: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            });
            return null;
        });
    }
}