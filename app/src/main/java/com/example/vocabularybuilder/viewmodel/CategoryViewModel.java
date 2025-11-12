package com.example.vocabularybuilder.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.vocabularybuilder.data.model.Category;
import com.example.vocabularybuilder.data.repository.CategoryRepository;
import java.util.List;
// No longer need CompletableFuture
// import java.util.concurrent.CompletableFuture;

public class CategoryViewModel extends AndroidViewModel {
    private final CategoryRepository mRepository;
    private final LiveData<List<Category>> mAllCategories;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        mRepository = new CategoryRepository(application);
        // Initialize the LiveData object once
        mAllCategories = mRepository.getAllCategories();
    }

    // --- Write Operations ---
    // These are "fire-and-forget" methods.
    // The ViewModel doesn't need to return anything, as the
    // Repository handles running it on a background thread.

    public void insert(@NonNull Category category) {
        mRepository.insert(category);
    }

    public void deleteCategory(@NonNull String name) {
        mRepository.deleteCategory(name);
    }

    // --- Read Operations ---
    // These return the LiveData objects from the repository.
    // Your UI will "observe" these for changes.

    public LiveData<List<Category>> getAllCategories() {
        return mAllCategories;
    }

    public LiveData<Category> getCategoryByName(@NonNull String name) {
        return mRepository.getCategoryByName(name);
    }
}