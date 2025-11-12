package com.example.vocabularybuilder.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vocabularybuilder.data.model.Word;
import com.example.vocabularybuilder.databinding.RecyclerviewItemBinding; // IMPROVEMENT: Import ViewBinding

//
// IMPROVEMENT 1: Removed 'implements Filterable'.
// ListAdapter is not designed to be used with the Filterable interface.
// Filtering should be handled in the ViewModel or Activity, and the
// resulting filtered list should be submitted via submitList().
//
public class WordListAdapter extends ListAdapter<Word, WordListAdapter.WordViewHolder> {

    private OnItemClickListener listener;
    private OnDeleteClickListener deleteListener;
    // IMPROVEMENT 1: Removed 'wordListFull' and all Filter-related code.

    public WordListAdapter(@NonNull DiffUtil.ItemCallback<Word> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // IMPROVEMENT 2: Use ViewBinding to inflate the layout
        RecyclerviewItemBinding binding = RecyclerviewItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new WordViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word current = getItem(position);
        holder.bind(current); // Pass the full Word object
    }

    // IMPROVEMENT 1: Removed the overridden submitList() method.
    // The default implementation from ListAdapter is all we need.

    // IMPROVEMENT 1: Removed getFilter() and the wordFilter object.

    /**
     * The DiffUtil.ItemCallback implementation for our Word ListAdapter.
     */
    public static class WordDiff extends DiffUtil.ItemCallback<Word> {
        @Override
        public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
            // IMPROVEMENT 3: Made the content check more robust
            return oldItem.getWord().equals(newItem.getWord()) &&
                    oldItem.getMeaning().equals(newItem.getMeaning()) &&
                    oldItem.isLearned() == newItem.isLearned();
        }
    }

    // --- Click Listener Interfaces ---

    public interface OnItemClickListener {
        void onItemClick(Word word);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Word word);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    // --- ViewHolder Class ---

    /**
     * ViewHolder class that uses ViewBinding.
     */
    class WordViewHolder extends RecyclerView.ViewHolder {

        // IMPROVEMENT 2: Use the binding class instead of individual views
        private final RecyclerviewItemBinding binding;

        public WordViewHolder(RecyclerviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding; // Store the binding

            // Set listeners on the root view (the CardView)
            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });

            // Set listener on the delete button
            binding.deleteWordButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (deleteListener != null && position != RecyclerView.NO_POSITION) {
                    deleteListener.onDeleteClick(getItem(position));
                }
            });
        }

        public void bind(Word word) {
            binding.textView.setText(word.getWord());
            // You could also bind other data here, e.g.:
            // binding.partOfSpeech.setText(word.getPartOfSpeech());
        }
    }
}