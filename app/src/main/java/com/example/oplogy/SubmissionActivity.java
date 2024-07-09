package com.example.oplogy;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SubmissionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SubmissionAdapter submissionAdapter;
    private final List<SubmissionStudent> studentsList = new ArrayList<>();
    ArrayList<Integer> studentNumbersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submission);

        // 戻るボタンの処理
        ImageView backButton = findViewById(R.id.BackMain_fromSubmission);
        backButton.setOnClickListener(v -> finish());


        // インテントから提出状況の生徒の数を取得
        studentNumbersList=getIntent().getIntegerArrayListExtra("submissionStudents");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        submissionAdapter = new SubmissionAdapter(studentsList);
        recyclerView.setAdapter(submissionAdapter);

        // 生徒のリストを取得
        fetchStudents();
    }
    private void fetchStudents() {
        // インテントから生徒のリストを取得
        ArrayList<SubmissionStudent> submissionStudentsList = getIntent().getParcelableArrayListExtra("submissionStudents");

        // 生徒のリストを反復処理し、それをRecyclerViewに表示
        studentsList.addAll(submissionStudentsList);

        // データが変更されたことをアダプターに通知
        submissionAdapter.notifyDataSetChanged();
    }
}

