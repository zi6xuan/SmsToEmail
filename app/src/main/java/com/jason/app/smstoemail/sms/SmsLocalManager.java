package com.jason.app.smstoemail.sms;

import android.content.Context;

import com.jason.app.smstoemail.views.SmsMsg;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class SmsLocalManager {
    private static SmsLocalManager mInstance = null;
    private static String SMS_LOCAL = "sms_local.json";

    private int mMaxCount = 1000;
    //
    private Context mContext = null;
    private JSONArray mJsonArray = new JSONArray();
    private boolean mMerger=false;

    public static SmsLocalManager getInstace() {
        if (mInstance == null) {
            mInstance = new SmsLocalManager();
        }
        return mInstance;
    }

    public void init(Context con) {
        mContext = con;
    }

    public void setMaxCount(int maxCount) {
        this.mMaxCount = maxCount;
    }

    public void add(SmsMsg msg) {
        mJsonArray.put(msg.toJson());
        save();
    }

    //
    public ArrayList<SmsMsg> load() {
        ArrayList<SmsMsg> arrayList = new ArrayList<>();
        try {
            InputStream is = mContext.openFileInput(SMS_LOCAL);
            byte buf[] = new byte[4096];
            int len = 0;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((len = is.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
            bos.flush();
            bos.close();
            is.close();
            String text = bos.toString("UTF-8");
            JSONArray jary = mJsonArray = new JSONArray(text);
            for (int i = 0; i < jary.length(); i++) {
                JSONObject json = jary.getJSONObject(i);
                arrayList.add(new SmsMsg(json));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private void save() {
        try {
            FileOutputStream fos = mContext.openFileOutput(SMS_LOCAL, Context.MODE_PRIVATE);
            //删除最早的条数，保证不超过MAX_COUNT
            JSONArray jary = new JSONArray(mJsonArray.toString());
            int len = jary.length() - mMaxCount;
            for (int i = 0; i < len; i++) {
                jary.remove(0);
            }
            String text = jary.toString();
            byte buf[] = text.getBytes();
            fos.write(buf);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCount() {
        return mJsonArray.length();
    }

    public int getMaxCount() {
        return mMaxCount;
    }

    public void setMerger(boolean merger) {
        this.mMerger = merger;
    }

    public boolean isMerger() {
        return mMerger;
    }
}
