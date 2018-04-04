package com.jason.app.smstoemail.fragments;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.jason.app.smstoemail.R;
import com.jason.app.smstoemail.sms.SmsLocalManager;
import com.jason.app.smstoemail.utils.AndrUtils;
import com.jason.app.smstoemail.views.SmsMsg;
import com.jason.app.smstoemail.views.SmsView;

import java.util.ArrayList;

/**
 * Created by tlcyp on 2018/03/23.
 */

public class MessageFragment extends NFragment {
    private static final String TAG = "MessageFragment";
    private LinearLayout mContent = null;
    private ValueAnimator mScrollAni = null;

    public MessageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(!AndrUtils.isAssetsConfig());
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) this.getActivity()).getSupportActionBar().setTitle(R.string.message);
        mContent = this.getActivity().findViewById(R.id.content);
        ArrayList<SmsMsg> list = SmsLocalManager.getInstace().load();
        for (int i = 0; i < list.size(); i++) {
            mContent.addView(new SmsView(this.getActivity(), list.get(i), i + 1));
        }
        updateScrollView(false);
    }

    private void updateScrollView(final boolean ani) {
        mContent.post(new Runnable() {
            @Override
            public void run() {
                final ScrollView sv = ((ScrollView) mContent.getParent());
                int offset = mContent.getMeasuredHeight() - sv.getHeight();
                if (offset < 0) {
                    offset = 0;
                }
                if (!ani) {
                    sv.scrollTo(0, offset);
                } else {
                    if (mScrollAni != null) {
                        mScrollAni.cancel();
                    }
                    mScrollAni = ValueAnimator.ofInt(sv.getScrollY(), offset).setDuration(800);
                    mScrollAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int offy = (int) animation.getAnimatedValue();
                            sv.scrollTo(0, offy);
                        }
                    });
                    mScrollAni.start();
                }
            }
        });
    }

    public void addSms(SmsMsg msg) {
        SmsLocalManager.getInstace().add(msg);
        int len = SmsLocalManager.getInstace().getCount();
        mContent.addView(new SmsView(this.getActivity(), msg, len));
        if (mContent.getChildCount() > SmsLocalManager.getInstace().getMaxCount()) {
            mContent.removeViewAt(0);
        }
        updateScrollView(true);
    }
}
