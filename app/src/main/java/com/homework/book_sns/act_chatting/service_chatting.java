package com.homework.book_sns.act_chatting;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.homework.book_sns.javaclass.Chatting_roomList_ofClient;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class service_chatting extends Service {

    private static Thread chattingThread;

    public static Socket socket;
    public static PrintWriter senWriter;
    public static BufferedReader receiveChat;

    public service_chatting() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        chattingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress severAddr = InetAddress.getByName(MyVolleyConnection.IP);
                    socket = new Socket(severAddr, MyVolleyConnection.CHAT_PORT);
                    senWriter = new PrintWriter(socket.getOutputStream());
                    senWriter.println(LoginSharedPref.getUserId(getApplicationContext())); // user_id를 보내준다.
                    senWriter.flush();
                    Log.d("hch", "run:************** "+LoginSharedPref.getUserId(getApplicationContext()));


                    receiveChat = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while (true) {
                        String jsonText = receiveChat.readLine();
                        //
                        if(jsonText != null && (activity_chatting_room.act_chatting_room != null)) {
                            sendChat_toChatRoom(jsonText);
                            Log.d("hch", "run: "+jsonText);
                        } else if(jsonText != null && (activity_chatting_list.act_chatting_list != null)) {
                            sendChat_toChatList(jsonText);
                        }
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        chattingThread.start();


        return super.onStartCommand(intent, flags, startId);
    }


    private void sendChat_toChatRoom(String msg) {
        Intent intent = new Intent(getApplicationContext(), activity_chatting_room.class);
        intent.putExtra("msg_from_service", msg);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void sendChat_toChatList(String msg) {
        Intent intent = new Intent(getApplicationContext(), activity_chatting_list.class);
        intent.putExtra("msg_from_service", msg);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        new Thread() {
            @Override
            public void run() {
                super.run();


                Thread.currentThread().interrupt();
                if(chattingThread != null) {
                    chattingThread.interrupt();
                    chattingThread = null;
                }

                if(socket != null) {
                    try {
                        if(receiveChat != null) {
                            receiveChat.close();
                        }
                        socket.close();
                        senWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();

        Log.d("hch", "onDestroy: ");

    }
}