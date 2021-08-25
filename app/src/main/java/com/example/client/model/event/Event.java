package com.example.client.model.event;

import android.graphics.Bitmap;

public class Event {
    private String AchieveDataPhone;

    public Event(String achieveDataPhone) {
        AchieveDataPhone= achieveDataPhone;
    }


    public String getAchieveDataPhone() {
        return AchieveDataPhone;
    }

    public void setAchieveDataPhone(String achieveDataPhone) {
        AchieveDataPhone = achieveDataPhone;
    }
}
