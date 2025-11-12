package com.example.vocabularybuilder.data.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import com.example.vocabularybuilder.data.model.QuizQuestion;
import com.example.vocabularybuilder.data.model.QuizResult;
import com.example.vocabularybuilder.data.model.QuizResultWithQuestions; // Added import

import java.util.List;

@Dao
public interface QuizDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertResult(@NonNull QuizResult quizResult);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestions(@NonNull List<QuizQuestion> questions);

    // This query is now correct because QuizResultWithQuestions exists.
    @Transaction
    @Query("SELECT * FROM quiz_results ORDER BY date DESC")
    LiveData<List<QuizResultWithQuestions>> getQuizHistory();
}