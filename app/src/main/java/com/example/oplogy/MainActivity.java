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

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //    ダイアログの宣言
    private AlertDialog alertDialog;

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
        imageUuid.setOnClickListener(this);


//        セットアップ用のインテント
        setUp = findViewById(R.id.setUp);
        setUp.setOnClickListener(this);
        imageSetup = findViewById(R.id.imageSetup);
        imageSetup.setOnClickListener(this);

//        ルート作成用のインテント
        root = findViewById(R.id.root);
        root.setOnClickListener(this);
        imageRoot = findViewById(R.id.imageRoot);
        imageRoot.setOnClickListener(this);

//        提出状況のインテント
        submission = findViewById(R.id.submission);
        submission.setOnClickListener(this);
        imageSubmission = findViewById(R.id.imageSubmission);
        imageSubmission.setOnClickListener(this);

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
        if(view == imageUuid){
            imageUuid.setImageResource(R.drawable.ischecked_uuid);
            showUUIDYesNoDialog();//UUIDを表示するかのダイアログ
        }
//        セットアップのクリック処理
        if(view == setUp){
            imageSetup.setImageResource(R.drawable.ischecked_uuid);
            Intent toSetup = new Intent(MainActivity.this,SetUpActivity.class);
            startActivity(toSetup);
            finish();   // 画面遷移後元の状態に戻す
        }
        if (view == imageSetup){
            imageSetup.setImageResource(R.drawable.ischecked_uuid);
            Intent toSetup = new Intent(MainActivity.this,SetUpActivity.class);
            startActivity(toSetup);
            finish();   // 画面遷移後元の状態に戻す
        }

//        ルート作成のクリック処理
        if(view == root){
            imageRoot.setImageResource(R.drawable.pin);
            fetchDataAndCreateRoute();

        }
        if(view == imageRoot){
            imageRoot.setImageResource(R.drawable.pin);
            fetchDataAndCreateRoute();
        }
//        提出状況のクリック処理
        if(view == submission){
            Intent toSubmission = new Intent(MainActivity.this,SubmissionActivity.class);
            startActivity(toSubmission);
        }
        if(view == imageSubmission){
            Intent toSubmission = new Intent(MainActivity.this,SubmissionActivity.class);
            startActivity(toSubmission);
        }
    }
    //UUIDを表示するかのダイアログ
    private void showUUIDYesNoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // この 'this' が問題でないか確認
        builder.setTitle("クラスID");
        builder.setMessage("あなたのクラスIDを表示しますか？");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String classId = CreateUUID.generateUUID();
                Toast.makeText(MainActivity.this, "クラスID: " + classId, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("DialogNO", "DialogでNoが選ばれました");
            }
        });

        alertDialog = builder.create();
        alertDialog.show();

    }
    //ルート作成の非同期処理
    private void fetchDataAndCreateRoute() {
        //非同期処理の開始
        ExecutorService executor = Executors.newSingleThreadExecutor();

        CountDownLatch latch = new CountDownLatch(2);

        // タスク1: ローカルDBから生徒数を取得
        executor.execute(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "SetUpTable").build();
            SetUpTableDao setUpTableDao = db.setUpTableDao();

            Log.d("MainActivity", "db" + setUpTableDao.getAll());

            int totalStudent = setUpTableDao.getTotalStudent();
            int myDataListSize = firestoreReception.getMyDataListSize();

            runOnUiThread(() -> {
                if (totalStudent != myDataListSize) {
                    showRouteCreationDialog(latch);
                } else {
                    latch.countDown();
                }
            });
        });

        // タスク2: Firestoreからデータを取得
        executor.execute(() -> {
            List<MyDataClass> myDataList = firestoreReception.getMyDataList();
            CreateRoot createRoot = new CreateRoot(MainActivity.this);
            createRoot.receiveData(myDataList);
            latch.countDown();
        });

        new Thread(() -> {
            try {
                latch.await();  // Both tasks must call countDown() before this returns
                runOnUiThread(() -> {
                    Intent toRoot = new Intent(MainActivity.this, Maps.class);
                    startActivity(toRoot);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        executor.shutdown();
    }
    //ルート作成のダイアログ
    private void showRouteCreationDialog(CountDownLatch latch) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("警告")
                .setMessage("人数が足りてませんがそれでもルート作成を行いますか？")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        latch.countDown();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

}