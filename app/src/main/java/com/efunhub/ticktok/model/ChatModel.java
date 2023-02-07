package com.efunhub.ticktok.model;

public class ChatModel {
    Integer flag;
    String Msg;

    public ChatModel(Integer flag, String Msg){
        this.flag = flag;
        this.Msg = Msg;

    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }
}
