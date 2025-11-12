package com.example.vocabularybuilder;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull; // Added NonNull
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
// Removed unused RecyclerView import

import com.example.vocabularybuilder.data.model.Word;
// IMPROVEMENT 1: Use ViewBinding
import com.example.vocabularybuilder.databinding.ActivityDictionaryBinding;
import com.example.vocabularybuilder.ui.adapters.WordListAdapter;
import com.example.vocabularybuilder.viewmodel.WordViewModel;

import java.util.ArrayList; // Added for filtering
import java.util.List; // Added for filtering

public class DictionaryActivity extends AppCompatActivity implements WordListAdapter.OnItemClickListener {

    private WordViewModel mWordViewModel;
    private WordListAdapter mAdapter;

    // IMPROVEMENT 1: Use ViewBinding instead of findViewById
    private ActivityDictionaryBinding binding;

    // IMPROVEMENT 3: Store the full list for filtering
    private List<Word> mAllWordsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // IMPROVEMENT 1: Inflate layout using ViewBinding
        binding = ActivityDictionaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // IMPROVEMENT 2 (CRASH FIX): Set the Toolbar from the layout
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.action_dictionary);
        }

        // Setup RecyclerView
        mAdapter = new WordListAdapter(new WordListAdapter.WordDiff());
        binding.dictionaryRecyclerview.setAdapter(mAdapter);
        binding.dictionaryRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setOnItemClickListener(this);

        // Setup ViewModel and observe data
        mWordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        mWordViewModel.getAllWords().observe(this, words -> {
            // IMPROVEMENT 3: Save the full list locally
            mAllWordsList.clear();
            mAllWordsList.addAll(words);
            // Submit the full list to the adapter
            mAdapter.submitList(words);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dictionary, menu); // This will now work

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // IMPROVEMENT 3 (CRASH FIX):
                // Removed adapter.getFilter().filter(newText);
                // Instead, filter the local list and submit the result to the ListAdapter.
                filter(newText);
                return true;
            }
        });

        return true;
    }

    /**
     * IMPROVEMENT 3: New filtering method.
     * This manually filters the full list and submits the filtered list
     * to the ListAdapter.
     */
    private void filter(String query) {
        List<Word> filteredList = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            filteredList.addAll(mAllWordsList);
        } else {
            String filterPattern = query.toLowerCase().trim();
            for (Word word : mAllWordsList) {
                if (word.getWord().toLowerCase().contains(filterPattern)) {
                    filteredList.add(word);
                }
            }
        }
        mAdapter.submitList(filteredList);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onItemClick(Word word) {
        Intent intent = new Intent(this, WordDetailActivity.class);

        // --- IMPROVEMENT (CRITICAL FIX): ---
        // Used the constant from WordDetailActivity.java
        // instead of a hard-coded string.
        intent.putExtra(WordDetailActivity.EXTRA_WORD_ID, word.getId());

        startActivity(intent);
    }
}