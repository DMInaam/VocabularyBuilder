package com.example.vocabularybuilder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils; // IMPROVEMENT: Import TextUtils
// IMPROVEMENT 1: Removed unused Button, EditText, TextView imports
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

// IMPROVEMENT 1: Import ViewBinding
import com.example.vocabularybuilder.databinding.ActivityQuizSetupBinding;
import com.example.vocabularybuilder.viewmodel.WordViewModel;

public class QuizSetupActivity extends AppCompatActivity {

    // IMPROVEMENT 1: Use ViewBinding
    private ActivityQuizSetupBinding binding;
    private WordViewModel mWordViewModel;

    // --- IMPROVEMENT 3 (BUILD FIX): ---
    // These are the constants that QuizActivity.java needs.
    public static final String EXTRA_CATEGORY = "com.example.vocabularybuilder.EXTRA_CATEGORY";
    public static final String EXTRA_NUM_QUESTIONS = "com.example.vocabularybuilder.EXTRA_NUM_QUESTIONS";

    private String mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // IMPROVEMENT 1: Inflate layout using ViewBinding
        binding = ActivityQuizSetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // IMPROVEMENT 1: Remove all findViewById calls

        // IMPROVEMENT 2 (CRASH FIX): Set up the Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // Set the click listener for the navigation icon (ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        mWordViewModel = new ViewModelProvider(this).get(WordViewModel.class);

        // Get category from intent - This logic is correct!
        mCategory = getIntent().getStringExtra(EXTRA_CATEGORY);

        // Get word count based on category
        if (mCategory != null && !mCategory.isEmpty()) {
            // Set a specific title for the category quiz
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Quiz: " + mCategory);
            }
            mWordViewModel.getWordCountForCategory(mCategory).thenAccept(count -> runOnUiThread(() -> {
                String prompt = getString(R.string.quiz_setup_prompt) +
                        "\n(You have " + count + " " + mCategory + " words available)";
                binding.quizSetupPrompt.setText(prompt);
            }));
        } else {
            // Set a generic title for the general quiz
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("General Quiz");
            }
            // If no category specified, default to learned and user-added words
            mWordViewModel.getLearnedAndUserAddedWordCount().thenAccept(count -> runOnUiThread(() -> {
                String prompt = getString(R.string.quiz_setup_prompt) +
                        "\n(You have " + count + " words available)";
                binding.quizSetupPrompt.setText(prompt);
            }));
        }

        // IMPROVEMENT 1: Set listener on the binding object
        binding.buttonStartNewQuiz.setOnClickListener(v -> {
            String numQuestionsStr = "";
            if (binding.editTextNumQuestions.getText() != null) {
                numQuestionsStr = binding.editTextNumQuestions.getText().toString().trim();
            }

            if (TextUtils.isEmpty(numQuestionsStr)) { // Use TextUtils for safety
                Toast.makeText(this, "Please enter a number of words", Toast.LENGTH_SHORT).show();
                return;
            }

            int numQuestions;
            try {
                numQuestions = Integer.parseInt(numQuestionsStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (numQuestions < 4) {
                Toast.makeText(this, "Quiz must have at least 4 words", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check word count based on category - This logic is excellent!
            if (mCategory != null) {
                mWordViewModel.getWordCountForCategory(mCategory).thenAccept(count -> runOnUiThread(() -> {
                    if (numQuestions > count) {
                        Toast.makeText(this, "You don't have that many " + mCategory + " words available", Toast.LENGTH_SHORT).show();
                    } else {
                        startQuiz(numQuestions);
                    }
                }));
            } else {
                // If no category specified, check learned and user-added words
                mWordViewModel.getLearnedAndUserAddedWordCount().thenAccept(count -> runOnUiThread(() -> {
                    if (numQuestions > count) {
                        Toast.makeText(this, "You don't have that many words available", Toast.LENGTH_SHORT).show();
                    } else {
                        startQuiz(numQuestions);
                    }
                }));
            }
        });
    }

    private void startQuiz(int numQuestions) {
        Intent intent = new Intent(QuizSetupActivity.this, QuizActivity.class);
        // IMPROVEMENT 3 (BUILD FIX): Use the static constant
        intent.putExtra(EXTRA_NUM_QUESTIONS, numQuestions);
        intent.putExtra(EXTRA_CATEGORY, mCategory); // Also pass category to quiz
        startActivity(intent);
        finish();
    }
}