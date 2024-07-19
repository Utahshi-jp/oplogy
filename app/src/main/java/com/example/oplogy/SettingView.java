package com.example.oplogy;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingView extends AppCompatActivity implements View.OnClickListener {
    //    formコピー用のURL
    private static final String URL_TO_COPY = "https://docs.google.com/forms/d/e/1FAIpQLScKI_ca01nO7die7SqZyThiqa7NB7gcucMJtiV_-sc3eZX6KQ/viewform";

    private View backButton;
    private View creatUUID;
    private View imageUuid;
    private View setUp;
    private View imageSetup;
    private View formURL;
    private View imageFormURL;
    private AlertDialog alertDialog;
    private int classId;
    private FirestoreReception_classIdDatabase firestoreReception_classIdDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_view);

        // 戻るボタンの処理
        backButton = findViewById(R.id.BackMain_fromSetting);
        backButton.setOnClickListener(this);

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
    }

    @Override
    public void onClick(View view) {
        if (view == backButton) {
            Intent intent = new Intent(SettingView.this, MainActivity.class);
            startActivity(intent);
        }
        //        ID作成のクリック処理
        if (view == creatUUID) {
            showUUIDYesNoDialog();//UUIDを表示するかのダイアログ
        }
        if (view == imageUuid) {
            showUUIDYesNoDialog();//UUIDを表示するかのダイアログ
        }
//        セットアップのクリック処理
        if (view == setUp) {
            Intent toSetup = new Intent(SettingView.this, SetUpActivity.class);
            toSetup.putExtra("classId", classId);
            startActivity(toSetup);
        }
        if (view == imageSetup) {
            Intent toSetup = new Intent(SettingView.this, SetUpActivity.class);
            startActivity(toSetup);
        }

//      formコピー用のクリック処理
        if (view == formURL) {
            copyUrlToClipboard(URL_TO_COPY);
        }
        if (view == imageFormURL) {
            copyUrlToClipboard(URL_TO_COPY);
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

    //Roomから現在のクラスIDを取得するメソッド
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

    //  データベースのインスタンスを取得するメソッド
    private AppDatabase getDatabaseInstance() {
        return Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "SetUpTable").build();
    }

}