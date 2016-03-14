package com.yw.sclib;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wengyiming on 2016/3/14.
 */
public class Sc {
    private Activity actionActivity;
    private Activity targetActivity;
    private String iconUrl;
    private int iconRes;
    private String mName;
    private Object tag;
    private boolean allowRepeat;
    private ScCreateResultCallback callback;

    public Sc(Builder builder) {
        this.actionActivity = builder.getActionActivity();
        this.targetActivity = builder.getTargetActivity();
        this.iconUrl = builder.getIconUrl();
        this.iconRes = builder.getIconRes();
        this.mName = builder.getName();
        this.allowRepeat = builder.isAllowRepeat();
        this.tag = builder.getmObjects();
        this.callback = builder.getCallback();
    }

    public void createSc() {
        // 创建前判断是否存在
        if (!ShortcutSuperUtils.isShortCutExist(actionActivity, mName, getShortCutIntent())) {
            if (TextUtils.isEmpty(iconUrl)) {
                ShortcutUtils.addShortcut(actionActivity, getShortCutIntent(), mName, allowRepeat,
                        BitmapFactory.decodeResource(actionActivity.getResources(), iconRes));
                callback.createSuccessed("created", tag);
            } else {
                //TODO download bitmap for shortcut
                getImageURI(iconUrl, actionActivity.getCacheDir() + File.separator + mName, new CallBack() {
                    @Override
                    public void onRequestComplete(Uri result) {
                        if (result == null) {
                            callback.createError("download error", tag);
                            return;
                        }
                        ShortcutUtils.addShortcut(actionActivity, getShortCutIntent(), mName, allowRepeat,
                                getBitmapFromUri(result));
                        callback.createSuccessed("created", tag);
                    }
                });
            }
        }
    }

    public void updateShortcutTest() {
        if (TextUtils.isEmpty(iconUrl)) {
            ShortcutSuperUtils.updateShortcutIcon(actionActivity, mName, getShortCutIntent(),
                    BitmapFactory.decodeResource(actionActivity.getResources(), iconRes));
        } else {
        }
    }

    private Intent getShortCutIntent() {
        // 使用MAIN，可以避免部分手机(比如华为、HTC部分机型)删除应用时无法删除快捷方式的问题
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setClass(actionActivity, targetActivity.getClass());
        return intent;
    }

    public static class Builder implements ScInterface {
        private Activity actionActivity;
        private Activity targetActivity;
        private String iconUrl;
        private int iconRes;
        private String mName;
        private boolean allowRepeat;
        private Object mObjects;
        private ScCreateResultCallback callback;

        public boolean isAllowRepeat() {
            return allowRepeat;
        }

        @Override
        public Builder setAllowRepeat(boolean allowRepeat) {
            this.allowRepeat = allowRepeat;
            return this;
        }


        public Builder(Activity actionActivity, Activity targetActivity) {
            this.actionActivity = actionActivity;
            this.targetActivity = targetActivity;
        }

        @Override
        public Builder setName(String name) {
            this.mName = name;
            return this;
        }

        @Override
        public Builder setCallBack(ScCreateResultCallback callback) {
            this.callback = callback;
            return this;
        }

        public ScCreateResultCallback getCallback() {
            return callback;
        }

        @Override
        public Builder setIcon(String url) {
            this.iconUrl = url;
            return this;
        }

        @Override
        public Builder setIcon(int res) {
            this.iconRes = res;
            return this;
        }

        @Override
        public Builder setTag(Object o) {
            this.mObjects = o;
            return this;
        }

        @Override
        public Sc build() {
            return new Sc(this);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "actionActivity=" + actionActivity +
                    ", targetActivity=" + targetActivity +
                    ", iconUrl='" + iconUrl + '\'' +
                    ", iconRes=" + iconRes +
                    ", mName='" + mName + '\'' +
                    ", allowRepeat=" + allowRepeat +
                    ", mObjects=" + mObjects +
                    ", callback=" + callback +
                    '}';
        }

        public Activity getTargetActivity() {
            return targetActivity;
        }

        public Activity getActionActivity() {
            return actionActivity;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public int getIconRes() {
            return iconRes;
        }

        public String getName() {
            return mName;
        }

        public Object getmObjects() {
            return mObjects;
        }
    }


    public interface ScInterface {
        Builder setName(String name);

        Builder setIcon(String url);

        Builder setAllowRepeat(boolean allowRepeat);

        Builder setIcon(int res);

        Builder setTag(Object o);

        Builder setCallBack(ScCreateResultCallback callback);

        Sc build();

    }

    private Handler handler = new Handler(Looper.getMainLooper());

    public Bitmap getBitmapFromUri(Uri uri) {
        try {
            // 读取uri所在的图片
            return MediaStore.Images.Media.getBitmap(actionActivity.getContentResolver(), uri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片
     * 这里的path是图片的地址
     */
    public void getImageURI(final String path, final String filePath, final CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File targetFile = new File(filePath);
                if (!targetFile.getParentFile().exists()) {
                    targetFile.getParentFile().mkdirs();
                }
                String name = getMD5(path) + path.substring(path.lastIndexOf("."));
                final File file = new File(targetFile.getParentFile(), name);
                // 如果图片存在本地缓存目录，则不去服务器下载
                if (file.exists()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onRequestComplete(Uri.fromFile(file));
                        }
                    });
                } else {
                    // 从网络上获取图片
                    URL url;
                    try {
                        url = new URL(path);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(5000);
                        conn.setRequestMethod("GET");
                        conn.setDoInput(true);
                        if (conn.getResponseCode() == 200) {

                            InputStream is = conn.getInputStream();
                            FileOutputStream fos = new FileOutputStream(file);
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                            }
                            is.close();
                            fos.close();
                            // 返回一个URI对象
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onRequestComplete(Uri.fromFile(file));
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onRequestComplete(null);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onRequestComplete(null);
                            }
                        });
                    }

                }
            }
        }).start();

    }

    public static String getMD5(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            return getHashString(digest);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getHashString(MessageDigest digest) {
        StringBuilder builder = new StringBuilder();
        for (byte b : digest.digest()) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString();
    }

    public interface CallBack {
        void onRequestComplete(Uri result);
    }

}
