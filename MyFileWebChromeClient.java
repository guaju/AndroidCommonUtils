package com.iheima.myapplication.chromeclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyFileWebChromeClient extends WebChromeClient {
    public static final int REQUEST_FILE_PICKER = 1;
    public ValueCallback<Uri> mFilePathCallback;
    public ValueCallback<Uri[]> mFilePathCallbacks;
    Activity mContext;
    /**
     * 记录选择图片的 uri
     */
    public Uri imageUri;

    @Override
    public void onReceivedTitle(WebView webView, String s) {
        super.onReceivedTitle(webView, s);
    }

    public MyFileWebChromeClient(Activity mContext) {
        super();
        this.mContext = mContext;
    }

    /**
     * Android < 3.0 调用这个方法
     *
     * @param filePathCallback
     */
    public void openFileChooser(final ValueCallback<Uri> filePathCallback) {
        mFilePathCallback = filePathCallback;
        take();
    }

    /**
     * 3.0 + 调用这个方法
     *
     * @param filePathCallback
     * @param acceptType
     */

    public void openFileChooser(final ValueCallback filePathCallback, final String acceptType) {
        mFilePathCallback = filePathCallback;
        take();
    }


    /**
     * js上传文件的<input type="file" name="avatar" id="avatar" />事件捕获,也就在这里啦
     * Android >4.1.1调用这个方法
     *
     * @param filePathCallback
     * @param acceptType
     * @param capture
     */


    public void openFileChooser(final ValueCallback<Uri> filePathCallback, final String acceptType, final String capture) {
        mFilePathCallback = filePathCallback;
        take();
    }

    @Override
    public boolean onShowFileChooser(final WebView webView, final ValueCallback<Uri[]> filePathCallback, final FileChooserParams fileChooserParams) {
        mFilePathCallbacks = filePathCallback;
        take();
        return true;
    }

    /**
     * 返回选择相册或者拍照的图片
     */
    private void take() {
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyApp");
        // Create the storage directory if it does not exist
        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs();
        }
        File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        imageUri = Uri.fromFile(file);

        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = mContext.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent i = new Intent(captureIntent);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            i.setPackage(packageName);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntents.add(i);

        }
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        mContext.startActivityForResult(chooserIntent, REQUEST_FILE_PICKER);
    }

}

