package com.example.oplogy;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirestoreReception {

    private FirebaseFirestore db;

    public FirestoreReception() {
        db = FirebaseFirestore.getInstance();
    }

    //firestoreから受け取ったデータを束ねるためのマップ
    public List<MyDataClass> myDataList = new ArrayList<>();

    //ClassIdを引数にデータの作成を行う
    public void getDocumentsByClassId(int classId) {
        myDataList.clear();
        CollectionReference collectionRef = db.collection("testAddressArray");

        // classIdが引数のものを取得する
        collectionRef.whereEqualTo("classId", classId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // データの取得に成功した場合
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();


                                // ドキュメントのデータをMyDataClassのインスタンスにマッピング
                                MyDataClass myData = new MyDataClass(
                                        (String) data.get("patronName"),
                                        ((Long) data.get("classId")).intValue(),
                                        (List<String>) data.get("address"),
                                        (List<Timestamp>) data.get("firstDay"),
                                        ((Long) data.get("studentNumber")).intValue(),
                                        (String) data.get("childName"),
                                        (List<Timestamp>) data.get("thirdDay"),
                                        (List<Timestamp>) data.get("secondDay")
                                );
                                //リストに追加
                                myDataList.add(myData);
                            }


                            //取得したデータをログ表示
                            for (MyDataClass data : myDataList) {
                                Log.i("FirestoreReceptiond", "data: " + data.toString());
                            }
                        } else {
                            Log.w("FirestoreReceptiond", "Error getting documents.", task.getException());
                        }
                        Log.i("FirestoreReceptiond", "data: " + myDataList.size());
                    }
                });

    }

    //Dataのリストのサイズを返す
    public int getMyDataListSize() {
        return myDataList.size();
    }

    //Dataのリストを返す
    public List<MyDataClass> getMyDataList() {
        return myDataList;
    }
}
