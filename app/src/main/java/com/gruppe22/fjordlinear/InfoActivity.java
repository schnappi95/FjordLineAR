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
        // Unødvendig?
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

    // endrer informasjonen i fragmentet til den nye plasseringen
    public void endreText() {

        String text = "";

        TextView tv1 = (TextView)findViewById(R.id.textView2);
        ImageView iv1 = findViewById(R.id.imageview1);


        switch (hentInformasjon()){

            case "fisk":
                text = "LAKSevåg";
                iv1.setImageResource(R.drawable.guttatur);
                break;

            case "tur":
                text = "Ulriken";
                iv1.setImageResource(R.drawable.schnappi);
                break;

            case "bygg":
                text = "Bergen tingrett";
                iv1.setImageResource(R.drawable.guttatur);
                break;

            case "bad":
                text = "Verftet";
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

        return sharedPreferences.getString("plassering", "");

    }

}
