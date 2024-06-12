package com.example.oplogy;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Map;

@Entity(
        foreignKeys = @ForeignKey(entity = SetupTable.class,//外部キーとしてSetupTableクラスを指定
                parentColumns = "id",//親クラスのidと結びつけ
                childColumns = "data",//子クラスのprofileIdと結びつけ
                onDelete = ForeignKey.CASCADE//親クラスが削除されたら子クラスも削除
        )
)
public class FormTable {
        @PrimaryKey(autoGenerate = true)
        public int id;

        @ColumnInfo(name = "data")
        public Map<String, Object> data;

        // Getters and setters if needed
}
