package com.example.vocabularybuilder.data.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.vocabularybuilder.data.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    // --- Write Operations ---
    // These are one-off actions, so they don't need LiveData.
    // The Repository will call these on a background thread.

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(@NonNull Category category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(@NonNull List<Category> categories);

    @Query("DELETE FROM categories WHERE name = :name")
    void deleteCategory(@NonNull String name);

    // --- Read Operations ---
    // These return LiveData to be observed by the UI.
    // Room automatically runs these on a background thread.

    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * FROM categories WHERE name = :name")
    LiveData<Category> getCategoryByName(@NonNull String name);
}
