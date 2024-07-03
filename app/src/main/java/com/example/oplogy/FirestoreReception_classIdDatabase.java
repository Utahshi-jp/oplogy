package com.example.oplogy;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class FirestoreReception_classIdDatabase {
    private FirebaseFirestore db;
    private List<Integer> classIdList= new ArrayList<>();

    public FirestoreReception_classIdDatabase() {
        db = FirebaseFirestore.getInstance();
    }


    public List<Integer> getAllDocumentsFromClassIdDatabase() {
        db.collection("classId_Database")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("結果", document.getId() + " => " + document.getData());
                                //データをListに追加
                                classIdList.add(((Long) document.get("classId")).intValue());
                            }
                        } else {
                            Log.d("結果", "Error getting documents: ", task.getException());
                        }
                    }
                });

        return classIdList;
    }

    public List<Integer> getClassIdList() {
        return classIdList;
    }
}