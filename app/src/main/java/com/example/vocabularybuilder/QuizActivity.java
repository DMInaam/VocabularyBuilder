package com.example.vocabularybuilder;

import android.animation.ObjectAnimator;
import android.app.Dialog; // This is no longer used, but import is harmless
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.Window; // This is no longer used, but import is harmless
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.vocabularybuilder.data.model.QuizQuestion;
import com.example.vocabularybuilder.data.model.QuizResult;
import com.example.vocabularybuilder.data.model.Word;
import com.example.vocabularybuilder.databinding.ActivityQuizBinding;
// IMPROVEMENT: Removed DialogQuizScoreBinding import, as it's no longer used here
import com.example.vocabularybuilder.viewmodel.WordViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityQuizBinding binding;
    private WordViewModel mWordViewModel;

    private List<Word> mQuizWords;
    private Word mCorrectWord;
    private final List<Button> mOptionButtons = new ArrayList<>();
    private int mCurrentQuestionIndex = 0;
    private int mScore = 0;
    private int mNumQuestions = 0;
    private final List<QuizQuestion> mQuizQuestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // IMPROVEMENT 1: Inflate layout using ViewBinding
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // IMPROVEMENT 2 (CRASH FIX): Set up the Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // Set the click listener for the navigation icon (ic_close)
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Add option buttons to the list
        mOptionButtons.add(binding.quizOption1);
        mOptionButtons.add(binding.quizOption2);
        mOptionButtons.add(binding.quizOption3);
        mOptionButtons.add(binding.quizOption4);

        for (Button button : mOptionButtons) {
            button.setOnClickListener(this);
        }
        binding.quizNextButton.setOnClickListener(this);

        // Get quiz parameters from Intent
        mNumQuestions = getIntent().getIntExtra(QuizSetupActivity.EXTRA_NUM_QUESTIONS, 5);
        String category = getIntent().getStringExtra(QuizSetupActivity.EXTRA_CATEGORY);

        mWordViewModel = new ViewModelProvider(this).get(WordViewModel.class);

        // Update progress bar max
        binding.quizProgress.setMax(mNumQuestions);
        updateProgress();

        //
        // IMPROVEMENT 3 (BUILD FIX):
        // Call the new, smart 'getQuizWords' method from the ViewModel
        //
        mWordViewModel.getQuizWords(category, mNumQuestions * 4).thenAccept(words -> runOnUiThread(() -> {
            if (words != null && words.size() >= 4 && words.size() >= mNumQuestions) {
                mQuizWords = words;
                loadNewQuestion();
            } else {
                binding.quizQuestion.setText("Not enough words for a quiz!");
                for(Button b : mOptionButtons) { b.setVisibility(View.GONE); }
                binding.quizNextButton.setVisibility(View.GONE);
            }
        }));
    }

    private void loadNewQuestion() {
        if (mCurrentQuestionIndex >= mNumQuestions) {
            finishQuiz();
            return;
        }

        updateProgress();
        setOptionsEnabled(true);
        resetButtonColors();
        binding.quizNextButton.setVisibility(View.GONE);

        mCorrectWord = mQuizWords.get(mCurrentQuestionIndex);
        binding.quizQuestion.setText(mCorrectWord.getWord());

        List<Word> options = new ArrayList<>();
        options.add(mCorrectWord);

        List<Word> distractors = new ArrayList<>(mQuizWords);
        distractors.remove(mCorrectWord);
        Collections.shuffle(distractors);

        for (int i = 0; i < 3; i++) {
            options.add(distractors.get(i));
        }
        Collections.shuffle(options);

        for (int i = 0; i < mOptionButtons.size(); i++) {
            mOptionButtons.get(i).setText(options.get(i).getMeaning());
        }
        mCurrentQuestionIndex++;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.quiz_next_button) {
            loadNewQuestion();
        } else { // An option button was clicked
            Button clickedButton = (Button) v;
            String selectedMeaning = clickedButton.getText().toString();
            boolean isCorrect = selectedMeaning.equals(mCorrectWord.getMeaning());

            mQuizQuestions.add(new QuizQuestion(0, mCorrectWord.getWord(), selectedMeaning, mCorrectWord.getMeaning(), isCorrect));

            // IMPROVEMENT 5 (UI FIX): Use theme-aware tinting, not solid colors
            if (isCorrect) {
                clickedButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary)));
                mScore++;
            } else {
                clickedButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.error_red)));
                highlightCorrectAnswer();
            }
            setOptionsEnabled(false);
            binding.quizNextButton.setVisibility(View.VISIBLE);
        }
    }

    //
    // --- IMPROVEMENT (CRITICAL FIX): ---
    // This method no longer shows a dialog. It now starts the
    // 'QuizScoreActivity' and passes it the score.
    //
    private void finishQuiz() {
        // 1. Save the quiz result and questions to the database
        QuizResult result = new QuizResult(mScore, new Date());
        mWordViewModel.saveQuizResult(result, mQuizQuestions);

        // 2. Update the user's streak
        mWordViewModel.updateStreak();

        // 3. Start the QuizScoreActivity
        Intent intent = new Intent(QuizActivity.this, QuizScoreActivity.class);
        intent.putExtra(QuizScoreActivity.EXTRA_FINAL_SCORE, mScore);
        intent.putExtra(QuizScoreActivity.EXTRA_NUM_QUESTIONS, mNumQuestions);
        startActivity(intent);

        // 4. Finish this QuizActivity so the user can't go "back" to it
        finish();
    }

    // (The private updateStreak() method was correctly removed)

    private void highlightCorrectAnswer() {
        for (Button button : mOptionButtons) {
            if (button.getText().toString().equals(mCorrectWord.getMeaning())) {
                // IMPROVEMENT 5 (UI FIX): Use theme-aware tinting
                button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary)));
                break;
            }
        }
    }

    private void updateProgress() {
        // Update progress text
        String progressText = getString(R.string.quiz_question_count, mCurrentQuestionIndex + 1, mNumQuestions);
        binding.quizQuestionCount.setText(progressText);
        // Update progress bar
        binding.quizProgress.setProgress(mCurrentQuestionIndex + 1);
    }

    private void setOptionsEnabled(boolean enabled) {
        for (Button button : mOptionButtons) {
            button.setEnabled(enabled);
        }
    }

    private void resetButtonColors() {
        // IMPROVEMENT 6 (UI FIX):
        // Resetting the tint to 'null' restores the default
        // outlined button appearance (transparent background).
        for (Button button : mOptionButtons) {
            button.setBackgroundTintList(null);
        }
    }
}