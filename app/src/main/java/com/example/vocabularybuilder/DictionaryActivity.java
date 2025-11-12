package com.example.vocabularybuilder;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull; 
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.vocabularybuilder.data.model.Word;
import com.example.vocabularybuilder.databinding.ActivityDictionaryBinding;
import com.example.vocabularybuilder.ui.adapters.WordListAdapter;
import com.example.vocabularybuilder.viewmodel.WordViewModel;

import java.util.ArrayList; 
import java.util.List;

public class DictionaryActivity extends AppCompatActivity implements WordListAdapter.OnItemClickListener {

    private WordViewModel mWordViewModel;
    private WordListAdapter mAdapter;

    private ActivityDictionaryBinding binding;

    private List<Word> mAllWordsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDictionaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
            mAllWordsList.clear();
            mAllWordsList.addAll(words);
            // Submit the full list to the adapter
            mAdapter.submitList(words);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dictionary, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        return true;
    }

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
        intent.putExtra(WordDetailActivity.EXTRA_WORD_ID, word.getId());
        startActivity(intent);
    }
}