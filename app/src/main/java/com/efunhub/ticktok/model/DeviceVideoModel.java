package com.efunhub.ticktok.model;

import android.net.Uri;

public class DeviceVideoModel {
    String videoTitle;
    String videoDuration;
    Uri videoUri;
    String selectionFlag="0";

    public String getSelectionFlag() {
        return selectionFlag;
    }

    public void setSelectionFlag(String selectionFlag) {
        this.selectionFlag = selectionFlag;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }
}
