package com.zxing.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.jtblk.client.Wallet;
import com.doughnut.R;
import com.doughnut.activity.BaseActivity;
import com.doughnut.activity.TokenTransferActivity;
import com.doughnut.activity.WalletImportActivity;
import com.doughnut.config.AppConfig;
import com.doughnut.config.Constant;
import com.doughnut.utils.GsonUtil;
import com.doughnut.utils.QRUtils;
import com.doughnut.utils.ToastUtil;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.decoding.InactivityTimer;
import com.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

/**
 * 二维码扫描
 */
public class CaptureActivity extends BaseActivity implements Callback, View.OnClickListener {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private Vector<BarcodeFormat> decodeFormats;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private ImageView mImgLight;
    private String characterSet;
    private boolean hasSurface;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    private static final int REQUEST_CODE = 100;
    private static final int PARSE_BARCODE_SUC = 300;
    private static final int PARSE_BARCODE_FAIL = 303;
    private ProgressDialog mProgress;
    private String photo_path;
    private boolean isLight = false;

    public static void navToActivity(Activity context, int requestCode) {
        Intent intent = new Intent(context, CaptureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivityForResult(intent, requestCode);
    }

    public static void startCaptureActivity(Context context) {
        Intent intent = new Intent(context, CaptureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        CameraManager.init(getApplication());

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

        TitleBar mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back_white);
        mTitleBar.setTitleBarBackColor(R.color.transparent);
        mTitleBar.setTitle(getString(R.string.titleBar_scan));
        mTitleBar.setRightText(R.string.tv_album);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }

            @Override
            public void onRightClick(View view) {
                // todo 相册选择
                PictureSelector.create(CaptureActivity.this)
                        .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .imageSpanCount(4)// 每行显示个数
                        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                        .previewImage(true)// 是否可预览图片
                        .isCamera(false)// 是否显示拍照按钮
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .enableCrop(false)// 是否裁剪
                        .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        .openClickSound(false)// 是否开启点击声音
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            }
        });

        mImgLight = findViewById(R.id.img_light);
        mImgLight.setOnClickListener(this);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();
            switch (msg.what) {
                case PARSE_BARCODE_SUC:
                    onResultHandler((String) msg.obj);
                    break;
                case PARSE_BARCODE_FAIL:
                    Toast.makeText(CaptureActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                    if (cursor.moveToFirst()) {
                        photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    }
                    cursor.close();

                    if (TextUtils.isEmpty(photo_path)) {
                        return;
                    }
                    mProgress = new ProgressDialog(CaptureActivity.this);
                    mProgress.setMessage(getString(R.string.dialog_content_scanning));
                    mProgress.setCancelable(false);
                    mProgress.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = QRUtils.scanningImage(photo_path);
                            if (result != null) {
                                Message m = mHandler.obtainMessage();
                                m.what = PARSE_BARCODE_SUC;
                                m.obj = result.getText();
                                mHandler.sendMessage(m);
                            } else {
                                Message m = mHandler.obtainMessage();
                                m.what = PARSE_BARCODE_FAIL;
                                m.obj = "Scan failed!";
                                mHandler.sendMessage(m);
                            }
                        }
                    }).start();
                    break;
                case PictureConfig.CHOOSE_REQUEST:
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if (selectList != null && selectList.size() > 0) {
                        photo_path = selectList.get(0).getPath();
                    }

                    if (TextUtils.isEmpty(photo_path)) {
                        return;
                    }
                    mProgress = new ProgressDialog(CaptureActivity.this);
                    mProgress.setMessage(getString(R.string.dialog_content_scanning));
                    mProgress.setCancelable(false);
                    mProgress.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = QRUtils.scanningImage(photo_path);
                            if (result != null) {
                                Message m = mHandler.obtainMessage();
                                m.what = PARSE_BARCODE_SUC;
                                m.obj = result.getText();
                                mHandler.sendMessage(m);
                            } else {
                                Message m = mHandler.obtainMessage();
                                m.what = PARSE_BARCODE_FAIL;
                                m.obj = "Scan failed!";
                                mHandler.sendMessage(m);
                            }
                        }
                    }).start();
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewfinderView = findViewById(R.id.viewfinder_view);
        SurfaceView surfaceView = findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    public void handleDecode(Result result) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        onResultHandler(resultString);
    }

    private void onResultHandler(String resultString) {
        if (TextUtils.isEmpty(resultString)) {
            ToastUtil.toast(CaptureActivity.this, getResources().getString(R.string.toast_qr_fail));
            return;
        }

        try {

            // 钱包地址=>跳转转账页面
            if (Wallet.isValidAddress(resultString)) {
                TokenTransferActivity.startTokenTransferActivity(this, resultString, "", "");
                CaptureActivity.this.finish();
                return;
            }

            // 钱包私钥=>跳转私钥导入页面
            if (Wallet.isValidSecret(resultString)) {
                WalletImportActivity.startWalletImportActivity(this, 0, resultString);
                CaptureActivity.this.finish();
                return;
            }

            GsonUtil result = new GsonUtil(resultString);
            List<String> keys = result.getKey();

            // 跳转转账页面
            if (keys.contains(Constant.RECEIVE_ADDRESS_KEY) && keys.contains(Constant.TOEKN_AMOUNT) && keys.contains(Constant.TOEKN_AMOUNT)) {
                String address = result.getString(Constant.RECEIVE_ADDRESS_KEY, "");
                String amount = result.getString(Constant.TOEKN_AMOUNT, "");
                String tokenName = result.getString(Constant.TOEKN_AMOUNT, "");
                TokenTransferActivity.startTokenTransferActivity(this, address, amount, tokenName);
            }
            // JSON格式字符串=>跳转KeyStore导入页面
            else if (result.isValid()) {
                WalletImportActivity.startWalletImportActivity(this, 1, resultString);
            } else {
                ToastUtil.toast(CaptureActivity.this, getResources().getString(R.string.toast_qr_err));
                // 2秒后重新扫描
                AppConfig.postDelayOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (handler != null) {
                            handler.restartPreviewAndDecode();
                        }
                    }
                }, 2000);
                return;
            }
        } catch (Exception e) {
            ToastUtil.toast(CaptureActivity.this, getResources().getString(R.string.toast_qr_fail));
            return;
        }
        CaptureActivity.this.finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
            Rect rect = CameraManager.get().getFramingRect();
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mImgLight.getLayoutParams();
            int screenHight = ViewUtil.getWindowHight(this);
            layoutParams.topMargin = rect.bottom + (screenHight - ViewUtil.dip2px(this, 50) - rect.bottom) / 3;
            mImgLight.setLayoutParams(layoutParams);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };


    @Override
    public void onClick(View v) {
        if (v == mImgLight) {
            if (isLight) {
                CameraManager.get().closeLight();
                isLight = false;
            } else {
                CameraManager.get().openLight();
                isLight = true;
            }
        }
    }
}