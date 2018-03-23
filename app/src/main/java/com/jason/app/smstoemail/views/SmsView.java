package com.jason.app.smstoemail.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jason.app.smstoemail.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SmsView extends RelativeLayout {

    private SmsMsg mMsg = null;
    private SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("YYYY/MM/dd", Locale.getDefault());
    private SimpleDateFormat sdfHHmm = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public SmsView(Context context, SmsMsg msg, int index) {
        super(context);
        LayoutInflater mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.msg_layout, this);
        mMsg = msg;
        Date date = new Date(msg.getTimestampMillis());
        TextView tex0 = findViewById(R.id.texCount);
        tex0.setVisibility(View.GONE);
//        tex0.setText(index + "");
        TextView tex1 = findViewById(R.id.texDate);
        tex1.setText(sdfYYYYMMDD.format(date));
        TextView tex2 = findViewById(R.id.texTime);
        tex2.setText(sdfHHmm.format(date));
        TextView tex3 = findViewById(R.id.texForm);
        tex3.setText(msg.getOriginatingAddress());
        TextView tex4 = findViewById(R.id.texCont);
        tex4.setText(msg.getMessageBody());
    }
}
