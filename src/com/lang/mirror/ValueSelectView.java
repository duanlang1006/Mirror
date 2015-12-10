package com.lang.mirror;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.shly.shlymirror.R;


public class ValueSelectView extends LinearLayout implements OnSeekBarChangeListener, OnClickListener {

    private SeekBar bar;
    private int currentProgress = 0;
    private ImageView btnSub;
    private ImageView btnAdd;
    private MainActivity activity;

    public ValueSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_numberselectview, null);
        activity = ((MainActivity) context);
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        bar = (SeekBar) view.findViewById(R.id.view_sb_selectnum);
        btnSub = (ImageView) view.findViewById(R.id.view_btn_subtract);
        btnAdd = (ImageView) view.findViewById(R.id.view_btn_add);

        bar.setOnSeekBarChangeListener(this);
        bar.setProgress(50);

        btnAdd.setOnClickListener(this);
        btnSub.setOnClickListener(this);

        this.addView(view);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            currentProgress = (int) (progress * (activity.getMaxZoom() / 100.0f));
            activity.setZoom(currentProgress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.view_btn_subtract) {
            currentProgress = activity.getZoom();
            currentProgress -= 1;
            bar.setProgress((int) (currentProgress / (activity.getMaxZoom() / 100.0f)));
            activity.setZoom((currentProgress > 0 ? currentProgress : activity.getZoom()));

        } else {
            currentProgress = activity.getZoom();
            currentProgress += 1;
            bar.setProgress((int) (currentProgress / (activity.getMaxZoom() / 100.0f)));
            activity.setZoom(currentProgress > activity.getMaxZoom() ? activity.getZoom() : currentProgress);
        }
    }

}
