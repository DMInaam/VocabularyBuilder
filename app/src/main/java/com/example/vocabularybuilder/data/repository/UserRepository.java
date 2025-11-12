package com.example.vocabularybuilder.data.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

// IMPROVEMENT 1: Import the new Constants class
import com.example.vocabularybuilder.utils.Constants;
import com.example.vocabularybuilder.data.WordRoomDatabase;
import com.example.vocabularybuilder.data.dao.UserDao;
import com.example.vocabularybuilder.data.model.User;

import java.util.concurrent.CompletableFuture;

public class UserRepository {

    private final UserDao mUserDao;
    private final SharedPreferences mSharedPreferences;
    private final MutableLiveData<User> mCurrentUser = new MutableLiveData<>();
    // Removed mApplication as it's no longer needed after constructor

    public UserRepository(Application application) {
        WordRoomDatabase db = WordRoomDatabase.getDatabase(application);
        mUserDao = db.userDao();

        // IMPROVEMENT 1: Use the Constants class for the file key
        mSharedPreferences = application.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);

        // Load the current user if logged in
        loadCurrentUser();
    }

    private void loadCurrentUser() {
        // IMPROVEMENT 1: Use the Constants class for keys
        boolean isLoggedIn = mSharedPreferences.getBoolean(Constants.IS_LOGGED_IN_KEY, false);
        if (isLoggedIn) {
            String currentUsername = mSharedPreferences.getString(Constants.CURRENT_USERNAME_KEY, null);
            if (currentUsername != null) {
                // This async query is correct
                CompletableFuture.supplyAsync(() -> mUserDao.findByUsername(currentUsername), WordRoomDatabase.databaseWriteExecutor)
                        .thenAccept(user -> {
                            if (user != null) {
                                mCurrentUser.postValue(user);
                            }
                        });
            }
        }
    }

    public LiveData<User> getCurrentUser() {
        return mCurrentUser;
    }

    public CompletableFuture<Long> register(@NonNull User user) {
        return CompletableFuture.supplyAsync(() -> mUserDao.insert(user), WordRoomDatabase.databaseWriteExecutor);
    }

    public CompletableFuture<User> findByUsername(@NonNull String username) {
        return CompletableFuture.supplyAsync(() -> mUserDao.findByUsername(username), WordRoomDatabase.databaseWriteExecutor);
    }

    // IMPROVEMENT 2 (DAO MISMATCH):
    // Removed the 'findByName' method. It does not exist in UserDao.java
    // because the 'name' column is not unique.

    // IMPROVEMENT 3 (LOGIN LOGIC):
    // This is the new public login method. It asynchronously checks
    // credentials against the database.
    public CompletableFuture<User> login(@NonNull String username, @NonNull String password) {
        return CompletableFuture.supplyAsync(() -> mUserDao.getUser(username, password), WordRoomDatabase.databaseWriteExecutor)
                .thenApply(user -> {
                    if (user != null) {
                        // If user is found, save login state
                        setLoggedInUser(user);
                    }
                    return user; // Will be null if login failed, or the User object if success
                });
    }

    public void logout() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        // IMPROVEMENT 1: Use the Constants class for keys
        editor.putBoolean(Constants.IS_LOGGED_IN_KEY, false);
        editor.remove(Constants.CURRENT_USERNAME_KEY);
        editor.apply();
        mCurrentUser.postValue(null);
    }

    // This is the private method that saves login state,
    // renamed from your original public 'login' method.
    private void setLoggedInUser(@NonNull User user) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        // IMPROVEMENT 1: Use the Constants class for keys
        editor.putBoolean(Constants.IS_LOGGED_IN_KEY, true);
        editor.putString(Constants.CURRENT_USERNAME_KEY, user.getUsername());
        editor.apply();
        mCurrentUser.postValue(user);
    }
}