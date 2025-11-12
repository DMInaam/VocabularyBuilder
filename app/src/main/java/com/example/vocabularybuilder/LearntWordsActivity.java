package com.example.vocabularybuilder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vocabularybuilder.databinding.ActivityWordListBinding;
import com.example.vocabularybuilder.data.model.Word;
import com.example.vocabularybuilder.ui.adapters.WordListAdapter;
import com.example.vocabularybuilder.viewmodel.WordViewModel;

public class LearntWordsActivity extends AppCompatActivity implements WordListAdapter.OnItemClickListener {

    private WordViewModel mWordViewModel;
    private WordListAdapter mAdapter;
    private ActivityWordListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWordListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Learned Words");
        }

        // Hide the button container (Take Quiz / Delete Category)
        binding.buttonContainer.setVisibility(View.GONE);

        // Setup RecyclerView
        mAdapter = new WordListAdapter(new WordListAdapter.WordDiff());
        binding.wordListRecyclerview.setAdapter(mAdapter);
        binding.wordListRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.setOnItemClickListener(this);

        // Setup ViewModel
        mWordViewModel = new ViewModelProvider(this).get(WordViewModel.class);

        // Observe the list of learned words
        mWordViewModel.getLearnedWords().observe(this, words -> {
            mAdapter.submitList(words);
        });
    }

    // Handle the Toolbar's back arrow
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // Handle clicks on a word in the list
    @Override
    public void onItemClick(Word word) {
        Intent intent = new Intent(this, WordDetailActivity.class);
        // Use the constant from WordDetailActivity
        intent.putExtra(WordDetailActivity.EXTRA_WORD_ID, word.getId());
        startActivity(intent);
    }
}