package com.example.oplogy;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //    formコピー用のURL
    private static final String URL_TO_COPY = "https://docs.google.com/forms/d/e/1FAIpQLScKI_ca01nO7die7SqZyThiqa7NB7gcucMJtiV_-sc3eZX6KQ/viewform";
    //    ダイアログの宣言
    private AlertDialog alertDialog;
    //    ID作成のTextViewとImageView
    private TextView creatUUID;
    private ImageView imageUuid;
    //    セットアップのTextViewとImageView
    private TextView setUp;
    private ImageView imageSetup;
    //    formコピー用のボタン
    private TextView formURL;
    private ImageView imageFormURL;
    //    ルート作成のTextViewとImageView
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

//        formコピー用のインテント
        formURL = findViewById(R.id.formURL);
        formURL.setOnClickListener(this);
        imageFormURL = findViewById(R.id.imageFormURL);
        imageFormURL.setOnClickListener(this);


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

//      formコピー用のクリック処理
        if (view == formURL) {
            imageFormURL.setImageResource(R.drawable.ischecked_uuid);
            copyUrlToClipboard(URL_TO_COPY);
        }
        if (view == imageFormURL) {
            imageFormURL.setImageResource(R.drawable.ischecked_uuid);
            copyUrlToClipboard(URL_TO_COPY);
        }

//        ルート作成のクリック処理
        if (view == root) {
            imageRoot.setImageResource(R.drawable.pin);
            checkSetupAndCreateRoute(this::fetchDataAndCreateRoute);
        }
        if (view == imageRoot) {
            imageRoot.setImageResource(R.drawable.pin);
            checkSetupAndCreateRoute(this::fetchDataAndCreateRoute);
        }
