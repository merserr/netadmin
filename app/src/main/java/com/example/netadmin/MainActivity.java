package com.example.netadmin;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class MainActivity  extends AppCompatActivity  {


    public final static String WIDGET_PREF = "widget_pref";
    public final static String IPADDRESSA = "ipaddress1";
    public final static String IPADDRESSB = "ipaddress2";
    public final static String IPADDRESSC = "ipaddress3";
    public final static String NETCHOICE = "1";
    public final static String PORT = "port";
    public final static String PASSWORD = "password";


    public static final String BROADCAST_ACTION = "com.example.netadmin";
    public final static String MASSAGE = "inputMassage";
    private static final String FILENAME = "herdcontroldata.csv";
    private static final String LOG_TAG ="==MainActivity==" ;
    private static final String FILENAME_SD = "herdcontroldata.csv";
    private static final String DIR_SD = "Install";


    String herddata = "";
    String passwrd;
    String ipaddress_srv;
    String ipaddress_srv1;
    String ipaddress_srv2;
    String ipaddress_srv3;
    String netchoice;
    String port_srv;
    int intport_srv=23; // 1688

    String ipaddress;
    String macaddress="1122.3344.5566";
    String factory="amx";
    String name="panel";
    String age="age";

    //Создаем список вьюх которые будут создаваться
    private List<View> allEds;
    //счетчик чисто декоративный для визуального отображения edittext'ov
    private int counter = 0;
    BroadcastReceiver br;
    private Activity view2;
    Context ctx;

   @SuppressLint("MissingSuperCall")
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       setContentView(R.layout.activity_main);
       ctx = (Context)MainActivity.this;



       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

       //    ============  add logo in toolbar  ===================
       //    getSupportActionBar().setDisplayShowHomeEnabled(true);
       //    getSupportActionBar().setLogo(R.mipmap.lutron_control);
       //    getSupportActionBar().setDisplayUseLogoEnabled(true);

       //    ============  add logo in toolbar (2nd variant) ======
       ActionBar menu = getSupportActionBar();
       menu.setDisplayShowHomeEnabled(true);
       menu.setIcon(R.mipmap.ic_launcher);


       final SharedPreferences sp = getSharedPreferences(WIDGET_PREF, 0);
       passwrd = sp.getString(MainActivity.PASSWORD, null);
       ipaddress_srv1 = sp.getString(MainActivity.IPADDRESSA, "192.168.1.4");
       ipaddress_srv2 = sp.getString(MainActivity.IPADDRESSB, "192.168.100.4");
       ipaddress_srv3 = sp.getString(MainActivity.IPADDRESSC, "192.168.1.4");
       port_srv = sp.getString(MainActivity.PORT, "1688");

       netchoice = sp.getString(MainActivity.NETCHOICE, "1");
       Log.d(LOG_TAG, "netchoice = " + netchoice );

       if(netchoice.equals("1")) {
           ipaddress_srv = ipaddress_srv1;
       }else if(netchoice.equals("2")){
           ipaddress_srv = ipaddress_srv2;
       }else if(netchoice.equals("3")){
           ipaddress_srv = ipaddress_srv3;
       }
       Log.d(LOG_TAG, "ipaddress_srv = " + ipaddress_srv );



       try {
           intport_srv = Integer.parseInt(port_srv);
       } catch (NumberFormatException nfe) {

       }


       //Toolbar toolbar = findViewById(R.id.toolbar);
       //toolbar.setLogo(R.mipmap.ic_launcher);        // add logo in toolbar
       //setSupportActionBar(toolbar);                 // add name & calling menu


       //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_USE_LOGO);


       //==========================================================================
       // Receive massage from TCPService and make Toast
       //==========================================================================
       // create BroadcastReceiver

       br = new BroadcastReceiver() {
           public void onReceive(Context context, Intent intent) {
               String massage1 = intent.getStringExtra(MASSAGE);
                       Log.d(LOG_TAG, "onReceive: massage1 = " + massage1);
               assert massage1 != null;
               Processing(massage1);
           }
       };
       // create filter for BroadcastReceiver
       IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
       // registration (On) BroadcastReceiver
       registerReceiver(br, intFilt);
       //==========================================================================

       Button button_get_data_from_database = (Button) findViewById(R.id.button_read_SD);
       Button button_get_data_from_cisco = (Button) findViewById(R.id.button_read_cisco);

       //инициализировали наш массив с edittext.aми
       allEds = new ArrayList<View>();

       //находим наш linear который у нас под кнопкой add edittext в activity_main.xml


        /*
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createfield("00:11:22:33:44:55");
            }
        });
*/

       button_get_data_from_database.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
               vibrator.vibrate(70);
               allEds.clear();
               final LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
               linear.removeAllViews();
               String source = "database";
               get_data_from_server(source);
           }
       });
       button_get_data_from_database.setOnLongClickListener(new View.OnLongClickListener(){
           @Override
           public boolean onLongClick(View view) {
               Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
               vibrator.vibrate(70);
               allEds.clear();
               final LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
               linear.removeAllViews();
               get_test_data();
               return true;
           }
       });

        button_get_data_from_cisco.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
               vibrator.vibrate(70);
               allEds.clear();
               final LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
               linear.removeAllViews();
               String source = "cisco";
               get_data_from_server(source);
           }
       });

   }

    private void get_test_data() {
        Log.d(LOG_TAG, "get_test_data");
        String inputMassage = "{{192.168.1.1:a80c.0df3.1234:-:Cisco:Gateway}{192.168.1.5:1122.3344.5501:4:Gigabyte:HTPC}{192.168.1.3:2211.546b.3259:0:Apple:father}{192.168.1.4:0051.ad32.2f30:0:Sony:guest}{192.168.1.6:684f.f07d.dd55:1:D-Link:switch}{192.168.1.8:7654.fda7.d3f8:32:Xiaomi:son}{192.168.1.11:b887.b277.a591:58:no data:guest2}{192.168.1.22:a50c.11f3.3f02:-:Cisco:phone}}";
        Intent intent1 = new Intent(MainActivity.BROADCAST_ACTION);
        intent1.putExtra(MASSAGE, inputMassage);
        sendBroadcast(intent1);
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
    }


    void writeFile() {
        try {
            Log.d(LOG_TAG, "writeFile");
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(FILENAME, MODE_PRIVATE)));
            // пишем данные
            bw.write(herddata);
            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл записан");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void readFile() {
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FILENAME)));
            String str = "";
            // читаем содержимое
            //Log.d(LOG_TAG, "readFile");
            while ((str = br.readLine()) != null) {
                // Log.d(LOG_TAG, str);
                Log.d(LOG_TAG, "read File: "+str);
                parseline(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void readFileSD() {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        //  формируем объект File, который содержит путь к файлу
        Log.d(LOG_TAG, String.valueOf(sdPath));
        File sdFile = new File(sdPath, FILENAME_SD);
        try {
            // открываем поток для чтения
            Log.d(LOG_TAG, "==BufferedReader==");

            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                Log.d(LOG_TAG, str);
                herddata=herddata+str+"\n\r";
            }
            Log.d(LOG_TAG, herddata);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void get_data_from_server(String source){
        //Toast.makeText(this, "Отслеживание переключения: " + (isChecked ? "on" : "off"), Toast.LENGTH_SHORT).show();

        //String macaddresfromtextview = ((TextView)((View)(compoundButton.getParent())).findViewWithTag("id1")).getText().toString();
        //Log.d(LOG_TAG, "======== if onCheckedChanged  =======: "+macaddresfromtextview);
        //String command = isChecked ? "ON :" : "OFF:";
        //String sending_command = command + macaddresfromtextview;
        String sending_command = "info";
        if(source.equals("database")){sending_command = "getdatafromdatabase";}
        if(source.equals("cisco")){sending_command = "info";}
     //   ctx0 = (Context) MainActivity.this;
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, TCPService.class);
        intent.putExtra("ipaddress", ipaddress_srv);
        intent.putExtra("port", intport_srv);
        intent.putExtra("command", sending_command);
        Log.d(LOG_TAG, "sending_command: "+sending_command);
        MainActivity.this.startService(intent);
    }

    String[] parseline(String str){
        String[] macaddroutput = new String[17]; //  ?????
        // if (!inputMassage.matches("\\[(\\[\"-?\\d{10}\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\"\\],?)+\\]")) { }

        if (str.matches("[0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}.*")) {
            Log.d(LOG_TAG, "input parseline Massage: "+ str);
            macaddroutput[0] = str.substring(0, (0 + 17));
            macaddroutput[1] = str.substring(18);
         //   Log.d(LOG_TAG, "macaddroutput = "+macaddroutput);
            createfield(macaddroutput);
        }

        return macaddroutput;
    }


    void createfield(String[] textfield){
        final LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
        counter++;
        //берем наш кастомный лейаут находим через него все наши кнопки и едит тексты, задаем нужные данные
        final View view = getLayoutInflater().inflate(R.layout.custom_edittext_layout, null);

        TextView text = (TextView) view.findViewById(R.id.editText);
        text.setText(textfield[0]);
     //   text.setText("192.168.100.222");

        TextView text2 = (TextView) view.findViewById(R.id.editText2);
        text2.setText(textfield[1]);
     //   text2.setText("1122.3344.5566");


//        TextView text3 = (TextView) view.findViewById(R.id.editText3);
//        text3.setText(textfield[2]);
     //   text3.setText("111");


        TextView text4 = (TextView) view.findViewById(R.id.editText4);
        text4.setText(textfield[3]);
     //  text4.setText("manufacture");


        TextView text5 = (TextView) view.findViewById(R.id.editText5);
        text5.setText(textfield[4]);
     //   text5.setText("name-name");

        Button buttongo = (Button) view.findViewById(R.id.button_go);
        buttongo.setText(textfield[2]);

     //   registerForContextMenu(buttongo);



        buttongo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(80);

             //   Log.d(LOG_TAG, "onLongClick: ");

                ipaddress = ((TextView)view.findViewWithTag("id1")).getText().toString();
                macaddress = ((TextView)view.findViewWithTag("id2")).getText().toString();
                factory = ((TextView)view.findViewWithTag("id4")).getText().toString();
                name = ((TextView)view.findViewWithTag("id5")).getText().toString();
                age = ((Button)view.findViewWithTag("id3")).getText().toString();


                Log.d(LOG_TAG, "===== ipaddress ====== : "+ipaddress);
                Log.d(LOG_TAG, "===== macaddress ===== : "+macaddress);
                Log.d(LOG_TAG, "===== factory ======== : "+factory);
                Log.d(LOG_TAG, "===== name =========== : "+name);

                Intent intent = new Intent();
                intent.putExtra("ipaddress_srv", ipaddress_srv);
                intent.putExtra("port_srv", port_srv);
                intent.putExtra("password", passwrd);
                intent.putExtra("ipaddress", ipaddress);
                intent.putExtra("macaddress", macaddress);
                intent.putExtra("factory", factory);
                intent.putExtra("name", name);
                intent.putExtra("age", age);
                intent.setClass(ctx, Control_panel.class);
                startActivity(intent);
                return false;
            }
        });

        //добавляем все что создаем в массив
        allEds.add(view);
        //добавляем елементы в linearlayout
        linear.addView(view);
    }

    void Processing(String inputMassage){
    //    Toast.makeText(MainActivity.this, inputMassage, Toast.LENGTH_LONG).show();
        //Log.d(LOG_TAG, "input Processing Massage1: "+ inputMassage);
        if (inputMassage.matches("\\{\\{.*\\}\\}")) {
            inputMassage = inputMassage.substring(2);

            String line[] = inputMassage.split("\\}\\{");  // по пробелу
            String clients[][] = new String[line.length][5];
            int count=0;
            while (count < line.length){
                String subline[] = line[count].split(":");
                clients[count][0] = subline[0];
                clients[count][1] = subline[1];
                clients[count][2] = subline[2];
                clients[count][3] = subline[3];
                clients[count][4] = subline[4];
                Log.d(LOG_TAG, clients[count][0]+"   "+clients[count][1]+"   "+clients[count][2]+"   "+clients[count][3]+"   "+clients[count][4]);
                createfield(subline);
                count++;
            }

        } else if (inputMassage.matches("No connect to .*")) {
            Toast.makeText(MainActivity.this, inputMassage, Toast.LENGTH_LONG).show();
        } else if (inputMassage.matches("\\{\"cli\": \"NOT\".*")) {
            Toast.makeText(MainActivity.this, "Client not ready", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //------------- menu setting and about ----------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                //your action
                //Log.d(LOG_TAG, "===action_settings===");
                setContentView(R.layout.config);
                final SharedPreferences sp = getSharedPreferences(WIDGET_PREF, 0);

                final EditText edit_text_ipaddress1 = (EditText) findViewById(R.id.et_ipaddress1);
                final EditText edit_text_ipaddress2 = (EditText) findViewById(R.id.et_ipaddress2);
                final EditText edit_text_ipaddress3 = (EditText) findViewById(R.id.et_ipaddress3);
                final EditText edit_text_port = (EditText) findViewById(R.id.et_port);
                final EditText edit_text_password = (EditText) findViewById(R.id.et_password);
                edit_text_ipaddress1.setText(sp.getString(MainActivity.IPADDRESSA, null));
                edit_text_ipaddress2.setText(sp.getString(MainActivity.IPADDRESSB, null));
                edit_text_ipaddress3.setText(sp.getString(MainActivity.IPADDRESSC, null));
                edit_text_port.setText(sp.getString(MainActivity.PORT, null));
                edit_text_password.setText(sp.getString(MainActivity.PASSWORD, null));

                if(netchoice.equals("1")) {
                    edit_text_ipaddress1.setTextColor(getResources().getColor(R.color.g));
                    edit_text_ipaddress2.setTextColor(getResources().getColor(R.color.gr));
                    edit_text_ipaddress3.setTextColor(getResources().getColor(R.color.gr));
                }else if(netchoice.equals("2")){
                    edit_text_ipaddress1.setTextColor(getResources().getColor(R.color.gr));
                    edit_text_ipaddress2.setTextColor(getResources().getColor(R.color.g));
                    edit_text_ipaddress3.setTextColor(getResources().getColor(R.color.gr));
                }else if(netchoice.equals("3")){
                    edit_text_ipaddress1.setTextColor(getResources().getColor(R.color.gr));
                    edit_text_ipaddress2.setTextColor(getResources().getColor(R.color.gr));
                    edit_text_ipaddress3.setTextColor(getResources().getColor(R.color.g));
                }

                edit_text_ipaddress1.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View view) {
                        SharedPreferences.Editor editor = sp.edit();
                        edit_text_ipaddress1.setTextColor(getResources().getColor(R.color.g));
                        edit_text_ipaddress2.setTextColor(getResources().getColor(R.color.gr));
                        edit_text_ipaddress3.setTextColor(getResources().getColor(R.color.gr));
                        editor.putString(NETCHOICE, "1");
                        editor.commit();
                        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        vibrator.vibrate(80);
                        return false;
                    }
                });
                edit_text_ipaddress2.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View view) {
                        SharedPreferences.Editor editor = sp.edit();
                        edit_text_ipaddress1.setTextColor(getResources().getColor(R.color.gr));
                        edit_text_ipaddress2.setTextColor(getResources().getColor(R.color.g));
                        edit_text_ipaddress3.setTextColor(getResources().getColor(R.color.gr));
                        editor.putString(NETCHOICE, "2");
                        editor.commit();
                        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        vibrator.vibrate(80);
                        return false;
                    }
                });
                edit_text_ipaddress3.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View view) {
                        SharedPreferences.Editor editor = sp.edit();
                        edit_text_ipaddress1.setTextColor(getResources().getColor(R.color.gr));
                        edit_text_ipaddress2.setTextColor(getResources().getColor(R.color.gr));
                        edit_text_ipaddress3.setTextColor(getResources().getColor(R.color.g));
                        editor.putString(NETCHOICE, "3");
                        editor.commit();
                        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        vibrator.vibrate(80);
                        return false;
                    }
                });

                Button button_ok = (Button) findViewById(R.id.button_ok);
                button_ok.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        //Log.d(LOG_TAG, "save_password");
                        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        vibrator.vibrate(80);
                        SharedPreferences.Editor editor = sp.edit();
                    //    editor.putString(IPADDRESS, edit_text_ipaddress.getText().toString());
                    //    editor.putString(PORT, edit_text_port.getText().toString());
                     //   editor.putString(PASSWORD, edit_text_password.getText().toString());


                        ipaddress_srv1 = edit_text_ipaddress1.getText().toString();
                        ipaddress_srv2 = edit_text_ipaddress2.getText().toString();
                        ipaddress_srv3 = edit_text_ipaddress3.getText().toString();
                        port_srv = edit_text_port.getText().toString();
                        passwrd = edit_text_password.getText().toString();

                        IpAddressValidator validator = new IpAddressValidator();
                        boolean wrongdata = false;
                       if(validator.isValid(ipaddress_srv1)){
                            Log.d(LOG_TAG, "======= ipaddress OK ========");
                        }else{
                            Log.d(LOG_TAG, "======= ipaddress Wrong! ========");
                            wrongdata = true;
                            Toast.makeText(MainActivity.this, "wrong ipaddress!", Toast.LENGTH_SHORT).show();
                        }
                        if(validator.isValid(ipaddress_srv2)){
                            Log.d(LOG_TAG, "======= ipaddress OK ========");
                        }else{
                            Log.d(LOG_TAG, "======= ipaddress Wrong! ========");
                            wrongdata = true;
                            Toast.makeText(MainActivity.this, "wrong ipaddress2!", Toast.LENGTH_SHORT).show();
                        }

                        if (port_srv.matches("\\d{1,5}")) {
                            intport_srv = Integer.parseInt(port_srv);  //65536
                            if(intport_srv>65536){
                                intport_srv=65536;
                                port_srv="65536";
                                //edit_text_port.setText(port);
                            }
                            Log.d(LOG_TAG, "======= port OK ========");
                        }else{
                            Toast.makeText(MainActivity.this, "wrong port!", Toast.LENGTH_SHORT).show();
                            wrongdata = true;
                            Log.d(LOG_TAG, "======= port Wrong! ========");
                        }
                        if (passwrd.matches(".{4,14}")) {
                            Log.d(LOG_TAG, "======= password OK ========");
                        }else{
                            Toast.makeText(MainActivity.this, "password too small or too long!", Toast.LENGTH_SHORT).show();
                            wrongdata = true;
                            Log.d(LOG_TAG, "======= password Wrong! ========");
                        }

                       // try {
                       //     intport = Integer.parseInt(port);
                       // } catch(NumberFormatException nfe) { }

                        if(!wrongdata){
                            editor.putString(IPADDRESSA, ipaddress_srv1);
                            editor.putString(IPADDRESSB, ipaddress_srv2);
                            editor.putString(IPADDRESSC, ipaddress_srv3);
                            editor.putString(PORT, port_srv);
                            editor.putString(PASSWORD, passwrd);
                            Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_SHORT).show();}

                        editor.commit();

                        ipaddress_srv1 = sp.getString(MainActivity.IPADDRESSA, null);
                        ipaddress_srv2 = sp.getString(MainActivity.IPADDRESSB, null);
                        ipaddress_srv3 = sp.getString(MainActivity.IPADDRESSC, null);
                        Log.d(LOG_TAG, ipaddress_srv1 +"   "+ ipaddress_srv2 +"   "+ ipaddress_srv3 );
                        }
                });
                break;
            case R.id.action_about:
                //your action
                Log.d(LOG_TAG, "===action_about===");
                setContentView(R.layout.about);
            break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public class IpAddressValidator {

        private static final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";

        private static final String IP_REGEXP = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;

        private final Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);

        // Return true when *address* is IP Address
        private boolean isValid(String address) {
            return IP_PATTERN.matcher(address).matches();
        }
    }



}