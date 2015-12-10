package com.lang.mirror;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.shly.shlymirror.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

public class PreviewView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private Camera mCamera;// Camera对象
    Context context;
    List<Size> mSupportedPreviewSizes;
    Size mPreviewSize;
    public static boolean isPreviewing = true;
    Toast toast ;
    Toast toast2;
    int zoom = -1;
    int divZoom = 4;
    boolean lock = false;
    
    public interface CameraCallBack{
    	public void openCamerError(int code);
    }
    CameraCallBack mCameraCallBack = null;
    public void setCameraCallBack(CameraCallBack callback){
    	mCameraCallBack = callback;
    }
    
    public PreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        holder = this.getHolder();
        holder.addCallback(this);
        this.context = context;
        isPreviewing = true;
    }

    @SuppressWarnings("deprecation")
    private int FindFrontCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // front camera :CAMERA_FACING_FRONT back camera
                // :CAMERA_FACING_BACK
                return camIdx;
            }
        }
        return 0;
    }

    public void setZoom(int zoom) {
        if (mCamera == null)
            return;
        Camera.Parameters params = mCamera.getParameters();
        params.setZoom(zoom);
        this.zoom = zoom;
        mCamera.setParameters(params);
    }

    public int getZoom() {
        if (mCamera == null )
            return 0;
        Camera.Parameters params = mCamera.getParameters();
        return params.getZoom();
    }

    public int getMaxZoom() {
        if (mCamera == null)
            return 0;
        Camera.Parameters params = mCamera.getParameters();
        return params.getMaxZoom() - divZoom;
    }
    
    /**  
     * 计时线程（防止在一定时间段内重复点击按钮）  
     */   
    private class TimeThread extends Thread {    
        public void run() {  
            try {  
                Thread.sleep(1200);  
                lock = true;  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }  
    
    long last = 0;
    long now =  0;
    public void takePhoto() {
        now = System.currentTimeMillis();
        if(now - last > 500)
        {
            if (mCamera == null)
                return;
            try {
                mCamera.takePicture(mShutter, null, pictureCallback);
                last = now;
            } catch (Exception e) {
                // TODO: handle exception
                if(toast != null)
                {
                    toast.cancel();
                    toast.setText(context.getString(R.string.ready));
                    toast.show();
                }
            }
        }
        else
        {
            if(toast != null)
            {
                toast.cancel();
                toast.setText(context.getString(R.string.ready));
                toast.show();
            }
            else
            {
                toast = Toast.makeText(context, context.getString(R.string.ready), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 150);
                toast.show();
            }
            return ;
        }
    }

    public void stopOrStart() {
        if (mCamera == null)
            return;
        if (isPreviewing) {
//            showToast(R.string.pause);
            mCamera.stopPreview();
            isPreviewing = false;
        } else {
//            showToast(R.string.recovery);
            mCamera.startPreview();
            isPreviewing = true;
        }
    }

    private Camera.ShutterCallback mShutter = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            //
        }
    };

    // Photo call back
    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        // @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            new SavePictureTask().execute(data);
            camera.startPreview();
        }
    };

    // save pic
    class SavePictureTask extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... params) {
            if (!isHaveSDCard() || params[0] == null) {
                sendMsg(R.string.save_fail);
                return null;
            }
            try {
                String fname = DateFormat.format("yyyyMMddhhmmss", new Date()).toString() + ".jpg";
                String imgPath = Environment.getExternalStorageDirectory() + "/Mirror/ShotImage/";
                imgPath = imgPath.replace("/storage/emulated/0", "/storage/sdcard0");
                File file = new File(imgPath);
                if (!file.exists())
                    file.mkdirs();
                imgPath = imgPath + fname;
                Bitmap bitmap = BitmapFactory.decodeByteArray(params[0], 0, params[0].length);
                Matrix m = new Matrix();
//                m.postScale(-1, 1);   //Horizontal inversion
                m.postScale(1, -1);
                m.setRotate(-90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                saveBitmapToSDCard(bitmap, imgPath);
                sendMsg(R.string.save_seccess);
                scanPhotos(imgPath, context);
                return "yes";
            } catch (Exception e) {
                // TODO: handle exception
                sendMsg(R.string.save_fail2);
            }
            
            
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
        }
    }

    /**
     * sacn photos add function by xushiyong 20150513
     */
    public void scanPhotos(String filePath, Context context) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    public void saveBitmapToSDCard(Bitmap bitmap, String path) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMsg(int id) {
        Message message = new Message();
        message.arg1 = id;
        handler.sendMessage(message);
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            showToastBottom(msg.arg1);
        };
    };

    private void showToast(int strID) {
        // TODO Auto-generated method stub
        if (toast == null)
            toast = Toast.makeText(context, context.getString(strID), Toast.LENGTH_SHORT);
        else
            toast.setText(context.getString(strID));
        toast.setGravity(Gravity.TOP, 0, 150);
        toast.show();
    }

    public void cancelShow()
    {
        if(toast2 != null)
            toast2.cancel();
    }
    
    private void showToastBottom(int strID) {
        // TODO Auto-generated method stub
        if(toast2 == null)
        {
            toast2 = Toast.makeText(context, context.getString(strID), Toast.LENGTH_SHORT);
            toast2.setGravity(Gravity.BOTTOM, 0, 150);
        }
        else
            toast2.setText(context.getString(strID));
        toast2.show();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        try {
            int cameraId = FindFrontCamera();
            if(cameraId == 0)
            {
                Toast toast = Toast.makeText(context, context.getString(R.string.not_have_front_camera), Toast.LENGTH_LONG);
                toast.show();
                 ((MainActivity)context).finishActivity();
                return;
            }
            if (cameraId != -1)
                mCamera = Camera.open(cameraId);
                mCamera.autoFocus(new AutoFocusCallback() {

                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    // TODO Auto-generated method stub

                }
            });
            Camera.Parameters params = mCamera.getParameters();
            if (zoom == -1)
                zoom = (params.getMaxZoom() - divZoom) / 3;
            params.setZoom(zoom);
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(params);

            mCamera.setPreviewDisplay(holder);// set preview display
            PriviewCallBack pre = new PriviewCallBack();// new a preview object
            mCamera.setPreviewCallback(pre); // she preview callback
            int degrees = getDisplayOritation(getDispalyRotation(), 0);
            mCamera.setDisplayOrientation(degrees);

            Camera.Parameters parameters = mCamera.getParameters();
            List<Size> list = parameters.getSupportedPreviewSizes();

            Size size = getOptimalPreviewSize(list, getWidth(), getHeight());
            // // get max size of preview and reset paramter for camera
            if (list != null && list.size() >= 1)
                parameters.setPreviewSize(size.width, size.height);
            mCamera.setParameters(parameters);
            mCamera.startPreview();// start preview
            mCamera.cancelAutoFocus();
        } catch (Exception e) {
            // TODO: handle exception
        	if(null != mCameraCallBack){
        		mCameraCallBack.openCamerError(0);
        	}
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            if (size.width == h && size.height == w)
                return size;

            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    protected DisplayMetrics getScreenWH() {
        DisplayMetrics dMetrics = new DisplayMetrics();
        dMetrics = context.getResources().getDisplayMetrics();
        return dMetrics;
    }

    private int getDisplayOritation(int degrees, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    private int getDispalyRotation() {
        int i = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
        switch (i) {
        case Surface.ROTATION_0:
            return 0;
        case Surface.ROTATION_90:
            return 90;
        case Surface.ROTATION_180:
            return 180;
        case Surface.ROTATION_270:
            return 270;
        }
        return 0;
    }

    class PriviewCallBack implements Camera.PreviewCallback {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
        }
    }

    /* stop and release camera */
    public void stopCamera() {
        if (mCamera != null) {
            try {
                /* stop preview */
                mCamera.setPreviewCallback(null);
                isPreviewing = false;
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        stopCamera();
    }

    /**
     * flag has SDCard
     */
    private boolean isHaveSDCard() {
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        return sdcardExist;
    }
}
