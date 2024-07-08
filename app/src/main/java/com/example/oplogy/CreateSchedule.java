package com.example.oplogy;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

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

//保護者の希望とSetUpActivityによって設定された情報をもとにスケジュールとルートを作成する
public class CreateSchedule {
    MyDataClass data;//Firestoreから受け取ったdataを入れる変数

    String startPointString;//家庭訪問の開始地点
    String startTimeHomeVisitString;//家庭訪問の開始時間
    String endTimeHomeVisitString;//家庭訪問の終了時間
    String intervalTimeString;//家庭訪問の一家庭当たりの時間
    String startBreakTimeString;//家庭訪問の休憩の開始時間
    String endBreakTimeString;//家庭訪問の休憩の終了時間


    int intervalInt;//家庭訪問の一家庭当たりの時間と移動時間の合計
    int startBreakTimeMinutesInt;//家庭訪問の開始時間から休憩時間までの時間
    int endBreakTimeMinutesInt;//家庭訪問の休憩終了時間から終了時間までの時間

    private final AppDatabase db;
    private int arraySizeInt;

    boolean notSecondDuplicatesBoolean = true;//スケジュールの重複の有無(第一希望日のみで通った場合も考えて初期はtrue)

    String[] date;


    public CreateSchedule(AppCompatActivity activity) {
        this.db = Room.databaseBuilder(activity.getApplicationContext(), AppDatabase.class, "SetUpTable").build();
        SharedPreferences sharedPreferences = activity.getSharedPreferences("visitingDate", Context.MODE_PRIVATE);

        String firstDay = sharedPreferences.getString("day1", null);
        String secondDay = sharedPreferences.getString("day2", null);
        String thirdDay = sharedPreferences.getString("day3", null);

        date = new String[]{firstDay, secondDay, thirdDay};

    }

