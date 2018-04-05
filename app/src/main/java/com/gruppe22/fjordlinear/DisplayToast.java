package com.gruppe22.fjordlinear;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Schnappi on 16.03.2018.
 */

public class DisplayToast implements Runnable {
    private final Context mContext;
    private String mText;

    public DisplayToast(Context mContext, String text){
        this.mContext = mContext;
        mText = text;
    }

    public void run(){
        Toast.makeText(mContext, mText, Toast.LENGTH_LONG).show();
    }
}
