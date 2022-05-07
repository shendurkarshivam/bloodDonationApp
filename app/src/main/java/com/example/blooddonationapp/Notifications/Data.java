
package com.example.blooddonationapp.Notifications;

import android.content.Intent;

public class Data {
    private String user;
    private String body;
    private String title;
    private String sent;
    private String type;
    private String topicId;

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    private Intent intent;

    public Data(String user, String body, String title, String sent, String type, String topicId) {
        this.user = user;
        //this.icon = icon;
        this.body = body;
        this.title = title;
        this.sent = sent;
        this.type=type;
        this.topicId=topicId;
    }

    public Data(String user, String body, String title, String sent, Intent intent) {
        this.user = user;
        this.body = body;
        this.title = title;
        this.sent = sent;
        this.intent = intent;
    }

    public Data() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }
}
