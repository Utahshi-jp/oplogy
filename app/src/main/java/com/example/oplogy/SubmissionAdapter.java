package com.example.oplogy;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SubmissionAdapter extends RecyclerView.Adapter<SubmissionAdapter.StudentViewHolder> {

    private List<SubmissionStudent> students;

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

    public SubmissionAdapter(List<SubmissionStudent> students) {
        this.students = students;
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_submission, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentViewHolder holder, int position) {
        SubmissionStudent student = students.get(position);
        holder.studentNumberTextView.setText(String.valueOf(student.getStudentNumber()));
        // 提出済みかどうかで表示を変える
        if (student.isSubmitted()) {
            holder.statusTextView.setText("提出済み");
            holder.statusTextView.setBackgroundColor(Color.BLACK);
            holder.statusTextView.setTextColor(Color.WHITE);
            holder.studentNumberTextView.setBackgroundColor(Color.BLACK);
            holder.studentNumberTextView.setTextColor(Color.WHITE);
        } else {
            holder.statusTextView.setText("未提出");
            holder.statusTextView.setBackgroundColor(Color.RED);
            holder.statusTextView.setTextColor(Color.WHITE);
            holder.studentNumberTextView.setBackgroundColor(Color.RED);
            holder.studentNumberTextView.setTextColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return students.size();
    }
}
