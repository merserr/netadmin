package com.example.netadmin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import static com.example.netadmin.MainActivity.BROADCAST_ACTION;
import static com.example.netadmin.MainActivity.MASSAGE;

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
    String pinganswer;
    String age;

    BroadcastReceiver br;


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
        age = "age = " + getIntent().getStringExtra("age") + " min";

        try {
            intport_srv = Integer.parseInt(port_srv);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }

        setContentView(R.layout.control_panel);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final TextView text = findViewById(R.id.editText);
        text.setText(ipaddress);

        final TextView text2 = findViewById(R.id.editText2);
        text2.setText(macaddress);

        final TextView text3 = findViewById(R.id.editText3);
        text3.setText(factory);

        final TextView text4 = findViewById(R.id.editText4);
        text4.setText(name);

        Button button_save = findViewById(R.id.button_save);
        button_save.setText(R.string.button_save);
        button_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_save ");
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(70);
                ipaddress = text.getText().toString();
                macaddress = text2.getText().toString();
                factory = text3.getText().toString();
                name = text4.getText().toString();

                MacAddressValidator macvalidator = new MacAddressValidator();
                IpAddressValidator ipvalidator = new IpAddressValidator();
                if (!ipvalidator.isValid(ipaddress)){
                    Toast.makeText(Control_panel.this, "wrong ipaddress!", Toast.LENGTH_SHORT).show();
                }else if (!macvalidator.isValid(macaddress)){
                    Toast.makeText(Control_panel.this, "wrong macaddress!", Toast.LENGTH_SHORT).show();
                }else if (factory.length()>20){
                    Toast.makeText(Control_panel.this, "factory too long!", Toast.LENGTH_SHORT).show();
                }else if (name.length()>20){
                Toast.makeText(Control_panel.this, "name too long!", Toast.LENGTH_SHORT).show();
                }else {
                    ipaddress = text.getText().toString();
                    macaddress = text2.getText().toString();
                    factory = text3.getText().toString();
                    name = text4.getText().toString();
                    sending_command = "save:ipaddress=" + ipaddress + ",macaddress=" + macaddress + ",factory=" + factory + ",name=" + name;
                    send_command_to_server();
                }
            }
        });

        Button button_on = findViewById(R.id.button_on);
        button_on.setText(R.string.button_on);
        button_on.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_on ");
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(70);

                MacAddressValidator validator = new MacAddressValidator();
                if (validator.isValid(macaddress)) {
                    sending_command = "on:macaddress=" + macaddress;
                    send_command_to_server();
                }else{
                    Toast.makeText(Control_panel.this, "wrong macaddress!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button button_off = findViewById(R.id.button_off);
        button_off.setText(R.string.button_off);
        button_off.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_off ");
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(70);
                MacAddressValidator validator = new MacAddressValidator();
                if (validator.isValid(macaddress)) {
                    sending_command = "off:macaddress=" + macaddress;
                    send_command_to_server();
                }else{
                    Toast.makeText(Control_panel.this, "wrong macaddress!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button button_full = findViewById(R.id.button_full);
        button_full.setText(R.string.button_full);
        button_full.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_full ");
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(70);
                IpAddressValidator validator = new IpAddressValidator();
                if (validator.isValid(ipaddress)) {
                    sending_command = "full:ipaddress=" + ipaddress;
                    send_command_to_server();
                }else{
                    Toast.makeText(Control_panel.this, "wrong ipaddress!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button button_low = findViewById(R.id.button_low);
        button_low.setText(R.string.button_low);
        button_low.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_low ");
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(70);
                IpAddressValidator validator = new IpAddressValidator();
                if (validator.isValid(ipaddress)) {
                    sending_command = "low:ipaddress=" + ipaddress;
                    send_command_to_server();
                }else{
                    Toast.makeText(Control_panel.this, "wrong ipaddress!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button button_ping = findViewById(R.id.button_ping);
        button_ping.setText(R.string.string_button_ping);
        button_ping.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_ping ");
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(70);
                ipaddress = text.getText().toString();
                IpAddressValidator validator = new IpAddressValidator();
                if (validator.isValid(ipaddress)) {
                    sending_command = "ping:ipaddress=" + ipaddress;
                    send_command_to_server();
                    TextView text_ping_answer = findViewById(R.id.ping_answer);
                    text_ping_answer.setText("???");
                    text_ping_answer.setTextColor(Color.WHITE);
                }
            }
        });

        final TextView text_ping_answer = findViewById(R.id.ping_answer);
        text_ping_answer.setText(age);

        //==========================================================================
        // Receive massage from TCPService and make Toast
        //==========================================================================
        // create BroadcastReceiver

        br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String massage2 = intent.getStringExtra(MASSAGE);

                Log.d(LOG_TAG, "onReceive: massage1 = " + massage2);
                assert massage2 != null;
               // Processing(massage1);
                if(massage2.matches("pinganswer:.*")){
                    pinganswer= massage2.substring(11);
                    text_ping_answer.setTextColor(Color.YELLOW);
                    if(pinganswer.matches(".5/5.*")) {text_ping_answer.setTextColor(Color.GREEN);}
                    if(pinganswer.matches(".0/5.*")) {text_ping_answer.setTextColor(Color.RED);}
                    Log.d(LOG_TAG, "===PING!=== = " + pinganswer);
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(70);
                    text_ping_answer.setText(pinganswer);

                }

            }
        };
        // create filter for BroadcastReceiver
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        // registration (On) BroadcastReceiver
        registerReceiver(br, intFilt);
        //==========================================================================
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
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

    public static class MacAddressValidator {

        private static final String fourhexdigit = "([0-9A-Fa-f]{4})";

        private static final String IP_REGEXP = fourhexdigit + "\\." + fourhexdigit + "\\." + fourhexdigit;

        private final Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);

        // Return true when *address* is Mac Address
        private boolean isValid(String address) {
            return IP_PATTERN.matcher(address).matches();
        }
    }


    public static class IpAddressValidator {

        private static final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";

        private static final String IP_REGEXP = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;

        private final Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);

        // Return true when *address* is IP Address
        private boolean isValid(String address) {
            return IP_PATTERN.matcher(address).matches();
        }
    }



}
