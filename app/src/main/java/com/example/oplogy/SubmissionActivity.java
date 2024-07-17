package com.example.oplogy;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SubmissionActivity extends AppCompatActivity {
    private final List<SubmissionStudent> studentsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SubmissionAdapter submissionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submission);

        // 戻るボタンの処理
        ImageView backButton = findViewById(R.id.BackMain_fromSubmission);
        backButton.setOnClickListener(v -> finish());

        // RecyclerViewとアダプターの初期化
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

        if (submissionStudentsList != null) {
            Log.d("SubmissionActivity", "Size of submissionStudentsList: " + submissionStudentsList.size());

            studentsList.addAll(submissionStudentsList);
        } else {
            Log.e("SubmissionActivity", "submissionStudentsList is null");
            Toast.makeText(this, "生徒のリストが取得できませんでした", Toast.LENGTH_SHORT).show();
        }

        // データが変更されたことをアダプターに通知
        submissionAdapter.notifyDataSetChanged();
    }
}
