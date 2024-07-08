package com.example.oplogy;

import androidx.room.TypeConverter;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Converters {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.JAPAN);


    // タイムスタンプを文字列(yyyy-mm-dd,日時)に変換
    @TypeConverter
    public static List<String> fromTimestampList(List<Timestamp> timestamps) {
        List<String> strings = new ArrayList<>();
        for (Timestamp timestamp : timestamps) {
            strings.add(format.format(timestamp.toDate()));
        }
        return strings;
    }

    @TypeConverter
    public static List<Timestamp> toTimestampList(List<String> strings) {
        List<Timestamp> timestamps = new ArrayList<>();
        for (String string : strings) {
            try {
                Date date = format.parse(string);
                timestamps.add(new Timestamp(date));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return timestamps;
    }
}