package com.example.oplogy;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {QuestionnaireForm.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    // データベースにアクセスするためのメソッドを提供する
    public abstract QuestionnaireFormDao questionnaireFormDao();
}