    //MainActivityからデータを受け取る
    public String receiveData(List<MyDataClass> myDataList, Context context) {

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
            timeCalculation(endTimeHomeVisitString, startBreakTimeString, endBreakTimeString);

            //家庭訪問全体のスケジュールの開始時間を要素とした配列の作成(例:1200,1220,1240のように各家庭への到着時間となる区切り)とログ表示
            int[][][] intervalArrayInt = homeVisitSchedule();
            outPutLogIntervalArray(intervalArrayInt);

            //スケジュール作成
            boolean notDuplicatesBoolean = createSchedule(myDataList, intervalArrayInt);

            //スケジュールの重複の確認
            if (!notDuplicatesBoolean) {
                //第二希望日で同じ処理を行う
                Log.d("CreateSchedule", "第二希望");
                secondSetData(myDataList);
                secondTimeZoneSort(myDataList);
                notSecondDuplicatesBoolean = secondCreateSchedule(myDataList, intervalArrayInt);
            }
        });
        //重複がなければ開始地点の緯度経度を返す
        if (notSecondDuplicatesBoolean) {
            //スケジュールを基準にソートする
            sortSchedule(myDataList);
            String startPointLatLngString = geocodeAddress(myDataList, context);
            Log.d("CreateSchedule", "startPointLatLngString" + startPointLatLngString);
            outPutLogSchedule(myDataList);
            return startPointLatLngString;
        }
        //重複があるときは""を返す
        Log.d("CreateSchedule", "重複によるエラー");
        return "";
    }


    private void setData(List<MyDataClass> myDataList) {
        for (int i = 0; i < myDataList.size(); i++) {
            // 希望時間帯の終了時刻から開始時刻を引いて希望時間帯の長さ(timezone)に入れる
            data = myDataList.get(i);
            //保護者の第一希望日
            List<Timestamp> firstDayList = data.getFirstDay();

            //保護者の第一希望日の開始時間
            Timestamp parentStartTimestamp = firstDayList.get(0);
            //保護者の第一希望日の終了時間
            Timestamp parentEndTimestamp = firstDayList.get(1);
            //保護者の第一希望日の希望時間帯の長さ
            Long timezoneLong = parentEndTimestamp.getSeconds() - parentStartTimestamp.getSeconds();
            data.setTimezone(timezoneLong);

            // TimeStampを日付に変換
            Date startDate = new Date(parentStartTimestamp.getSeconds() * 1000);
            Date endDate = new Date(parentEndTimestamp.getSeconds() * 1000);
            SimpleDateFormat sdfDateData = new SimpleDateFormat("yyyyMMdd");
            sdfDateData.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
            //第一希望日の日付
            String startDateString = sdfDateData.format(startDate);
            String endDateString = sdfDateData.format(endDate);


            SimpleDateFormat sdfMinutes = new SimpleDateFormat("HHmm");
            sdfMinutes.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
            //保護者の希望開始時間を時間表記にしたもの
            String parentStartTimeString = sdfMinutes.format(parentStartTimestamp.toDate());
            //保護者の希望終了時間を時間表記にしたもの
            String parentEndTimeString = sdfMinutes.format(parentEndTimestamp.toDate());


            // myDataList の中の data に追加する処理
            myDataList.get(i).setTimezone(timezoneLong);
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
                List<Timestamp> secondDayList = data.getSecondDay();
                //保護者の第二希望日の開始時間
                Timestamp parentStartTimestamp = secondDayList.get(0);
                //保護者の第二希望日の終了時間
                Timestamp parentEndTimestamp = secondDayList.get(1);
                //保護者の第二希望日の希望時間帯の長さ
                Long secondDayTimezoneLong = parentEndTimestamp.getSeconds() - parentStartTimestamp.getSeconds();
                data.setTimezone(secondDayTimezoneLong);

                // TimeStampを日付に変換
                Date startDate = new Date(parentStartTimestamp.getSeconds() * 1000);
                Date endDate = new Date(parentEndTimestamp.getSeconds() * 1000);
                SimpleDateFormat sdfDateData = new SimpleDateFormat("yyyyMMdd");
                sdfDateData.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
                //第二希望日の日付
                String secondDayStartDateString = sdfDateData.format(startDate);
                String secondDaySndDateString = sdfDateData.format(endDate);


                SimpleDateFormat sdfMinutes = new SimpleDateFormat("HHmm");
                sdfMinutes.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
                //保護者の希望開始時間を時間表記にしたもの
                String secondDayParentStartTimeString = sdfMinutes.format(parentStartTimestamp.toDate());
                //保護者の希望終了時間を時間表記にしたもの
                String secondDayParentEndTimeString = sdfMinutes.format(parentEndTimestamp.toDate());


                // myDataList の中の data に追加する処理
                myDataList.get(i).setSecondDayTimezone(secondDayTimezoneLong);
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
            Log.d("CreateSchedule", "(index: " + i + ") data: " + myDataList.get(i).getPatronName());
            Log.d("CreateSchedule", "(index: " + i + ") data: " + myDataList.get(i).getClass());
            Log.d("CreateSchedule", "(index: " + i + ") timezone: " + myDataList.get(i).getTimezone());
            Log.d("CreateSchedule", "(index: " + i + ") startDate: " + myDataList.get(i).getStartDateString());
            Log.d("CreateSchedule:outPutLogBeforeSort", "parentStartTimeString: " + myDataList.get(i).getParentStartTimeString());
            Log.d("CreateSchedule:outPutLogBeforeSort", "parentEndTimeString: " + myDataList.get(i).getParentEndTimeString());

        }
    }

    private void getRoomData() {
        // setUpActivityによって入力され、Roomに保存された値を取り出す処理
        //Roomの操作の定義
        SetUpTableDao setUpTableDao = db.setUpTableDao();
        startPointString = setUpTableDao.getStartPoint();
        startTimeHomeVisitString = setUpTableDao.getStartTime();
        endTimeHomeVisitString = setUpTableDao.getEndTime();
        intervalTimeString = setUpTableDao.getIntervalTime();
        startBreakTimeString = setUpTableDao.getStartBreakTime();
        endBreakTimeString = setUpTableDao.getEndBreakTime();
    }

    //Roomからのデータ取得に関するログ
    void outPutLogRoomData() {
        Log.d("CreateSchedule:outPutLogRoomData", "開始時間" + startTimeHomeVisitString);
        Log.d("CreateSchedule:outPutLogRoomData", "終了時刻" + endTimeHomeVisitString);
        Log.d("CreateSchedule:outPutLogRoomData", "一家庭当たりの所要時間" + intervalTimeString);
        Log.d("CreateSchedule:outPutLogRoomData", "休憩開始時刻" + startBreakTimeString);
        Log.d("CreateSchedule:outPutLogRoomData", "休憩終了時刻" + endBreakTimeString);
    }

    private void timeCalculation(String endTimeHomeVisitString, String startBreakTime, String endBreakTime) {
        //家庭訪問の合計時間を計算するため、家庭訪問の終了時間から開始時間を引いた数を求めている。但し、(0,2)によって先に１時間単位の差を求めた後に、(2,4)によって分単位の差を求めている
        int totalTimeInt = ((Integer.parseInt(endTimeHomeVisitString.substring(0, 2)) - (Integer.parseInt(startTimeHomeVisitString.substring(0, 2)))) * 60 + ((Integer.parseInt(endTimeHomeVisitString.substring(2, 4)))) - (Integer.parseInt(startTimeHomeVisitString.substring(2, 4))));
        //家庭訪問の休憩開始時間から家庭訪問の開始時間を引くことで家庭訪問の開始から休憩時間までの分数を計算
        startBreakTimeMinutesInt = ((Integer.parseInt(startBreakTime.substring(0, 2))) - (Integer.parseInt(startTimeHomeVisitString.substring(0, 2)))) * 60 + ((Integer.parseInt(startBreakTime.substring(2, 4))) - (Integer.parseInt(startTimeHomeVisitString.substring(2, 4))));
        //家庭訪問の休憩終了時間から家庭訪問の終了時間を引くことで休憩の終わりから家庭訪問の終了時間までの分数を計算
        endBreakTimeMinutesInt = ((Integer.parseInt(endBreakTime.substring(0, 2))) - (Integer.parseInt(startTimeHomeVisitString.substring(0, 2)))) * 60 + ((Integer.parseInt(endBreakTime.substring(2, 4))) - (Integer.parseInt(startTimeHomeVisitString.substring(2, 4))));
        intervalInt = Integer.parseInt(intervalTimeString) + 10;//移動時間込みの1家庭当たりの所要時間
        arraySizeInt = totalTimeInt / intervalInt;//家庭訪問の合計時間から移動時間込みの1家庭当たりの所要時間を割ることで配列の数を求めている
    }

    private int[][][] homeVisitSchedule() {
        //家庭訪問の開始時間からの経過分数を入れる配列
        List<Integer> intervalList = new ArrayList<>();
        startBreakTimeMinutesInt = (((Integer.parseInt(startTimeHomeVisitString.substring(0, 2))) + (startBreakTimeMinutesInt + (Integer.parseInt(startTimeHomeVisitString.substring(0, 2)))) / 60) % 24) * 100 + (startBreakTimeMinutesInt + (Integer.parseInt(startTimeHomeVisitString.substring(2, 4)))) % 60;
        endBreakTimeMinutesInt = (((Integer.parseInt(startTimeHomeVisitString.substring(0, 2))) + (endBreakTimeMinutesInt + (Integer.parseInt(startTimeHomeVisitString.substring(0, 2)))) / 60) % 24) * 100 + (endBreakTimeMinutesInt + (Integer.parseInt(startTimeHomeVisitString.substring(2, 4)))) % 60;

        //休憩時間を除いた家庭訪問の開始時間からの経過分数+家庭訪問の開始時間=家庭訪問のスケジュール区切りをintervalArrayに入れる処理
        for (int i = 0; i < arraySizeInt; i++) {
            int intervalMinutesInt = (((Integer.parseInt(startTimeHomeVisitString.substring(0, 2))) + (intervalInt * i) / 60) % 24) * 100 + (intervalInt * i) % 60;
            if (intervalMinutesInt % 100 >= 60) {
                intervalMinutesInt += 40; // 下2桁が60以上の場合は繰り上げる
            }
            //教師の休憩時間を除く処理
            if (intervalMinutesInt < startBreakTimeMinutesInt || intervalMinutesInt >= endBreakTimeMinutesInt) {
                intervalList.add(intervalMinutesInt);
            }
        }

        //[3]は家庭訪問の〇日目
        int[][][] intervalArrayInt = new int[3][intervalList.size()][2];
        for (int i = 0; i < intervalList.size(); i++) {
            for (int j = 0; j < 3; j++) {
                intervalArrayInt[j][i][0] = intervalList.get(i);//家庭訪問のスケジュール区切りの時間を要素に入れる
                intervalArrayInt[j][i][1] = 0;//家庭訪問のスケジュールにまだ保護者が割り当てられていないことを表す
            }
        }

        return intervalArrayInt;
    }

    private void outPutLogIntervalArray(int[][][] intervalArrayInt) {
        for (int i = 0; i < intervalArrayInt[0].length; i++) {
            Log.d("CreateSchedule", "inteintervalArray:(intex:" + i + ") :" + intervalArrayInt[0][i][0]);
        }
    }


    private Boolean createSchedule(List<MyDataClass> myDataList, int[][][] intervalArrayInt) {

        for (int i = 0; i < myDataList.size(); i++) {
            for (int j = 0; j < intervalArrayInt[0].length - 1; j++) {
                for (int x = 0; x < 3; x++) {
                    //家庭訪問の●日目が保護者の第一希望日かを判定する
                    //まだスケジュールを割り当てていない保護者かを判定する
                    if (date[x].equals(myDataList.get(i).getStartDateString()) && myDataList.get(i).getSchedule() == 0) {
                        checkSchedule(myDataList, intervalArrayInt, i, j, x, myDataList.get(i).getStartDateString());
                        break;
                    }
                }
            }

        }

        for (int i = 0; i < myDataList.size(); i++) {
            if (myDataList.get(i).getSchedule() == 0) {//重複により割り当てがされていない保護者がいないかの確認
                return false;
            }
        }
        return true;
    }

    private boolean secondCreateSchedule(List<MyDataClass> myDataList, int[][][] intervalArrayInt) {
        for (int i = 0; i < myDataList.size(); i++) {
            for (int j = 0; j < intervalArrayInt[0].length - 1; j++) {
                for (int x = 0; x < 3; x++) {
                    //家庭訪問の●日目が保護者の第一希望日かを判定する
                    //まだスケジュールを割り当てていない保護者かを判定する
                    if (date[x].equals(myDataList.get(i).getSecondDayStartDateString()) && myDataList.get(i).getSchedule() == 0) {
                        checkSchedule(myDataList, intervalArrayInt, i, j, x, myDataList.get(i).getSecondDayStartDateString());
                    }
                }
            }
        }

        for (int i = 0; i < myDataList.size(); i++) {
            if (myDataList.get(i).getSchedule() == 0) {//重複により割り当てがされていない保護者がいないかの確認
                return false;
            }
        }
        return true;
    }

    private void checkSchedule(List<MyDataClass> myDataList, int[][][] intervalArrayInt, int i, int j, int x, String desiredDateString) {
        //保護者の希望時間の開始と終了の間にまだ保護者の割り当てがされていないスケジュールの空き時間があるかの判定
        if (intervalArrayInt[x][j][0] >= Integer.parseInt(myDataList.get(i).getParentStartTimeString()) && intervalArrayInt[x][j + 1][0] <= Integer.parseInt(myDataList.get(i).getParentEndTimeString()) && intervalArrayInt[x][j][1] == 0) {
            intervalArrayInt[x][j][1] += 1;//その時間が割り当て済みでありこと
            myDataList.get(i).setSchedule(Integer.parseInt(desiredDateString.substring(4, 8) + intervalArrayInt[x][j][0]));//スケジュールをmyDataListに入れる(例:6041240(6月4日12時40分))
        }
    }

    private void sortSchedule(List<MyDataClass> myDataList) {
        Comparator<MyDataClass> comparator = Comparator.comparing(MyDataClass::getSchedule);
        //スケジュールを元にmyDataListをソートする
        myDataList.sort(comparator);
    }


    private String geocodeAddress(List<MyDataClass> myDataList, Context context) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            for (int i = 0; i < myDataList.size(); i++) {
                List<Address> addressesList = geocoder.getFromLocationName(myDataList.get(i).getAddress().toString(), 1);
                if (addressesList != null && !addressesList.isEmpty()) {
                    Address addressResult = addressesList.get(0);
                    //保護者の住所を緯度経度に変換する
                    double latitudeDouble = addressResult.getLatitude();
                    double longitudeDouble = addressResult.getLongitude();
                    //保護者の住所の緯度経度をmyDataListに追加する
                    myDataList.get(i).setLatLngString(String.valueOf(new LatLng(latitudeDouble, longitudeDouble)));
                }
            }
            //SetUpで設定した家庭訪問の開始地点を緯度経度に変換
            String startPointLatLngString = String.valueOf(geocoder.getFromLocationName(startPointString, 1));
            Log.d("CreateSchedule", "startPointLatLngString" + startPointLatLngString);
            return startPointLatLngString;
        } catch (IOException e) {
            Log.e("CreateSchedule", "緯度経度の取得に失敗: " + e);
        }
        return null;
    }

    private void outPutLogSchedule(List<MyDataClass> myDataList) {
        for (int i = 0; i < myDataList.size(); i++) {
            Log.d("CreateSchedule:outPutLogSchedule", "(index: " + i + ") data: " + myDataList.get(i));
            Log.d("CreateSchedule:outPutLogSchedule", "(index: " + i + ") Schedule: " + myDataList.get(i).getSchedule());
            Log.d("CreateSchedule", "(index: " + i + ") LatLng" + myDataList.get(i).getLatLngString());
        }
    }
}
