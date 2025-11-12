package com.example.vocabularybuilder.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.vocabularybuilder.data.DateConverter;

import java.util.Date;

@Entity(tableName = "quiz_results")
@TypeConverters(DateConverter.class) // This is correct!
public class QuizResult {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "score")
    private int score;

    // IMPROVEMENT 1: Added @NonNull
    // This ensures a QuizResult always has a date.
    @NonNull
    @ColumnInfo(name = "date")
    private Date date;

    // IMPROVEMENT 2 (CRITICAL FIX):
    // You MUST provide a no-argument (default) constructor for Room.
    public QuizResult() {
        // Initialize Date to the current time by default
        this.date = new Date();
    }

    // IMPROVEMENT 3: Added @Ignore to this constructor.
    // This is now a "convenience" constructor for you to use in your code.
    @Ignore
    public QuizResult(int score, @NonNull Date date) {
        this.score = score;
        this.date = date;
    }

    // --- Getters and Setters ---

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }
}