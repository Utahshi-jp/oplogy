package com.example.oplogy;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubmissionAdapter extends RecyclerView.Adapter<SubmissionAdapter.StudentViewHolder> {

    private final List<SubmissionStudent> studentsList;

    public SubmissionAdapter(List<SubmissionStudent> students) {
        this.studentsList = students;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_submission, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentViewHolder holder, int position) {
        SubmissionStudent student = studentsList.get(position);
        holder.studentNumberTextView.setText(String.valueOf(student.getStudentNumber()));
        updateStatus(holder, student.isSubmitted());
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    //真偽値に応じて提出済みか未提出をセットする
    private void updateStatus(StudentViewHolder holder, boolean isSubmitted) {
        if (isSubmitted) {
            holder.statusTextView.setText("提出済み");
            setColors(holder, Color.BLACK, Color.WHITE);
        } else {
            holder.statusTextView.setText("未提出");
            setColors(holder, Color.RED, Color.WHITE);
        }
    }

    //真偽値に応じて色をセットする
    private void setColors(StudentViewHolder holder, int backgroundColor, int textColor) {
        holder.statusTextView.setBackgroundColor(backgroundColor);
        holder.statusTextView.setTextColor(textColor);
        holder.studentNumberTextView.setBackgroundColor(backgroundColor);
        holder.studentNumberTextView.setTextColor(textColor);
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        public TextView studentNumberTextView;
        public TextView statusTextView;

        public StudentViewHolder(View view) {
            super(view);
            // レイアウトファイルのTextViewを取得
            studentNumberTextView = view.findViewById(R.id.studentNumberTextView);
            statusTextView = view.findViewById(R.id.statusTextView);
        }
    }
}
