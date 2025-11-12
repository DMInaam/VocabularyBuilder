package com.example.vocabularybuilder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull; 
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.vocabularybuilder.databinding.ActivityLoginBinding;
import com.example.vocabularybuilder.viewmodel.UserViewModel;

public class LoginActivity extends AppCompatActivity {

    private UserViewModel mUserViewModel;

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding.buttonLogin.setOnClickListener(v -> loginUser());

        binding.textGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish(); 
        });
    }

    private void loginUser() {
        
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