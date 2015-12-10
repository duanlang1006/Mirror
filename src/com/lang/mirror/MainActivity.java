package com.lang.mirror;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lang.mirror.utils.BrightnessTools;
import com.shly.shlymirror.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends Activity implements OnClickListener, PreviewView.CameraCallBack {

    PreviewView PreviewView;
    ImageView btnAdd;
    ImageView btnSub;
    ImageView btnHelp;
    ImageView btnBordFrame;
    ImageView imageView;
    ImageView btnTakePhoto;
    TextView lightText;
    RelativeLayout HelpView;
    int bright = 150;
    /**
     * 0|normal 1|help 2|pause  3|h and p
     */
    int state = 0;
    int bordIndex = 0;
    int[] bords = new int[]{R.drawable.boder_bg_01, R.drawable.boder_bg_02, 0};

    private StatusBarManager mStatusBarManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setTranslucentFlag();

        setContentView(R.layout.activity_main);
        findViewById();
        addListenerForView();
        setLinght(1);
        mStatusBarManager = (StatusBarManager) this.getSystemService(Context.STATUS_BAR_SERVICE);
    }

    String TAG = "XU";

    // SHLY: Add for full-screen start
    private void setTranslucentFlag() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.BASE) {
            // TODO(sansid): use the APIs directly when compiling against L sdk.
            // Currently we use reflection to access the flags and the API to set the transparency
            // on the System bars.
            try {
                getWindow().getAttributes().systemUiVisibility |=
                        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                Field drawsSysBackgroundsField = WindowManager.LayoutParams.class.getField(
                        "FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS");
                getWindow().addFlags(drawsSysBackgroundsField.getInt(null));

                Method setStatusBarColorMethod =
                        Window.class.getDeclaredMethod("setStatusBarColor", int.class);
                Method setNavigationBarColorMethod =
                        Window.class.getDeclaredMethod("setNavigationBarColor", int.class);
                setStatusBarColorMethod.invoke(getWindow(), Color.TRANSPARENT);
                setNavigationBarColorMethod.invoke(getWindow(), Color.TRANSPARENT);
            } catch (NoSuchFieldException e) {
                Log.w(TAG, "NoSuchFieldException while setting up transparent bars");
            } catch (NoSuchMethodException ex) {
                Log.w(TAG, "NoSuchMethodException while setting up transparent bars");
            } catch (IllegalAccessException e) {
                Log.w(TAG, "IllegalAccessException while setting up transparent bars");
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "IllegalArgumentException while setting up transparent bars");
            } catch (InvocationTargetException e) {
                Log.w(TAG, "InvocationTargetException while setting up transparent bars");
            } finally {
            }
        }
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        state = 0;
        PreviewView.isPreviewing = true;
        int state = StatusBarManager.DISABLE_EXPAND;
        //   state |= StatusBarManager.DISABLE_NOTIFICATION_ICONS;
        //   state |=  StatusBarManager.DISABLE_SYSTEM_INFO;
        mStatusBarManager.disable(state);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        PreviewView.cancelShow();
        int state = StatusBarManager.DISABLE_NONE;
        mStatusBarManager.disable(state);
        PreviewView.stopCamera();
    }

    private void addListenerForView() {
        // TODO Auto-generated method stub
        btnAdd.setOnClickListener(this);
        btnSub.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
        btnBordFrame.setOnClickListener(this);
        imageView.setOnClickListener(this);
        PreviewView.setOnClickListener(this);
        btnTakePhoto.setOnClickListener(this);
    }

    private void findViewById() {
        // TODO Auto-generated method stub
        btnAdd = (ImageView) this.findViewById(R.id.btn_add);
        btnSub = (ImageView) this.findViewById(R.id.btn_sub);
        btnHelp = (ImageView) this.findViewById(R.id.btn_help);
        imageView = (ImageView) this.findViewById(R.id.light);
        btnTakePhoto = (ImageView) this.findViewById(R.id.view_btn_takephoto);
        lightText = (TextView) this.findViewById(R.id.light_text);
        HelpView = (RelativeLayout) this.findViewById(R.id.HelpView);
        btnBordFrame = (ImageView) this.findViewById(R.id.btn_frame);
        PreviewView = (PreviewView) this.findViewById(R.id.PreviewView);
        PreviewView.setCameraCallBack(this);
    }

    public void setZoom(int zoom) {
        PreviewView.setZoom(zoom);
    }

    public int getZoom() {
        if (PreviewView == null)
            return 0;
        return PreviewView.getZoom();
    }

    public int getMaxZoom() {
        if (PreviewView == null)
            return getZoom();
        return PreviewView.getMaxZoom();
    }

    long last = 0;
    long now = 0;

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.view_btn_takephoto:
                if (state != 2 && state != 3) {
                    now = System.currentTimeMillis();
                    if (now - last > 1200) {
                        PreviewView.takePhoto();// getAndSaveCurrentImage();//PreviewView.stopOrStart();
                        last = now;
                    }
                }
                break;
            case R.id.btn_add:
                setLinght(1);
                break;
            case R.id.btn_sub:
                setLinght(-1);
                break;
            case R.id.light:
