package com.example.vocabularybuilder.utils;

/**
 * This class holds all constant values, such as SharedPreferences keys,
 * to keep them out of public resource files like strings.xml.
 */
public class Constants {

    // SharedPreferences File
    public static final String PREFERENCE_FILE_KEY = "com.example.vocabularybuilder.PREFERENCE_FILE_KEY";

    // SharedPreferences Keys
    public static final String IS_LOGGED_IN_KEY = "is_logged_in";
    public static final String CURRENT_USERNAME_KEY = "current_username";

    // --- Added keys for streak logic ---
    public static final String LAST_LEARNING_DATE_KEY = "last_learning_date";
    public static final String CURRENT_STREAK_KEY = "current_streak";


    // Private constructor so this class cannot be instantiated
    private Constants() {}
}
