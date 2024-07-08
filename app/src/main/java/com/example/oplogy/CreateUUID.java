package com.example.oplogy;


import java.util.List;

public class CreateUUID {

    public static int generateUUID(List<Integer> classIdList ){
        while (true){
            int uuid = (int)(Math.random() * 1000000);
            boolean isDuplicate = false;
            for(int classId : classIdList){
                if(classId==uuid){
                    //重複があればフラグを立て、ループを抜ける
                    isDuplicate = true;
                    break;
                }
            }
            //重複がなければ生成したUUIDを返す
            if (!isDuplicate) {
                //firestoreに挿入処理
                InsertClassIdforFirebase insertClassIdforFirebase = new InsertClassIdforFirebase();
                insertClassIdforFirebase.insertClassId(uuid);
                //テスト用
                uuid = 100;
                return uuid;
            }
        }
    }
}