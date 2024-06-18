package com.example.oplogy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

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
    //    提出状況のTextViewとImageView
    private TextView submission;

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

//      firestoreの受信関連の宣言
        db = FirebaseFirestore.getInstance();
        firestoreReception = new FirestoreReception(this);
//      受信するメソッドの呼び出し
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
//        if(view == setUp){
//            imageSetup.setImageResource(R.drawable.ischecked_uuid);
//            Intent toSetup = new Intent(MainActivity.this,SetupActivity.class);
//            startActivity(toSetup);
//
//        }
//        ルート作成のクリック処理
        if(view == root){
            imageRoot.setImageResource(R.drawable.pin);
            Intent toRoot = new Intent(MainActivity.this,RootSearchActivity.class);
            startActivity(toRoot);
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