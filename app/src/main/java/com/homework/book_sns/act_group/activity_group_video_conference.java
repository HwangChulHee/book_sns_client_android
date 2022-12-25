package com.homework.book_sns.act_group;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.homework.book_sns.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.socket.client.Socket;

public class activity_group_video_conference extends AppCompatActivity implements SignallingClient.SignalingInterface,
        View.OnClickListener {

    /* --------------------------- */
    // 액티비티 필수 객체들
    Context aContext;
    String TAG = "hch";
    /* --------------------------- */

    int group_id;

    PeerConnectionFactory peerConnectionFactory;
    MediaConstraints audioConstraints;
    MediaConstraints videoConstraints;
    MediaConstraints sdpConstraints;

    VideoSource frontVideoSource;
    VideoSource backVideoSource;
    VideoTrack frontLocalVideoTrack;
    VideoTrack backLocalVideoTrack;

    AudioSource audioSource;
    AudioTrack localAudioTrack;

    VideoCapturer frontVideoCapturerAndroid;

    SurfaceTextureHelper surfaceTextureHelper;
    MediaStream myStream;

    SurfaceViewRenderer localVideoView;
    SurfaceViewRenderer remoteVideoView;
    SurfaceViewRenderer remoteVideoView2;
    SurfaceViewRenderer remoteVideoView3;
    LinearLayout ll_bottom;

    Button btn_mic;
    Button btn_phone_disabled;
    Button btn_videocam;
    Button btn_switch_video;

    boolean isMirror = true;

    PeerConnection myPeerConnection;
    EglBase rootEglBase;

    boolean gotUserMedia;
    List<PeerConnection.IceServer> peerIceServers = new ArrayList<>();

    final int ALL_PERMISSIONS_CODE = 1;

    String roomName;
    HashMap<String, MediaStream> userStreams = new HashMap<>(); // 상대방의 socket_id와 mediaStream 저장
    HashMap<String, PeerConnection> receiverPCs = new HashMap<>(); // 상대방의 PeerConnection 저장
    ArrayList<String> remoteSocketIds = new ArrayList<>(); // 상대방의 소켓 id 목록
    HashMap<String , ViewHelper> viewHelpers = new HashMap<>(); // 상대방의 소켓 id와 viewHelper(videoTrack 과 videoView)



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_video_conference);

        myInit();
    }

    private void myInit() {
        aContext = this;
        ActionBar bar = getSupportActionBar();
        bar.hide();

        myGetIntent();
        myInitView();
    }

    private void myGetIntent() {
        Intent intent = getIntent();
        group_id = intent.getIntExtra("group_id", 0);
        roomName = String.valueOf(group_id);
        Log.d(TAG, "myGetIntent: ");
    }

    private void myInitView() {
        myCheckPermission();
    }


    public void myCheckPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, ALL_PERMISSIONS_CODE);
        } else {
            // all permissions already granted
            start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ALL_PERMISSIONS_CODE
                && grantResults.length == 2
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            // all permissions granted
            start();
        } else {
            Log.d(TAG, "onRequestPermissionsResult: ");
            start();
//            finish();
        }
    }

    public void start() {
        Log.i(TAG, "start: ");

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initViews();
        initVideos(); // surfaceRenderer에 Eglbase를 통해 초기화 진행한다.
        getIceServers(); // peerIceServer 변수에 ice 서버에 대한 정보를 저장한다.

        setMyPeerConnection(); // PeerConnection에 대한 각종 설정을 한다.
        setMyStream();
        // track에 addsink() 해준다. 오디오는 안해주는데, 추후 해당 부분 확인할것
        //sink가 무슨 의미가 있는지도 공부해야됨.

//        gotUserMedia = true; // 이건 왜 써놨지.. 필요없는거 같다.
        if (SignallingClient.getInstance().isInitiator) {
            SignallingClient.getInstance().getSocketID(roomName);
            // signallingClient가 초기화 되었다면 소켓 ID를 얻기 위해 시그널링 서버에 요청한다.
        }
    }


    private void initViews() {
        localVideoView = findViewById(R.id.local_surface_view);
        remoteVideoView = findViewById(R.id.remote_surface_view);
        remoteVideoView2 = findViewById(R.id.remote2_surface_view);
        remoteVideoView3 = findViewById(R.id.remote3_surface_view);
        ll_bottom = findViewById(R.id.ll_bottom);

        btn_mic = findViewById(R.id.btn_agvc_mic);
        btn_phone_disabled = findViewById(R.id.btn_agvc_phone_disabled);
        btn_videocam = findViewById(R.id.btn_agvc_videocam);
        btn_switch_video = findViewById(R.id.btn_agvc_switch_video);

        btn_mic.setOnClickListener(this);
        btn_phone_disabled.setOnClickListener(this);
        btn_videocam.setOnClickListener(this);
        btn_switch_video.setOnClickListener(this);
    }
    private void initVideos() {
        rootEglBase = EglBase.create();
        localVideoView.init(rootEglBase.getEglBaseContext(), null);
        remoteVideoView.init(rootEglBase.getEglBaseContext(), null);
        remoteVideoView2.init(rootEglBase.getEglBaseContext(), null);
        remoteVideoView3.init(rootEglBase.getEglBaseContext(), null);

        localVideoView.setZOrderMediaOverlay(true);
        remoteVideoView.setZOrderMediaOverlay(true);
        remoteVideoView2.setZOrderMediaOverlay(true);
        remoteVideoView3.setZOrderMediaOverlay(true);
    }
    private void getIceServers() {
        Log.i(TAG, "getIceServers: ");

        String url = "stun:stun.l.google.com:19302";
        PeerConnection.IceServer peerIceServer = PeerConnection.IceServer.builder(url).createIceServer();
        peerIceServers.add(peerIceServer);

        String turnUrl = "turn:15.164.105.239:3478";
        PeerConnection.IceServer turnServer = PeerConnection.IceServer.builder(turnUrl).setUsername("hch").setPassword("4597").createIceServer();
        peerIceServers.add(turnServer);

        Log.d(TAG, "getIceServers: stun 서버 관련 정보 "+peerIceServers.get(0).toString());
        Log.d(TAG, "getIceServers: turn 서버 관련 정보 "+peerIceServers.get(1).toString());
    }

    private void setMyPeerConnection () {
        SignallingClient.getInstance().init(this, roomName);

        //Initialize PeerConnectionFactory globals.
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(this)
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);

        //Create a new PeerConnectionFactory instance - using Hardware encoder and decoder.
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        DefaultVideoEncoderFactory defaultVideoEncoderFactory = new DefaultVideoEncoderFactory(
                rootEglBase.getEglBaseContext(),  /* enableIntelVp8Encoder */true,
                /* enableH264HighProfile */true);
        DefaultVideoDecoderFactory defaultVideoDecoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory();
    }
    private void setMyStream() {
        //Create MediaConstraints - Will be useful for specifying video and audio constraints.
        audioConstraints = new MediaConstraints();
        videoConstraints = new MediaConstraints();

        //Now create a VideoCapturer instance.

        VideoCapturer backVideoCapturerAndroid;

        frontVideoCapturerAndroid = createCameraCapturer(new Camera1Enumerator(false), "front");
//        backVideoCapturerAndroid = createCameraCapturer(new Camera1Enumerator(false), "back");
        //createCameraCapturer를 통해서 카메라의 정보를 가져온다.

        //Create a VideoSource instance
        if (frontVideoCapturerAndroid != null) {
            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());

            frontVideoSource = peerConnectionFactory.createVideoSource(frontVideoCapturerAndroid.isScreencast());
//            backVideoSource = peerConnectionFactory.createVideoSource(backVideoCapturerAndroid.isScreencast());

            // VideoCapturer 를 통해 videoSource를 만들어준다. 옵션에 대해서는.. 일단 스킵.

            frontVideoCapturerAndroid.initialize(surfaceTextureHelper, this, frontVideoSource.getCapturerObserver());
            frontVideoCapturerAndroid.startCapture(1024, 720, 30);

//            backVideoCapturerAndroid.initialize(surfaceTextureHelper, this, backVideoSource.getCapturerObserver());
//            backVideoCapturerAndroid.startCapture(1024, 720, 30);
        }

        frontLocalVideoTrack = peerConnectionFactory.createVideoTrack("100", frontVideoSource);
