package com.homework.book_sns;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.homework.book_sns.act_chatting.activity_chatting_list;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.Noti_info;
import com.homework.book_sns.javaclass.Noti_msg;
import com.homework.book_sns.javaclass.User_info;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class service_noti extends Service {

    private static String TAG = "hch";
    private final String SERVICE_NAME = "service_noti";
    private final String SERVICE_FUNCTION = "알림 처리";


    private static Thread notiThread;

    public static Socket notiSocket;
    public static PrintWriter notiWriter;
    public static BufferedReader notiReader;

    Intent serviceIntent;
    int serviceFlags;
    int serviceStartId;

    // Channel에 대한 id 생성 : Channel을 구부하기 위한 ID 이다.
    private static final String CHANNEL_ID = "8889";
    // Channel을 생성 및 전달해 줄 수 있는 Manager 생성
    private NotificationManager mNotificationManager;
    // Notivication에 대한 ID 생성
    private static final int NOTIFICATION_ID = 8889;


    public service_noti() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(TAG, "알림 onBind: ");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log_service("1. 알림 onCreate: ");

        setNotiThread();
        notiThread.start(); // 소켓 및 스트림, 수신 쓰레드 설정
        enter_noti_server(); // 입장 신호 보냄

        set_noti(); // 알림 설정 메서드
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        log_service("2. 알림 onStartCommand: ");
        serviceIntent = intent;
        serviceFlags = flags;
        serviceStartId = startId;


        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        log_service("알림 onDestroy: ");
    }

    // 소켓을 초기화 시켜주고 버퍼를 초기화 시켜준다.
    private void setNotiThread() {

        // 소켓을 초기화 시켜주고 버퍼를 초기화 시켜준다.
        Thread setThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    InetAddress severAddr = InetAddress.getByName(MyVolleyConnection.IP);
                    notiSocket = new Socket(severAddr, MyVolleyConnection.NOTI_PORT);
                    notiWriter = new PrintWriter(notiSocket.getOutputStream());
                    notiReader = new BufferedReader(new InputStreamReader(notiSocket.getInputStream()));

                    log_service("1-1-1 setNotiThread의 쓰레드 실행 후, 버퍼 초기화 완료");
                }catch (Exception e){
                    log_service("1-1-2 setNotiThread의 쓰레드 실행 후, 버퍼 초기화시 오류"+e);
                    e.printStackTrace();
                }
            }
        });

        try{
            setThread.start();
            log_service("1-1 setNotiThread의 setThread 시작");

            setThread.join();
            log_service("1-2 setNotiThread의 setThread 종료");
        }catch (Exception e){
            log_service("setTread 관련 오류"+e);
        }

        // 해당 쓰레드를 통해 알림을 받아준다.
        notiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                log_service("1-3 알림받는 쓰레드(notiThread) 시작");
                try{
                    while(true){
                        String msgJsonText = notiReader.readLine();
                        log_service("알림받음(notiThread) : "+msgJsonText);
                        receive_noti(msgJsonText); // 알림을 받았을 때 처리해주는 메서드
                    }

                }catch (Exception e){
                    log_service("알림 받는 쓰레드(notiThread) 종료");
                    e.printStackTrace();
                }

            }
        });
    }

    // 알림을 받았을 때 처리해주는 메서드 : setNotiThread() 안에서 쓰인다.
    private void receive_noti(String msgJsonText) {
        log_service("receive_noti 실행");
        
        Noti_msg noti_msg = new Noti_msg();
        Gson gson = new Gson();
        noti_msg = gson.fromJson(msgJsonText, Noti_msg.class);
        Noti_info noti_info = gson.fromJson(noti_msg.getContent(), Noti_info.class);

        if(noti_info.getTarget_user_id() == noti_info.getUser_id()) {
            return; // 자기 자신은 알림 제외.
        }


        // 알림 활성화 여부에 따라.. 노티 생성 여부가 결정된다.
        if(noti_info.isTarget_noti_active()) {
            log_service("노티 발생 : receive_noti");
            make_noti(noti_info); // 노티를 만들어 주는 메서드
        }

        if(fragment_noti.fragment_noti != null) {
            log_service("fragment_noti 활성화 상태 - 알림 프래그먼트로 알림 전달 receive_noti");
            Intent intent = new Intent(getApplicationContext(), activity_home_main.class);
            intent.putExtra("noti_info", noti_msg.getContent());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void set_noti() {
        //notification manager 생성
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 기기(device)의 SDK 버전 확인 ( SDK 26 버전 이상인지 - VERSION_CODES.O = 26)
        if(android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.O){
            //Channel 정의 생성자( construct 이용 )
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,"Test Notification",mNotificationManager.IMPORTANCE_HIGH);
            //Channel에 대한 기본 설정
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            // Manager을 이용하여 Channel 생성
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

    }

    private void make_noti(Noti_info noti_info) {

        String noti_title = null;
        String noti_text = noti_info.getNoti_content();
        Intent notificationIntent = null;

        if(noti_info.getNoti_type().equals("추천")){

            noti_title = "추천 알림";
            if(activity_home_main.ACTIVITY_HOME_MAIN != null){
                notificationIntent = new Intent(this,
                        com.homework.book_sns.act_review.activity_review_read_detail.class);
            }else{
                notificationIntent = new Intent(this,
                        activity_home_main.class);
            }
            notificationIntent.putExtra("type", "noti");
            notificationIntent.putExtra("noti_type", "추천");
            notificationIntent.putExtra("review_board_id", String.valueOf(noti_info.getNoti_page_id()));
            notificationIntent.putExtra("noti_info", noti_info);

        } else if(noti_info.getNoti_type().equals("댓글")) {

            noti_title = "댓글 알림";
            if(activity_home_main.ACTIVITY_HOME_MAIN != null){
                notificationIntent = new Intent(this,
                        com.homework.book_sns.act_review.activity_review_read_detail.class);
            }else{
                notificationIntent = new Intent(this,
                        activity_home_main.class);
            }
            notificationIntent.putExtra("type", "noti");
            notificationIntent.putExtra("noti_type", "댓글");
            notificationIntent.putExtra("review_board_id", String.valueOf(noti_info.getNoti_page_id()));
            notificationIntent.putExtra("noti_info", noti_info);


        } else if(noti_info.getNoti_type().equals("답글")) {



        } else if(noti_info.getNoti_type().equals("팔로우")){

            noti_title = "팔로우 알림";
            if(activity_home_main.ACTIVITY_HOME_MAIN != null){
                notificationIntent = new Intent(this,
                        activity_member_page.class);
            }else{
                notificationIntent = new Intent(this,
                        activity_home_main.class);
            }
            notificationIntent.putExtra("type", "noti");
            notificationIntent.putExtra("noti_type", "팔로우");
            User_info user_info = new User_info(String.valueOf(noti_info.getUser_id()),
                    noti_info.getUser_name(), noti_info.getUser_profile());
            notificationIntent.putExtra("user_info", user_info);
            notificationIntent.putExtra("noti_info", noti_info);

        }

        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(noti_title)
                .setContentText(noti_text)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);

        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

    private void enter_noti_server() {
        log_service("1-3 enter_noti_server() 메서드 시작");
        Gson gson = new Gson();
        Noti_msg noti_msg = new Noti_msg("enter",
                LoginSharedPref.getUserId(getApplicationContext()));
        String enterJson =  gson.toJson(noti_msg);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    log_service("1-3 enter_noti_server(), 알림 서버에 enter 발송");
                    service_noti.notiWriter.println(enterJson);
                    service_noti.notiWriter.flush();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void exit_noti_server() {
        Log.d(TAG, "exper_noti_server: ");
        Gson gson = new Gson();

        Noti_msg noti_msg = new Noti_msg("enter",
                "8888888");
        String exitJson =  gson.toJson(noti_msg);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Log.d(TAG, "exper 발송: ");
                    service_noti.notiWriter.println(exitJson);
                    service_noti.notiWriter.flush();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void log_service(String msg) {
        Log.d(TAG, "서비스 이름: "+SERVICE_NAME +", 서비스 기능 : "+SERVICE_FUNCTION
                +", 로그 내용 : "+msg);
    }

}