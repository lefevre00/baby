package com.example.michael.myapplication;

import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.michael.myapplication.baby.BabyPhone;
import com.example.michael.myapplication.parent.ParentPhone;
import com.example.michael.myapplication.server.NanoHttpServer;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private TextView wifiAdressTv;
    private TextView otherAdressTv;
    private RadioButton radioParent;
    private RadioButton radioChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        radioChild = (RadioButton) findViewById(R.id.radio_child);
        radioParent = (RadioButton) findViewById(R.id.radio_parent);
        wifiAdressTv = (TextView) findViewById(R.id.wifi_adress);
        otherAdressTv = (TextView) findViewById(R.id.other_adress);

        radioChild.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Timber.e("Interrupted");
                            }
                            new BabyPhone(getApplicationContext()).publish();
                        }
                    }).start();
                }
            }
        });


        radioParent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new ParentPhone(getApplicationContext()).publish().lookForChild();
                    }
                }).start();
            }
        });

//        new Thread(new Runnable() {
//            public void run() {
//                new NanoHttpServer().start();
//            }
//        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void[] params) {
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            }

            @Override
            protected void onPostExecute(String adress) {
                wifiAdressTv.setText(adress == null ? "Echec" : adress);
            }
        }.execute();


        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void[] params) {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress()) {
                                String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                                Timber.d("Other adress : %s", ip);
                                return ip;
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
                return cm.getActiveNetworkInfo().getTypeName();
            }

            @Override
            protected void onPostExecute(String adress) {
                otherAdressTv.setText(adress == null ? "Echec" : adress);
            }
        }.execute();
    }
}
