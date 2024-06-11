package com.example.oplogy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView showText;
    private EditText selectText;

    private Button btnshow;

    private FirebaseFirestore db;
    private FirestoreReception firestoreReception;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        db = FirebaseFirestore.getInstance();
        firestoreReception = new FirestoreReception();
        showText = findViewById(R.id.showText);
        btnshow = findViewById(R.id.btnShow);
        selectText = findViewById(R.id.selectEdit);

        btnshow.setOnClickListener(v -> {
            String classId = selectText.getText().toString();
            firestoreReception.getDocumentsByClassId(Integer.parseInt(classId));
        });
    }

    @Override
    public void onClick(View view) {

    }
}