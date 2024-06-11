package com.example.oplogy;

import android.util.Log;

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

    public FirestoreReception() {
        db = FirebaseFirestore.getInstance();
    }

    //ClassIdを引数にデータの作成を行う
    public void getDocumentsByClassId(int classId) {
        CollectionReference collectionRef = db.collection("questionnaireForms");

        // classIdが引数のものを取得する
        collectionRef.whereEqualTo("classId", classId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // データの取得に成功した場合
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();

                                // デバッグ用のログ出力
                                Log.d("FirestoreReception", "Document ID: " + document.getId());
                                Log.d("FirestoreReception", "Data: " + data);

                                // ここでデータを取得し、必要に応じて処理を行います
                                String parentName = (String) data.get("patronName");
                                String childName = (String) data.get("childName");
                                String studentId = (String) data.get("studentNumber");
                                Timestamp address = (Timestamp) data.get("adress");
                                List<Timestamp> firstDay = (List<Timestamp>) data.get("firstDay");
                                List<Timestamp> secondDay = (List<Timestamp>) data.get("secondDay");
                                List<Timestamp> thirdDay = (List<Timestamp>) data.get("thirdDay");

                                // 取得したデータを使って必要な処理を行う
                                Log.d("FirestoreReception", "ParentName: " + parentName);
                                Log.d("FirestoreReception", "ChildName: " + childName);
                                Log.d("FirestoreReception", "StudentNumber: " + studentId);
                                Log.d("FirestoreReception", "Address: " + address.toDate());
                                Log.d("FirestoreReception", "First Day: " + firstDay);
                                Log.d("FirestoreReception", "Second Day: " + secondDay);
                                Log.d("FirestoreReception", "Third Day: " + thirdDay);
                            }
                        } else {
                            Log.w("FirestoreReception", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}