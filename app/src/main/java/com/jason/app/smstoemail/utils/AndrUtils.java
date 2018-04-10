package com.jason.app.smstoemail.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;

import com.jason.app.smstoemail.MainSmsActivity;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;

public class AndrUtils {
    private static final String TAG = "AndrUtils";

    public static final int REQUEST_PERMISSION = 1;


    private static Context mContext = null;
    private static View mContentView = null;
    private static JSONObject mConfig = null;
    private static NPermissionCallback mPermissionCallback = null;
    private static final String PRIMARY_CHANNEL = "default";

    public static void init(Context con) {
        checkPlayServices((Activity) con);
        mContext = con;
        mContentView = ((Activity) mContext).findViewById(android.R.id.content);
    }

    public static Context getContext() {
        return mContext;
    }

    public static boolean checkPlayServices(Activity activity) {
//        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (apiAvailability.isUserResolvableError(resultCode)) {
//                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
//                        .show();
//            } else {
//                Log.e(TAG, "GooglePlayServer is not supported.");
//            }
//            return false;
//        }
//        Log.i(TAG, "GooglePlayServer is valid");
        return true;
    }

    public static Bitmap getImageForFile(String file) {
        Bitmap bitmap = BitmapFactory.decodeFile(new File(mContext.getFilesDir(), file).getPath());
        return bitmap;
    }

    public static boolean fileExist(String file) {
        return new File(mContext.getFilesDir(), file).exists();
    }

    public static void saveFileForBytes(Context con, byte[] bufs, String file) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(con.getFilesDir(), file));
            fos.write(bufs);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] getBytesFromFile(Context con, String file) {
        try {
            FileInputStream fis = new FileInputStream(new File(con.getFilesDir(), file));
            byte[] newbuf = getBytesFromFile(fis);
            fis.close();
            return newbuf;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getBytesFromFile(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[512];
            int len = 0;
            while ((len = is.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }
            baos.flush();
            byte[] newbuf = baos.toByteArray();
            baos.close();
            return newbuf;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveBitmap(Bitmap bitmap, String file, Bitmap.CompressFormat format) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(mContext.getFilesDir(), file));
            bitmap.compress(format, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getString(int id) {
        return mContext.getString(id);
    }

    public static String getPackageName() {
        return mContext.getPackageName();
    }

    public static void playSystemSound(int type) {
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (am == null) return;
        final int ringerMode = am.getRingerMode();
        switch (ringerMode) {
            case AudioManager.RINGER_MODE_SILENT: //サイレント
            {
                //do nothing
            }
            break;
            case AudioManager.RINGER_MODE_VIBRATE: //振動
            {
//                Vibrator vbtor = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
//                if (vbtor != null) vbtor.vibrate(new long[]{0, 200, 200, 200, 200, 200}, -1);
            }
            break;
            case AudioManager.RINGER_MODE_NORMAL: //ベル
            {
                Uri uri = RingtoneManager.getDefaultUri(type);
                Ringtone rt = RingtoneManager.getRingtone(mContext.getApplicationContext(), uri);
                rt.play();
            }
            break;
        }
    }

    public static String getVersionName() {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface NPermissionCallback {
        void onSuccess();

        void onFail();
    }

    public static boolean checkPermission(String[] perm, NPermissionCallback call) {
        boolean isSuc = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String per : perm) {
                if (ActivityCompat.checkSelfPermission(mContext, per) != PackageManager.PERMISSION_GRANTED) {
                    isSuc = false;
                    break;
                }
            }
            if (isSuc) {
                return true;
            }
            mPermissionCallback = call;
            ((Activity) mContext).requestPermissions(perm, REQUEST_PERMISSION);
        }
        return isSuc;
    }

    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            boolean isSuc = true;
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    isSuc = false;
                    break;
                }
            }
            if (mPermissionCallback != null) {
                if (isSuc) {
                    mPermissionCallback.onSuccess();
                } else {
                    mPermissionCallback.onFail();
                }
            }
        }
    }

    public static boolean isAssetsConfig(Context con) {
        try {
            return con.getAssets().open("settings.json") != null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getConfigIni(Context con,String model, String key) {
        if (mConfig == null) {
            try {
                InputStream is = con.getAssets().open("settings.json");
                byte[] buf = getBytesFromFile(is);
                is.close();
                String text = new String(buf, "UTF-8");
                mConfig = new JSONObject(text);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }
        try {
            return mConfig.getJSONObject(model).getString(key);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return null;
    }

    public static String getSystemLanguage() {
        String able = mContext.getResources().getConfiguration().locale.getLanguage();
        return able;
    }

    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        String strTz = tz.getDisplayName(false, TimeZone.SHORT);
        return strTz;
    }

    public static void sendNotication(Context context, int id, String title, String content) {
        NotificationCompat.Builder mBuilder = new NotificationCompat
                .Builder(mContext, PRIMARY_CHANNEL)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content);
        Intent resultIntent = new Intent(mContext, MainSmsActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainSmsActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());
    }

    public static void sendNotication(int id, String title, String content) {
        sendNotication(mContext, id, title, content);
    }

    public static Snackbar createSnackbar(String msg, int duration) {
        return Snackbar.make(mContentView, msg, duration);
    }

    public static Snackbar createSnackbar(int resid, int duration) {
        return Snackbar.make(mContentView, resid, duration);
    }

    public static void onResume(Activity act) {
        checkPlayServices(act);
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    System.out.print("Foreground App:" + appProcess.processName);
                    return false;
                } else {
                    System.out.print("Background App:" + appProcess.processName);
                    return true;
                }
            }
        }
        return false;
    }

    public static void assetsPathToSdCard(final String path, final String destf) {
        if (path == null || destf == null) {
            return;
        }
        try {
            File parent = new File(destf);
            if (!parent.exists()) {
                parent.mkdirs();
            }
            String files[] = mContext.getAssets().list(path);
            for (String file : files) {
                if (file != null) {
                    assetsFileToSdCard(path + "/" + file, destf);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void assetsFileToSdCard(final String srcf, final String destf) {
        if (srcf == null || destf == null) {
            return;
        }
        try {
            String paths[] = srcf.split("/");
            if (paths.length <= 0) {
                return;
            }
            InputStream is = mContext.getAssets().open(srcf);
            FileOutputStream fos = new FileOutputStream(destf + "/" + paths[paths.length - 1]);
            byte[] buf = new byte[409600];  //40k buffer
            int len = 0;
            while ((len = is.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmapDrawable(int id) {
        Drawable drawable = mContext.getResources().getDrawable(id);
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        return getBitmapDrawableWH(id, w, h);
    }

    public static Bitmap getBitmapDrawableWH(int id, int width, int height) {
        Drawable drawable = mContext.getResources().getDrawable(id);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
