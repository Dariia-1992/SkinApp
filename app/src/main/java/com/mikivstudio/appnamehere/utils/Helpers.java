package com.mikivstudio.appnamehere.utils;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;

import com.mikivstudio.appnamehere.BuildConfig;
import com.mikivstudio.appnamehere.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Labeeb on 12-May-17.
 */

public class Helpers {
    public static final String TAG = "AAAA_SKINS";

    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static File createFileInGallery(Context context, String skinName) {
        File folder = getAppFolder(context);
        return new File(String.format("%s/%s.png",
                folder.getAbsolutePath(),
                skinName));
    }

    public static File createFileInApplication(String skinName) {
        File folder = getPEFolder();
        return new File(String.format("%s/%s.png",
                folder.getAbsolutePath(),
                skinName));
    }

    public static void copyFile(File src, File dest) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dest)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public static void goToStore(Context context) {
        if (context == null)
            return;

        final String appPackageName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void goToDevPage(Context context) {
        if (context == null)
            return;

        final String developerId = BuildConfig.DEVELOPER_ID;
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=" + developerId)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=" + developerId)));
        }
    }

    public static void shareApp(Context context) {
        if (context == null)
            return;

        String appPackageName = context.getPackageName();
        String appName = context.getString(R.string.app_name);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                String.format("Check out \"%s\"\r\nhttps://play.google.com/store/apps/details?id=%s", appName, appPackageName));
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    private static File getAppFolder(Context context) {
        String pathName = context.getString(R.string.app_name);
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), pathName);
        if (!storageDir.exists())
            storageDir.mkdirs();

        return storageDir;
    }

    private static File getPEFolder() {
        File storageDir = new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe");
        if (!storageDir.exists())
            storageDir.mkdirs();

        return storageDir;
    }
}
