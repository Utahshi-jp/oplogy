package com.example.oplogy;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QuestionnaireFormDao {

    @Query("SELECT * FROM QuestionnaireForm")
    List<QuestionnaireForm> getAll();

    // このメソッドは、QuestionnaireFormのリストを受け取り、それらをデータベースに挿入します。
    @Insert
    void insertAll(QuestionnaireForm... questionnaireForms);
}
