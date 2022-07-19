package com.homework.book_sns.act_chatting;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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

                    receiveChat = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while (true) {
                        String jsonText = receiveChat.readLine();
                        //
                        if(jsonText != null && (activity_chatting_room.act_chatting_room != null)) {
                            sendChat_toAct(jsonText);
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

    private void sendChat_toAct(String msg) {
        Intent intent = new Intent(getApplicationContext(), activity_chatting_room.class);
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

    }
}