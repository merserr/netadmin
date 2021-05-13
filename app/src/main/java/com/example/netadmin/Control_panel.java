package com.example.netadmin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

public class Control_panel extends Activity{

    @SuppressLint("MissingSuperCall")
    protected void onCreate(Bundle savedInstanceState) {

        String ipaddress = getIntent().getStringExtra("ipaddress");
        String macaddress = getIntent().getStringExtra("macaddress");
        String factory = getIntent().getStringExtra("factory");
        String name = getIntent().getStringExtra("name");

        setContentView(R.layout.control_panel);


    }






}
