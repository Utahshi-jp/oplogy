package com.example.oplogy;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SetUpTable {
    //主キー
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String teacherName;
    public String startPoint;
    public String endPoint;
    public String startTime;
    public String endTime;
    public String breakStartTime;
    public String breakEndTime;
    public int totalStudent;

    //コンストラクタ
    public SetUpTable(String teacherName, String startPoint, String endPoint, String startTime, String endTime, String breakStartTime,String breakEndTime, int totalStudent) {
        this.teacherName = teacherName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.startTime = startTime;
        this.endTime = endTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
        this.totalStudent = totalStudent;
    }
    //getter
    public int getId() {
        return id;
    }
    public String getTeacherName() {
        return teacherName;
    }
    public String getStartPoint() {
        return startPoint;
    }
    public String getEndPoint() {
        return endPoint;
    }
    public String getStartTime() {
        return startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public String getBreakStartTime() {
        return breakStartTime;
    }
    public String getBreakEndTime() {
        return breakEndTime;
    }
    public int getTotalStudent() {
        return totalStudent;
    }
    //setter
    public void setId(int id) {
        this.id = id;
    }
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }
    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public void setBreakStartTime(String breakStartTime) {
        this.breakStartTime = breakStartTime;
    }
    public void setBreakEndTime(String breakEndTime) {
        this.breakEndTime = breakEndTime;
    }
    public void setTotalStudent(int totalStudent) {
        this.totalStudent = totalStudent;
    }
}