//            if(state != 2 && state != 3)
//                PreviewView.takePhoto();// getAndSaveCurrentImage();//PreviewView.stopOrStart();
//            else
//                showToastById(R.string.notphoto);
                break;
            case R.id.PreviewView:
                HelpView.setVisibility(View.GONE);
                state = 0;
//            clickPreview();
                break;
            case R.id.btn_frame:
                changeBord();
                break;
            case R.id.btn_help:
                if (state == 0) {
                    state = 1;
                    HelpView.setVisibility(View.VISIBLE);
                } else if (state == 1) {
                    state = 0;
                    HelpView.setVisibility(View.GONE);
                } else if (state == 2) {
                    state = 3;
                    HelpView.setVisibility(View.VISIBLE);
                } else if (state == 3) {
                    state = 2;// release help state
                    HelpView.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    private void changeBord() {
        // TODO Auto-generated method stub
        PreviewView.setBackgroundResource(bords[bordIndex]);
        bordIndex++;
        if (bordIndex >= bords.length)
            bordIndex = 0;
    }

    public void finishActivity() {
        this.finish();
    }

    private void clickPreview() {
        if (state == 2) {
            state = 0;
            //imageView.setVisibility(View.VISIBLE);
            PreviewView.stopOrStart();
        } else if (state == 0) {
            state = 2;
            //imageView.setVisibility(View.GONE);
            PreviewView.stopOrStart();
        } else if (state == 1) {
            //imageView.setVisibility(View.VISIBLE);
            HelpView.setVisibility(View.GONE);
            state = 0;
        } else if (state == 3) {
            HelpView.setVisibility(View.GONE);
            PreviewView.stopOrStart();
            state = 0;
        }
    }

    private void setLinght(int type) {
        int value = 10 / type;
        bright += value;
        if (bright > 255)
            bright = 255;
        if (bright < 1)
            bright = 1;

        showToast();
        BrightnessTools.setBrightness(this, bright);
    }

    private void showToastById(int id) {
        // TODO Auto-generated method stub
//        float p = bright / 255.0f;
//        int pInt = (int) (p * 100);
        Toast toast = Toast.makeText(this, getString(id), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 160);
        toast.show();
    }

    private void showToast() {
        // TODO Auto-generated method stub
        float p = bright / 255.0f;
        int pInt = (int) (p * 100);
        String text = /**getString(R.string.light) + " " + */pInt + "%";
        lightText.setText(text);
//        Toast toast = Toast.makeText(this, getString(R.string.light) + " "
//                + pInt + "%", Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.TOP, 0, 160);
//        toast.show();
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
//        Intent home = new Intent(Intent.ACTION_MAIN);  
//        home.addCategory(Intent.CATEGORY_HOME);   
//        startActivity(home); 
        super.onBackPressed();
        PreviewView.cancelShow();
    }


    @Override
    public void openCamerError(int code) {
        // TODO Auto-generated method stub
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.open_err_title)
                .setMessage(R.string.open_err_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        finish();
                    }
                }).create();
        dialog.show();
    }
}
