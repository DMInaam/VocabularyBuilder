package com.example.vocabularybuilder.data.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.vocabularybuilder.data.WordRoomDatabase;
import com.example.vocabularybuilder.data.dao.CategoryDao;
import com.example.vocabularybuilder.data.model.Category;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CategoryRepository {

    private final CategoryDao mCategoryDao;

    public CategoryRepository(Application application) {
        WordRoomDatabase db = WordRoomDatabase.getDatabase(application);
        mCategoryDao = db.categoryDao();
    }

    // --- Write Operations (Async) ---
    // These are one-off actions, so we use CompletableFuture (or void).

    public void insert(@NonNull Category category) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mCategoryDao.insert(category);
        });
    }

    public void deleteCategory(@NonNull String name) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mCategoryDao.deleteCategory(name);
        });
    }

    // --- Read Operations (Observable) ---
    // These return LiveData, which the ViewModel and UI will observe.
    // Room handles the background threading for LiveData queries automatically.

    public LiveData<List<Category>> getAllCategories() {
        return mCategoryDao.getAllCategories();
    }

    public LiveData<Category> getCategoryByName(@NonNull String name) {
        return mCategoryDao.getCategoryByName(name);
    }
}