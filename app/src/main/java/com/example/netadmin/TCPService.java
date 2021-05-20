package com.example.netadmin;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static com.example.netadmin.MainActivity.MASSAGE;

public class TCPService extends Service {

    final String LOG_TAG = "==TCPService==";
    String ipaddress = "0.0.0.0";
    int port = 1;
    String command = "";
    String sendcommand = "ooo";

    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent!=null) {
            ipaddress = intent.getStringExtra("ipaddress");
            port = intent.getIntExtra("port", 2);
            command = intent.getStringExtra("command");
        }

        new TCPConnect(ipaddress, port, command).execute();

        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("StaticFieldLeak")
    class TCPConnect extends AsyncTask<Void, Integer, Void> {

        boolean connect=false;
        final int port;
        final String ipaddress;
        final String sendstring;
        String inputMassage;
        Socket socket = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        BufferedReader mBufferIn = null;

        public TCPConnect(String ipaddress_in, int port_in, String command) {
            // TODO Auto-generated constructor stub
            ipaddress = ipaddress_in;
            port=port_in;
            sendstring = "\r\n"+command+"\r\n";

            //Log.d(LOG_TAG, "command: "+command);
            // ---------- Проверка данных -------------
            //    KBP, [2:4:22], 2
            // if (command.matches("O[NF].:[0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}.*")) {
          //  if (command.matches("save.*")) {
          //      sendstring = "\r\n"+command+"\r\n";
          //  }else{
          //      sendstring="\r\ninfo\r\n";
          //  }

            Log.d(LOG_TAG, "sendstring: "+sendstring);

        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(LOG_TAG, "doInBackground sendstring: "+sendstring);

            Log.d(LOG_TAG, "sending comand to address "+ipaddress+" port "+port);

            //sendstring="\r\ninfo\r\n";

            try {
                socket = new Socket();
                socket.setSoTimeout(15000);
                socket.connect(new InetSocketAddress(ipaddress, port), 2000);
                connect=socket.isConnected();

                dataInputStream = new DataInputStream(socket.getInputStream()); //read from client through inputstream

                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                //    dataOutputStream.writeBytes(" ");
                dataOutputStream.writeBytes(sendstring);

                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //read input massage from buffer
                inputMassage = mBufferIn.readLine();

//  delay 5000ms
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
//                }

                dataInputStream.close();
                dataOutputStream.close();
                socket.close();
                //               Log.d(LOG_TAG, "--socket.close()--");

            }catch (UnknownHostException e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "--UnknownHostException--");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "--IOException--");
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!connect) {
                Log.d(LOG_TAG, "NOT CONNECT");

                // send massage "No connect" to MainActivity
                Intent intent2 = new Intent(MainActivity.BROADCAST_ACTION);
                intent2.putExtra(MASSAGE, "No connect to "+ipaddress+":"+port);
                sendBroadcast(intent2);
            } else {
                Log.d(LOG_TAG, "inputMassage2: "+ inputMassage);

                Intent intent1 = new Intent(MainActivity.BROADCAST_ACTION);
                intent1.putExtra(MASSAGE, inputMassage);
                sendBroadcast(intent1);
            }
            connect=false;
        }
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }
}
