package com.example.vocabularybuilder.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Import ImageView for animation
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
// IMPROVEMENT 1: Import ViewBinding
import com.example.vocabularybuilder.databinding.ItemQuizHistoryBinding;
import com.example.vocabularybuilder.data.model.QuizResultWithQuestions; // Correct import
import java.text.SimpleDateFormat;
import java.util.Locale;

public class QuizHistoryAdapter extends ListAdapter<QuizResultWithQuestions, QuizHistoryAdapter.QuizHistoryViewHolder> {

    public QuizHistoryAdapter(@NonNull DiffUtil.ItemCallback<QuizResultWithQuestions> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public QuizHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // IMPROVEMENT 1: Use ViewBinding to inflate
        ItemQuizHistoryBinding binding = ItemQuizHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new QuizHistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizHistoryViewHolder holder, int position) {
        QuizResultWithQuestions currentResult = getItem(position);
        holder.bind(currentResult);
    }

    /**
     * The DiffUtil.ItemCallback implementation for our QuizHistory ListAdapter.
     */
    public static class QuizHistoryDiff extends DiffUtil.ItemCallback<QuizResultWithQuestions> {
        @Override
        public boolean areItemsTheSame(@NonNull QuizResultWithQuestions oldItem, @NonNull QuizResultWithQuestions newItem) {
            return oldItem.getQuizResult().getId() == newItem.getQuizResult().getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull QuizResultWithQuestions oldItem, @NonNull QuizResultWithQuestions newItem) {
            // Check the score and the number of questions
            return oldItem.getQuizResult().getScore() == newItem.getQuizResult().getScore() &&
                    oldItem.getQuestions().size() == newItem.getQuestions().size();
        }
    }

    /**
     * ViewHolder that uses ViewBinding and handles the expand/collapse logic.
     */
    class QuizHistoryViewHolder extends RecyclerView.ViewHolder {

        // IMPROVEMENT 1: Use the binding class
        private final ItemQuizHistoryBinding binding;

        public QuizHistoryViewHolder(@NonNull ItemQuizHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding; // Store the binding

            // The click listener is set on the *entire* card
            binding.getRoot().setOnClickListener(v -> {
                boolean isVisible = binding.questionsContainer.getVisibility() == View.VISIBLE;
                binding.questionsContainer.setVisibility(isVisible ? View.GONE : View.VISIBLE);

                // IMPROVEMENT 2: Animate the expand icon
                animateExpandIcon(binding.expandIcon, isVisible);
            });
        }

        void bind(QuizResultWithQuestions result) {
            // Format the date
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy h:mm a", Locale.getDefault());
            binding.dateTextView.setText(sdf.format(result.getQuizResult().getDate()));

            // Calculate percentage based on score and total questions
            int totalQuestions = result.getQuestions().size();
            int score = result.getQuizResult().getScore();
            int percentage = totalQuestions > 0 ? (int) Math.round(((double) score / totalQuestions) * 100) : 0;

            // Set the score text
            String scoreText = "Score: " + score + "/" + totalQuestions + " (" + percentage + "%)";
            binding.scoreTextView.setText(scoreText);

            // Set up the nested RecyclerView
            QuizQuestionAdapter questionAdapter = new QuizQuestionAdapter();
            binding.questionsRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            binding.questionsRecyclerView.setAdapter(questionAdapter);
            questionAdapter.submitList(result.getQuestions());

            // Ensure the nested part is hidden by default when binding
            binding.questionsContainer.setVisibility(View.GONE);
            // Ensure the icon is in the default (down) state
            binding.expandIcon.setRotation(0);
        }

        /**
         * IMPROVEMENT 2: Helper method to animate the expand/collapse icon.
         */
        private void animateExpandIcon(ImageView icon, boolean isExpanded) {
            if (isExpanded) {
                // Animate to 0 degrees (pointing down)
                icon.animate().rotation(0).setDuration(200).start();
            } else {
                // Animate to 180 degrees (pointing up)
                icon.animate().rotation(180).setDuration(200).start();
            }
        }
    }
}