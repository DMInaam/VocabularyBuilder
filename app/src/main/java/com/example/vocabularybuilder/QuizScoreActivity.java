package com.example.vocabularybuilder;

import android.content.Intent;
import android.os.Bundle;
// IMPROVEMENT 1: Removed unused Button and TextView imports
import androidx.appcompat.app.AppCompatActivity;

// IMPROVEMENT 1: Import ViewBinding
import com.example.vocabularybuilder.databinding.ActivityQuizScoreBinding;

public class QuizScoreActivity extends AppCompatActivity {

    // --- IMPROVEMENT 3 (BUILD FIX): ---
    // These constants are needed to receive the score from QuizActivity.
    public static final String EXTRA_FINAL_SCORE = "com.example.vocabularybuilder.EXTRA_FINAL_SCORE";
    public static final String EXTRA_NUM_QUESTIONS = "com.example.vocabularybuilder.EXTRA_NUM_QUESTIONS";

    // IMPROVEMENT 1: Use ViewBinding
    private ActivityQuizScoreBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // IMPROVEMENT 1: Inflate layout using ViewBinding
        binding = ActivityQuizScoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // IMPROVEMENT 1: Remove all findViewById calls

        // Get the score from the Intent
        int score = getIntent().getIntExtra(EXTRA_FINAL_SCORE, 0);
        int numQuestions = getIntent().getIntExtra(EXTRA_NUM_QUESTIONS, 1); // Default to 1 to avoid divide-by-zero

        // IMPROVEMENT 2: Set the text using string resources
        String scoreText = getString(R.string.quiz_score_subtitle, score, numQuestions);
        binding.finalScoreText.setText(scoreText);

        // IMPROVEMENT 2: Calculate and set the percentage (for the title)
        int percentage = (int) Math.round(((double) score / numQuestions) * 100);
        String percentText = getString(R.string.quiz_score_percent, percentage);
        // We will set the main title to the percentage
        binding.scoreTitle.setText(percentText);

        // IMPROVEMENT 1: Set listener on the binding object
        binding.buttonFinishQuiz.setOnClickListener(v -> {
            // Finish this activity and return to the main screen
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}