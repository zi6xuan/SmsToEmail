package com.jason.app.smstoemail.views;

import android.telephony.SmsMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class SmsMsg {

    private long timestampMillis;
    private String originatingAddress;
    private String messageBody;

    public SmsMsg(long timet, String ads, String msg) {
        timestampMillis = timet;
        originatingAddress = ads;
        messageBody = msg;
    }

    public SmsMsg(JSONObject json) throws JSONException {
        this(json.getLong("timestamp"), json.getString("address"), json.getString("msgBody"));
    }

    public SmsMsg(SmsMessage msg) {
        this(msg.getTimestampMillis(), msg.getOriginatingAddress(), msg.getMessageBody());
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    public String getOriginatingAddress() {
        return originatingAddress;
    }

    public String getBody() {
        return messageBody;
    }

    public void setBody(String body) {
        this.messageBody = body;
    }

    public JSONObject toJson() {
        try {
            JSONObject json = new JSONObject();
            json.put("timestamp", timestampMillis);
            json.put("address", originatingAddress);
            json.put("msgBody", messageBody);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
