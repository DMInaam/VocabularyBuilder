package com.example.vocabularybuilder;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View; // IMPROVEMENT: Import View
import android.widget.Toast; // IMPROVEMENT: Import Toast

import androidx.annotation.NonNull; // IMPROVEMENT: Import NonNull
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
// IMPROVEMENT 1: Removed unused RecyclerView, Button, TextView imports

import com.example.vocabularybuilder.data.model.Category;
import com.example.vocabularybuilder.data.model.Word;
import com.example.vocabularybuilder.data.model.WordCategoryCrossRef;
// IMPROVEMENT 1: Import ViewBinding
import com.example.vocabularybuilder.databinding.ActivityWordListBinding;
import com.example.vocabularybuilder.ui.adapters.WordListAdapter;
import com.example.vocabularybuilder.viewmodel.CategoryViewModel;
import com.example.vocabularybuilder.viewmodel.WordViewModel;

import java.util.List;

public class WordListActivity extends AppCompatActivity implements WordListAdapter.OnItemClickListener {

    // This constant is correct and is used by MainActivity
    public static final String EXTRA_CATEGORY = "com.example.vocabularybuilder.CATEGORY";

    private WordViewModel mWordViewModel;
    private CategoryViewModel mCategoryViewModel;
    private WordListAdapter mAdapter;
    private String mCategoryName;

    // IMPROVEMENT 4 (BUILD FIX):
    // We need to hold the Category object to get its ID for deleting
    private Category mCurrentCategoryObject;

    // IMPROVEMENT 1: Use ViewBinding
    private ActivityWordListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // IMPROVEMENT 1: Inflate layout using ViewBinding
        binding = ActivityWordListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // IMPROVEMENT 1: Remove all findViewById calls

        // IMPROVEMENT 2 (CRASH FIX): Set up the Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setup RecyclerView
        mAdapter = new WordListAdapter(new WordListAdapter.WordDiff());
        binding.wordListRecyclerview.setAdapter(mAdapter);
        binding.wordListRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setOnItemClickListener(this);

        // Setup ViewModels
        mWordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        mCategoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        mCategoryName = getIntent().getStringExtra(EXTRA_CATEGORY);

        if (mCategoryName != null) {
            // Set the toolbar title
            getSupportActionBar().setTitle(mCategoryName);

            // IMPROVEMENT 5 (ARCHITECTURE FIX):
            // We just call setCategoryForList. The ViewModel's switchMap
            // will handle all the "if/else" logic for us.
            mWordViewModel.setCategoryForList(mCategoryName);

            // This one observer now works for ALL category types
            mWordViewModel.getWordsForCategoryList().observe(this, words -> {
                mAdapter.submitList(words);
            });

            // IMPROVEMENT 6 (UI LOGIC):
            // Check if this is a custom category to show the "Delete" buttons
            if (!mWordViewModel.isCefrLevel(mCategoryName)) {

                // 1. Observe the Category object so we can get its ID
                mCategoryViewModel.getCategoryByName(mCategoryName).observe(this, category -> {
                    mCurrentCategoryObject = category;
                });

                // 2. Set up the word delete listener
                mAdapter.setOnDeleteClickListener(this::showDeleteWordConfirmationDialog);

                // 3. Show and configure the "Delete Category" button
                binding.buttonDeleteCategory.setVisibility(View.VISIBLE);
                binding.buttonDeleteCategory.setOnClickListener(v -> {
                    showDeleteCategoryConfirmationDialog();
                });
            } else {
                // This is a CEFR level (A1, etc.), so hide the delete button
                binding.buttonDeleteCategory.setVisibility(View.GONE);
            }

        } else {
            // Fallback: This should not happen if navigated to correctly
            getSupportActionBar().setTitle("All Words");
            mWordViewModel.getAllWords().observe(this, mAdapter::submitList);
            binding.buttonDeleteCategory.setVisibility(View.GONE);
        }

        // Setup take quiz button
        binding.buttonTakeQuiz.setOnClickListener(v -> {
            if (mCategoryName != null) {
                Intent intent = new Intent(this, QuizSetupActivity.class);
                intent.putExtra(QuizSetupActivity.EXTRA_CATEGORY, mCategoryName);
                startActivity(intent);
            }
        });
    }

    // This logic is correct
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // This logic is correct
    @Override
    public void onItemClick(Word word) {
        Intent intent = new Intent(this, WordDetailActivity.class);
        intent.putExtra(WordDetailActivity.EXTRA_WORD_ID, word.getId());
        startActivity(intent);
    }

    // --- Delete Logic Methods ---

    private void showDeleteWordConfirmationDialog(Word word) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Word")
                .setMessage("Are you sure you want to remove '" + word.getWord() + "' from this category?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    removeWordFromCategory(word);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeWordFromCategory(Word word) {
        // IMPROVEMENT 4 (BUILD FIX):
        // We now use the mCurrentCategoryObject that our LiveData observer fetched.
        if (mCurrentCategoryObject != null) {
            WordCategoryCrossRef crossRef = new WordCategoryCrossRef(word.getId(), mCurrentCategoryObject.getId());
            mWordViewModel.deleteWordCategoryCrossRef(crossRef);
            Toast.makeText(this, "Word removed from category", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error: Category not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteCategoryConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete the category '" + mCategoryName + "'? This will not delete the words, only remove them from this category.")
                .setPositiveButton("Delete Category", (dialog, which) -> {
                    deleteCategory();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteCategory() {
        // IMPROVEMENT 5 (BUILD FIX):
        // The ViewModel's 'deleteCategory' method is 'void' (fire-and-forget).
        // We just call it. We don't need .thenAccept()
        mCategoryViewModel.deleteCategory(mCategoryName);

        Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show();
        finish(); // Go back to previous page
    }
}