package com.example.vocabularybuilder.data;

import androidx.room.TypeConverter;
import java.util.Date;

/**
 * This class tells Room how to store a 'Date' object in the database.
 * It converts the Date to a simple 'long' (timestamp) for storage,
 * and converts it back to a 'Date' when reading.
 */
public class DateConverter {

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }
}