package com.example.vocabularybuilder.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// IMPROVEMENT 1: Added a unique index.
// This ensures that two words cannot have the same text (e.g., two "hello"s),
// which is critical for a vocabulary app.
@Entity(tableName = "word_table", indices = {@Index(value = {"word"}, unique = true)})
public class Word {

    @PrimaryKey(autoGenerate = true)
    private int id;

    // IMPROVEMENT 2: Added @NonNull and @ColumnInfo.
    // @NonNull ensures these fields can't be null in the database.
    // @ColumnInfo is good practice for all fields.
    @NonNull
    @ColumnInfo(name = "word")
    private String word;

    @NonNull
    @ColumnInfo(name = "meaning")
    private String meaning;

    @NonNull
    @ColumnInfo(name = "part_of_speech")
    private String partOfSpeech;

    @NonNull
    @ColumnInfo(name = "example")
    private String example;

    @NonNull
    @ColumnInfo(name = "cefr_level")
    private String cefrLevel; // (e.g., "A1", "B2", "User Added")

    @ColumnInfo(name = "is_learned")
    private boolean learned;

    @ColumnInfo(name = "is_prebuilt")
    private boolean isPrebuilt; // (true for A1/B2, false for User Added)

    // IMPROVEMENT 3 (CRITICAL FIX):
    // You MUST provide a no-argument (default) constructor for Room.
    // Because String fields are @NonNull, we initialize them to an empty string.
    public Word() {
        this.word = "";
        this.meaning = "";
        this.partOfSpeech = "";
        this.example = "";
        this.cefrLevel = "";
        // booleans default to false, which is correct
    }

    // IMPROVEMENT 4: Added @Ignore to this constructor.
    // This is now a "convenience" constructor for you to use in your code.
    // Room will use the default constructor above.
    @Ignore
    public Word(@NonNull String word, @NonNull String meaning, @NonNull String partOfSpeech, @NonNull String example, @NonNull String cefrLevel, boolean learned, boolean isPrebuilt) {
        this.word = word;
        this.meaning = meaning;
        this.partOfSpeech = partOfSpeech;
        this.example = example;
        this.cefrLevel = cefrLevel;
        this.learned = learned;
        this.isPrebuilt = isPrebuilt;
    }

    // Convenience constructor for pre-built words (hidden from Room)
    @Ignore
    public Word(@NonNull String word, @NonNull String meaning, @NonNull String partOfSpeech, @NonNull String example, @NonNull String cefrLevel) {
        this(word, meaning, partOfSpeech, example, cefrLevel, false, true);
    }

    // Convenience constructor for user-added words (hidden from Room)
    @Ignore
    public Word(@NonNull String word) {
        this(word, "", "", "", "User Added", false, false);
    }

    // --- Getters and Setters ---
    // Room uses these to get/set data.

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getWord() {
        return word;
    }

    public void setWord(@NonNull String word) {
        this.word = word;
    }

    @NonNull
    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(@NonNull String meaning) {
        this.meaning = meaning;
    }

    @NonNull
    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(@NonNull String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    @NonNull
    public String getExample() {
        return example;
    }

    public void setExample(@NonNull String example) {
        this.example = example;
    }

    @NonNull
    public String getCefrLevel() {
        return cefrLevel;
    }

    public void setCefrLevel(@NonNull String cefrLevel) {
        this.cefrLevel = cefrLevel;
    }

    public boolean isLearned() {
        return learned;
    }

    public void setLearned(boolean learned) {
        this.learned = learned;
    }

    public boolean isPrebuilt() {
        return isPrebuilt;
    }

    public void setPrebuilt(boolean prebuilt) {
        this.isPrebuilt = prebuilt;
    }
}