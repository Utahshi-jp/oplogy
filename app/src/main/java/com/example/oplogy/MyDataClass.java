package com.example.oplogy;

import com.google.firebase.Timestamp;

import java.util.List;

public class MyDataClass {
    String patronName;
    int classId;
    List<String> address;
    List<Timestamp> firstDay;
    int studentNumber;
    String childName;
    List<Timestamp> thirdDay;
    List<Timestamp> secondDay;
    double latitude;

    public MyDataClass(String patronName, int classId, List<String> address, List<Timestamp> firstDay, int studentNumber, String childName, List<Timestamp> thirdDay, List<Timestamp> secondDay) {
        this.patronName = patronName;
        this.classId = classId;
        this.address = address;
        this.firstDay = firstDay;
        this.studentNumber = studentNumber;
        this.childName = childName;
        this.thirdDay = thirdDay;
        this.secondDay = secondDay;
    }

    @Override
    public String toString() {
        return "MyDataClass{" +
                "patronName='" + patronName + '\'' +
                ", classId=" + classId +
                ", address=" + address +
                ", firstDay=" + firstDay +
                ", studentNumber=" + studentNumber +
                ", childName='" + childName + '\'' +
                ", thirdDay=" + thirdDay +
                ", secondDay=" + secondDay +
                '}';
    }
}
