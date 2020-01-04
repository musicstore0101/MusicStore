package app.music.musicstore;

import android.Manifest;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity
        {

    public long downloadID;
    private Button button;

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                Toast.makeText(MainActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
                System.out.println(" !!!! Shantanu download completed");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button=findViewById(R.id.download_pardesio_se);

        //shantanu test setting second button as invisible
        findViewById(R.id.button2).setVisibility(View.INVISIBLE);
        Log.d("SHANTANU CLICK", "onCreate is being executed");
        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(view);
            }
        });
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

    // shantanu testing this
    public void sendMessage(View view) {
        System.out.println("Shantanu is here!!!!!!!!!!!!!!!!!!!");
        Log.d("SHANTANU CLICK", "sendMessage is being executed");
        switch(view.getId()) {
            case R.id.download_pardesio_se:
                findViewById(R.id.button2).setVisibility(view.VISIBLE);
                Log.d("SHANTANU","sendMessage case is hit");

                Request request;// Set if download is allowed on roaming network
                request = new Request(Uri.parse("http://ec2-13-234-37-59.ap-south-1.compute.amazonaws.com:8080/static/Baharo_phool_barsao.mp3"))

                        .setTitle("Baharo phool barsao downloading..")// Title of the Download Notification
                        .setDescription("Downloading")// Description of the Download Notification
                        .setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)// Visibility of the download Notification
                        //.setRequiresCharging(false)// Set if charging is required to begin the download
                        .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                        .setAllowedOverRoaming(true);

                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "MusicStore")
                        .mkdirs();

                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"Baharo_phool_barsao.mp3");
                //request.setDestinationInExternalPublicDir("/Rafi/","Baharo_phool_barsao.mp3");

                DownloadManager downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                assert downloadManager != null;
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 1);
                    // this will request for permission when permission is not true
                    System.out.println("Shantanu requesting for permission");
                }else{
                    // Download code here
                    try {
                        downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        findViewById(R.id.button3).setVisibility(view.VISIBLE);
                    }
                    System.out.println("@@@@@ Shantanu after enqueuing request");
                }

                break;
            default:
                throw new IllegalStateException("Shantanu Unexpected value: " + view.getId());
        }
    }

    public void onDestroy() {
          super.onDestroy();
          unregisterReceiver(onDownloadComplete);
    }
}
