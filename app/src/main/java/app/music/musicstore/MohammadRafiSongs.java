package app.music.musicstore;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import app.music.musicstore.GlobalDefinitions;

import static app.music.musicstore.GlobalDefinitions.g_externalStorageDownloadPath;
import static app.music.musicstore.GlobalDefinitions.g_mohammadRafiDownloadPath;
import static app.music.musicstore.GlobalDefinitions.g_mohammadRafiSongList;
import static app.music.musicstore.GlobalDefinitions.g_mohammadRafiSongListName;
import static app.music.musicstore.GlobalDefinitions.g_parseMohammadRafiSongList;


/* A new Scrollable activity*/
public class MohammadRafiSongs extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mohammad_rafi_songs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        parseMohammadRafiSongList();

        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        createMohammadRafiDownloadButtons();

    }

    private long startSongDownload(String requestUri, String filename) {
        DownloadManager.Request request;// Set if download is allowed on roaming network
        request = new DownloadManager.Request(Uri.parse(requestUri))

                .setTitle(filename + " downloading..")// Title of the Download Notification
                .setDescription("Downloading")// Description of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)// Visibility of the download Notification
                //.setRequiresCharging(false)// Set if charging is required to begin the download
                .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);


        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        assert downloadManager != null;

        try {
            return downloadManager.enqueue(request);// enqueue puts the download request in the queue.
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            // shantanu to work later
            //if (downloadID == id)
            {
                Toast.makeText(MohammadRafiSongs.this, "Download Completed", Toast.LENGTH_SHORT).show();
                System.out.println(" !!!! Shantanu onDownloadComplete");

                DownloadManager.Query q = new DownloadManager.Query();
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                Cursor c = downloadManager.query(q);
                if (c.moveToFirst()) {
                    //Log.i("handleData()", "Download ID: " + downloadID + " / " + c.getInt(c.getColumnIndex(DownloadManager.COLUMN_ID)));
                    Log.i("handleData()", "Download Status: " + c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)));
                    if (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        Log.i("handleData()", "Download URI: " + uriString);
                    } else if (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_FAILED) {
                        Log.i("handleData()", "Reason: " + c.getString(c.getColumnIndex(DownloadManager.COLUMN_REASON)));
                        Toast.makeText(MohammadRafiSongs.this, "May be " + c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON)), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    public void onDestroy() {
        super.onDestroy();
        //shantanu
        unregisterReceiver(onDownloadComplete);
    }

    private void createMohammadRafiDownloadButtons() {
        LinearLayout rafi_layout = (LinearLayout) findViewById(R.id.rafi_linear_layout);

        //shantanu delete later
        Toast.makeText(MohammadRafiSongs.this, "Button show start " + g_mohammadRafiSongList.size(), Toast.LENGTH_SHORT).show();

        for (int i = 0; i < g_mohammadRafiSongList.size(); i++) {
            button = new Button(this);
            button.setText(g_mohammadRafiSongList.get(i));
            //write definition to fetch the id and set it to this button in GlobalDefinition class
            //button.setId(GlobalDefinitions.getButtonId(songName[i]));
            final int p = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //String uri = "http://ec2-13-234-37-59.ap-south-1.compute.amazonaws.com:8080/static/mohammad_rafi/badan_pe_sitare.mp3";
                    String uri = g_mohammadRafiDownloadPath + g_mohammadRafiSongList.get(p) + ".mp3";
                    System.out.println("Shantanu URI to song is : " + uri);
                    String filename = g_mohammadRafiSongList.get(p);
                    startSongDownload(uri, filename);
                    Toast.makeText(MohammadRafiSongs.this, "Download starting..", Toast.LENGTH_SHORT).show();
                }
            });
            rafi_layout.addView(button);
            button.setVisibility(View.VISIBLE);
        }
        Toast.makeText(MohammadRafiSongs.this, "Button show end", Toast.LENGTH_SHORT).show();
    }

    private void parseMohammadRafiSongList() {
        try {
            GlobalDefinitions.g_parseMohammadRafiSongList();
        } catch (FileNotFoundException e) {
            Toast.makeText(MohammadRafiSongs.this, "FileNotFoundException", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(MohammadRafiSongs.this, "IOException", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}