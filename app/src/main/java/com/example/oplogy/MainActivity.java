package com.example.oplogy;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textView;
    private Button btnShow;
    private Button btnAdd;
    private EditText number;
    private EditText address;
    private EditText date;
    private EditText time;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    Button button;
//    TextView textView;
    EditText editText;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


         textView=findViewById(R.id.showText);
         btnShow=findViewById(R.id.btnShow);
         btnAdd=findViewById(R.id.btnAdd);

         number=findViewById(R.id.editNumber);
         address=findViewById(R.id.editAddress);
         date=findViewById(R.id.editDate);
         time=findViewById(R.id.editTime);


        // ⑤Read Data
        // Firestoreのコレクション「users」のドキュメント一覧を取得する
        // 非同期で取得処理が動作する。結果を受け取るために処理完了時のリスナーをセットする
        db.collection("questionnaireForms").get().addOnCompleteListener(task -> {
            String data = "";
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("@FB1", document.getId() + "=>" + document.getData());
                    data += document.getId() + "=>" + document.getData() + "\n";
                }
            } else {
                data = "Error getting documents." + task.getException().getMessage();
            }
            textView.setText(data);//編集したデータを画面下部に表示
        });



        btnShow.setOnClickListener(v -> {

        });



        findViewById(R.id.mapmapcreate).setOnClickListener(
                view->{


                }
        );
    }

    @Override
    public void onClick(View v) {


    }
}
class User {
    private String number;
    private String address;
    private String date;
    private String time;

    public User() {
    }

    public User(String number, String address, String data, String time) {
        this.number = number;
        this.address = address;
        this.date = data;
        this.time = time;
    }
    //getterとsetter
    public String getNumber() {
        return number;
    }
    public String getAddress() {
        return address;
    }
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setTime(String time) {
        this.time = time;
    }

}