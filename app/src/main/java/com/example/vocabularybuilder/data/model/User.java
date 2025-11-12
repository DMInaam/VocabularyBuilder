package com.example.vocabularybuilder.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// This is a great Room entity. It correctly sets up the table name
// and adds a unique index to the username, which is a critical optimization.
@Entity(tableName = "users", indices = {@Index(value = {"username"}, unique = true)})
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;

    // IMPROVEMENT 1: Added @NonNull.
    // This tells Room that these columns cannot be null in the database,
    // which adds a powerful layer of data integrity.
    @NonNull
    @ColumnInfo(name = "name") // @ColumnInfo is good practice, though not required
    private String name;

    @NonNull
    @ColumnInfo(name = "username")
    private String username;

    @NonNull
    @ColumnInfo(name = "password")
    private String password;

    // Default constructor required by Room.
    // IMPROVEMENT 2: Because the fields are @NonNull, the default
    // constructor must initialize them to a default non-null value.
    public User() {
        this.name = "";
        this.username = "";
        this.password = "";
    }

    @Ignore
    public User(@NonNull String username, @NonNull String password) {
        this.name = ""; // Initialize name even in this constructor
        this.username = username;
        this.password = password;
    }

    @Ignore
    // Constructor with name
    public User(@NonNull String name, @NonNull String username, @NonNull String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    // --- Getters and Setters ---
    // Room uses these to create the object from database data.

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

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }
}