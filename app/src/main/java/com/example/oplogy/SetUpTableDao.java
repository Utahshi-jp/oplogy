package com.example.oplogy;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SetUpTableDao {
    @Insert
    void insertAll(SetUpTable... setUpTables);
    //更新処理
    @Update
    void update(SetUpTable setUpTable);
    //名前が一致しているかの確認
    @Query("SELECT * FROM SetUpTable WHERE teacherName = :name LIMIT 1")
    SetUpTable findByName(String name);
}
