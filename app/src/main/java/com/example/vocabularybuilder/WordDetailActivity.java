package com.example.vocabularybuilder;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType; // This import is no longer needed
import android.view.Menu; // IMPROVEMENT: Import Menu
import android.view.MenuItem; // IMPROVEMENT: Import MenuItem
import android.view.View; // This import is no longer needed
import android.widget.EditText; // This import is no longer needed
import android.widget.Toast; // IMPROVEMENT: Import Toast

import androidx.annotation.NonNull; // IMPROVEMENT: Import NonNull
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat; // IMPROVEMENT: Import for tinting
import androidx.lifecycle.ViewModelProvider;

import com.example.vocabularybuilder.data.model.Category;
import com.example.vocabularybuilder.data.model.Word;
import com.example.vocabularybuilder.data.model.WordCategoryCrossRef;
// IMPROVEMENT 1: Import ViewBinding
import com.example.vocabularybuilder.databinding.ActivityWordDetailBinding;
import com.example.vocabularybuilder.databinding.DialogCreateCategoryBinding; // IMPROVEMENT: Import Dialog Binding
import com.example.vocabularybuilder.viewmodel.CategoryViewModel;
import com.example.vocabularybuilder.viewmodel.WordViewModel;

import java.util.ArrayList; // IMPROVEMENT: Import ArrayList
import java.util.List;

public class WordDetailActivity extends AppCompatActivity {

    // --- CRITICAL (BUILD FIX): ---
    // These constants are required by SearchActivity.java to pass the Word ID.
    public static final String EXTRA_WORD_ID = "com.example.vocabularybuilder.EXTRA_WORD_ID";
    public static final String EXTRA_WORD_NAME = "com.example.vocabularybuilder.EXTRA_WORD_NAME";

    // IMPROVEMENT 1: Use ViewBinding
    private ActivityWordDetailBinding binding;
    private WordViewModel mWordViewModel;
    private CategoryViewModel mCategoryViewModel;
    private Word mCurrentWord;

    // IMPROVEMENT 4: Store the category list as a member variable
    private List<Category> mCategoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // IMPROVEMENT 1: Inflate layout using ViewBinding
        binding = ActivityWordDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // IMPROVEMENT 1: Remove all findViewById calls

