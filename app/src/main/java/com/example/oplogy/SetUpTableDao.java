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

    //削除処理
    @Query("DELETE FROM SetUpTable")
    void deleteAll();
    //全件取得
    @Query("SELECT * FROM SetUpTable")
    List<SetUpTable> getAll();

    @Query("SELECT * FROM SetUpTable WHERE teacherName = :name LIMIT 1")
    SetUpTable findByName(String name);

    @Query("SELECT totalStudent FROM SetUpTable")
    int getTotalStudent();
    //開始時間と終了時間の取得
    @Query("SELECT startTime FROM SetUpTable")
    String getStartTime();
    @Query("SELECT endTime FROM SetUpTable")
    String getEndTime();
    //教師名の取得
    @Query("SELECT teacherName FROM SetUpTable")
    String getTeacherName();
    @Query("SELECT intervalTime FROM SetUpTable")
    String getIntervalTime();
    @Query("SELECT StartBreakTime FROM SetUpTable")
    String getStartBreakTime();
    @Query("SELECT EndBreakTime FROM SetUpTable")
    String getEndBreakTime();
    //クラスIDの取得
    @Query("SELECT classId FROM SetUpTable")
    int getClassId();
}
