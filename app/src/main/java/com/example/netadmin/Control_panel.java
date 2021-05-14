package com.example.netadmin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Control_panel extends Activity{

    String ipaddress_srv;
    String port_srv;
    int intport_srv;
    String sending_command;
    String password;

    String ipaddress;
    String macaddress="1122.3344.5566";
    String factory="amx";
    String name="panel";



    private static final String LOG_TAG = "===Control_panel===" ;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ipaddress_srv = getIntent().getStringExtra("ipaddress_srv");
        port_srv = getIntent().getStringExtra("port_srv");
        password = getIntent().getStringExtra("password");
        ipaddress = getIntent().getStringExtra("ipaddress");
        macaddress = getIntent().getStringExtra("macaddress");
        factory = getIntent().getStringExtra("factory");
        name = getIntent().getStringExtra("name");

        try {
            intport_srv = Integer.parseInt(port_srv);
        } catch (NumberFormatException nfe) {

        }

        setContentView(R.layout.control_panel);

        TextView text = (TextView) findViewById(R.id.editText);
        text.setText(ipaddress);

        TextView text2 = (TextView) findViewById(R.id.editText2);
        text2.setText(macaddress);

        final TextView text3 = (TextView) findViewById(R.id.editText3);
        text3.setText(factory);

        final TextView text4 = (TextView) findViewById(R.id.editText4);
        text4.setText(name);

        Button button_save = (Button) findViewById(R.id.button_save);
        button_save.setText("Save");
        //    buttongo.setOnClickListener();
        button_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(LOG_TAG, "onClick button_save ");


                factory = text3.getText().toString();
                name = text4.getText().toString();

                sending_command = "save:ipaddress="+ipaddress+",macaddress="+macaddress+",factory="+factory+",name="+name;
                send_command_to_server();

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

    void send_command_to_server(){

        Log.d(LOG_TAG, "send_command_to_server "+ipaddress_srv+" port "+intport_srv);

        //Toast.makeText(this, "Отслеживание переключения: " + (isChecked ? "on" : "off"), Toast.LENGTH_SHORT).show();

        //String macaddresfromtextview = ((TextView)((View)(compoundButton.getParent())).findViewWithTag("id1")).getText().toString();
        //Log.d(LOG_TAG, "======== if onCheckedChanged  =======: "+macaddresfromtextview);
        //String command = isChecked ? "ON :" : "OFF:";
        //String sending_command = command + macaddresfromtextview;
        //String sending_command = "info";

        //   ctx0 = (Context) MainActivity.this;
        Intent intent = new Intent();
        intent.setClass(Control_panel.this, TCPService.class);
        intent.putExtra("ipaddress", ipaddress_srv);
        intent.putExtra("port", intport_srv);
        intent.putExtra("command", sending_command);
        Log.d(LOG_TAG, "sending_command: "+sending_command);
        Control_panel.this.startService(intent);
    }


}
