package com.homework.book_sns.act_group;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.net.URISyntaxException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Webrtc_Step3
 * Created by vivek-3102 on 11/03/17.
 */

class SignallingClient {
    private static final String TAG = "hch";

    private static SignallingClient instance;
    private String roomName = null;
    private Socket socket;
    private String socketID;
    boolean isChannelReady = false;
    boolean isInitiator = false;
    boolean isStarted = false;
    boolean isCaller = false;
    boolean isCallee = false;
    boolean isCandidate = true;
    private SignalingInterface callback;

    //This piece of code should not go into production!!
    //This will help in cases where the node server is running in non-https server and you want to ignore the warnings
    @SuppressLint("TrustAllX509TrustManager")
    private final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }

        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType) {
        }
    }};

    public static SignallingClient getInstance() {
//        Log.i(TAG, "getInstance: ");
        
        if (instance == null) {
            instance = new SignallingClient();
            Log.i(TAG, "getInstance: instance 생성");
        }
        if (instance.roomName == null) {
            //set the room name here
            instance.roomName = "vivek17";
        }
        return instance;
    }

    public void init(SignalingInterface signalingInterface, String roomName) {
        Log.i(TAG, "init: ");

        this.callback = signalingInterface;
        this.roomName = roomName;

        try {
            //set the socket.io url here
            socket = IO.socket("https://booksns.tk:3000/");
            socket.connect();
            isInitiator = true;

            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("socket", socket.toString());
                Log.d(TAG, "socket id 값: "+jsonObject);

            }catch (Exception e){
                Log.d(TAG, "json error: ");
            }

            //ac2 - socket ID 받은 후 connect_start() - socket.on("socket_id_response")
            socket.on("socket_id_response" , data -> {
                try{
                    JSONObject jsonData = (JSONObject) data[0];
                    socketID = jsonData.getString("socket_id");

                    Log.i(TAG, "ac2 - socket ID 받기 - socket.on(\"socket_id_response\") : "+socketID);
                    callback.connect_start();

                }catch (Exception e){
                    Log.e(TAG, "json 오류", e);
                }

            });

            //c0 - socket.on("allUsers")
            socket.on("allUsers", data -> {
                try{
                  JSONObject jsonData = (JSONObject) data[0];
                  JSONArray anotherMemberInfos = jsonData.getJSONArray("users");
                  int memberCount = anotherMemberInfos.length();

                  Log.i(TAG, "c0 - socket.on(\"allUsers\") "+jsonData);
                  for (int i = 0; i < memberCount; i++){
                      JSONObject senderMember = anotherMemberInfos.getJSONObject(i);
                      String senderMemberID = senderMember.getString("id");
                      callback.createReceivePC(senderMemberID , socket);
                  }

                }catch (Exception e){
                    Log.e(TAG, "json 오류", e);
                }
            });

            //c3 - socket.on("getSenderAnswer")
            socket.on("getSenderAnswer", data -> {
                try{
                    JSONObject jsonData = (JSONObject) data[0];
                    Log.i(TAG, "c3 - socket.on(\"getSenderAnswer\"): "+jsonData);
                    JSONObject sdpData = jsonData.getJSONObject("sdp");
                    callback.getSenderAnswer(sdpData);

                }catch (Exception e){
                    Log.e(TAG, "json 오류", e);
                }
            });
            //c5 - socket.on("getSenderCandidate")
            socket.on("getSenderCandidate", data -> {
                try{
                    JSONObject jsonData = (JSONObject) data[0];
                    Log.i(TAG, "c5 - socket.on(\"getSenderCandidate\"): "+jsonData);
                    JSONObject candidate = jsonData.getJSONObject("candidate");

                    callback.getSenderCandidate(candidate);
                }catch (Exception e){

                }


            });

            //c6 - socket.on("userEnter")
            socket.on("userEnter", data -> {
                try{
                    JSONObject jsonData = (JSONObject) data[0];
                    Log.i(TAG, "c6 - socket.on(\"userEnter\") : "+jsonData);
                    callback.createReceivePC(jsonData.getString("id"), socket);
                }catch (Exception e){

                }

            });

            //c8 - socket.on("getReceiverAnswer")
            socket.on("getReceiverAnswer", data -> {
                Log.i(TAG, "c8 - socket.on(\"getReceiverAnswer\"): "+data);
                try{
                    JSONObject jsonData = (JSONObject) data[0];
                    String senderMemberID = jsonData.getString("id");
                    JSONObject sdpData = jsonData.getJSONObject("sdp");
                    callback.getReceiverAnswer(senderMemberID, sdpData);

                }catch (Exception e){
                    Log.e(TAG, "json 오류", e);
                }
            });
            //c10 - socket.on("getReceiverCandidate")
            socket.on("getReceiverCandidate", data -> {
                try{
                    JSONObject jsonData = (JSONObject) data[0];
                    Log.i(TAG, "c10 - socket.on(\"getReceiverCandidate\"): "+jsonData);

                    String senderID = jsonData.getString("id");
                    JSONObject candidate = jsonData.getJSONObject("candidate");

                    callback.getReceiverCandidate(senderID, candidate);

                }catch (Exception e){
                    Log.e(TAG, "json 오류", e);
                }
            });
            //dc2 - socket.on("userExit")
            socket.on("userExit", data -> {
                try{
                    JSONObject jsonData = (JSONObject) data[0];
                    Log.i(TAG, "dc2 - socket.on(\"userExit\") : "+jsonData);
                    String socket_id = jsonData.getString("id");
                    callback.userExit(socket_id);

                }catch (Exception e){

                }


            });


        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /*SFU 방식으로 새로만든 메서드*/
    public void joinRoom(String roomName) {
        try{
            JSONObject object = new JSONObject();
            object.put("id", socketID);
            object.put("roomID", roomName);

            socket.emit("joinRoom", object);

        }catch (Exception e){

        }

    }

    //ac1 - socket 얻기 - emit("socket_id_request")
    public void getSocketID(String roomID){
        try {
            JSONObject object = new JSONObject();
            object.put("roomID", roomID);

            socket.emit("socket_id_request", object);
            Log.i(TAG, "ac1 - socket 얻기 - emit(\"socket_id_request\")"+object);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //c2 - emit("senderOffer")
    public void senderOffer(SessionDescription sdp, String roomID) {
        try {
            JSONObject object = new JSONObject();
            JSONObject sdpObject = new JSONObject();
            sdpObject.put("type", sdp.type.canonicalForm());
            sdpObject.put("sdp", sdp.description);

            object.put("sdp", sdpObject);
            object.put("roomID", roomID);
            object.put("senderSocketID", socketID);


            socket.emit("senderOffer", object);
            Log.i(TAG, "c2 - emit(\"senderOffer\") : "+object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //c4 - emit("senderCandidate")
    public void senderCandidate(IceCandidate iceCandidate) {
        try {
            JSONObject object = new JSONObject();
            object.put("senderSocketID", socketID);

            JSONObject iceObject = new JSONObject();
            iceObject.put("candidate", iceCandidate.sdp);
            iceObject.put("sdpMid", iceCandidate.sdpMid);
            iceObject.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);

            object.put("candidate", iceObject);

            socket.emit("senderCandidate", object);
            Log.i(TAG, "c4 - emit(\"senderCandidate\"): "+object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //c7 - emit("receiverOffer")
    public void receiverOffer(SessionDescription sdp, String senderSocketID, String roomID) {
        try {
            JSONObject object = new JSONObject();
            JSONObject sdpObject = new JSONObject();
            sdpObject.put("type", sdp.type.canonicalForm());
            sdpObject.put("sdp", sdp.description);

            object.put("sdp", sdpObject);
            object.put("roomID", roomID);
            object.put("receiverSocketID", socketID);
            object.put("senderSocketID", senderSocketID);


            socket.emit("receiverOffer", object);
            Log.i(TAG, "c7 - emit(\"receiverOffer\") : "+object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //c9 - emit("receiverCandidate")
    public void receiverCandidate(IceCandidate iceCandidate, String senderSocketID) {
        try {
            JSONObject object = new JSONObject();
            object.put("receiverSocketID", socketID);
            object.put("senderSocketID", senderSocketID);

            JSONObject iceObject = new JSONObject();
            iceObject.put("candidate", iceCandidate.sdp);
            iceObject.put("sdpMid", iceCandidate.sdpMid);
            iceObject.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);

            object.put("candidate", iceObject);

            socket.emit("receiverCandidate", object);
            Log.i(TAG, "c9 - emit(\"receiverCandidate\"): "+object);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void myDisconnect() {
        socket.emit("myDisconnect");
        socket.disconnect();
        socket.close();
    }

    interface SignalingInterface {

        void connect_start();

        //c7 - createReceiverPC
        void createReceivePC(String senderSocketID, Socket mySocket);

        //c3
        void getSenderAnswer(JSONObject sdp);
        //c5
        void getSenderCandidate(JSONObject candidate);

        //c8
        void getReceiverAnswer(String senderSocketID, JSONObject sdp);
        //c10
        void getReceiverCandidate(String senderSocketID, JSONObject candidate);

        void userExit(String socket_id);

    }
}
