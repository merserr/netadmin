package com.example.netadmin;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Control_panel extends Activity{


    private static final String LOG_TAG = "===Control_panel===" ;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String ipaddress = getIntent().getStringExtra("ipaddress");
        String macaddress = getIntent().getStringExtra("macaddress");
        String factory = getIntent().getStringExtra("factory");
        String name = getIntent().getStringExtra("name");

        setContentView(R.layout.control_panel);

        TextView text = (TextView) findViewById(R.id.editText);
        text.setText(ipaddress);

        TextView text2 = (TextView) findViewById(R.id.editText2);
        text2.setText(macaddress);

        TextView text3 = (TextView) findViewById(R.id.editText3);
        text3.setText(factory);

        TextView text4 = (TextView) findViewById(R.id.editText4);
        text4.setText(name);

        Button button_save = (Button) findViewById(R.id.button_save);
        button_save.setText("Save");
        //    buttongo.setOnClickListener();
        button_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(LOG_TAG, "onClick button_save ");
            }
        });

        Button button_on = (Button) findViewById(R.id.button_on);
        button_on.setText("On");
        //    buttongo.setOnClickListener();
        button_on.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(LOG_TAG, "onClick button_on ");
            }
        });

        Button button_off = (Button) findViewById(R.id.button_off);
        button_off.setText("Off");
        //    buttongo.setOnClickListener();
        button_off.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(LOG_TAG, "onClick button_off ");
            }
        });


    }



}
