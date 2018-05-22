package com.gruppe22.fjordlinear;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {



    private static final String TAG = "InfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // oppdaterer teksten i informajsonstavlen gjevnlig
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                endreText();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "InterruptedException i informasjonstavle thread");
                }
            }
        };
        t.start();
    }

    // endrer informajsonen i fragmentet til den nye plaseringen (håper jeg :))
    public void endreText() {

        String text = "";

        TextView tv1 = (TextView)findViewById(R.id.textView2);
        ImageView iv1 = findViewById(R.id.imageview1);


        switch (hentInformasjon()){

            case "Solheim":
                text = "Her bor Knut";
                iv1.setImageResource(R.drawable.guttatur);
                break;

            case "Eiffel Tower":
                text = "Noe tekst";
                iv1.setImageResource(R.drawable.schnappi);
                break;
        }

        tv1.setText(text);
    }


    public String hentInformasjon()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("informasjon", Context.MODE_PRIVATE);

        return sharedPreferences.getString("plasering", "");

    }

}
