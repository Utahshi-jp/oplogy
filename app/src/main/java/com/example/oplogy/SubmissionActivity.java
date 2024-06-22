package com.example.oplogy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.oplogy.databinding.SubmissionBinding;

import java.util.ArrayList;
import java.util.List;

public class SubmissionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SubmissionAdapter submissionAdapter;
    private List<SubmissionStudent> students = new ArrayList<>();
    ArrayList<Integer> studentNumbers = new ArrayList<>();
    private int totalStudent = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submission);

        ImageView backButton = findViewById(R.id.BackMain_fromSubmission);
        backButton.setOnClickListener(v -> {
            finish();
        });


        studentNumbers=getIntent().getIntegerArrayListExtra("submissionStudents");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        submissionAdapter = new SubmissionAdapter(students);
        recyclerView.setAdapter(submissionAdapter);

        fetchStudents();
    }
    private void fetchStudents() {
    // インテントから生徒のリストを取得
    ArrayList<SubmissionStudent> submissionStudents = getIntent().getParcelableArrayListExtra("submissionStudents");

    // 生徒のリストを反復処理し、それをRecyclerViewに表示
    for (SubmissionStudent student : submissionStudents) {
        students.add(student);
    }

    // データが変更されたことをアダプターに通知
    submissionAdapter.notifyDataSetChanged();
}
}

