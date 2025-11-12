package com.example.vocabularybuilder;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vocabularybuilder.api.ApiClient;
import com.example.vocabularybuilder.data.model.Word;
import com.example.vocabularybuilder.databinding.ActivitySearchBinding;
import com.example.vocabularybuilder.ui.adapters.WordListAdapter;
import com.example.vocabularybuilder.viewmodel.WordViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private WordListAdapter mAdapter;
    private List<Word> mSearchResults;
    private WordViewModel mWordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mWordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        initViews();
        setupRecyclerView();
        setupSearchFunctionality();
    }

    private void initViews() {
        mSearchResults = new ArrayList<>();
    }

    private void setupRecyclerView() {
        mAdapter = new WordListAdapter(new WordListAdapter.WordDiff());
        binding.searchResultsRecyclerView.setAdapter(mAdapter);
        binding.searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // IMPROVEMENT: Use method reference
        mAdapter.setOnItemClickListener(this::addWordToDatabaseAndNavigate);
    }

    private void setupSearchFunctionality() {
        binding.searchLayout.setEndIconOnClickListener(v -> performSearch());

        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    mSearchResults.clear();
                    mAdapter.submitList(mSearchResults);
                    binding.noResultsTextView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void performSearch() {
        String query = "";
        if (binding.searchEditText.getText() != null) {
            query = binding.searchEditText.getText().toString().trim();
        }

        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a word to search", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        ApiClient.ApiService apiService = ApiClient.getClient().create(ApiClient.ApiService.class);
        Call<List<ApiClient.ApiWord>> call = apiService.getWordDefinition(query);

        // IMPROVEMENT: Use diamond operator <>
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ApiClient.ApiWord>> call, @NonNull Response<List<ApiClient.ApiWord>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Word> words = convertApiWordsToAppWords(response.body());
                    mSearchResults.clear();
                    mSearchResults.addAll(words);
                    mAdapter.submitList(mSearchResults);
                    binding.noResultsTextView.setVisibility(View.GONE);
                } else {
                    mSearchResults.clear();
                    mAdapter.submitList(mSearchResults);
                    binding.noResultsTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ApiClient.ApiWord>> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(SearchActivity.this, "Search failed: " + t.getMessage(), Toast.LENGTH_LONG).show();

                mSearchResults.clear();
                mAdapter.submitList(mSearchResults);
                binding.noResultsTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private List<Word> convertApiWordsToAppWords(List<ApiClient.ApiWord> apiWords) {
        List<Word> appWords = new ArrayList<>();

        for (ApiClient.ApiWord apiWord : apiWords) {
            if (apiWord.getMeanings() != null && !apiWord.getMeanings().isEmpty()) {
                ApiClient.Meaning firstMeaning = apiWord.getMeanings().get(0);

                if (firstMeaning.getDefinitions() != null && !firstMeaning.getDefinitions().isEmpty()) {
                    ApiClient.Definition firstDefinition = firstMeaning.getDefinitions().get(0);

                    String meaning = firstDefinition.getDefinition();
                    String example = firstDefinition.getExample() != null ? firstDefinition.getExample() : "";
                    String partOfSpeech = firstMeaning.getPartOfSpeech() != null ? firstMeaning.getPartOfSpeech() : "";

                    Word word = new Word(apiWord.getWord(), meaning, partOfSpeech, example, "User Added", false, false);
                    appWords.add(word);
                }
            }
        }

        return appWords;
    }

    private void showLoading(boolean show) {
        if (show) {
            binding.searchProgressBar.setVisibility(View.VISIBLE);
            binding.searchLayout.setEnabled(false);
        } else {
            binding.searchProgressBar.setVisibility(View.GONE);
            binding.searchLayout.setEnabled(true); // <-- CRITICAL FIX: Removed the stray "Analyst" text
        }
    }

    private void addWordToDatabaseAndNavigate(Word word) {
        Toast.makeText(this, "Loading word...", Toast.LENGTH_SHORT).show();

        mWordViewModel.findOrInsertWord(word).thenAccept(wordWithId -> {
            runOnUiThread(() -> {
                if (wordWithId != null) {
                    Intent intent = new Intent(SearchActivity.this, WordDetailActivity.class);
                    intent.putExtra(WordDetailActivity.EXTRA_WORD_ID, wordWithId.getId());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Error saving word", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}