package br.com.movies_tek.data.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        if (value == null) {
            return new Date(0L);
        }

        return new Date(value);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        if (date == null) {
            return 0L;
        }

        return date.getTime();
    }
}