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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
    public final static String IPADDRESS = "ipaddress";
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
    String port;
    int intport=23; // 1688

    String ipaddress;
    String macaddress="1122.3344.5566";
    String factory="amx";
    String name="panel";

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
       ipaddress_srv = sp.getString(MainActivity.IPADDRESS, null);
       port = sp.getString(MainActivity.PORT, null);

       try {
           intport = Integer.parseInt(port);
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
               //        Log.d(LOG_TAG, "onReceive: massage1 = " + massage1);
               assert massage1 != null;
               Processing(massage1);
           }
       };
       // create filter for BroadcastReceiver
       IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
       // registration (On) BroadcastReceiver
       registerReceiver(br, intFilt);
       //==========================================================================

       Button button_save = (Button) findViewById(R.id.button_save);
       Button button_read = (Button) findViewById(R.id.button_read);
       Button button_readSD = (Button) findViewById(R.id.button_read_SD);
       Button addButton = (Button) findViewById(R.id.button);
       Button button_read_cisco = (Button) findViewById(R.id.button_read_cisco);

       //инициализировали наш массив с edittext.aьи
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

       button_save.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               writeFile();
           }
       });

       button_readSD.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               readFileSD();
           }
       });

       button_read.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               readFile();
           }
       });

       button_read_cisco.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               get_data_from_server();
           }
       });

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

    void get_data_from_server(){
        //Toast.makeText(this, "Отслеживание переключения: " + (isChecked ? "on" : "off"), Toast.LENGTH_SHORT).show();

        //String macaddresfromtextview = ((TextView)((View)(compoundButton.getParent())).findViewWithTag("id1")).getText().toString();
        //Log.d(LOG_TAG, "======== if onCheckedChanged  =======: "+macaddresfromtextview);
        //String command = isChecked ? "ON :" : "OFF:";
        //String sending_command = command + macaddresfromtextview;
        String sending_command = "info";

     //   ctx0 = (Context) MainActivity.this;
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, TCPService.class);
        intent.putExtra("ipaddress", ipaddress_srv);
        intent.putExtra("port", intport);
        intent.putExtra("command", sending_command);
        Log.d(LOG_TAG, "sending_command: "+sending_command);
        MainActivity.this.startService(intent);


    }

    String[] parseline(String str){
        String[] macaddroutput = new String[17]; //  ?????
        // if (!inputMassage.matches("\\[(\\[\"-?\\d{10}\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\"\\],?)+\\]")) { }

        if (str.matches("[0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}.*")) {
            Log.d(LOG_TAG, "input parseline Massage: "+ str);
            //int dummyi;
            //dummyi = str.indexOf("r001") + 8;
            //macaddroutput = str.substring(dummyi, (dummyi + 1));
            macaddroutput[0] = str.substring(0, (0 + 17));
            macaddroutput[1] = str.substring(18);
            Log.d(LOG_TAG, "macaddroutput = "+macaddroutput);
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

        registerForContextMenu(buttongo);



        buttongo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

             //   Log.d(LOG_TAG, "onLongClick: ");

                ipaddress = ((TextView)view.findViewWithTag("id1")).getText().toString();
                macaddress = ((TextView)view.findViewWithTag("id2")).getText().toString();
                factory = ((TextView)view.findViewWithTag("id4")).getText().toString();
                name = ((TextView)view.findViewWithTag("id5")).getText().toString();


                Log.d(LOG_TAG, "===== ipaddress ====== : "+ipaddress);
                Log.d(LOG_TAG, "===== macaddress ===== : "+macaddress);
                Log.d(LOG_TAG, "===== factory ======== : "+factory);
                Log.d(LOG_TAG, "===== name =========== : "+name);

                Intent intent = new Intent();
                intent.putExtra("ipaddress", ipaddress);
                intent.putExtra("macaddress", macaddress);
                intent.putExtra("factory", factory);
                intent.putExtra("name", name);
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

    private void initialise_control_panel() {

        //TextView text = (TextView) view2.findViewById(R.id.editText);
        //text.setText(textfield[0]);
           //text.setText("192.168.100.222");



    }


    void Processing(String inputMassage){
    //    Toast.makeText(MainActivity.this, inputMassage, Toast.LENGTH_LONG).show();
        //Log.d(LOG_TAG, "input Processing Massage1: "+ inputMassage);
        if (inputMassage.matches("\\{\\{.*\\}\\}")) {

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
        //        Log.d(LOG_TAG, clients[count][0]+"   "+clients[count][1]+"   "+clients[count][2]+"   "+clients[count][3]+"   "+clients[count][4]);
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

                final EditText edit_text_ipaddress = (EditText) findViewById(R.id.et_ipaddress);
                final EditText edit_text_port = (EditText) findViewById(R.id.et_port);
                final EditText edit_text_password = (EditText) findViewById(R.id.et_password);
                edit_text_ipaddress.setText(sp.getString(MainActivity.IPADDRESS, null));
                edit_text_port.setText(sp.getString(MainActivity.PORT, null));
                edit_text_password.setText(sp.getString(MainActivity.PASSWORD, null));

                Button button_ok = (Button) findViewById(R.id.button_ok);
                button_ok.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        //Log.d(LOG_TAG, "save_password");
                        SharedPreferences.Editor editor = sp.edit();
                    //    editor.putString(IPADDRESS, edit_text_ipaddress.getText().toString());
                    //    editor.putString(PORT, edit_text_port.getText().toString());
                     //   editor.putString(PASSWORD, edit_text_password.getText().toString());


                        ipaddress_srv = edit_text_ipaddress.getText().toString();
                        port = edit_text_port.getText().toString();
                        passwrd = edit_text_password.getText().toString();

                        IpAddressValidator validator = new IpAddressValidator();
                        boolean wrongdata = false;
                        if(validator.isValid(ipaddress_srv)){
                            Log.d(LOG_TAG, "======= ipaddress OK ========");
                        }else{
                            Log.d(LOG_TAG, "======= ipaddress Wrong! ========");
                            wrongdata = true;
                            Toast.makeText(MainActivity.this, "wrong ipaddress!", Toast.LENGTH_SHORT).show();
                        }
                        if (port.matches("\\d{1,5}")) {
                            intport = Integer.parseInt(port);  //65536
                            if(intport>65536){
                                intport=65536;
                                port="65536";
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
                            editor.putString(IPADDRESS, ipaddress_srv);
                            editor.putString(PORT, port);
                            editor.putString(PASSWORD, passwrd);


                            Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_SHORT).show();}


                        editor.commit();
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