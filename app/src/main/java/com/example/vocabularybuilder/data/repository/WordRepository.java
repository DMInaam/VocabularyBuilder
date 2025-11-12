package com.example.vocabularybuilder.data.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.vocabularybuilder.api.ApiClient;
import com.example.vocabularybuilder.data.WordRoomDatabase;
import com.example.vocabularybuilder.data.dao.QuizDao;
import com.example.vocabularybuilder.data.dao.WordDao;
import com.example.vocabularybuilder.data.dao.WordCategoryCrossRefDao;
import com.example.vocabularybuilder.data.model.QuizResultWithQuestions;
import com.example.vocabularybuilder.data.model.QuizQuestion;
import com.example.vocabularybuilder.data.model.QuizResult;
import com.example.vocabularybuilder.data.model.Word;
import com.example.vocabularybuilder.data.model.WordCategoryCrossRef;
import com.example.vocabularybuilder.utils.Constants;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WordRepository {

    private final WordDao mWordDao;
    private final QuizDao mQuizDao;
    private final WordCategoryCrossRefDao mWordCategoryCrossRefDao;
    private final LiveData<List<Word>> mAllWords;
    private final ApiClient.ApiService mApiService;
    private final SharedPreferences mSharedPreferences;

    // LiveData for Streak Count (observed by Profile)
    private final MutableLiveData<Integer> mStreakCount = new MutableLiveData<>();


    public WordRepository(Application application) {
        WordRoomDatabase db = WordRoomDatabase.getDatabase(application);
        mWordDao = db.wordDao();
        mQuizDao = db.quizDao();
        mWordCategoryCrossRefDao = db.wordCategoryCrossRefDao();
        mAllWords = mWordDao.getAllWords();
        mApiService = ApiClient.getClient().create(ApiClient.ApiService.class);
        mSharedPreferences = application.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);

        // Load the initial streak count
        loadStreakCount();
    }

    // --- Streak Logic ---

    /**
     * Public method for the ViewModel to get the observable streak count.
     */
    public LiveData<Integer> getStreakCount() {
        return mStreakCount;
    }

    /**
     * Loads the current streak from SharedPreferences on a background thread
     * and posts the value to the LiveData.
     */
    private void loadStreakCount() {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> {
            int streak = mSharedPreferences.getInt(Constants.CURRENT_STREAK_KEY, 0);
            mStreakCount.postValue(streak);
        });
    }

    /**
     * Updates the user's learning streak.
     * This logic is now centralized, secure, and runs on a background thread.
     */
    public void updateStreak() {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Get today's date for comparison
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String today = dateFormat.format(new Date());

            String lastLearningDate = mSharedPreferences.getString(Constants.LAST_LEARNING_DATE_KEY, "");

            // If user has already learned today, do nothing.
            if (today.equals(lastLearningDate)) {
                return;
            }

            SharedPreferences.Editor editor = mSharedPreferences.edit();

            // Check if yesterday was the last learning day
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -1);
            String yesterday = dateFormat.format(cal.getTime());

            int currentStreak = mSharedPreferences.getInt(Constants.CURRENT_STREAK_KEY, 0);

            if (yesterday.equals(lastLearningDate)) {
                // Extend the streak
                currentStreak++;
            } else {
                // Reset streak if last learning was not yesterday
                currentStreak = 1;
            }

            // Post the new value to the LiveData so the Profile screen updates
            mStreakCount.postValue(currentStreak);

            editor.putInt(Constants.CURRENT_STREAK_KEY, currentStreak);
            editor.putString(Constants.LAST_LEARNING_DATE_KEY, today);
            editor.apply();
        });
    }

    // --- Word Read Operations (LiveData) ---

    public LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }

    public LiveData<List<Word>> getWordsByCefrLevel(@NonNull String level) {
        return mWordDao.getWordsByCefrLevel(level);
    }

    public LiveData<List<Word>> getWordsByCategory(@NonNull String categoryName) {
        return mWordDao.getWordsByCategory(categoryName);
    }

    public LiveData<List<Word>> getLearnedWords() {
        return mWordDao.getLearnedWords();
    }

    public LiveData<List<Word>> getUserAddedWords() {
        return mWordDao.getUserAddedWords();
    }

    public LiveData<Word> getWordOfTheDay() {
        return mWordDao.getWordOfTheDay();
    }

    public LiveData<Word> getWordById(int wordId) {
        return mWordDao.getWordById(wordId);
    }

    /**
     * This is the smart method for the WordList.
     * It checks the category name and calls the correct DAO method.
     */
    public LiveData<List<Word>> getWordsForCategoryList(@NonNull String category) {
        if ("User Added".equals(category)) {
            return getUserAddedWords();
        } else if (isCefrLevel(category)) {
            return getWordsByCefrLevel(category);
        } else {
            // This is a custom user category (e.g., "My Nouns")
            return getWordsByCategory(category);
        }
    }

    // --- Word Write Operations (Async) ---

    public void insert(Word word) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> mWordDao.insert(word));
    }

    /**
     * This new method checks if a word exists. If it does, it returns it.
     * If not, it inserts it and *then* returns it.
     * This guarantees we get a valid Word with its ID for SearchActivity.
     */
    public CompletableFuture<Word> findOrInsertWord(Word word) {
        return CompletableFuture.supplyAsync(() -> {
            // Check if the word already exists
            Word existingWord = mWordDao.getWordByName(word.getWord());
            if (existingWord != null) {
                // It exists! Return the existing word with its ID.
                return existingWord;
            } else {
                // It's a new word. Insert it.
                mWordDao.insert(word);
                // Fetch the word we just inserted to get its ID.
                return mWordDao.getWordByName(word.getWord());
            }
        }, WordRoomDatabase.databaseWriteExecutor);
    }

    public void update(Word word) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> mWordDao.update(word));
    }

    public void delete(Word word) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> mWordDao.delete(word));
    }

    // --- Category Cross-Ref Operations (Async) ---

    public void insertWordCategoryCrossRef(WordCategoryCrossRef crossRef) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> mWordCategoryCrossRefDao.insert(crossRef));
    }

    public void deleteWordCategoryCrossRef(WordCategoryCrossRef crossRef) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> mWordCategoryCrossRefDao.delete(crossRef.wordId, crossRef.categoryId));
    }

    public CompletableFuture<Integer> getCrossRefCount(int wordId, int categoryId) {
        return CompletableFuture.supplyAsync(() -> mWordCategoryCrossRefDao.getCrossRefCount(wordId, categoryId),
                WordRoomDatabase.databaseWriteExecutor);
    }

    // --- API Network Operation ---

    public void fetchWordFromApi(String word) {
        mApiService.getWordDefinition(word).enqueue(new Callback<List<ApiClient.ApiWord>>() {
            @Override
            public void onResponse(@NonNull Call<List<ApiClient.ApiWord>> call, @NonNull Response<List<ApiClient.ApiWord>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    try {
                        ApiClient.ApiWord apiWord = response.body().get(0);
                        ApiClient.Meaning firstMeaning = apiWord.getMeanings().get(0);
                        ApiClient.Definition firstDefinition = firstMeaning.getDefinitions().get(0);

                        Word newWord = new Word(
                                apiWord.getWord(),
                                firstDefinition.getDefinition(),
                                firstMeaning.getPartOfSpeech(),
                                firstDefinition.getExample() != null ? firstDefinition.getExample() : "", // Handle null example
                                "User Added",
                                false,
                                false
                        );
                        insert(newWord);
                    } catch (Exception e) {
                        Log.e("WordRepository", "API parsing error", e);
                        insert(new Word(word));
                    }
                } else {
                    insert(new Word(word));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ApiClient.ApiWord>> call, @NonNull Throwable t) {
                Log.e("WordRepository", "API call failed", t);
                insert(new Word(word));
            }
        });
    }

    // --- Quiz Generation Operations (Async) ---

    public CompletableFuture<List<Word>> getLearnedAndUserAddedWords(int limit) {
        return CompletableFuture.supplyAsync(() -> mWordDao.getQuizWordsForLearnedAndUserAdded(limit), WordRoomDatabase.databaseWriteExecutor);
    }

    public CompletableFuture<List<Word>> getWordsByCefrLevel(String cefrLevel, int limit) {
        return CompletableFuture.supplyAsync(() -> mWordDao.getQuizWordsForCefrLevel(cefrLevel, limit), WordRoomDatabase.databaseWriteExecutor);
    }

    // --- Profile/Count Operations (Async) ---

    public CompletableFuture<Integer> getLearnedAndUserAddedWordCount() {
        return CompletableFuture.supplyAsync(mWordDao::getLearnedAndUserAddedWordCount, WordRoomDatabase.databaseWriteExecutor);
    }

    public CompletableFuture<Integer> getWordCountForCategory(String category) {
        if (isCefrLevel(category)) {
            return CompletableFuture.supplyAsync(() -> mWordDao.getWordCountForCategory(category), WordRoomDatabase.databaseWriteExecutor);
        } else {
            return CompletableFuture.supplyAsync(() -> mWordDao.getWordCountForCategoryName(category), WordRoomDatabase.databaseWriteExecutor);
        }
    }

    public boolean isCefrLevel(String category) {
        return "A1".equals(category) || "A2".equals(category) ||
                "B1".equals(category) || "B2".equals(category) ||
                "C1".equals(category) || "C2".equals(category) ||
                "User Added".equals(category);
    }

    // --- Quiz History Operations ---

    public void saveQuizResult(QuizResult result, List<QuizQuestion> questions) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> {
            long resultId = mQuizDao.insertResult(result);
            for (QuizQuestion question : questions) {
                question.setQuizResultId(resultId);
            }
            mQuizDao.insertQuestions(questions);
        });
    }

    public LiveData<List<QuizResultWithQuestions>> getQuizHistory() {
        return mQuizDao.getQuizHistory();
    }

    public CompletableFuture<Integer> getLearnedWordCount() {
        return CompletableFuture.supplyAsync(mWordDao::getLearnedWordCount, WordRoomDatabase.databaseWriteExecutor);
    }

    public CompletableFuture<Integer> getWordsLearnedTodayCount() {
        // Placeholder - requires a 'dateLearned' field in Word.java
        return CompletableFuture.completedFuture(0);
    }
}