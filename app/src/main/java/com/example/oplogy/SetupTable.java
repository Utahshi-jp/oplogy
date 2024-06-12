package com.example.oplogy;


import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.Date;
import java.util.List;

@Entity(tableName = "teacher_table")
public class SetupTable {
    //フィールド
    @PrimaryKey()
    public int classId;
    @ColumnInfo(name = "teacher_name")
    public String teacherName;
    @ColumnInfo(name = "startpoint")
    public String startpoint;
    @ColumnInfo(name = "subject")
    public String startTime;
    @ColumnInfo(name = "end_time")
    public String endTime;
    @ColumnInfo(name = "break_time")
    public String breakTime;
    @ColumnInfo(name = "total_students")
    public int totalStudents;

    //コンストラクタ
    public SetupTable(int classId,String startpoint , String teacherName, String startTime, String endTime, String breakTime, int totalStudents) {
        this.classId = classId;
        this.startpoint = startpoint;
        this.teacherName = teacherName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.breakTime = breakTime;
        this.totalStudents = totalStudents;
    }

    //ゲッター
    public int getClassId() {
        return classId;
    }
    public String getTeacherName() {
        return teacherName;
    }
    public String getStartTime() {
        return startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public String getBreakTime() {
        return breakTime;
    }
    public int getTotalStudents() {
        return totalStudents;
    }
    public String getStartpoint() {
        return startpoint;
    }
    //セッター
    public void setClassId(int classId) {
        this.classId = classId;
    }
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public void setBreakTime(String breakTime) {
        this.breakTime = breakTime;
    }
    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }
    public void setStartpoint(String startpoint) {
        this.startpoint = startpoint;
    }
}