package com.example.vocabularybuilder.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "quiz_questions",
        foreignKeys = @ForeignKey(entity = QuizResult.class,
                parentColumns = "id",
                childColumns = "quizResultId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("quizResultId")})
public class QuizQuestion {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "quizResultId")
    private long quizResultId;

    // IMPROVEMENT 1: Added @NonNull and @ColumnInfo
    @NonNull
    @ColumnInfo(name = "word")
    private String word;

    @NonNull
    @ColumnInfo(name = "userAnswer")
    private String userAnswer;

    @NonNull
    @ColumnInfo(name = "correctAnswer")
    private String correctAnswer; // To show in the history

    @ColumnInfo(name = "isCorrect")
    private boolean isCorrect;

    // IMPROVEMENT 2 (CRITICAL FIX):
    // You MUST provide a no-argument (default) constructor for Room.
    // Because String fields are @NonNull, we initialize them.
    public QuizQuestion() {
        this.word = "";
        this.userAnswer = "";
        this.correctAnswer = "";
        // Primitives (long, boolean) default to 0 and false, which is fine.
    }

    // IMPROVEMENT 3: Added @Ignore to this constructor.
    // This is now a "convenience" constructor for you to use in your code.
    // Room will use the default constructor above.
    @Ignore
    public QuizQuestion(long quizResultId, @NonNull String word, @NonNull String userAnswer, @NonNull String correctAnswer, boolean isCorrect) {
        this.quizResultId = quizResultId;
        this.word = word;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
        this.isCorrect = isCorrect;
    }

    // --- Getters and Setters ---

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // Aliases for adapter compatibility
    public long getQuestionId() {
        return id;
    }

    @NonNull
    public String getQuestionText() {
        return word;
    }

    public long getQuizResultId() {
        return quizResultId;
    }

    public void setQuizResultId(long quizResultId) {
        this.quizResultId = quizResultId;
    }

    @NonNull
    public String getWord() {
        return word;
    }

    public void setWord(@NonNull String word) {
        this.word = word;
    }

    @NonNull
    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(@NonNull String userAnswer) {
        this.userAnswer = userAnswer;
    }

    @NonNull
    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(@NonNull String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}