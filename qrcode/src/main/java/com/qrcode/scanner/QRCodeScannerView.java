package com.qrcode.scanner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.qrcode.scanner.camera.JinCameraManager;
import com.qrcode.scanner.decode.DecodeCallback;

import java.io.IOException;

/**
 * author: admin
 * date: 2017/03/14
 * version: 0
 * mail: secret
 * desc: ScannerView
 */

public class QRCodeScannerView extends FrameLayout implements SurfaceHolder.Callback {

    ScannerRectView mRectView;
    SurfaceView mSurfaceView;

    JinCameraManager mCameraManager;

    DecodeCallback mDecodeCallbackInst;

    public QRCodeScannerView(Context context) {
        this(context, null);
    }

    public QRCodeScannerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRectView = new ScannerRectView(getContext(), attrs);
        mSurfaceView = new SurfaceView(getContext());
        mSurfaceView.getHolder().addCallback(this);

        mCameraManager = JinCameraManager.get(getContext());

        addView(mSurfaceView);
        addView(mRectView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public final ScannerRectView getScannerRectView() {
        return mRectView;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mCameraManager = JinCameraManager.get(getContext());
        mCameraManager.getCameraPreviewCallback().setDecodeCallback(mDecodeCallbackInst);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCameraManager.release();
        mCameraManager = null;
    }

    public QRCodeScannerView setDecodeCallback(DecodeCallback callback) {
        mDecodeCallbackInst = callback;
        mCameraManager.getCameraPreviewCallback().setDecodeCallback(callback);
        return this;
    }

    public void startDecode() {
        System.out.println("ccccccccccccccccccccccccccc startDecode " + mCameraManager.requestPreviewFrameOnce());
        if (!mCameraManager.requestPreviewFrameOnce()) {
            startDecodeDelay(800);
        }
    }

    public void startDecodeDelay(long millions) {
        if (getHandler() != null) {
            getHandler().removeCallbacksAndMessages(null);
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                startDecode();
            }
        }, millions);
    }

    // SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            System.out.println("ccccccccccccccccccccccccccc ScannerView surfaceCreated ");
            setDecodeCallback(mDecodeCallbackInst);
            mCameraManager.openDriver(holder);
            mCameraManager.startPreview();
        } catch (IOException e) {
            System.out.println("ccccccccccccccccccccccccccc ScannerView  Camera Open Fail " + e.getMessage());
            e.printStackTrace();
        }
    }

    // SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        System.out.println("ccccccccccccccccccccccccccc surfaceChanged");
    }

    // SurfaceHolder.Callback
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCameraManager.stopPreview();
        mCameraManager.closeDriver();
    }
}
