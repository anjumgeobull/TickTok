package com.efunhub.ticktok.model;

public class VideoSpeedModel {
    public String videoSpeed;
    public String selectionFlag="0";

    public VideoSpeedModel(String videoSpeed, String selectionFlag) {
        this.videoSpeed = videoSpeed;
        this.selectionFlag=selectionFlag;
    }

    public String getVideoSpeed() {
        return videoSpeed;
    }

    public void setVideoSpeed(String videoSpeed) {
        this.videoSpeed = videoSpeed;
    }

    public String getSelectionFlag() {
        return selectionFlag;
    }

    public void setSelectionFlag(String selectionFlag) {
        this.selectionFlag = selectionFlag;
    }
}
