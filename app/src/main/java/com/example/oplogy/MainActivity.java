package com.example.oplogy;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
    private FirestoreReception_classIdDatabase firestoreReception_classIdDatabase;

    //取得するためのクラスID
    private int classId;
    private String address;


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
        Log.d("MainActivity", "geocodeAddress");


        //TODO:classIdの初期値を取得
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                AppDatabase db = getDatabaseInstance();
                SetUpTableDao setUpTableDao = db.setUpTableDao();
                classId = setUpTableDao.getClassId();
                firestoreReception.getDocumentsByClassId(classId);
            } catch (Exception e) {
                //無視して続行
                e.printStackTrace();
            }

        });

    }



    //    クリック処理
    @Override
    public void onClick(View view) {
//        ID作成のクリック処理
        if (view == creatUUID) {
            imageUuid.setImageResource(R.drawable.ischecked_uuid);
            showUUIDYesNoDialog();//UUIDを表示するかのダイアログ
        }
        if (view == imageUuid) {
            imageUuid.setImageResource(R.drawable.ischecked_uuid);
            showUUIDYesNoDialog();//UUIDを表示するかのダイアログ
        }
//        セットアップのクリック処理
        if (view == setUp) {
            imageSetup.setImageResource(R.drawable.ischecked_uuid);
            Intent toSetup = new Intent(MainActivity.this, SetUpActivity.class);
            toSetup.putExtra("classId", classId);
            startActivity(toSetup);
            finish();   // 画面遷移後元の状態に戻す
        }
        if (view == imageSetup) {
            imageSetup.setImageResource(R.drawable.ischecked_uuid);
            Intent toSetup = new Intent(MainActivity.this, SetUpActivity.class);
            startActivity(toSetup);
            finish();   // 画面遷移後元の状態に戻す
        }

//        ルート作成のクリック処理
        if (view == root) {
            imageRoot.setImageResource(R.drawable.pin);
            fetchDataAndCreateRoute();

        }
        if (view == imageRoot) {
            imageRoot.setImageResource(R.drawable.pin);
            fetchDataAndCreateRoute();
        }
//        提出状況のクリック処理
        if (view == submission) {
            ArrayList<SubmissionStudent> submissionStudents = getSubmissionStudents();
            Intent toSubmission = new Intent(MainActivity.this, SubmissionActivity.class);
            toSubmission.putParcelableArrayListExtra("submissionStudents", submissionStudents);
            startActivity(toSubmission);
        }
        if (view == imageSubmission) {
            ArrayList<SubmissionStudent> submissionStudents = getSubmissionStudents();
            Intent toSubmission = new Intent(MainActivity.this, SubmissionActivity.class);
            toSubmission.putParcelableArrayListExtra("submissionStudents", submissionStudents);
            startActivity(toSubmission);
        }
    }

    //IDに関する処理
    private void showUUIDYesNoDialog() {
        firestoreReception_classIdDatabase = new FirestoreReception_classIdDatabase();
        List<Integer> classIdList = firestoreReception_classIdDatabase.getAllDocumentsFromClassIdDatabase();


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("クラスID");
        builder.setMessage("あなたのクラスIDを表示/もしくは新規で作成しますか？");

        builder.setPositiveButton("作成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                classId = CreateUUID.generateUUID(classIdList);
                // 生成されたクラスIDを表示するメソッド
                showClassIdDialog("生成されたクラスID",classId);
            }
        });
        builder.setNegativeButton("表示", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //roomを扱うため非同期処理
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    // 現在のクラスIDを取得
                    int currentClassId = getCurrentClassIdFromRoom();
                    runOnUiThread(() -> {
                        // 現在のクラスIDを表示するダイアログ
                        showClassIdDialog("現在のクラスID",currentClassId);
                    });
                });
                executor.shutdown();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();

    }
    private int getCurrentClassIdFromRoom() {
        AppDatabase db = getDatabaseInstance();
        SetUpTableDao setUpTableDao = db.setUpTableDao();

        // 現在のクラスIDを取得
        return setUpTableDao.getClassId();
    }
    //クラスIDを表示するダイアログ
    private void showClassIdDialog(String title, int classId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage("クラスID: " + classId);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //ルート作成の非同期処理
    private void fetchDataAndCreateRoute() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            AppDatabase db = getDatabaseInstance();            SetUpTableDao setUpTableDao = db.setUpTableDao();
            int totalStudent = setUpTableDao.getTotalStudent();
            int myDataListSize = firestoreReception.getMyDataListSize();

            //総生徒数と提出済みになっている生徒の数が一致するかの確認
            runOnUiThread(() -> {
                if (totalStudent != myDataListSize) {
                    //未提出者がいることの警告ダイアログ
                    showRouteCreationDialog();
                } else {
                    //ルート作成
                    createRoute(executor);
                }
            });
        });

        // `fetchDataAndCreateRoute`メソッド内では、shutdownを呼び出さない
    }

    private void showRouteCreationDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("警告")
                .setMessage("人数が足りてませんがそれでもルート作成を行いますか？")
                .setPositiveButton("OK", (dialog, which) -> {
                    // 新しいExecutorServiceを作成してタスクを実行
                    ExecutorService dialogExecutor = Executors.newSingleThreadExecutor();
                    createRoute(dialogExecutor);
                    dialogExecutor.shutdown();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void createRoute(ExecutorService executor) {
        executor.execute(() -> {
            List<MyDataClass> myDataList = null;
            while (myDataList == null) {
                myDataList = firestoreReception.getMyDataList();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            //final宣言することによって、スレッドセーフになる(ラムダ式内で使えるようにする)
            final List<MyDataClass> finalMyDataList = myDataList;
            CreateSchedule createSchedule = new CreateSchedule(MainActivity.this);
            Boolean notDuplicates = createSchedule.receiveData(myDataList, getApplicationContext());

            runOnUiThread(() -> {
                if (notDuplicates) {
                    Log.d("MainActivity", "スケジュール作成成功");
                    saveMyDataList(myDataList);
                    Intent toRoot = new Intent(MainActivity.this, Maps.class);
                    startActivity(toRoot);
                } else {
                    //保護者の重複による警告ダイアログ
                    showErrorDialog(finalMyDataList);
                }
            });

            // createRouteの最後にexecutorをシャットダウン
            executor.shutdown();
        });
    }
    private void saveMyDataList(List<MyDataClass> myDataList) {
        // 共有プリファレンスのインスタンスを取得
        SharedPreferences sharedPreferences = getSharedPreferences("MyDataList", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // MyDataListをJSON形式に変換
        Gson gson = new Gson();
        String json = gson.toJson(myDataList);

        // JSON形式のデータを共有プリファレンスに保存
        editor.putString("myDataList", json);
        editor.apply();
    }

    private void showErrorDialog(List<MyDataClass> myDataList) {
        List<Integer> studentNumbers = new ArrayList<>();
        for (MyDataClass data : myDataList) {
            if (data.getSchedule() == 0) {
                studentNumbers.add(data.getStudentNumber());
            }
        }
        StringBuilder message = new StringBuilder("保護者の重複が重大でルート作成ができません。調整してください。\n出席番号: ");
        for (int i = 0; i < studentNumbers.size(); i++) {
            message.append(studentNumbers.get(i));
            if (i < studentNumbers.size() - 1) {
                message.append(", ");
            }
        }
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("警告")
                .setMessage(message.toString())
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }


    private AppDatabase getDatabaseInstance() {
        return Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "SetUpTable").build();
    }



    //提出状況の取得
    private ArrayList<SubmissionStudent> getSubmissionStudents() {
        ArrayList<SubmissionStudent> submissionStudents = new ArrayList<>();
        List<MyDataClass> myDataList = firestoreReception.getMyDataList();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(1);

        executor.execute(() -> {
            // 1. Roomデータベースから全生徒数を取得
            AppDatabase db = getDatabaseInstance();
            SetUpTableDao setUpTableDao = db.setUpTableDao();
            int totalStudent = setUpTableDao.getTotalStudent();
            // 2. Firestoreから生徒番号のリストを取得
            ArrayList<Integer> firestoreStudentNumbers = new ArrayList<>();
            for (MyDataClass myData : myDataList) {
                int studentNumber = myData.getStudentNumber();
                firestoreStudentNumbers.add(studentNumber);
            }

            // 3. SubmissionStudentオブジェクトのリストを作成
            for (int i = 1; i <= totalStudent; i++) {
                boolean submitted = firestoreStudentNumbers.contains(i);
                submissionStudents.add(new SubmissionStudent(i, submitted));
            }

            // 4. データベース操作が完了したことを通知
            latch.countDown();
        });

        try {
            // データベース操作が完了するのを待つ
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();

        // SubmissionStudentオブジェクトのリストを返す
        return submissionStudents;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
}