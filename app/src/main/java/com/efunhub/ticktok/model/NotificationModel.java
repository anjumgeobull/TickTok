package com.efunhub.ticktok.model;

import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class NotificationModel extends BaseServiceResponseModel {
    @SerializedName("_id")
    @Expose
    private String id;
     @SerializedName("from_customers")
    @Expose
    private String fromCustomers;
    @SerializedName("to_customers")
    @Expose
    private String toCustomers;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("show_notification_days")
    @Expose
    private String showNotificationDays;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
   @SerializedName("rdate")
    @Expose
    private String rdate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromCustomers() {
        return fromCustomers;
    }

    public void setFromCustomers(String fromCustomers) {
        this.fromCustomers = fromCustomers;
    }

    public String getToCustomers() {
        return toCustomers;
    }

    public void setToCustomers(String toCustomers) {
        this.toCustomers = toCustomers;
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

    public String getShowNotificationDays() {
        return showNotificationDays;
    }

    public void setShowNotificationDays(String showNotificationDays) {
        this.showNotificationDays = showNotificationDays;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getRdate() {
        return rdate;
    }

    public void setRdate(String rdate) {
        this.rdate = rdate;
    }
}
