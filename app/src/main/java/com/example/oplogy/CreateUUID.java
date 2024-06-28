package com.example.oplogy;


import java.util.List;

public class CreateUUID {

    public static int generateUUID(List<String> classIdList ){
        while (true){
            String uuid = String.valueOf((int)(Math.random() * 1000000));
            boolean isDuplicate = false;
            for(String classId : classIdList){
                if(classId.equals(uuid)){
                    //重複があればフラグを立て、ループを抜ける
                    isDuplicate = true;
                    break;
                }
            }
            //重複がなければ生成したUUIDを返す
            if (!isDuplicate) {
                //firestoreに挿入処理
                //テスト用
                uuid="100";
                return Integer.parseInt(uuid);
            }
        }
    }
}