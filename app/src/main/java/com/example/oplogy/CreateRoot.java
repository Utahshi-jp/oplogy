package com.example.oplogy;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CreateRoot {
    MyDataClass data;//Firestoreから受け取ったdataを入れる変数
    String startTimeHomeVisit;//家庭訪問の開始時間
    String endTimeHomeVisit;//家庭訪問の終了時間
    String intervalTime;//家庭訪問の一家庭当たりの時間
    String startBreakTime;//家庭訪問の休憩の開始時間
    String endBreakTime;//家庭訪問の休憩の終了時間


    int interval;//家庭訪問の一家庭当たりの時間と移動時間の合計
    int startBreakTimeMinutes;//家庭訪問の開始時間から休憩時間までの時間
    int endBreakTimeMinutes;//家庭訪問の休憩終了時間から終了時間までの時間

    private final AppDatabase db;
    private int arraySize;
    boolean Duplicates=true;
    boolean secondDuplicates=true;

    private Context context;

//    String testdata[] = {"20240604", "20240605", "20240606"};
    String[] testdata;


    public CreateRoot(AppCompatActivity activity) {
        this.db = Room.databaseBuilder(activity.getApplicationContext(), AppDatabase.class, "SetUpTable").build();
        SharedPreferences sharedPreferences= activity.getSharedPreferences("visitingDate", Context.MODE_PRIVATE);
        String day1=sharedPreferences.getString("day1",null);
        String day2=sharedPreferences.getString("day2",null);
        String day3=sharedPreferences.getString("day3",null);

        testdata= new String[]{day1, day2, day3};

        for(String day:testdata){
            Log.d("sharepre","day"+day);
        }

    }

    public Boolean receiveData(List<MyDataClass> myDataList) {

        //myDataListの要素data第一希望日と第二希望日に以下を追加する
        //・保護者の希望時間帯の長さ
        //・家庭訪問の日付
        //・保護者の希望時間帯の開始と終了時間
        setData(myDataList);
        //希望時間帯の長さ順に並び替える前のログ
        outPutLogSort(myDataList);
        //保護者の希望時間帯が短い順にmyDataListのDataを並び替える
        timeZoneSort(myDataList);
        //希望時間帯の長さ順に並び替えたあとのログ
        outPutLogSort(myDataList);


        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            //Roomから以下の情報を取得
            //・家庭訪問全体の開始時間
            //・家庭訪問全体の終了時間
            //・一家庭あたりの家庭訪問の時間
            //・家庭訪問の休憩開始時間
            //・家庭訪問の休憩終了時間
            getRoomData();

            //Roomから取得した情報のログ

            outPutLogRoomData();

            //以下の情報を計算
            //・家庭訪問の合計時間
            //・家庭訪問の開始時間から休憩時間までの分数
            //・家庭訪問の休憩終了時間から終了時間までの分数
            timeCalculation(endTimeHomeVisit, startBreakTime, endBreakTime);

            //家庭訪問全体のスケジュールの開始時間を要素とした配列の作成
            int[][][] intervalArray = homeVisitSchedule();


            outPutLogIntervalArray(intervalArray);
            //スケジュール作成
            Duplicates = createSchedule(myDataList, intervalArray);

            //重複によるエラー確認
            if (!Duplicates) {
                sortSchedule(myDataList);

            } else {
                //第二希望日で同じ処理を行う
                Log.d("CreateRoot", "第二希望");
                secondSetData(myDataList);
                secondTimeZoneSort(myDataList);
                secondDuplicates = secondCreateSchedule(myDataList, intervalArray);
                if (!secondDuplicates) {
                    sortSchedule(myDataList);

                } else {
                    Log.d("CreateRoot", "重複によるエラー");
                }
            }
        });

        if (!secondDuplicates) {
            geocodeAddress(myDataList);
            outPutLogSchedule(myDataList);
            return true;
        } else {
            return false;
        }
    }


    private void setData(List<MyDataClass> myDataList) {
        for (int i = 0; i < myDataList.size(); i++) {
            // 希望時間帯の終了時刻から開始時刻を引いて希望時間帯の長さ(timezone)に入れる
            data = myDataList.get(i);
            //保護者の第一希望日
            List<Timestamp> firstDay = data.getFirstDay();

            //保護者の第一希望日の開始時間
            Timestamp parentStartTime = firstDay.get(0);
            //保護者の第一希望日の終了時間
            Timestamp parentEndTime = firstDay.get(1);
            //保護者の第一希望日の希望時間帯の長さ
            Long timezone = parentEndTime.getSeconds() - parentStartTime.getSeconds();
            data.setTimezone(timezone);

            // TimeStampを日付に変換
            Date startDate = new Date(parentStartTime.getSeconds() * 1000);
            Date endDate = new Date(parentEndTime.getSeconds() * 1000);
            SimpleDateFormat sdfDateData = new SimpleDateFormat("yyyyMMdd");
            sdfDateData.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
            //第一希望日の日付
            String startDateString = sdfDateData.format(startDate);
            String endDateString = sdfDateData.format(endDate);


            SimpleDateFormat sdfMinutes = new SimpleDateFormat("HHmm");
            sdfMinutes.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
            //保護者の希望開始時間を時間表記にしたもの
            String parentStartTimeString = sdfMinutes.format(parentStartTime.toDate());
            //保護者の希望終了時間を時間表記にしたもの
            String parentEndTimeString = sdfMinutes.format(parentEndTime.toDate());


            // myDataList の中の data に追加する処理
            myDataList.get(i).setTimezone(timezone);
            myDataList.get(i).setStartDateString(startDateString);
            myDataList.get(i).setEndDateString(endDateString);
            myDataList.get(i).setParentStartTimeString(parentStartTimeString);
            myDataList.get(i).setParentEndTimeString(parentEndTimeString);
        }
    }

    //setDataと処理は同じ(第二希望は任意なのでその点だけ確認)
    private void secondSetData(List<MyDataClass> myDataList) {
        for (int i = 0; i < myDataList.size(); i++) {
            // 希望時間帯の終了時刻から開始時刻を引いて希望時間帯の長さ(timezone)に入れる
            data = myDataList.get(i);
            if (myDataList.get(i).getSecondDay() != null) {
                //保護者の第二希望日
                List<Timestamp> secondDay = data.getSecondDay();
                //保護者の第二希望日の開始時間
                Timestamp parentStartTime = secondDay.get(0);
                //保護者の第二希望日の終了時間
                Timestamp parentEndTime = secondDay.get(1);
                //保護者の第二希望日の希望時間帯の長さ
                Long secondDayTimezone = parentEndTime.getSeconds() - parentStartTime.getSeconds();
                data.setTimezone(secondDayTimezone);

                // TimeStampを日付に変換
                Date startDate = new Date(parentStartTime.getSeconds() * 1000);
                Date endDate = new Date(parentEndTime.getSeconds() * 1000);
                SimpleDateFormat sdfDateData = new SimpleDateFormat("yyyyMMdd");
                sdfDateData.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
                //第二希望日の日付
                String secondDayStartDateString = sdfDateData.format(startDate);
                String secondDaySndDateString = sdfDateData.format(endDate);


                SimpleDateFormat sdfMinutes = new SimpleDateFormat("HHmm");
                sdfMinutes.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
                //保護者の希望開始時間を時間表記にしたもの
                String secondDayParentStartTimeString = sdfMinutes.format(parentStartTime.toDate());
                //保護者の希望終了時間を時間表記にしたもの
                String secondDayParentEndTimeString = sdfMinutes.format(parentEndTime.toDate());


                // myDataList の中の data に追加する処理
                myDataList.get(i).setSecondDayTimezone(secondDayTimezone);
                myDataList.get(i).setSecondDayStartDateString(secondDayStartDateString);
                myDataList.get(i).setSecondDayEndDateString(secondDaySndDateString);
                myDataList.get(i).setSecondDayParentStartTimeString(secondDayParentStartTimeString);
                myDataList.get(i).setSecondDayParentEndTimeString(secondDayParentEndTimeString);
            }
        }
    }

    private void timeZoneSort(List<MyDataClass> myDataList) {
        // timezoneを比較するComparator→timezoneが短い順に並べる
        Comparator<MyDataClass> comparator = Comparator.comparing(MyDataClass::getTimezone);
        // myDataListをtimezoneの値でソート
        myDataList.sort(comparator);
    }

    private void secondTimeZoneSort(List<MyDataClass> myDataList) {
        // timezoneを比較するComparator→timezoneが短い順に並べる
        Comparator<MyDataClass> comparator = Comparator.comparing(MyDataClass::getSecondDayTimezone);
        // myDataListをtimezoneの値でソート
        myDataList.sort(comparator);
    }

    private void outPutLogSort(List<MyDataClass> myDataList) {
        for (int i = 0; i < myDataList.size(); i++) {
            Log.d("CreateRoot", "(index: " + i + ") data: " + myDataList.get(i).getPatronName());
            Log.d("CreateRoot", "(index: " + i + ") data: " + myDataList.get(i).getClass());
            Log.d("CreateRoot", "(index: " + i + ") timezone: " + myDataList.get(i).getTimezone());
            Log.d("CreateRoot", "(index: " + i + ") startDate: " + myDataList.get(i).getStartDateString());
            Log.d("CreateRoot:outPutLogBeforeSort", "parentStartTimeString: " + myDataList.get(i).getParentStartTimeString());
            Log.d("CreateRoot:outPutLogBeforeSort", "parentEndTimeString: " + myDataList.get(i).getParentEndTimeString());

        }
    }

    private void getRoomData() {
        // setUpActivityによって入力され、Roomに保存された値を取り出す処理
        //Roomの操作の定義
        SetUpTableDao setUpTableDao = db.setUpTableDao();
        startTimeHomeVisit = setUpTableDao.getStartTime();
        endTimeHomeVisit = setUpTableDao.getEndTime();
        intervalTime = setUpTableDao.getIntervalTime();
        startBreakTime = setUpTableDao.getStartBreakTime();
        endBreakTime = setUpTableDao.getEndBreakTime();
    }

    //Roomからのデータ取得に関するログ
    void outPutLogRoomData() {
        Log.d("CreateRoot:outPutLogRoomData", "開始時間" + startTimeHomeVisit);
        Log.d("CreateRoot:outPutLogRoomData", "終了時刻" + endTimeHomeVisit);
        Log.d("CreateRoot:outPutLogRoomData", "一家庭当たりの所要時間" + intervalTime);
        Log.d("CreateRoot:outPutLogRoomData", "休憩開始時刻" + startBreakTime);
        Log.d("CreateRoot:outPutLogRoomData", "休憩終了時刻" + endBreakTime);
    }

    private void timeCalculation(String endTimeHomeVisit, String startBreakTime, String endBreakTime) {
        //家庭訪問の合計時間を計算するため、家庭訪問の終了時間から開始時間を引いた数を求めている。但し、(0,2)によって先に１時間単位の差を求めた後に、(2,4)によって分単位の差を求めている
        int totalTime = ((Integer.parseInt(endTimeHomeVisit.substring(0, 2)) - (Integer.parseInt(startTimeHomeVisit.substring(0, 2)))) * 60 + ((Integer.parseInt(endTimeHomeVisit.substring(2, 4)))) - (Integer.parseInt(startTimeHomeVisit.substring(2, 4))));
        //家庭訪問の休憩開始時間から家庭訪問の開始時間を引くことで家庭訪問の開始から休憩時間までの分数を計算
        startBreakTimeMinutes = ((Integer.parseInt(startBreakTime.substring(0, 2))) - (Integer.parseInt(startTimeHomeVisit.substring(0, 2)))) * 60 + ((Integer.parseInt(startBreakTime.substring(2, 4))) - (Integer.parseInt(startTimeHomeVisit.substring(2, 4))));
        //家庭訪問の休憩終了時間から家庭訪問の終了時間を引くことで休憩の終わりから家庭訪問の終了時間までの分数を計算
        endBreakTimeMinutes = ((Integer.parseInt(endBreakTime.substring(0, 2))) - (Integer.parseInt(startTimeHomeVisit.substring(0, 2)))) * 60 + ((Integer.parseInt(endBreakTime.substring(2, 4))) - (Integer.parseInt(startTimeHomeVisit.substring(2, 4))));
        interval = Integer.parseInt(intervalTime) + 10;//移動時間込みの1家庭当たりの所要時間
        arraySize = totalTime / interval;//家庭訪問の合計時間から移動時間込みの1家庭当たりの所要時間を割ることで配列の数を求めている
    }

    private int[][][] homeVisitSchedule() {
        //家庭訪問の開始時間からの経過分数を入れる配列
        List<Integer> intervalList = new ArrayList<>();
        startBreakTimeMinutes = (((Integer.parseInt(startTimeHomeVisit.substring(0, 2))) + (startBreakTimeMinutes + (Integer.parseInt(startTimeHomeVisit.substring(0, 2)))) / 60) % 24) * 100 + (startBreakTimeMinutes + (Integer.parseInt(startTimeHomeVisit.substring(2, 4)))) % 60;
        endBreakTimeMinutes = (((Integer.parseInt(startTimeHomeVisit.substring(0, 2))) + (endBreakTimeMinutes + (Integer.parseInt(startTimeHomeVisit.substring(0, 2)))) / 60) % 24) * 100 + (endBreakTimeMinutes + (Integer.parseInt(startTimeHomeVisit.substring(2, 4)))) % 60;

        //休憩時間を除いた家庭訪問の開始時間からの経過分数+家庭訪問の開始時間=家庭訪問のスケジュール区切りをintervalArrayに入れる処理
        for (int i = 0; i < arraySize; i++) {
            int intervalMinutes = (((Integer.parseInt(startTimeHomeVisit.substring(0, 2))) + (interval * i) / 60) % 24) * 100 + (interval * i) % 60;
            if (intervalMinutes % 100 >= 60) {
                intervalMinutes += 40; // 下2桁が60以上の場合は繰り上げる
            }
            if (intervalMinutes < startBreakTimeMinutes || intervalMinutes >= endBreakTimeMinutes) {
                intervalList.add(intervalMinutes);
            }
        }


        int[][][] intervalArray = new int[3][intervalList.size()][2];
        for (int i = 0; i < intervalList.size(); i++) {
            for (int j = 0; j < 3; j++) {
                intervalArray[j][i][0] = intervalList.get(i);
                intervalArray[j][i][1] = 0;//割り当てされていないことを表す
            }
        }

        return intervalArray;
    }

    private void outPutLogIntervalArray(int[][][] intervalArray) {
        for (int i = 0; i < intervalArray[0].length; i++) {
            Log.d("CreateRoot", "inteintervalArray:(intex:" + i + ") :" + intervalArray[0][i][0]);
        }
    }


    private Boolean createSchedule(List<MyDataClass> myDataList, int[][][] intervalArray) {

        for (int i = 0; i < myDataList.size(); i++) {
            for (int j = 0; j < intervalArray[0].length - 1; j++) {
                for (int x = 0; x < 3; x++) {
                    if (testdata[x].equals(myDataList.get(i).getStartDateString()) && myDataList.get(i).getSchedule() == 0) {
                        String desiredDate = myDataList.get(i).getStartDateString();
                        checkSchedule(myDataList, intervalArray, i, j, x, desiredDate);
                        break;
                    }
                }
            }

        }

        for (int i = 0; i < myDataList.size(); i++) {
            if (myDataList.get(i).getSchedule() == 0) ;
            return true;
        }
        return false;
    }

    private boolean secondCreateSchedule(List<MyDataClass> myDataList, int[][][] intervalArray) {
        for (int i = 0; i < myDataList.size(); i++) {
            for (int j = 0; j < intervalArray[0].length - 1; j++) {
                for (int x = 0; x < 3; x++) {
                    if (testdata[x].equals(myDataList.get(i).getSecondDayStartDateString()) && myDataList.get(i).getSchedule() == 0) {
                        String desiredDate = myDataList.get(i).getSecondDayStartDateString();
                        checkSchedule(myDataList, intervalArray, i, j, x, desiredDate);
                    }
                }
            }
        }

        for (int i = 0; i < myDataList.size(); i++) {
            if (myDataList.get(i).getSchedule() == 0) {
                return true;
            }
        }
        return false;
    }

    private void checkSchedule(List<MyDataClass> myDataList, int[][][] intervalArray, int i, int j, int x, String desiredDate) {

        if (intervalArray[x][j][0] >= Integer.parseInt(myDataList.get(i).getParentStartTimeString()) && intervalArray[x][j + 1][0] <= Integer.parseInt(myDataList.get(i).getParentEndTimeString()) && intervalArray[x][j][1] == 0) {
            intervalArray[x][j][1] += 1;//割り当て済みを表す
            myDataList.get(i).setSchedule(Integer.parseInt(desiredDate.substring(4, 8) + String.valueOf(intervalArray[x][j][0])));
        }
    }

    private void sortSchedule(List<MyDataClass> myDataList) {

        Comparator<MyDataClass> comparator = Comparator.comparing(MyDataClass::getSchedule);
        myDataList.sort(comparator);
    }



    private void geocodeAddress(List<MyDataClass> myDataList) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            for(int i=0;i<myDataList.size();i++) {
                List<Address> addresses = geocoder.getFromLocationName(myDataList.get(i).getAddress().toString(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address addressResult = addresses.get(0);
                    double latitude = addressResult.getLatitude();
                    double longitude = addressResult.getLongitude();
                    myDataList.get(i).setLatLng(new LatLng(latitude, longitude));
                }
            }
        } catch (IOException e) {
            Log.e("CreateRoot", "緯度経度の取得に失敗: " +e);
        }
    }

    private void outPutLogSchedule(List<MyDataClass> myDataList) {
        for (int i = 0; i < myDataList.size(); i++) {
            Log.d("CreateRoot:outPutLogSchedule", "(index: " + i + ") data: " + myDataList.get(i));
            Log.d("CreateRoot:outPutLogSchedule", "(index: " + i + ") Schedule: " + myDataList.get(i).getSchedule());
            Log.d("CreateRoot","(index: " + i + ") LatLng"+myDataList.get(i).getLatLng());
        }
    }
}
