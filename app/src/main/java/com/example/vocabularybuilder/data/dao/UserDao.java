package com.example.vocabularybuilder.data.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.vocabularybuilder.data.model.User;

@Dao
public interface UserDao {

    // This is perfect for registration.
    // onConflict = IGNORE will fail if the username is not unique.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(@NonNull User user);

    // This is great for checking if a user *exists* during registration.
    @Query("SELECT * FROM users WHERE username = :username")
    User findByUsername(@NonNull String username);

    // IMPROVEMENT 1: Added a specific query for checking login credentials.
    // This is more secure and efficient for the login process.
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User getUser(@NonNull String username, @NonNull String password);

    // IMPROVEMENT 2: Added a query to get a user by their ID.
    // This returns LiveData, so your ProfileActivity can *observe*
    // the user's data for any changes.
    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<User> getUserById(int id);

    // IMPROVEMENT 3: Removed findByName(String name).
    // The 'name' column is not unique, so this query could return
    // the wrong user. It's safer to query by 'username' (which is unique)
    // or 'id' (which is the primary key).
}