package com.example.oplogy;
import android.os.Parcel;
import android.os.Parcelable;

public class SubmissionStudent implements Parcelable {
    private int studentNumberInt;
    private boolean submitted;

    public SubmissionStudent(int studentNumber, boolean submitted) {
        this.studentNumberInt = studentNumber;
        this.submitted = submitted;
    }

    protected SubmissionStudent(Parcel in) {
        studentNumberInt = in.readInt();
        submitted = in.readByte() != 0;
    }

    public static final Creator<SubmissionStudent> CREATOR = new Creator<SubmissionStudent>() {
        @Override
        public SubmissionStudent createFromParcel(Parcel in) {
            return new SubmissionStudent(in);
        }

        @Override
        public SubmissionStudent[] newArray(int size) {
            return new SubmissionStudent[size];
        }
    };

    public int getStudentNumber() {
        return studentNumberInt;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(studentNumberInt);
        parcel.writeByte((byte) (submitted ? 1 : 0));
    }
}