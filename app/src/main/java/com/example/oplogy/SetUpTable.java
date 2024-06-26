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
    public String startTime;
    public String endTime;
    public String intervalTime;
    public String startBreakTime;
    public String endBreakTime;
    public int totalStudent;

    //TODO: ここのコードをあとで実装する。roomにint classIdの作成
    int classId;


    //コンストラクタ
    public SetUpTable(String teacherName, String startPoint, String startTime, String endTime,
                        String intervalTime, String startBreakTime, String endBreakTime, int totalStudent,int classId) {
        this.teacherName = teacherName;
        this.startPoint = startPoint;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.startBreakTime = startBreakTime;
        this.endBreakTime = endBreakTime;
        this.totalStudent = totalStudent;
        this.classId = classId;
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
    public String getStartTime() {
        return startTime;
    }
    public String getEndTime() {
        return endTime;
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
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public void setTotalStudent(int totalStudent) {
        this.totalStudent = totalStudent;
    }
}