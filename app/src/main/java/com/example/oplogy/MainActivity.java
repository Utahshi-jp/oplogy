package com.example.oplogy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //    ID作成のTextViewとImageView
    private TextView creatUUID;
    private ImageView imageUuid;


    //    セットアップのTextViewとImageView
    private TextView setUp;
    private ImageView imageSetup;

    //    セットアップのTextViewとImageView
    private TextView root;
    private ImageView imageRoot;
    //    提出状況のTextViewとImageView
    private TextView submission;
    private ImageView imageSubmission;

    //firestoreの受信関連
    private FirebaseFirestore db;
    private FirestoreReception firestoreReception;


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
//        提出状況のインテント
        submission = findViewById(R.id.submission);
        submission.setOnClickListener(this);
        imageSubmission = findViewById(R.id.imageSubmission);

//      firestoreの受信関連
        db = FirebaseFirestore.getInstance();
        firestoreReception = new FirestoreReception();

        firestoreReception.getDocumentsByClassId(100);

    }


//    クリック処理
    @Override
    public void onClick(View view) {
//        ID作成のクリック処理
        if(view == creatUUID){
            imageUuid.setImageResource(R.drawable.ischecked_uuid);
            showUUIDYesNoDialog();//UUIDを表示するかのダイアログ

        }
//        セットアップのクリック処理
        if(view == setUp){
            imageSetup.setImageResource(R.drawable.ischecked_uuid);
            Intent toSetup = new Intent(MainActivity.this,SetUpActivity.class);
            startActivity(toSetup);

        }
//        ルート作成のクリック処理
        if(view == root){
            imageRoot.setImageResource(R.drawable.pin);

            ExecutorService executor = Executors.newSingleThreadExecutor();

            executor.execute(() -> {
                AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "SetUpTable").build();
                SetUpTableDao setUpTableDao = db.setUpTableDao();

                // データベースに登録されている生徒の数、formにデータを送信した生徒の合計数をを取得
                int totalStudent = setUpTableDao.getTotalStudent();
                int myDataListSize = firestoreReception.myDataList.size();

                runOnUiThread(() -> {
                    if (totalStudent != myDataListSize) {
                        // 値が一致しない場合、ダイアログを表示
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("警告")
                                .setMessage("人数が足りてませんがそれでもルート作成を行いますか？")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent toRoot = new Intent(MainActivity.this,Maps.class);
                                        startActivity(toRoot);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else {
                        Intent toRoot = new Intent(MainActivity.this,Maps.class);
                        startActivity(toRoot);
                    }
                });
            });
        }
//        提出状況のクリック処理
        if(view == submission){
            Intent toSubmission = new Intent(MainActivity.this,SubmissionActivity.class);
            startActivity(toSubmission);
        }
    }
    private void showUUIDYesNoDialog() {
        //ダイアログの表示
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("クラスID");
        builder.setMessage("あなたのクラスIDを表示しますか？");

        //YESのときは初回はUUIDを生成、表示
        //二回目以降は保存されたUUIDを表示
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String classId = CreateUUID.generateUUID();//classIDにuuidが入ってる
                Toast.makeText(MainActivity.this, "クラスID: " + classId, Toast.LENGTH_SHORT).show();//テスト用
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("DialogNO","DialogでNoが選ばれました");
            }
        });
        builder.show();
    }
}