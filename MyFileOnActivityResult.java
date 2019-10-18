package com.iheima.myapplication.chromeclient;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import static android.app.Activity.RESULT_OK;

public class MyFileOnActivityResult {

    private static MyFileOnActivityResult myFileOnActivityResult;

    public MyFileOnActivityResult(MyFileWebChromeClient myFileWebChromeClient) {
        this.myFileWebChromeClient = myFileWebChromeClient;
    }

    public static MyFileOnActivityResult getInstance(MyFileWebChromeClient myFileWebChromeClient) {
        if (myFileOnActivityResult == null) {
            myFileOnActivityResult = new MyFileOnActivityResult(myFileWebChromeClient);
        }
        return myFileOnActivityResult;
    }

    MyFileWebChromeClient myFileWebChromeClient;

    /**
     * 以下代码是为了适应H5调用本地图片并且显示在h5上
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == MyFileWebChromeClient.REQUEST_FILE_PICKER) {
            if (null == myFileWebChromeClient.mFilePathCallback && null == myFileWebChromeClient.mFilePathCallbacks) {
                return;
            }
            Uri result = ((intent == null || resultCode != RESULT_OK) ? null : intent.getData());
            if (myFileWebChromeClient.mFilePathCallbacks != null) {
                onActivityResultAboveL(requestCode, resultCode, intent);
            } else if (myFileWebChromeClient.mFilePathCallback != null) {

//                if (result != null) {
//                    String path = getPath(getApplicationContext(),
//                            result);
//                    Uri uri = Uri.fromFile(new File(path));
//                    progress_webview.mUploadMessage
//                            .onReceiveValue(uri);
//                } else {
//                    progress_webview.mUploadMessage.onReceiveValue(progress_webview.imageUri);
//                }
                myFileWebChromeClient.mFilePathCallback = null;
            }
        }

    }

    @SuppressWarnings("null")
    @TargetApi(Build.VERSION_CODES.BASE)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != MyFileWebChromeClient.REQUEST_FILE_PICKER
                || myFileWebChromeClient.mFilePathCallbacks == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                /**
                 * 如果返回的结构为空，那么不从结果里面拿数据，而是直接从选择图片的路径拿
                 */
                results = new Uri[]{myFileWebChromeClient.imageUri};
            } else {
                /**
                 * 如果返回了数据，则将返回的数据解析成 uri
                 */
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    /**
                     * 如果获取的图片经过了裁剪
                     */
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }

                if (dataString != null)
                /**
                 * 没有经过裁剪，直接取得的图片
                 */
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        /**
         * 如果经过了上面的处理，result 不为空，说明用户确实取得了图片，那么将 result 返回即可
         *
         * 否则，返回空的图片（图片只有名字，并没有真正的图片）
         */
        if (results != null) {
            myFileWebChromeClient.mFilePathCallbacks.onReceiveValue(results);
            myFileWebChromeClient.mFilePathCallbacks = null;
        } else {
            results = new Uri[]{myFileWebChromeClient.imageUri};
            myFileWebChromeClient.mFilePathCallbacks.onReceiveValue(results);
            myFileWebChromeClient.mFilePathCallbacks = null;
        }
    }
}