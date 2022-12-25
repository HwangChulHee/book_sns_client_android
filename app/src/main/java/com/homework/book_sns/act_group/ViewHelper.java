package com.homework.book_sns.act_group;

import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

public class ViewHelper {
    VideoTrack videoTrack;
    SurfaceViewRenderer videoView;

    public ViewHelper(VideoTrack videoTrack, SurfaceViewRenderer videoView) {
        this.videoTrack = videoTrack;
        this.videoView = videoView;
    }
}
