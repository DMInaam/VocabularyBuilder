package com.example.vocabularybuilder.ui.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vocabularybuilder.R;
import com.example.vocabularybuilder.data.model.QuizQuestion;
// IMPROVEMENT 1: Import ViewBinding
import com.example.vocabularybuilder.databinding.ItemQuizQuestionDetailBinding;

public class QuizQuestionAdapter extends ListAdapter<QuizQuestion, QuizQuestionAdapter.QuizQuestionViewHolder> {

    public QuizQuestionAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<QuizQuestion> DIFF_CALLBACK = new DiffUtil.ItemCallback<QuizQuestion>() {
        @Override
        public boolean areItemsTheSame(@NonNull QuizQuestion oldItem, @NonNull QuizQuestion newItem) {
            return oldItem.getQuestionId() == newItem.getQuestionId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull QuizQuestion oldItem, @NonNull QuizQuestion newItem) {
            // IMPROVEMENT: More robust content check
            return oldItem.getQuestionText().equals(newItem.getQuestionText()) &&
                    oldItem.getUserAnswer().equals(newItem.getUserAnswer()) &&
                    oldItem.isCorrect() == newItem.isCorrect();
        }
    };

    @NonNull
    @Override
    public QuizQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // IMPROVEMENT 1: Use ViewBinding to inflate
        ItemQuizQuestionDetailBinding binding = ItemQuizQuestionDetailBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new QuizQuestionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizQuestionViewHolder holder, int position) {
        QuizQuestion currentQuestion = getItem(position);
        holder.bind(currentQuestion);
    }

    /**
     * ViewHolder that uses ViewBinding and handles all display logic.
     */
    static class QuizQuestionViewHolder extends RecyclerView.ViewHolder {

        // IMPROVEMENT 1: Use the binding class
        private final ItemQuizQuestionDetailBinding binding;
        private final Context context; // Needed for string and color resources

        public QuizQuestionViewHolder(@NonNull ItemQuizQuestionDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = binding.getRoot().getContext();
        }

        void bind(QuizQuestion question) {
            binding.questionTextView.setText(question.getQuestionText());

            // IMPROVEMENT 2: Use string resources
            String userAnswer = context.getString(R.string.quiz_history_your_answer, question.getUserAnswer());
            binding.userAnswerTextView.setText(userAnswer);

            // IMPROVEMENT 3: Implement logic for correct/incorrect answers
            if (question.isCorrect()) {
                // --- Set CORRECT state ---
                binding.answerStatusIcon.setImageResource(R.drawable.ic_check_circle);
                // Tint the icon to the app's primary (greenish) color
                binding.answerStatusIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary))
                );
                // Hide the "correct answer" text, since they got it right
                binding.correctAnswerTextView.setVisibility(View.GONE);

            } else {
                // --- Set INCORRECT state ---
                binding.answerStatusIcon.setImageResource(R.drawable.ic_cancel);
                // Tint the icon to the app's error (red) color
                binding.answerStatusIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.error_red))
                );
                // Show the "correct answer" text
                binding.correctAnswerTextView.setVisibility(View.VISIBLE);
                String correctAnswer = context.getString(R.string.quiz_history_correct_answer, question.getCorrectAnswer());
                binding.correctAnswerTextView.setText(correctAnswer);
            }
        }
    }
}