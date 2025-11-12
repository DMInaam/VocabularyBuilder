package com.example.vocabularybuilder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.vocabularybuilder.data.model.Category;
import com.example.vocabularybuilder.databinding.ActivityMainBinding;
import com.example.vocabularybuilder.databinding.DialogCreateCategoryBinding;
import com.example.vocabularybuilder.ui.adapters.CategoryAdapter;
import com.example.vocabularybuilder.utils.Constants;
import com.example.vocabularybuilder.viewmodel.CategoryViewModel;
import com.example.vocabularybuilder.viewmodel.WordViewModel;

// --- IMPROVEMENT: Added these imports ---
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WordViewModel mWordViewModel;
    private CategoryViewModel mCategoryViewModel;
    private CategoryAdapter mCategoryAdapter;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use Constants.java for SharedPreferences keys
        SharedPreferences sharedPref = getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPref.getBoolean(Constants.IS_LOGGED_IN_KEY, false);

        if (!isLoggedIn) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Inflate layout using ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup ViewModels
        mWordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        mCategoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // Call the setup method
        setupViews();
    }

    private void setupViews() {
        // Setup Toolbar
        setSupportActionBar(binding.toolbar);

        // Setup Word of the Day observer
        mWordViewModel.getWordOfTheDay().observe(this, word -> {
            if (word != null) {
                binding.wordOfTheDayText.setText(word.getWord());
            } else {
                binding.wordOfTheDayText.setText("No word available");
            }
        });

        // Setup "Start Quiz" button
        binding.buttonStartQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuizSetupActivity.class);
            startActivity(intent);
        });

        // Setup categories RecyclerView
        setupCategoryRecycler();

        // Load categories from the database
        loadCategories();

        // Setup FAB
        binding.fabAddWord.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewWordActivity.class);
            startActivity(intent);
        });
    }

    private void setupCategoryRecycler() {
        mCategoryAdapter = new CategoryAdapter();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 2; // Position 0 (TYPE_CREATE) spans 2 columns
                } else {
                    return 1; // All other items (TYPE_CATEGORY) span 1 column
                }
            }
        });

        binding.categoriesRecyclerView.setLayoutManager(layoutManager);
        binding.categoriesRecyclerView.setAdapter(mCategoryAdapter);

        // Set the click listener for the adapter
        mCategoryAdapter.setOnCategoryClickListener(new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category) {
                // --- IMPROVEMENT (CRITICAL BUILD FIX): ---
                // Declared the 'intent' object before using it.
                Intent intent = new Intent(MainActivity.this, WordListActivity.class);
                intent.putExtra(WordListActivity.EXTRA_CATEGORY, category.getName());
                startActivity(intent);
            }

            @Override
            public void onCreateCategoryClick() {
                showCreateCategoryDialog();
            }
        });
    }

    private void loadCategories() {
        // Observe custom categories and manually add the static CEFR categories
        mCategoryViewModel.getAllCategories().observe(this, customCategories -> {

            List<Category> allCategories = new ArrayList<>();

            // 1. Add static "User Added" category
            allCategories.add(new Category("User Added"));

            // 2. Add static CEFR Level categories
            allCategories.add(new Category("A1"));
            allCategories.add(new Category("A2"));
            allCategories.add(new Category("B1"));
            allCategories.add(new Category("B2"));
            allCategories.add(new Category("C1"));
            allCategories.add(new Category("C2"));

            // 3. Add all the user's custom categories
            if (customCategories != null) {
                allCategories.addAll(customCategories);
            }

            // 4. Send the complete list to the adapter
            mCategoryAdapter.setCategories(allCategories);
        });
    }

    private void showCreateCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // --- IMPROVEMENT (CRITICAL BUILD FIX): ---
        // Made 'dialogBinding' final so the lambda can access it.
        final DialogCreateCategoryBinding dialogBinding = DialogCreateCategoryBinding.inflate(getLayoutInflater());

        // Set the View, but NOT the custom title (it's already in the view)
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
        // --- IMPROVEMENT (CRITICAL BUILD FIX): ---
        // Correctly created the new Category object before inserting.
        Category category = new Category(name);
        mCategoryViewModel.insert(category);

        Toast.makeText(this, "Category created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_dictionary) {
            Intent intent = new Intent(this, DictionaryActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}