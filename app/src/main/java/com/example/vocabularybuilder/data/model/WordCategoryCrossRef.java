package com.example.vocabularybuilder.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

//
// This is the "Join Table" for your Many-to-Many relationship.
//
// IMPROVEMENT 1: Added @ForeignKey constraints.
// This is a critical improvement for database integrity.
// - onDelete = ForeignKey.CASCADE: If a Word or Category is deleted,
//   all of its links in this table will be automatically deleted too.
//   This prevents "orphaned" data.
//
@Entity(tableName = "word_category_cross_ref",
        primaryKeys = {"wordId", "categoryId"},
        foreignKeys = {
                @ForeignKey(entity = Word.class,
                        parentColumns = "id",
                        childColumns = "wordId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Category.class,
                        parentColumns = "id",
                        childColumns = "categoryId",
                        onDelete = ForeignKey.CASCADE)
        },
        // IMPROVEMENT 2: Added indices for faster queries.
        indices = {@Index(value = "wordId"), @Index(value = "categoryId")}
)
public class WordCategoryCrossRef {

    // IMPROVEMENT 3: Added @ColumnInfo and made fields public final.
    // For a simple data-holding class like this, public final fields
    // are a clean and standard Room practice.
    @ColumnInfo(name = "wordId")
    public final int wordId;

    @ColumnInfo(name = "categoryId")
    public final int categoryId;

    // This is the constructor Room will use.
    public WordCategoryCrossRef(int wordId, int categoryId) {
        this.wordId = wordId;
        this.categoryId = categoryId;
    }

    //
    // CRITICAL FIX: You are *not* required to have a no-argument
    // constructor *if* Room can see a constructor that uses all fields,
    // like the one above. My previous notes on other files were
    // because they had *no* constructor that Room could use.
    // This file's structure is correct.
    //
    // I have removed the unnecessary getters and setters, as Room can
    // access the public final fields directly.
    //
}