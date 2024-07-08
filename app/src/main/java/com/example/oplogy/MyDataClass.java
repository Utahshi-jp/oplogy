package com.example.oplogy;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;

import java.util.List;

public class MyDataClass {

    private String patronNameString;
    private int classIdInt;
    private List<String> addressList;
    private List<Timestamp> firstDayList;
    private int studentNumberInt;
    private String childNameString;
    private List<Timestamp> thirdDayList;
    private List<Timestamp> secondDayList;
    private double latitudeDouble;
    private Long timezoneLong;
    private String startDateString;
    private String endDateString;
    private String assignedStartTimeString;
    private int assignedIndexInt;
    private boolean linkingBoolean;
    private String parentStartTimeString;
    private String parentEndTimeString;
    private int scheduleInt;
    private String secondDayStartDateString;
    private String secondDayEndDateString;
    private Long secondDayTimezoneLong;
    private String secondDayParentStartTimeString;
    private String secondDayParentEndTimeString;
    private LatLng latLng;


    public MyDataClass(String patronName, int classId, List<String> address, List<Timestamp> firstDay, int studentNumber, String childName, List<Timestamp> thirdDay, List<Timestamp> secondDay) {
        this.patronNameString = patronName;
        this.classIdInt = classId;
        this.addressList = address;
        this.firstDayList = firstDay;
        this.studentNumberInt = studentNumber;
        this.childNameString = childName;
        this.thirdDayList = thirdDay;
        this.secondDayList = secondDay;
    }

    @Override
    public String toString() {
        return "MyDataClass{" +
                "patronName='" + patronNameString + '\'' +
                ", classId=" + classIdInt +
                ", address=" + addressList +
                ", firstDay=" + firstDayList +
                ", studentNumber=" + studentNumberInt +
                ", childName='" + childNameString + '\'' +
                ", thirdDay=" + thirdDayList +
                ", secondDay=" + secondDayList +
                '}';
    }

    //getter
    public String getPatronName() {
        return patronNameString;
    }

    //setter
    public void setPatronName(String patronNameString) {
        this.patronNameString = patronNameString;
    }

    public int getClassId() {
        return classIdInt;
    }

    public void setClassId(int classId) {
        this.classIdInt = classId;
    }

    public List<String> getAddress() {
        return addressList;
    }

    public void setAddress(List<String> address) {
        this.addressList = address;
    }

    public List<Timestamp> getFirstDay() {
        return firstDayList;
    }

    public void setFirstDay(List<Timestamp> firstDay) {
        this.firstDayList = firstDay;
    }

    public int getStudentNumber() {
        return studentNumberInt;
    }

    public void setStudentNumber(int studentNumber) {
        this.studentNumberInt = studentNumber;
    }

    public String getChildName() {
        return childNameString;
    }

    public void setChildName(String childName) {
        this.childNameString = childName;
    }

    public List<Timestamp> getThirdDay() {
        return thirdDayList;
    }

    public void setThirdDay(List<Timestamp> thirdDay) {
        this.thirdDayList = thirdDay;
    }

    public List<Timestamp> getSecondDay() {
        return secondDayList;
    }

    public void setSecondDay(List<Timestamp> secondDay) {
        this.secondDayList = secondDay;
    }

    public double getLatitude() {
        return latitudeDouble;
    }

    public void setLatitude(double latitudeDouble) {
        this.latitudeDouble = latitudeDouble;
    }

    public void setEndDateString(String endDateString) {
        this.endDateString = endDateString;
    }

    public Long getTimezone() {
        return timezoneLong;
    }

    public void setTimezone(Long timezoneLong) {
        this.timezoneLong = timezoneLong;
    }

    public String getStartDateString() {
        return startDateString;
    }

    public void setStartDateString(String startDateString) {
        this.startDateString = startDateString;
    }


    public void setAssignedStartTime(String assignedStartTime) {
        this.assignedStartTimeString = assignedStartTime;

    }

    public String getAssignedEndTime() {
        return assignedStartTimeString;
    }

    public void setAssignedIndex(int assignedIndexInt) {
        this.assignedIndexInt = assignedIndexInt;
    }

    public int getAssignedIndex() {
        return assignedIndexInt;
    }

    public void setLinking(boolean linking) {
        this.linkingBoolean = linking;
    }

    public boolean getLinking() {
        return linkingBoolean;
    }

    public void setParentStartTimeString(String parentStartTimeString) {
        this.parentStartTimeString = parentStartTimeString;
    }

    public void setParentEndTimeString(String parentEndTimeString) {
        this.parentEndTimeString = parentEndTimeString;
    }

    public String getParentStartTimeString() {
        return parentStartTimeString;
    }

    public String getParentEndTimeString() {
        return parentEndTimeString;
    }

    public void setSchedule(int scheduleInt) {
        this.scheduleInt = scheduleInt;
    }

    public int getSchedule() {
        return scheduleInt;
    }

    public void setSecondDayStartDateString(String secondDayStartDateString) {
        this.secondDayStartDateString = secondDayStartDateString;
    }

    public void setSecondDayEndDateString(String secondDayEndDateString) {
        this.secondDayEndDateString = secondDayEndDateString;
    }

    public void setSecondDayTimezone(Long secondDayTimezone) {
        this.secondDayTimezoneLong = secondDayTimezone;
    }

    public Long getSecondDayTimezone() {
        return secondDayTimezoneLong;
    }


    public void setSecondDayParentStartTimeString(String secondDayParentStartTimeString) {
        this.secondDayParentStartTimeString = secondDayParentStartTimeString;
    }

    public void setSecondDayParentEndTimeString(String secondDayParentEndTimeString) {
        this.secondDayParentEndTimeString = secondDayParentEndTimeString;
    }

    public String getSecondDayParentStartTimeString() {
        return secondDayParentStartTimeString;
    }

    public String getSecondDayParentEndTimeString() {
        return secondDayParentEndTimeString;
    }

    public String getSecondDayStartDateString() {
        return secondDayStartDateString;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng=latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }
}