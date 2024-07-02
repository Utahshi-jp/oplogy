package com.example.oplogy;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class InsertClassIdforFirebase {
    public void insertClassId(int classId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("classId", classId); // classId is inserted as a number

        db.collection("classId_Database").add(data)
                .addOnSuccessListener(documentReference -> System.out.println("DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> System.err.println("Error adding document: " + e));
    }
}
