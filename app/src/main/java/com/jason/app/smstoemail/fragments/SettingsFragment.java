package com.jason.app.smstoemail.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jason.app.smstoemail.EmailMager;
import com.jason.app.smstoemail.R;
import com.jason.app.smstoemail.sms.SmsLocalManager;
import com.jason.app.smstoemail.utils.AndrUtils;

public class SettingsFragment extends NFragment {

    private String mSendEmail;
    private String mToEmail;
    private String mSendServer;
    private String mPassword;
    private int mMaxRetry = 50;
    private int mRetryTimeInterval = 3000;
    private TextView mTexRetryInterval;
    private TextView mTexMaxRetry;
    private TextView mTexPassword;
    private TextView mTexServer;
    private TextView mTexEmail;
    private TextView mTexToEmail;
    private TextView mTexMaxCount;
    private int mMaxCount = 1000;

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(!AndrUtils.isAssetsConfig(this.getActivity()));
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) this.getActivity()).getSupportActionBar().setTitle(R.string.settings);
        load(this.getContext());
        //
        mTexEmail = this.getActivity().findViewById(R.id.sendEmail);
        mTexEmail.setText(mSendEmail);
        mTexToEmail = this.getActivity().findViewById(R.id.toEmail);
        mTexToEmail.setText(mToEmail);
        mTexServer = this.getActivity().findViewById(R.id.sendServer);
        mTexServer.setText(mSendServer);
        mTexPassword = this.getActivity().findViewById(R.id.password);
        mTexPassword.setText(mPassword);
        mTexMaxRetry = this.getActivity().findViewById(R.id.maxRetry);
        mTexMaxRetry.setText(String.valueOf(mMaxRetry));
        mTexRetryInterval = this.getActivity().findViewById(R.id.retryTimeInterval);
        mTexRetryInterval.setText(String.valueOf(mRetryTimeInterval));
        mTexMaxCount = this.getActivity().findViewById(R.id.maxCount);
        mTexMaxCount.setText(String.valueOf(mMaxCount));
        Button btnTest = this.getActivity().findViewById(R.id.btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSuc = EmailMager.getInstance().sendMail(getString(R.string.test), getString(R.string.testCont));
                if (isSuc) {
                    AndrUtils.createSnackbar(R.string.sendedTest, Snackbar.LENGTH_LONG).show();
                } else {
                    AndrUtils.createSnackbar(R.string.sendedFailed, Snackbar.LENGTH_LONG).show();
                }
            }
        });
        Button btnSave = this.getActivity().findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendEmail = mTexEmail.getText().toString();
                mToEmail = mTexToEmail.getText().toString();
                mSendServer = mTexServer.getText().toString();
                mPassword = mTexPassword.getText().toString();
                mMaxRetry = Integer.parseInt(mTexMaxRetry.getText().toString());
                mRetryTimeInterval = Integer.parseInt(mTexRetryInterval.getText().toString());
                mMaxCount = Integer.parseInt(mTexMaxCount.getText().toString());
                //
                EmailMager inst = EmailMager.getInstance();
                if (inst != null) {
                    inst.setSendEmail(mSendEmail);
                    inst.setToEmail(mToEmail);
                    inst.setSendServer(mSendServer);
                    inst.setPassword(mPassword);
                    inst.setMaxRetry(mMaxRetry);
                    inst.setRetryTime(mRetryTimeInterval);
                }
                SmsLocalManager.getInstace().setMaxCount(mMaxCount);
                //
                save(SettingsFragment.this.getContext());
                AndrUtils.createSnackbar(R.string.saved, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public static void load(Context con, EmailMager inst) {
        if (inst != null) {
            SettingsFragment sf = new SettingsFragment();
            sf.load(con);
            inst.setSendEmail(sf.mSendEmail);
            inst.setToEmail(sf.mToEmail);
            inst.setSendServer(sf.mSendServer);
            inst.setPassword(sf.mPassword);
            inst.setMaxRetry(sf.mMaxRetry);
            inst.setRetryTime(sf.mRetryTimeInterval);
            SmsLocalManager.getInstace().setMaxCount(sf.mMaxCount);
        }
    }

    private void load(Context con) {
        if (AndrUtils.isAssetsConfig(con)) {
            mSendEmail = AndrUtils.getConfigIni(con, "mail", "SendEmail");
            mToEmail = AndrUtils.getConfigIni(con, "mail", "ToEmail");
            mSendServer = AndrUtils.getConfigIni(con, "mail", "SendServer");
            mPassword = AndrUtils.getConfigIni(con, "mail", "Password");
            mMaxRetry = Integer.parseInt(AndrUtils.getConfigIni(con, "normal", "MaxRetry"));
            mRetryTimeInterval = Integer.parseInt(AndrUtils.getConfigIni(con, "normal", "RetryTimeInterval"));
            mMaxCount = Integer.parseInt(AndrUtils.getConfigIni(con, "normal", "MaxCount"));
        } else {
            SharedPreferences spf = con.getSharedPreferences("settings.xml", Context.MODE_PRIVATE);
            mSendEmail = spf.getString("SendEmail", mSendEmail);
            mToEmail = spf.getString("ToEmail", mToEmail);
            mSendServer = spf.getString("SendServer", mSendServer);
            mPassword = spf.getString("Password", mPassword);
            mMaxRetry = spf.getInt("MaxRetry", mMaxRetry);
            mRetryTimeInterval = spf.getInt("RetryTimeInterval", mRetryTimeInterval);
            mMaxCount = spf.getInt("MaxCount", mMaxCount);
        }
    }

    private void save(Context con) {
        if (!AndrUtils.isAssetsConfig(con)) {
            SharedPreferences spf = con.getSharedPreferences("settings.xml", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = spf.edit();
            editor.putString("SendEmail", mSendEmail);
            editor.putString("ToEmail", mToEmail);
            editor.putString("SendServer", mSendServer);
            editor.putString("Password", mPassword);
            editor.putInt("MaxRetry", mMaxRetry);
            editor.putInt("RetryTimeInterval", mRetryTimeInterval);
            editor.putInt("MaxCount", mMaxCount);
            editor.apply();
        }
    }
}
