package com.example.vocabularybuilder;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull; 
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.vocabularybuilder.databinding.ActivityNewWordBinding;
import com.example.vocabularybuilder.viewmodel.WordViewModel;

public class NewWordActivity extends AppCompatActivity {

    private ActivityNewWordBinding binding;
    private WordViewModel mWordViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewWordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        // Set the click listener for the navigation icon (ic_close)
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Get the ViewModel
        mWordViewModel = new ViewModelProvider(this).get(WordViewModel.class);

        // Set listener on the save button
        binding.buttonSave.setOnClickListener(view -> {
            String wordText = "";
            if (binding.editWord.getText() != null) {
                wordText = binding.editWord.getText().toString().trim();
            }

            if (TextUtils.isEmpty(wordText)) {
                Toast.makeText(this, R.string.empty_not_saved, Toast.LENGTH_SHORT).show();
            } else {
                // Fetch the word from the API. The repository will handle insertion.
                mWordViewModel.fetchWordFromApi(wordText);

                // Call the ViewModel to update the streak.
                // All logic is now in the repository.
                mWordViewModel.updateStreak();

                // Finish the activity
                finish();
            }
        });
    }
}