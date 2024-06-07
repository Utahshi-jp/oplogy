package com.example.oplogy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //    ID作成のTextViewとImageView
    private TextView creatUUID;
    private ImageView imageUuid;
    private int previousCreateUUid = 0; //元の画像のインデックス


    //    セットアップのTextViewとImageView
    private TextView setUp;
    private ImageView imageSetup;
    private int previousSetUp = 0; //元の画像のインデックス

    //    セットアップのTextViewとImageView
    private TextView root;
    private ImageView imageRoot;
    private int previousRoot = 0; //元の画像のインデックス

//    未提出・提出済みのボタン
    private Button notSubmission;
    private Button submission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        ID作成用のインテント
        creatUUID = findViewById(R.id.creatUUID);
        creatUUID.setOnClickListener(this);
        imageUuid = findViewById(R.id.imageUuid);


//        セットアップ用のインテント
        setUp = findViewById(R.id.setUp);
        setUp.setOnClickListener(this);
        imageSetup = findViewById(R.id.imageSetup);

//        ルート作成用のインテント
        root = findViewById(R.id.root);
        root.setOnClickListener(this);
        imageRoot = findViewById(R.id.imageRoot);

//        未提出のボタンのインテント
        notSubmission = findViewById(R.id.notSubmission);
        notSubmission.setOnClickListener(this);
//        未提出のボタンのインテント
        submission = findViewById(R.id.submission);
        submission.setOnClickListener(this);

    }


//    クリック処理
    @Override
    public void onClick(View view) {
//        ID作成のクリック処理
        if(view == creatUUID){
            imageUuid.setImageResource(R.drawable.ischecked_uuid);
            Intent toCreateUUID = new Intent(MainActivity.this, CreateUUID.class);
            startActivity(toCreateUUID);

        }
//        セットアップのクリック処理
        if(view == setUp){
            imageSetup.setImageResource(R.drawable.ischecked_uuid);
            Intent toSetup = new Intent(MainActivity.this,SetupActivity.class);
            startActivity(toSetup);

        }
//        ルート作成のクリック処理
        if(view == root){
            imageRoot.setImageResource(R.drawable.pin);
            Intent toRoot = new Intent(MainActivity.this,RootSearchActivity.class);
            startActivity(toRoot);

        }

        if(view == notSubmission){
            Intent toNotsubmission = new Intent(MainActivity.this,SubmissionActivity.class);
            startActivity(toNotsubmission);
        }

        if(view == submission){
            Intent toSubmission = new Intent(MainActivity.this,SubmissionActivity.class);
            startActivity(toSubmission);
        }


    }


}