package com.example.vocabularybuilder.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vocabularybuilder.R;
import com.example.vocabularybuilder.data.model.Category;
import com.example.vocabularybuilder.databinding.ItemCategoryBinding;
import com.example.vocabularybuilder.databinding.ItemCreateCategoryBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the category grid on the main screen.
 * This adapter handles TWO view types:
 * 1. TYPE_CREATE: The "Add New Category" button at the start.
 * 2. TYPE_CATEGORY: The dynamic list of category cards.
 */
public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CATEGORY = 0;
    private static final int TYPE_CREATE = 1;

    private List<Category> categories = new ArrayList<>();
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
        void onCreateCategoryClick();
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    /**
     * IMPROVEMENT 2: Replaced 'notifyDataSetChanged()' with DiffUtil.
     * This method now calculates the difference between the old and new lists
     * and provides smooth animations for insertions, deletions, and moves.
     */
    public void setCategories(List<Category> newCategories) {
        CategoryDiffCallback diffCallback = new CategoryDiffCallback(this.categories, newCategories);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.categories.clear();
        this.categories.addAll(newCategories);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_CREATE) {
            // IMPROVEMENT 1: Use ViewBinding
            ItemCreateCategoryBinding binding = ItemCreateCategoryBinding.inflate(inflater, parent, false);
            return new CreateCategoryViewHolder(binding);
        } else {
            // IMPROVEMENT 1: Use ViewBinding
            ItemCategoryBinding binding = ItemCategoryBinding.inflate(inflater, parent, false);
            return new CategoryViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryViewHolder) {
            // Adjust position since create button is at index 0
            Category category = categories.get(position - 1);
            ((CategoryViewHolder) holder).bind(category);
        }
        // No binding needed for CreateCategoryViewHolder
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_CREATE; // First item is the create button
        }
        return TYPE_CATEGORY;
    }

    @Override
    public int getItemCount() {
        return categories.size() + 1; // +1 for the create category button at the start
    }

    /**
     * ViewHolder for the category cards (e.g., "A1", "My Words")
     */
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        // IMPROVEMENT 1: Use ViewBinding
        private final ItemCategoryBinding binding;
        private final Context context;

        CategoryViewHolder(@NonNull ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = binding.getRoot().getContext();

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && position > 0 && listener != null) {
                    // Adjust position since create button is at index 0
                    listener.onCategoryClick(categories.get(position - 1));
                }
            });
        }

        void bind(Category category) {
            binding.categoryName.setText(category.getName());

            // IMPROVEMENT 3: Dynamically set the card color
            int color = getCategoryColor(category.getName());
            binding.categoryCard.setCardBackgroundColor(color);
        }

        /**
         * Helper method to return a color based on the category name.
         * This re-implements the colors from your original static layout.
         */
        private int getCategoryColor(String categoryName) {
            switch (categoryName) {
                case "A1":
                    return Color.parseColor("#4CAF50");
                case "A2":
                    return Color.parseColor("#8BC34A");
                case "B1":
                    return Color.parseColor("#CDDC39");
                case "B2":
                    return Color.parseColor("#FFEB3B");
                case "C1":
                    return Color.parseColor("#FF9800");
                case "C2":
                    return Color.parseColor("#F44336");
                case "User Added":
                default:
                    // Default color for "User Added" or any custom category
                    return Color.parseColor("#FFC107");
            }
        }
    }

    /**
     * ViewHolder for the "Add New Category" card
     */
    class CreateCategoryViewHolder extends RecyclerView.ViewHolder {
        CreateCategoryViewHolder(@NonNull ItemCreateCategoryBinding binding) {
            super(binding.getRoot());

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCreateCategoryClick();
                }
            });
        }
    }

    /**
     * IMPROVEMENT 2: DiffUtil.Callback for efficient list updates.
     */
    public static class CategoryDiffCallback extends DiffUtil.Callback {
        private final List<Category> oldList;
        private final List<Category> newList;

        public CategoryDiffCallback(List<Category> oldList, List<Category> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getName().equals(newList.get(newItemPosition).getName());
        }
    }
}