//        backLocalVideoTrack = peerConnectionFactory.createVideoTrack("100", backVideoSource);


        //create an AudioSource instance
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);


        localVideoView.setVisibility(View.VISIBLE);
        // And finally, with our VideoRenderer ready, we
        // can add our renderer to the VideoTrack.
        frontLocalVideoTrack.addSink(localVideoView); // audioTrack은 왜 추가안해주지.. 싶은데
//        backLocalVideoTrack.addSink(localVideoView); // back은 addsink 해줘야되나.. 뭔가 오류날 것 같은데;


        localVideoView.setMirror(true);
        remoteVideoView.setMirror(true);
        remoteVideoView2.setMirror(true);
        remoteVideoView3.setMirror(true);
    }


    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator, String mode) {
        final String[] deviceNames = enumerator.getDeviceNames();
        // CameraEnumerator 클래스의 getDeviceNames() 메서드를 활용해 deviceNames를 가져온다.

        if(mode.equals("front")) {
            Logging.d(TAG, "Looking for front facing cameras.");
            for (String deviceName : deviceNames) {
                Log.d(TAG, "createCameraCapturer: deviceName"+deviceName);
                if (enumerator.isFrontFacing(deviceName)) {
                    Logging.d(TAG, "Creating front facing camera capturer.");
                    VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                    if (videoCapturer != null) {
                        return videoCapturer;
                    }
                }
            }
        } else if(mode.equals("back")) {
            Logging.d(TAG, "Looking for other cameras.");
            for (String deviceName : deviceNames) {
                if (!enumerator.isFrontFacing(deviceName)) {
                    Logging.d(TAG, "Creating other camera capturer.");
                    VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                    if (videoCapturer != null) {
                        return videoCapturer;
                    }
                }
            }
        }

        return null;
    }

    //connect_start() - ac2. socket_id를 얻은 후, 보내는 사람의 peerConnection을 만들어준다.
    @Override
    public void connect_start() {
        // c1 시작
        createSenderPeerConnection();
        createSenderOffer();
        SignallingClient.getInstance().joinRoom(roomName);
        active_btn(); // 버튼 활성화.
    }

    //c1 - crateSenderPeerConnection
    private void createSenderPeerConnection() {
        Log.i(TAG, "c1 - createSenderPeerConnection: ");

        PeerConnection.RTCConfiguration rtcConfig =
                new PeerConnection.RTCConfiguration(peerIceServers);
        // TCP candidates are only useful when connecting to a server that supports
        // ICE-TCP.
//        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
//        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
//        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
//        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
//
//        // Use ECDSA encryption.
//        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
        myPeerConnection = peerConnectionFactory.createPeerConnection(rtcConfig,
                new CustomPeerConnectionObserver("localPeerCreation") {
                    @Override
                    public void onIceCandidate(IceCandidate iceCandidate) {
                        Log.i(TAG, "c4 - onIceCandidate: candidate 발신 ");
                        super.onIceCandidate(iceCandidate);
                        SignallingClient.getInstance().senderCandidate(iceCandidate);
                    }

                    @Override
                    public void onAddStream(MediaStream mediaStream) {
                        Log.i(TAG, "onAddStream: 서버의 스트림이라 아무것도 하진 않음."+mediaStream);
                        super.onAddStream(mediaStream);
                    }
                });

        // 나의 stream을 얻어온 다음, stream에 오디오와 비디오 트랙을 추가하고 myPeerConnection에 저장해준다.
        // senderPeerConnection을 통해 받아오는 mediaStream은 서버의 stream이기 때문에 상관없다.
        // peerConnection의 세팅을 통해서 stream이 생성되는 것 같다.
        addStreamToLocalPeer();
    }
    //c2 - createSenderOffer
    private void createSenderOffer() {
        Log.i(TAG, "c2 - createSenderOffer: ");
        sdpConstraints = new MediaConstraints();
        sdpConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "false"));
        sdpConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"));
        myPeerConnection.createOffer(new CustomSdpObserver("receiverCreateOffer") {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);
                myPeerConnection.setLocalDescription(new CustomSdpObserver("receiverSetLocalDesc"), sessionDescription);
                SignallingClient.getInstance().senderOffer(sessionDescription, roomName);
            }
        }, sdpConstraints);
    }

    //c3
    @Override
    public void getSenderAnswer(JSONObject sdp) {
        Log.i(TAG, "c3 - getSenderAnswer: ");
        try {
            myPeerConnection.setRemoteDescription(new CustomSdpObserver("myPeerConnectionSetRemote"),
                    new SessionDescription(
                            SessionDescription.Type.fromCanonicalForm(sdp.getString("type").toLowerCase()),
                            sdp.getString("sdp")));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "error : onAnswerReceived: ");
        }
    }
    //c5
    @Override
    public void getSenderCandidate(JSONObject candidate) {
        Log.i(TAG, "c5 - getSenderCandidate: ");
        try {
            myPeerConnection.addIceCandidate(new IceCandidate(
                    candidate.getString("sdpMid"),
                    candidate.getInt("sdpMLineIndex"),
                    candidate.getString("candidate")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //c7 - createReceivePC
    @Override
    public void createReceivePC(String senderSocketID, Socket mySocket) {
        Log.i(TAG, "c7 - createReceivePC: "+senderSocketID);
        PeerConnection receiverPC = createReceiverPeerConnection(senderSocketID, mySocket);
        receiverPCs.put(senderSocketID, receiverPC);
        createReceiverOffer(receiverPC, mySocket, senderSocketID);
    }
    private PeerConnection createReceiverPeerConnection(String senderSocketID, Socket mySocket) {
        PeerConnection receiverPC;

        PeerConnection.RTCConfiguration rtcConfig =
                new PeerConnection.RTCConfiguration(peerIceServers);
        receiverPC = peerConnectionFactory.createPeerConnection(rtcConfig,
                new CustomPeerConnectionObserver("receivePeerCreation") {
                    @Override
                    public void onIceCandidate(IceCandidate iceCandidate) {
                        Log.i(TAG, "c9 - onIceCandidate: candidate 발신 ");
                        super.onIceCandidate(iceCandidate);
                        SignallingClient.getInstance().receiverCandidate(iceCandidate, senderSocketID);
                    }

                    @Override
                    public void onAddStream(MediaStream mediaStream) {
                        Log.i(TAG, "c11 - userStreams에 stream 추가. "+mediaStream);
                        super.onAddStream(mediaStream);
                        userStreams.put(senderSocketID, mediaStream);
                        remoteSocketIds.add(senderSocketID);
                        gotRemoteStream(senderSocketID);
                    }
                });

        return receiverPC;
    }
    private void createReceiverOffer(PeerConnection receiverPC, Socket mySocket, String senderSocketID) {
        sdpConstraints = new MediaConstraints();
        sdpConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        receiverPC.createOffer(new CustomSdpObserver("receiverCreateOffer") {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);
                receiverPC.setLocalDescription(new CustomSdpObserver("receiverSetLocalDesc"), sessionDescription);
                SignallingClient.getInstance().receiverOffer(sessionDescription, senderSocketID, roomName);
            }
        }, sdpConstraints);

    }

    //c8
    @Override
    public void getReceiverAnswer(String senderSocketID, JSONObject sdp) {
        Log.i(TAG, "c8 - getReceiverAnswer: ");
        PeerConnection receiverPC = receiverPCs.get(senderSocketID);
        try {
            receiverPC.setRemoteDescription(new CustomSdpObserver("receiverSetRemote"),
                    new SessionDescription(
                            SessionDescription.Type.fromCanonicalForm(sdp.getString("type").toLowerCase()),
                            sdp.getString("sdp")));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "error : onAnswerReceived: ");
        }
    }
    //c10
    @Override
    public void getReceiverCandidate(String senderSocketID, JSONObject candidate) {
        Log.i(TAG, "c10 - getReceiverCandidate: ");
        PeerConnection receiverPC = receiverPCs.get(senderSocketID);
        try {
            receiverPC.addIceCandidate(new IceCandidate(
                    candidate.getString("sdpMid"),
                    candidate.getInt("sdpMLineIndex"),
                    candidate.getString("candidate")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void userExit(String socket_id) {
        receiverPCs.get(socket_id).close();
        receiverPCs.remove(socket_id);
        userStreams.remove(socket_id);

        for(int i =0; i < remoteSocketIds.size(); i++) {
            if(socket_id.equals(remoteSocketIds.get(i))) {
                remoteSocketIds.remove(i);
            };
        }
        updateView();
    }

    private void addStreamToLocalPeer() {
        //creating local mediastream
        myStream = peerConnectionFactory.createLocalMediaStream("102");
        localAudioTrack.setVolume(1000);
        myStream.addTrack(localAudioTrack);
        myStream.addTrack(frontLocalVideoTrack);
//        myStream.addTrack(backLocalVideoTrack);

        myPeerConnection.addStream(myStream);
        Log.d(TAG, "addStreamToLocalPeer: size ");
    }
    private void gotRemoteStream(String socketID) {
        //we have remote video stream. add to the renderer.
        int viewNum = remoteSocketIds.size(); // remoteSocketIds에 있는 size를 구하고..
        MediaStream stream = userStreams.get(socketID); // socketID에 해당하는 stream을 가져온다.
        VideoTrack videoTrack = stream.videoTracks.get(0);
        Log.d(TAG, "gotRemoteStream: videoTracks의 size "+stream.videoTracks.size());

        SurfaceViewRenderer thisView = changeView(viewNum); // viewNum에 따라 화면 조정

        ViewHelper viewHelper = new ViewHelper(videoTrack, thisView);
        viewHelpers.put(socketID, viewHelper);

        runOnUiThread(() -> {
            try {
                ll_bottom.setVisibility(View.VISIBLE);
                thisView.setVisibility(View.VISIBLE);
                videoTrack.addSink(thisView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private SurfaceViewRenderer changeView(int viewNum){
        SurfaceViewRenderer thisView;

        if(viewNum == 1) { // 상대방이 한 명일 때, 총 화면은 2개
            thisView = remoteVideoView;

        } else if(viewNum == 2) { // 상대방이 2일 때, 총 화면은 3
            thisView = remoteVideoView2;

        } else if(viewNum == 3) { // 상대방이 3일 때, 총 화면은 4
            thisView = remoteVideoView3;

        } else {
            thisView = null;
        }

        return thisView;
    }

    private void updateView() {

        runOnUiThread(() -> {
            try {
                remoteVideoView.setVisibility(View.GONE);
                remoteVideoView2.setVisibility(View.GONE);
                remoteVideoView3.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }); // 다 화면을 제거해주고..


        if(remoteSocketIds.size() == 0) { //원격 접속자가 없을때의 changeSizeView
            runOnUiThread(() -> {
                try {
                    ll_bottom.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } else { //원격 접속자가 있을 때..

            for(int i=0; i<remoteSocketIds.size(); i++) {

                String socketID = remoteSocketIds.get(i);
                MediaStream stream = userStreams.get(socketID); // socketID에 해당하는 stream을 가져오고
                VideoTrack videoTrack = stream.videoTracks.get(0); // 해당 스트림의 videoTack을 넣어준다.


                SurfaceViewRenderer thisView = changeView(i+1);
                runOnUiThread(() -> {
                    try {
                        VideoTrack removeTrack = viewHelpers.get(socketID).videoTrack;
                        SurfaceViewRenderer removeView = viewHelpers.get(socketID).videoView;
                        removeView.setVisibility(View.GONE);
                        removeTrack.removeSink(removeView); // 나간 사람의 뷰를 gone 시켜준다.

                        thisView.setVisibility(View.VISIBLE);
                        videoTrack.addSink(thisView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }


    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: 메서드 시작");
        switch(view.getId()){
            case R.id.btn_agvc_mic:
                if(btn_mic.isSelected()) {
                    btn_mic.setSelected(false);
                    btn_mic.setEnabled(false);
                } else {
                    btn_mic.setSelected(true);
                    btn_mic.setEnabled(false);
                }
                click_mic_btn();
                btn_mic.setEnabled(true);
                break;
            case R.id.btn_agvc_phone_disabled:
                finish();
                break;
            case R.id.btn_agvc_videocam:
                if(btn_videocam.isSelected()) {
                    btn_videocam.setSelected(false);
                    btn_videocam.setEnabled(false);
                } else {
                    btn_videocam.setSelected(true);
                    btn_videocam.setEnabled(false);
                }
                click_videocam_btn();
                btn_videocam.setEnabled(true);
                break;
            case R.id.btn_agvc_switch_video:
                if(btn_switch_video.isSelected()) {
                    btn_switch_video.setSelected(false);
                    btn_switch_video.setEnabled(false);
                    Log.d(TAG, "onClick: false");
                } else {
                    btn_switch_video.setSelected(true);
                    btn_switch_video.setEnabled(false);
                    Log.d(TAG, "onClick: true");
                }
                click_video_switch_btn();
                btn_switch_video.setEnabled(true);
                break;
            default:
                break;
        }
    }

    private void click_mic_btn() {
        int size = myStream.audioTracks.size(); // stream의 videoTracks의 크기를 구해준다.
        Log.d(TAG, "click_volume_btn: "+size);
        for (int i = 0; i < size; i++){
            myStream.audioTracks.get(i).setEnabled(!myStream.audioTracks.get(i).enabled());
            //audioTrack의 활성화 => 비활성화 또는 로 비활성화 => 활성화로 바꾸어준다.
        }
    }
    private void click_videocam_btn() {
        int size = myStream.videoTracks.size(); // stream의 videoTracks의 크기를 구해준다.
        Log.d(TAG, "click_videocam_btn: "+size);
        for (int i = 0; i < size; i++){
            myStream.videoTracks.get(i).setEnabled(!myStream.videoTracks.get(i).enabled());
            //videoTrack의 활성화 => 비활성화 또는 로 비활성화 => 활성화로 바꾸어준다.
        }
    }
    private void click_video_switch_btn() {
        Log.d(TAG, "click_video_switch_btn: ");

        CameraVideoCapturer cameraVideoCapturer = (CameraVideoCapturer) frontVideoCapturerAndroid;
        cameraVideoCapturer.switchCamera(null);
        isMirror = ! isMirror;
//        localVideoView.setMirror(isMirror);
    }


    private void active_btn() {

        runOnUiThread(() -> {
            try {
                btn_mic.setEnabled(true);
                btn_videocam.setEnabled(true);
                btn_switch_video.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        if(SignallingClient.getInstance().isInitiator) {
            SignallingClient.getInstance().myDisconnect();
        }
    }
}