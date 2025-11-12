package com.example.vocabularybuilder.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.vocabularybuilder.data.model.QuizResultWithQuestions;
import com.example.vocabularybuilder.data.model.QuizQuestion;
import com.example.vocabularybuilder.data.model.QuizResult;
import com.example.vocabularybuilder.data.model.Word;
import com.example.vocabularybuilder.data.model.WordCategoryCrossRef;
import com.example.vocabularybuilder.data.repository.WordRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * WordViewModel is the central "brain" for all UI controllers (Activities)
 * that need to interact with Word or Quiz data.
 * It is responsible for:
 * 1. Holding LiveData for the UI to observe (e.g., list of words, word of the day).
 * 2. Calling the WordRepository to fetch or update data.
 * 3. Surviving configuration changes (like screen rotation).
 */
public class WordViewModel extends AndroidViewModel {

    private final WordRepository mRepository;

    // LiveData for the WordListActivity, driven by mCategoryName
    private final MutableLiveData<String> mCategoryName = new MutableLiveData<>();
    private final LiveData<List<Word>> mWordsForCategoryList;

    private final LiveData<List<Word>> mAllWords; // For Dictionary

    // LiveData for the ProfileActivity's streak count
    private final LiveData<Integer> mStreakCount;

    public WordViewModel(@NonNull Application application) {
        super(application);
        mRepository = new WordRepository(application);
        mAllWords = mRepository.getAllWords();
        // --- IMPROVEMENT: Initialize Streak LiveData ---
        mStreakCount = mRepository.getStreakCount();

        //
        // This is the "smart" LiveData for your WordListActivity.
        // It observes the mCategoryName LiveData. When mCategoryName changes
        // (e.g., user clicks "A1"), it "switches" to call the repository's
        // smart 'getWordsForCategoryList' method, which returns the
        // correct LiveData<List<Word>> for that category.
        //
        mWordsForCategoryList = Transformations.switchMap(mCategoryName, category ->
                mRepository.getWordsForCategoryList(category)
        );
    }

    // --- WordListActivity ---

    /**
     * Called by WordListActivity to set which category to display.
     * This triggers the 'mWordsForCategoryList' switchMap to update.
     */
    public void setCategoryForList(@NonNull String category) {
        mCategoryName.setValue(category);
    }

    /**
     * Observed by WordListActivity to get the list of words.
     */
    public LiveData<List<Word>> getWordsForCategoryList() {
        return mWordsForCategoryList;
    }

    // --- IMPROVEMENT: Added this pass-through method ---
    // This lets the Activity ask if a category is a CEFR level
    // to know whether to show the "Delete" button.
    public boolean isCefrLevel(String category) {
        return mRepository.isCefrLevel(category);
    }

    // --- DictionaryActivity ---
    public LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }

    // --- LearntWordsActivity ---
    /**
     * --- IMPROVEMENT (CRITICAL BUILD FIX): ---
     * Added the missing method that LearntWordsActivity needs.
     */
    public LiveData<List<Word>> getLearnedWords() {
        return mRepository.getLearnedWords();
    }

    // --- WordDetailActivity ---
    public LiveData<Word> getWordById(int wordId) {
        return mRepository.getWordById(wordId);
    }

    public void update(Word word) {
        mRepository.update(word);
    }

    public void deleteWord(Word word) {
        mRepository.delete(word);
    }

    public void insertWordCategoryCrossRef(WordCategoryCrossRef crossRef) {
        mRepository.insertWordCategoryCrossRef(crossRef);
    }

    public void deleteWordCategoryCrossRef(WordCategoryCrossRef crossRef) {
        mRepository.deleteWordCategoryCrossRef(crossRef);
    }

    public CompletableFuture<Integer> getCrossRefCount(int wordId, int categoryId) {
        return mRepository.getCrossRefCount(wordId, categoryId);
    }

    // --- IMPROVEMENT (CRITICAL FIX): ---
    // Expose the new 'findOrInsertWord' method for SearchActivity.
    public CompletableFuture<Word> findOrInsertWord(Word word) {
        return mRepository.findOrInsertWord(word);
    }

    // --- NewWordActivity ---
    public void fetchWordFromApi(String word) {
        mRepository.fetchWordFromApi(word);
    }

    // --- QuizSetupActivity / QuizActivity ---

    /**
     * Gets the words for a quiz.
     * This is a one-off async call, so it uses CompletableFuture.
     */
    public CompletableFuture<List<Word>> getQuizWords(String category, int limit) {
        if (category != null && !category.isEmpty()) {
            return mRepository.getWordsByCefrLevel(category, limit);
        } else {
            // Default quiz: learned and user-added words
            return mRepository.getLearnedAndUserAddedWords(limit);
        }
    }

    public void saveQuizResult(QuizResult result, List<QuizQuestion> questions) {
        mRepository.saveQuizResult(result, questions);
    }

    // --- ProfileActivity ---
    public LiveData<List<QuizResultWithQuestions>> getQuizHistory() {
        return mRepository.getQuizHistory();
    }

    public CompletableFuture<Integer> getLearnedWordCount() {
        return mRepository.getLearnedWordCount();
    }

    public CompletableFuture<Integer> getWordsLearnedTodayCount() {
        return mRepository.getWordsLearnedTodayCount();
    }

    public CompletableFuture<Integer> getWordCountForCategory(String category) {
        return mRepository.getWordCountForCategory(category);
    }

    // --- IMPROVEMENT (CRITICAL BUILD FIX): ---
    // Added the missing method that QuizSetupActivity needs.
    public CompletableFuture<Integer> getLearnedAndUserAddedWordCount() {
        return mRepository.getLearnedAndUserAddedWordCount();
    }

    public LiveData<Integer> getStreakCount() {
        return mStreakCount;
    }

    // --- MainActivity ---
    public LiveData<Word> getWordOfTheDay() {
        return mRepository.getWordOfTheDay();
    }

    // --- Streak Logic (called from NewWordActivity & QuizActivity) ---
    public void updateStreak() {
        mRepository.updateStreak();
    }
}