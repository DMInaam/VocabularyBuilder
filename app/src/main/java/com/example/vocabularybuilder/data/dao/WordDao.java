package com.example.vocabularybuilder.data.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import com.example.vocabularybuilder.data.model.Word;
import java.util.List;

@Dao
public interface WordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(@NonNull Word word);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(@NonNull List<Word> words);

    @Update
    void update(@NonNull Word word);

    @Delete
    void delete(@NonNull Word word);

    @Query("DELETE FROM word_table")
    void deleteAll();

    @Query("SELECT * FROM word_table ORDER BY word ASC")
    LiveData<List<Word>> getAllWords();

    @Query("SELECT * FROM word_table WHERE id = :wordId")
    LiveData<Word> getWordById(int wordId);

    // --- IMPROVEMENT (CRITICAL FIX): ---
    // Added a synchronous 'getWordByName' query.
    // This is required by the repository to check if a searched word
    // already exists in the database.
    @Query("SELECT * FROM word_table WHERE word = :word LIMIT 1")
    Word getWordByName(@NonNull String word);

    @Query("SELECT * FROM word_table WHERE cefr_level = 'User Added' ORDER BY word ASC")
    LiveData<List<Word>> getUserAddedWords();

    @Query("SELECT * FROM word_table WHERE cefr_level = :level ORDER BY word ASC")
    LiveData<List<Word>> getWordsByCefrLevel(@NonNull String level);

    @Query("SELECT * FROM word_table WHERE is_learned = 1 ORDER BY word ASC")
    LiveData<List<Word>> getLearnedWords();

    // --- Quiz Generation Queries ---
    @Query("SELECT * FROM word_table WHERE cefr_level = :level AND (is_learned = 1 OR cefr_level = 'User Added') ORDER BY RANDOM() LIMIT :limit")
    List<Word> getQuizWordsForCefrLevel(@NonNull String level, int limit);

    @Query("SELECT * FROM word_table WHERE is_learned = 1 OR cefr_level = 'User Added' ORDER BY RANDOM() LIMIT :limit")
    List<Word> getQuizWordsForLearnedAndUserAdded(int limit);


    // --- Count Queries (FIXED) ---
    // These return 'int' and are wrapped in CompletableFuture by the Repository.
    // This is the correct pattern.

    @Query("SELECT COUNT(*) FROM word_table WHERE is_learned = 1 OR cefr_level = 'User Added'")
    int getLearnedAndUserAddedWordCount();

    @Query("SELECT COUNT(*) FROM word_table WHERE is_learned = 1")
    int getLearnedWordCount();

    @Query("SELECT COUNT(*) FROM word_table WHERE cefr_level = :category AND (is_learned = 1 OR cefr_level = 'User Added')")
    int getWordCountForCategory(@NonNull String category);

    @Query("SELECT COUNT(*) FROM word_table w INNER JOIN word_category_cross_ref c ON w.id = c.wordId INNER JOIN categories cat ON c.categoryId = cat.id WHERE cat.name = :categoryName")
    int getWordCountForCategoryName(@NonNull String categoryName);

    // --- Many-to-Many Relationship ---
    @Transaction
    @Query("SELECT w.* FROM word_table w INNER JOIN word_category_cross_ref c ON w.id = c.wordId INNER JOIN categories cat ON c.categoryId = cat.id WHERE cat.name = :categoryName")
    LiveData<List<Word>> getWordsByCategory(@NonNull String categoryName);

    @Query("SELECT * FROM word_table ORDER BY RANDOM() LIMIT 1")
    LiveData<Word> getWordOfTheDay();

    @Query("SELECT COUNT(*) FROM word_table")
    int getWordCount();
}