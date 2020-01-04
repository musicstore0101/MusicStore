package app.music.musicstore;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


/* A new Scrollable activity*/
public class MohammadRafiSongs extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mohammad_rafi_songs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button = findViewById(R.id.badan_pe_sitare);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "http://ec2-13-234-37-59.ap-south-1.compute.amazonaws.com:8080/static/mohammad_rafi/badan_pe_sitare.mp3";
                String filename = "Badan_pe_sitare.mp3";
                startSongDownload(uri, filename);
            }
        });

        button = findViewById(R.id.baharo_phool_barsao);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "http://ec2-13-234-37-59.ap-south-1.compute.amazonaws.com:8080/static/mohammad_rafi/baharo_phool_barsao.mp3";
                String filename = "baharo_phool_barsao.mp3";
                startSongDownload(uri, filename);
            }
        });

        button = findViewById(R.id.bar_bar_dekho);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "http://ec2-13-234-37-59.ap-south-1.compute.amazonaws.com:8080/static/mohammad_rafi/bar_bar_dekho.mp3";
                String filename = "bar_bar_dekho.mp3";
                startSongDownload(uri, filename);
            }
        });

        button = findViewById(R.id.chun_chun_karti_ayi);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "http://ec2-13-234-37-59.ap-south-1.compute.amazonaws.com:8080/static/mohammad_rafi/chun_chun_karti_ayi.mp3";
                String filename = "chun_chun_karti_ayi.mp3";
                startSongDownload(uri, filename);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 1);
            // this will request for permission when permission is not true
            System.out.println("Shantanu requesting for permission");
        } else {
            // Download code here
            try {
                return downloadManager.enqueue(request);// enqueue puts the download request in the queue.
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
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
                System.out.println(" !!!! Shantanu download completed");
            }
        }
    };
}