package com.example.vocabularybuilder;

import android.content.Context;
import android.content.Intent;
// IMPROVEMENT: Removed unused SharedPreferences import
import android.os.Bundle;
import android.view.Menu; // IMPROVEMENT: Import Menu
import android.view.MenuItem; // IMPROVEMENT: Import MenuItem
// IMPROVEMENT: Removed unused Button and TextView imports
import android.widget.Toast;

import androidx.annotation.NonNull; // IMPROVEMENT: Import NonNull
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
// IMPROVEMENT: Removed unused RecyclerView import

// IMPROVEMENT: Removed unused User import
import com.example.vocabularybuilder.databinding.ActivityProfileBinding; // IMPROVEMENT: Use ViewBinding
import com.example.vocabularybuilder.ui.adapters.QuizHistoryAdapter;
import com.example.vocabularybuilder.viewmodel.UserViewModel;
import com.example.vocabularybuilder.viewmodel.WordViewModel;

// IMPROVEMENT: Removed unused Calendar and Date imports

public class ProfileActivity extends AppCompatActivity {

    private WordViewModel mWordViewModel;
    private UserViewModel mUserViewModel;

    // IMPROVEMENT 1: Use ViewBinding
    private ActivityProfileBinding binding;

    // IMPROVEMENT: Removed all old view fields (mNameTextView, etc.)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // IMPROVEMENT 1: Inflate layout using ViewBinding
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // IMPROVEMENT 2 (CRASH FIX): Set the Toolbar from the layout
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Title is already set in the XML (app:title="Profile")
        }

        // IMPROVEMENT 1: Remove all findViewById calls

        // Setup RecyclerView
        final QuizHistoryAdapter adapter = new QuizHistoryAdapter(new QuizHistoryAdapter.QuizHistoryDiff());
        binding.quizHistoryRecyclerview.setAdapter(adapter);
        binding.quizHistoryRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        // Setup ViewModels
        mWordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Load user profile details
        loadUserProfile();

        // Load statistics
        loadStatistics();

        // Observe Quiz History
        mWordViewModel.getQuizHistory().observe(this, adapter::submitList);

        // IMPROVEMENT 2 (CRASH FIX): Remove the old logout button listener
        // The logout button is now in the menu (onOptionsItemSelected)

        // Set click listener for your learnt words box
        // IMPROVEMENT 1: Use ViewBinding
        binding.yourLearntWordsCard.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LearntWordsActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserProfile() {
        // Get current user details from the UserViewModel
        // IMPROVEMENT 3 (ARCHITECTURE):
        // Simplified. The ViewModel is the single source of truth.
        // No need for a SharedPreferences fallback here.
        mUserViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                String name = user.getName();
                String username = user.getUsername();

                String displayName = (name != null && !name.trim().isEmpty()) ? name : username;
                binding.profileName.setText(displayName);
                binding.profileUsername.setText("@" + username);
            } else {
                // This case should ideally not happen if user is logged in
                // But if it does, log them out.
                logoutUser();
            }
        });
    }

    private void loadStatistics() {
        // Update learned words count
        mWordViewModel.getLearnedWordCount().thenAccept(count -> runOnUiThread(() -> {
            binding.learntWordsCount.setText(String.valueOf(count));
        }));

        // Update words learned today count
        mWordViewModel.getWordsLearnedTodayCount().thenAccept(count -> runOnUiThread(() -> {
            binding.wordsTodayCount.setText(String.valueOf(count));
        }));

        // IMPROVEMENT 4 (ARCHITECTURE):
        // Removed the manual 'updateStreakCount()' method.
        // We now *observe* the streak count from the ViewModel.
        mWordViewModel.getStreakCount().observe(this, streak -> {
            if (streak != null) {
                binding.streakCount.setText(String.valueOf(streak));
            }
        });
    }

    // IMPROVEMENT 4: Removed the 'updateStreakCount()' method.

    private void logoutUser() {
        // IMPROVEMENT 5 (ARCHITECTURE):
        // All SharedPreferences logic is now in the ViewModel/Repository.
        // The Activity just needs to call one simple method.
        mUserViewModel.logout();

        // Show feedback
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();

        // Navigate back to LoginActivity
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // --- IMPROVEMENT 2 (CRASH FIX): Handle Menu Events ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu defined in 'profile_menu.xml'
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.button_logout) {
            // Handle the logout button click
            logoutUser();
            return true;
        } else if (id == android.R.id.home) {
            // Handle the Toolbar's back arrow
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}