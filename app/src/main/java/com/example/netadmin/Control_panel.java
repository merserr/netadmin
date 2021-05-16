package com.example.netadmin;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

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
            nfe.printStackTrace();
        }

        setContentView(R.layout.control_panel);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final TextView text = (TextView) findViewById(R.id.editText);
        text.setText(ipaddress);

        final TextView text2 = (TextView) findViewById(R.id.editText2);
        text2.setText(macaddress);

        final TextView text3 = (TextView) findViewById(R.id.editText3);
        text3.setText(factory);

        final TextView text4 = (TextView) findViewById(R.id.editText4);
        text4.setText(name);

        Button button_save = (Button) findViewById(R.id.button_save);
        button_save.setText(R.string.button_save);
        button_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_save ");
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

        Button button_on = (Button) findViewById(R.id.button_on);
        button_on.setText(R.string.button_on);
        button_on.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_on ");
                MacAddressValidator validator = new MacAddressValidator();
                if (validator.isValid(macaddress)) {
                    sending_command = "on:macaddress=" + macaddress;
                    send_command_to_server();
                }else{
                    Toast.makeText(Control_panel.this, "wrong macaddress!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button button_off = (Button) findViewById(R.id.button_off);
        button_off.setText(R.string.button_off);
        button_off.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_off ");
                MacAddressValidator validator = new MacAddressValidator();
                if (validator.isValid(macaddress)) {
                    sending_command = "off:macaddress=" + macaddress;
                    send_command_to_server();
                }else{
                    Toast.makeText(Control_panel.this, "wrong macaddress!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button button_full = (Button) findViewById(R.id.button_full);
        button_full.setText(R.string.button_full);
        button_full.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_full ");
                IpAddressValidator validator = new IpAddressValidator();
                if (validator.isValid(ipaddress)) {
                    sending_command = "full:ipaddress=" + ipaddress;
                    send_command_to_server();
                }else{
                    Toast.makeText(Control_panel.this, "wrong ipaddress!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button button_low = (Button) findViewById(R.id.button_low);
        button_low.setText(R.string.button_low);
        button_low.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_low ");
                IpAddressValidator validator = new IpAddressValidator();
                if (validator.isValid(ipaddress)) {
                    sending_command = "low:ipaddress=" + ipaddress;
                    send_command_to_server();
                }else{
                    Toast.makeText(Control_panel.this, "wrong ipaddress!", Toast.LENGTH_SHORT).show();
                }
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
