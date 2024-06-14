package com.example.oplogy;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class FirestoreReception {

    private FirebaseFirestore db;

    public FirestoreReception() {
        db = FirebaseFirestore.getInstance();
    }

    //ClassIdを引数にデータの作成を行う
    public void getDocumentsByClassId(int classId) {
        CollectionReference collectionRef = db.collection("QuestionnaireForms");

        // classIdが引数のものを取得する
        collectionRef.whereEqualTo("classId", classId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // データの取得に成功した場合
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();

                                // CreateRootクラスのインスタンスを作成し、dataを引数として渡す
                                GeoCoding geoCoding = new GeoCoding();
                                geoCoding.processData(data);
                            }
                        } else {
                            Log.w("FirestoreReception", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}