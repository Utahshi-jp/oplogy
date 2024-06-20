package com.example.oplogy;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {SetUpTable.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    // データベースにアクセスするためのメソッドを提供する
    public abstract SetUpTableDao setUpTableDao();
}