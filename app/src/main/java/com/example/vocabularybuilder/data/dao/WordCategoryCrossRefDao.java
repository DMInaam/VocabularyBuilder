package com.example.vocabularybuilder.data.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData; // This import is unused, but harmless
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.vocabularybuilder.data.model.WordCategoryCrossRef;

@Dao
public interface WordCategoryCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(@NonNull WordCategoryCrossRef wordCategoryCrossRef);

    @Query("DELETE FROM word_category_cross_ref WHERE wordId = :wordId AND categoryId = :categoryId")
    void delete(int wordId, int categoryId);

    // --- Count Query (FIXED) ---
    // Returns 'int' and is wrapped in CompletableFuture by the Repository.
    @Query("SELECT COUNT(*) FROM word_category_cross_ref WHERE wordId = :wordId AND categoryId = :categoryId")
    int getCrossRefCount(int wordId, int categoryId);
}
