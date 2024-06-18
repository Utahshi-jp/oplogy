package com.example.oplogy;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.room.Room;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class FirestoreReception {

    private FirebaseFirestore db;
    private Context context;
    private AppDatabase appDatabase;

    public FirestoreReception(Context context) {
        db = FirebaseFirestore.getInstance();
        this.context = context;
        this.appDatabase = Room.databaseBuilder(context, AppDatabase.class, "FormsRoom").build();
    }

    public void getDocumentsByClassId(int classId) {
        CollectionReference collectionRef = db.collection("QuestionnaireForms");
        SharedPreferences sharedPreferences = context.getSharedPreferences("FirestoreData", MODE_PRIVATE);

        collectionRef.whereEqualTo("classId", classId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // すでに取得済みのドキュメントは除外
                                if (sharedPreferences.getBoolean(document.getId(), false)) {
                                    Log.w("FirestoreReception", "すでに取得済みのドキュメントです.");
                                    continue;
                                }
                                // firestoreのドキュメントのデータを取得
                                Map<String, Object> data = document.getData();

                                // データをエンティティクラスのインスタンスに変換
                                QuestionnaireForm form = new QuestionnaireForm();
                                form.classId = Integer.parseInt(data.get("classId").toString());
                                form.patronName = data.get("patronName").toString();
                                form.address = data.get("address").toString();
                                form.firstDay = Converters.fromTimestampList((List<Timestamp>) data.get("firstDay")).toString();
                                form.studentNumber = Integer.parseInt(data.get("studentNumber").toString());
                                form.childName = data.get("childName").toString();
                                form.thirdDay = Converters.fromTimestampList((List<Timestamp>) data.get("thirdDay")).toString();
                                form.secondDay = Converters.fromTimestampList((List<Timestamp>) data.get("secondDay")).toString();

                                // データベースに保存
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        appDatabase.questionnaireFormDao().insertAll(form);
                                        Log.w("FirestoreReception", "成功.");
                                    }
                                }).start();

                                // ドキュメントのIDを保存
                                // すでに取得済みのドキュメントは再度取得しないようにする
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(document.getId(), true);
                                editor.apply();
                            }
                        } else {
                            Log.w("FirestoreReception", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}