        // IMPROVEMENT 2 (CRASH FIX): Set up the Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Title is set to "Word Detail" in the XML, which is good
        }

        mWordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        mCategoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // --- Data Loading ---
        int wordId = getIntent().getIntExtra(EXTRA_WORD_ID, -1);

        // IMPROVEMENT 7 (LOGIC FIX):
        // The 'else if (searchedWordName != null)' block has been removed.
        // Our SearchActivity's new logic guarantees it always finds or
        // inserts a word and passes a valid ID.
        if (wordId != -1) {
            mWordViewModel.getWordById(wordId).observe(this, this::updateUI);
        } else {
            // This should not happen, but as a fallback:
            Toast.makeText(this, "Error: Word ID not found.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // --- Observers ---

        // IMPROVEMENT 4 (BUILD FIX):
        // Observe the category list here, in onCreate, so it's ready
        // when the user clicks the "Add to Category" button.
        mCategoryViewModel.getAllCategories().observe(this, categories -> {
            mCategoryList.clear();
            mCategoryList.addAll(categories);
        });

        // --- Click Listeners ---

        binding.fabBookmark.setOnClickListener(v -> {
            if (mCurrentWord != null) {
                boolean wasLearned = mCurrentWord.isLearned();
                mCurrentWord.setLearned(!mCurrentWord.isLearned());
                mWordViewModel.update(mCurrentWord); // This saves the change

                // IMPROVEMENT 3 (ARCHITECTURE FIX):
                // Call the ViewModel to handle streak logic
                if (!wasLearned && mCurrentWord.isLearned()) {
                    mWordViewModel.updateStreak();
                }
            }
        });

        binding.btnAddToCategory.setOnClickListener(v -> {
            // The category list is already loaded from our observer.
            showCategorySelectionDialog();
        });
    }

    private void updateUI(Word word) {
        if (word != null) {
            mCurrentWord = word;
            binding.detailWordTitle.setText(word.getWord());
            binding.detailPartOfSpeech.setText(String.format("(%s)", word.getPartOfSpeech()));
            binding.detailMeaning.setText(word.getMeaning());
            binding.detailExample.setText(word.getExample());

            // This logic is correct!
            if (word.isLearned()) {
                binding.fabBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                // IMPROVEMENT: Set tint to match theme
                binding.fabBookmark.setImageTintList(ContextCompat.getColorStateList(this, R.color.primary));
            } else {
                binding.fabBookmark.setImageResource(R.drawable.ic_bookmark_border);
                // IMPROVEMENT: Set tint to match theme
                binding.fabBookmark.setImageTintList(ContextCompat.getColorStateList(this, R.color.primary));
            }
        }
    }

    private void showCategorySelectionDialog() {
        // IMPROVEMENT 4 (BUILD FIX):
        // This method no longer fetches data. It just *uses* the
        // mCategoryList that our LiveData observer is populating.

        if (mCategoryList.isEmpty()) {
            showNoCategoriesDialog();
            return;
        }

        String[] categoryNames = new String[mCategoryList.size() + 1];
        for (int i = 0; i < mCategoryList.size(); i++) {
            categoryNames[i] = mCategoryList.get(i).getName();
        }
        categoryNames[mCategoryList.size()] = "Create New Category"; // This is correct

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Category");

        builder.setItems(categoryNames, (dialog, which) -> {
            if (which == mCategoryList.size()) { // "Create New Category" selected
                showCreateCategoryDialog();
            } else {
                // Add word to selected category
                Category category = mCategoryList.get(which);

                if (mCurrentWord != null) {
                    WordCategoryCrossRef crossRef = new WordCategoryCrossRef(mCurrentWord.getId(), category.getId());
                    mWordViewModel.insertWordCategoryCrossRef(crossRef);
                    Toast.makeText(this, "Added to category: " + category.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.show();
    }

    private void showNoCategoriesDialog() {
        // This logic is correct.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Categories Available");
        builder.setMessage("Would you like to create a new category?");

        builder.setPositiveButton("Create", (dialog, which) -> showCreateCategoryDialog());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showCreateCategoryDialog() {
        // IMPROVEMENT 5 (UI FIX):
        // Inflate our custom 'dialog_create_category.xml' layout
        // instead of creating an EditText programmatically.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogCreateCategoryBinding dialogBinding = DialogCreateCategoryBinding.inflate(getLayoutInflater());

        // Set the custom title and view from our binding
        builder.setCustomTitle(dialogBinding.dialogTitle);
        builder.setView(dialogBinding.getRoot());

        builder.setPositiveButton("Create", (dialog, which) -> {
            String categoryName = "";
            if (dialogBinding.categoryNameInput.getText() != null) {
                categoryName = dialogBinding.categoryNameInput.getText().toString().trim();
            }

            if (!categoryName.isEmpty()) {
                createCategory(categoryName);
            } else {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void createCategory(String name) {
        Category category = new Category(name);

        // IMPROVEMENT 6 (BUILD FIX):
        // The ViewModel's 'insert' method is 'void' (fire-and-forget).
        // We just call it. The LiveData observer in onCreate will
        // automatically get the new list, so it will be ready
        // the next time the user clicks "Add to Category".
        mCategoryViewModel.insert(category);

        Toast.makeText(this, "Category created", Toast.LENGTH_SHORT).show();
    }

    //
    // IMPROVEMENT 3 (ARCHITECTURE FIX):
    // The entire private updateStreak() method has been removed from this Activity
    // and moved into the WordRepository/ViewModel.
    //

    // --- IMPROVEMENT 2 (CRASH FIX): Handle Menu Events ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu defined in 'detail_menu.xml'
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete_word) {
            // Handle the delete button click
            if (mCurrentWord != null) {
                mWordViewModel.deleteWord(mCurrentWord);
                Toast.makeText(this, mCurrentWord.getWord() + " deleted", Toast.LENGTH_SHORT).show();
                finish(); // Go back to the previous screen
            }
            return true;
        } else if (id == android.R.id.home) {
            // Handle the Toolbar's back arrow
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}