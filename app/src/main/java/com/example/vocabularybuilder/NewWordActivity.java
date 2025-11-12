package com.example.vocabularybuilder;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull; // Import NonNull
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

// IMPROVEMENT 1: Import ViewBinding
import com.example.vocabularybuilder.databinding.ActivityNewWordBinding;
// Removed unused 'Word' model import
import com.example.vocabularybuilder.viewmodel.WordViewModel;

// Removed unused SharedPreferences, Date, Calendar, and Locale imports

public class NewWordActivity extends AppCompatActivity {

    // IMPROVEMENT 1: Use ViewBinding
    private ActivityNewWordBinding binding;
    private WordViewModel mWordViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // IMPROVEMENT 1: Inflate layout using ViewBinding
        binding = ActivityNewWordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // IMPROVEMENT 2 (CRASH FIX): Set up the Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Title is already in XML
        }
        // Set the click listener for the navigation icon (ic_close)
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Get the ViewModel
        mWordViewModel = new ViewModelProvider(this).get(WordViewModel.class);

        // Set listener on the save button
        binding.buttonSave.setOnClickListener(view -> {
            // IMPROVEMENT 1: Get text from the binding
            String wordText = "";
            if (binding.editWord.getText() != null) {
                wordText = binding.editWord.getText().toString().trim();
            }

            if (TextUtils.isEmpty(wordText)) {
                Toast.makeText(this, R.string.empty_not_saved, Toast.LENGTH_SHORT).show();
            } else {
                // Fetch the word from the API. The repository will handle insertion.
                mWordViewModel.fetchWordFromApi(wordText);

                // IMPROVEMENT 4 (ARCHITECTURE):
                // Call the ViewModel to update the streak.
                // All logic is now in the repository.
                mWordViewModel.updateStreak();

                // Finish the activity
                finish();
            }
        });
    }

    //
    // IMPROVEMENT 4 (ARCHITECTURE):
    // The entire private updateStreak() method has been removed from this Activity
    // and moved into the WordRepository, where it belongs.
    //
}