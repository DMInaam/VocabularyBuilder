package com.example.vocabularybuilder.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// IMPROVEMENT 1: Added a unique index.
// This ensures that two categories cannot have the same name.
@Entity(tableName = "categories", indices = {@Index(value = {"name"}, unique = true)})
public class Category {

    @PrimaryKey(autoGenerate = true)
    private int id;

    // IMPROVEMENT 2: Added @NonNull and @ColumnInfo
    // This makes the database more robust.
    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    // IMPROVEMENT 3 (CRITICAL FIX):
    // You MUST provide a no-argument (default) constructor for Room.
    // Because 'name' is @NonNull, we initialize it to an empty string.
    public Category() {
        this.name = "";
    }

    // This is the constructor you will use in your code.
    // It is "ignored" by Room, which is correct.
    @androidx.room.Ignore
    public Category(@NonNull String name) {
        this.name = name;
    }

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }
}