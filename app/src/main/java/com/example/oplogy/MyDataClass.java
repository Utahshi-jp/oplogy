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
    private Long Timezone;
    private String startDateString;
    private String endDateString;

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

    //getter
    public String getPatronName() {
        return patronName;
    }

    //setter
    public void setPatronName(String patronName) {
        this.patronName = patronName;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }

    public List<Timestamp> getFirstDay() {
        return firstDay;
    }

    public void setFirstDay(List<Timestamp> firstDay) {
        this.firstDay = firstDay;
    }

    public int getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(int studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public List<Timestamp> getThirdDay() {
        return thirdDay;
    }

    public void setThirdDay(List<Timestamp> thirdDay) {
        this.thirdDay = thirdDay;
    }

    public List<Timestamp> getSecondDay() {
        return secondDay;
    }

    public void setSecondDay(List<Timestamp> secondDay) {
        this.secondDay = secondDay;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setEndDateString(String endDateString) {
        this.endDateString = endDateString;
    }

    public Long getTimezone() {
        return Timezone;
    }

    public void setTimezone(Long Timezone) {
        this.Timezone = Timezone;
    }

    public String getStartDateString() {
        return startDateString;
    }
    public void setStartDateString(String startDateString) {
        this.startDateString = startDateString;
    }
}
