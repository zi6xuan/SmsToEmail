package com.jason.app.smstoemail;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.jason.app.smstoemail.fragments.MessageFragment;
import com.jason.app.smstoemail.fragments.SettingsFragment;
import com.jason.app.smstoemail.sms.SmsLocalManager;
import com.jason.app.smstoemail.utils.AndrUtils;
import com.jason.app.smstoemail.views.SmsMsg;

public class MainSmsActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static MainSmsActivity mInstance = null;
    private MessageFragment mMsgFragment = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        checkNormalSms();
        AndrUtils.init(this);
        SmsLocalManager.getInstace().init(this);
        //
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        mMsgFragment = new MessageFragment();
        transaction.add(R.id.viewContent, mMsgFragment);
//        transaction.addToBackStack(null);
        transaction.commit();
        //
        mInstance = this;
    }

    public static MainSmsActivity Inst() {
        return mInstance;
    }


    private void showSettings() {
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.viewContent, new SettingsFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void checkNormalSms() {
        String defaultSmsApp = null;
        String currentPn = getPackageName();//获取当前程序包名
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);//获取手机当前设置的默认短信应用的包名
        }
        Log.e(TAG, "default-pack=" + defaultSmsApp);
        if (!currentPn.equals(defaultSmsApp)) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, currentPn);
            startActivity(intent);
        }
    }

    //events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            showSettings();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addSms(SmsMsg msg) {
        if (mMsgFragment != null) {
            mMsgFragment.addSms(msg);
        }
    }
}
