package com.example.vocabularybuilder.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.vocabularybuilder.data.model.User;
import com.example.vocabularybuilder.data.repository.UserRepository;

import java.util.concurrent.CompletableFuture;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository mRepository;
    private final LiveData<User> mCurrentUser;

    public UserViewModel(@NonNull Application application) {
        super(application);
        mRepository = new UserRepository(application);
        mCurrentUser = mRepository.getCurrentUser();
    }

    public LiveData<User> getCurrentUser() {
        return mCurrentUser;
    }

    // --- IMPROVEMENT (FIXED): ---
    // Changed signature to match the secure login method in the repository.
    public CompletableFuture<User> login(@NonNull String username, @NonNull String password) {
        return mRepository.login(username, password);
    }

    public CompletableFuture<User> findByUsername(@NonNull String username) {
        return mRepository.findByUsername(username);
    }

    // --- IMPROVEMENT (FIXED): ---
    // Removed findByName(name) as it no longer exists in the repository.

    public void logout() {
        mRepository.logout();
    }

    public CompletableFuture<Long> register(@NonNull User user) {
        return mRepository.register(user);
    }
}