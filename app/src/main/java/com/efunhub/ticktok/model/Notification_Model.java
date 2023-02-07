package com.efunhub.ticktok.model;

import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;

import java.util.ArrayList;

public class Notification_Model extends BaseServiceResponseModel {

//        private float status;
//        private String msg;
    //ArrayList< Object > data = new ArrayList < Object > ();

    public ArrayList<NotificationList> data;

    public ArrayList<NotificationList> getData() {
        return data;
    }

    public void setData(ArrayList<NotificationList> data) {
        this.data = data;
    }

    //        // Getter Methods
//
//        public float getStatus() {
//            return status;
//        }
//
//        public String getMsg() {
//            return msg;
//        }
//
//        // Setter Methods
//
//        public void setStatus(float status) {
//            this.status = status;
//        }
//
//        public void setMsg(String msg) {
//            this.msg = msg;
//        }
    public static class NotificationList {

        public String _id;
        public String from_customers;
        public String to_customers;
        public String title;
        public String message;
        public String show_notification_days;
        public String rdate;
        public String updated_at;
        public String created_at;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getFrom_customers() {
            return from_customers;
        }

        public void setFrom_customers(String from_customers) {
            this.from_customers = from_customers;
        }

        public String getTo_customers() {
            return to_customers;
        }

        public void setTo_customers(String to_customers) {
            this.to_customers = to_customers;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getShow_notification_days() {
            return show_notification_days;
        }

        public void setShow_notification_days(String show_notification_days) {
            this.show_notification_days = show_notification_days;
        }

        public String getRdate() {
            return rdate;
        }

        public void setRdate(String rdate) {
            this.rdate = rdate;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }

}