//        提出状況のクリック処理
        if (view == submission) {
            checkSetupAndCreateRoute(() -> {
                ArrayList<SubmissionStudent> submissionStudents = getSubmissionStudents();
                Intent toSubmission = new Intent(MainActivity.this, SubmissionActivity.class);
                toSubmission.putParcelableArrayListExtra("submissionStudents", submissionStudents);
                startActivity(toSubmission);
            });
        }
        if (view == imageSubmission) {
            checkSetupAndCreateRoute(() -> {
                ArrayList<SubmissionStudent> submissionStudents = getSubmissionStudents();
                Intent toSubmission = new Intent(MainActivity.this, SubmissionActivity.class);
                toSubmission.putParcelableArrayListExtra("submissionStudents", submissionStudents);
                startActivity(toSubmission);
            });
        }
    }


    //ID作成、表示に関する処理
    private void showUUIDYesNoDialog() {
        firestoreReception_classIdDatabase = new FirestoreReception_classIdDatabase();
        List<Integer> classIdList = firestoreReception_classIdDatabase.getAllDocumentsFromClassIdDatabase();


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ID");
        builder.setMessage("あなたのIDを表示/もしくは新規で作成しますか？");

        //作成処理
        builder.setPositiveButton("作成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                classId = CreateUUID.generateUUID(classIdList);
                // 生成されたIDを表示するメソッド
                showClassIdDialog("生成されたID", classId);
            }
        });
        //表示処理
        builder.setNegativeButton("表示", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //roomを扱うため非同期処理
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    // 現在のクラスIDを取得
                    int currentClassId = getCurrentClassIdFromRoom();
                    if (currentClassId == 0) {
                        currentClassId = classId;
                    }
                    final int showDialogClassId = currentClassId;
                    runOnUiThread(() -> {
                        // 現在のクラスIDを表示するダイアログ
                        showClassIdDialog("現在のID", showDialogClassId);
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
        builder.setMessage("ID: " + classId);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //ルート作成、提出状況の遷移を行う前のチェックを行う処理
    private void checkSetupAndCreateRoute(Runnable onSetupComplete) {
        if (isClassIdSet()) {
            isSetupExists(classId).thenAccept(setupExists -> {
                if (setupExists) {
                    runOnUiThread(onSetupComplete);
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "先にセットアップを済ませてください", Toast.LENGTH_LONG).show();
                    });
                }
            }).exceptionally(ex -> {
                ex.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show();
                });
                return null;
            });
        } else {
            Toast.makeText(this, "先にIDの作成を行ってください", Toast.LENGTH_LONG).show();
        }
    }

    // クラスIDが設定されているかどうかを判定
    private boolean isClassIdSet() {
        // classIdが0より大きい場合、trueを返す
        return classId > 0;
    }

    // セットアップが存在するかどうかを判定
    private CompletableFuture<Boolean> isSetupExists(int classId) {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        return CompletableFuture.supplyAsync(() -> {
            AppDatabase db = getDatabaseInstance();
            SetUpTableDao setUpTableDao = db.setUpTableDao();
            //データベースの値を全取得
            List<SetUpTable> checkData = setUpTableDao.getAll();
            for (SetUpTable setUpTable : checkData) {
                //SetUpTableのclassIdと引数のclassIdが一致する場合、trueを返す
                if (setUpTable.classId == classId) {
                    return true;
                }
            }
            return false;
            //処理完了時にexecutorServiceをシャットダウン
        }, executorService).whenComplete((result, throwable) -> executorService.shutdown());
    }

    //クリップボードにURLをコピーする処理
    private void copyUrlToClipboard(String url) {
        try {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("URL", url);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "GoogleFormのURLをコピーしました", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "エラー コピーできませんでした", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error copying URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //ルート作成の非同期処理
    private void fetchDataAndCreateRoute() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            AppDatabase db = getDatabaseInstance();
            SetUpTableDao setUpTableDao = db.setUpTableDao();
            int totalStudentInt = setUpTableDao.getTotalStudent();
            int myDataListSizeInt = firestoreReception.getMyDataListSize();

            //総生徒数と提出済みになっている生徒の数が一致するかの確認
            runOnUiThread(() -> {
                if (totalStudentInt != myDataListSizeInt) {
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
        // ProgressDialogを作成
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        executor.execute(() -> {
            List<MyDataClass> myDataList = null;
            while (myDataList == null) {
                myDataList = firestoreReception.getMyDataList();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    runOnUiThread(progressDialog::dismiss); // 進行状況ダイアログを閉じる
                    return;
                }
            }

            //final宣言することによって、スレッドセーフになる(ラムダ式内で使えるようにする)
            final List<MyDataClass> finalMyDataList = myDataList;
            CreateSchedule createSchedule = new CreateSchedule(MainActivity.this);
            String startPointLatLngString = createSchedule.receiveData(myDataList, getApplicationContext());
            Boolean notDuplicatesBoolean = null;
            for (int i = 0; i < myDataList.size(); i++) {
                if (myDataList.get(i).getSchedule() == 0) {
                    notDuplicatesBoolean = false;
                    break;
                } else {
                    notDuplicatesBoolean = true;
                }
            }
            Boolean finalNotDuplicatesBoolean = notDuplicatesBoolean;
            Log.d("MainActivity", "重複判定" + String.valueOf(finalNotDuplicatesBoolean));

            runOnUiThread(() -> {
                progressDialog.dismiss(); // 進行状況ダイアログを閉じる
                if (finalNotDuplicatesBoolean) {
                    Log.d("MainActivity", "スケジュール作成成功");
                    saveMyDataList(finalMyDataList);
                    Intent toRoot = new Intent(MainActivity.this, Maps.class);
                    toRoot.putExtra("startPointLatLngString", startPointLatLngString);
                    startActivity(toRoot);
                } else {
                    // 保護者の重複による警告ダイアログ
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
        String jsonString = gson.toJson(myDataList);

        // JSON形式のデータを共有プリファレンスに保存
        editor.putString("myDataList", jsonString);
        editor.apply();
    }

    private void showErrorDialog(List<MyDataClass> myDataList) {
        List<Integer> studentNumbers = new ArrayList<>();
        for (MyDataClass data : myDataList) {
            if (data.getSchedule() == 0) {
                studentNumbers.add(data.getStudentNumber());
            }
        }
        StringBuilder message = new StringBuilder("保護者の重複が重大でルート作成ができません。保護者に連絡して調整してください。\n\n");
        for (int i = 0; i < studentNumbers.size(); i++) {
            message.append("出席番号:" + studentNumbers.get(i));
            message.append("\n保護者名:" + myDataList.get(i).getPatronName());
            message.append("\n第一希望 " + myDataList.get(i).getStartDateString().substring(4, 6) + "月");
            message.append(myDataList.get(i).getStartDateString().substring(6, 8) + "日");
            message.append(" " + myDataList.get(i).getParentStartTimeString().substring(0, 2));
            message.append(":" + myDataList.get(i).getParentStartTimeString().substring(2, 4));
            message.append("～" + myDataList.get(i).getParentEndTimeString().substring(0, 2));
            message.append(":" + myDataList.get(i).getParentEndTimeString().substring(2, 4));
            message.append("\n第二希望 " + myDataList.get(i).getSecondDayStartDateString().substring(4, 6) + "月");
            message.append(myDataList.get(i).getSecondDayStartDateString().substring(6, 8) + "日");
            message.append(" " + myDataList.get(i).getSecondDayParentStartTimeString().substring(0, 2));
            message.append(":" + myDataList.get(i).getSecondDayParentStartTimeString().substring(2, 4));
            message.append("～" + myDataList.get(i).getSecondDayParentEndTimeString().substring(0, 2));
            message.append(":" + myDataList.get(i).getSecondDayParentEndTimeString().substring(2, 4) + "\n\n");
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
            int totalStudentInt = setUpTableDao.getTotalStudent();
            // 2. Firestoreから生徒番号のリストを取得
            ArrayList<Integer> firestoreStudentNumbersList = new ArrayList<>();
            for (MyDataClass myData : myDataList) {
                int studentNumberInt = myData.getStudentNumber();
                firestoreStudentNumbersList.add(studentNumberInt);
            }

            // 3. SubmissionStudentオブジェクトのリストを作成
            for (int i = 1; i <= totalStudentInt; i++) {
                boolean submitted = firestoreStudentNumbersList.contains(i);
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