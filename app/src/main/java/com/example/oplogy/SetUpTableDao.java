package com.example.oplogy;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SetUpTableDao {
    @Insert
    void insertAll(SetUpTable... setUpTables);
}
