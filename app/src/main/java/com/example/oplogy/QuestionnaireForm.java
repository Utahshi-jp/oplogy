package com.example.oplogy;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class QuestionnaireForm {
    //主キー
    @PrimaryKey(autoGenerate = true)
    public int id;

    //その他フィールド
    public int classId;
    public String patronName;
    public String address;
    public String firstDay;
    public int studentNumber;
    public String childName;
    public String thirdDay;
    public String secondDay;
}
