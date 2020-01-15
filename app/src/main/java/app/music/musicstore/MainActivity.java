package app.music.musicstore;

import android.Manifest;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import static app.music.musicstore.GlobalDefinitions.g_externalStorageDownloadPath;
import static app.music.musicstore.GlobalDefinitions.g_mohammadRafiDownloadPath;
import static app.music.musicstore.GlobalDefinitions.g_mohammadRafiSongListName;

public class MainActivity extends AppCompatActivity {

    public long downloadID;
    static boolean rafiListDownloaded = false;
    public final int PERMISSION_EXTERNAL_STORAGE_WRITE = 1;
    public final int PERMISSION_EXTERNAL_STORAGE_READ = 2;
    public final int PERMISSION_INTERNET = 3;


    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                Toast.makeText(MainActivity.this, "Download Completed now", Toast.LENGTH_SHORT).show();
                System.out.println(" !!!! Shantanu download completed");

                //create mohammad rafi song list

                if (!hasAllRequiredPermissions()) {
                    getAllPermissionsFromUser();
                } else {

                    try {
                        GlobalDefinitions.g_parseMohammadRafiSongList();
                    } catch (FileNotFoundException e)
                    {
                        Toast.makeText(MainActivity.this, "FileNotFoundException", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "IOException", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!hasAllRequiredPermissions()) {
            getAllPermissionsFromUser();
        }

        //shantanu get the individual songs list downloaded from the server
        //downloadKishorKumarSongList();


        /*
        try {
            loadCertificates();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

         */

        Button button = findViewById(R.id.Mohammad_Rafi);

        //shantanu
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //shantanu this is a workaround to call this api twice
                // for correct solution, repeat this activity when the
                // permissions are granted upon the first request

                Toast.makeText(MainActivity.this, "Starting mohammad rafi activity", Toast.LENGTH_SHORT).show();
                startMohammadRafiActivity(view);
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
    public void startMohammadRafiActivity(View view) {


        switch (view.getId()) {
            case R.id.Mohammad_Rafi:

                Log.d("SHANTANU", "sendMessage case is hit");

                Intent intent = new Intent(this, MohammadRafiSongs.class);
                startActivity(intent);

                break;
            default:
                throw new IllegalStateException("Shantanu Unexpected value: " + view.getId());
        }
    }

    public void onDestroy() {
        super.onDestroy();
        //shantanu
        unregisterReceiver(onDownloadComplete);
    }

    private void downloadMohammadRafiSongList() {

        File rafiSongList = new File(g_externalStorageDownloadPath + g_mohammadRafiSongListName);

        if (rafiSongList.exists()) {
            System.out.println("Mohammad Rafi song list already downloaded. Returning.");
            //shantanu delete the file and download it again.
            rafiSongList.delete();
        }

        if (rafiListDownloaded == false) {
            Request request;// Set if download is allowed on roaming network
            request = new Request(Uri.parse(g_mohammadRafiDownloadPath + g_mohammadRafiSongListName))

                    .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                    .setAllowedOverRoaming(true);

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, g_mohammadRafiSongListName);

            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            assert downloadManager != null;

            if (hasAllRequiredPermissions()) {
                try {
                    downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.
                    rafiListDownloaded = true;
                } catch (Exception e) {
                    e.printStackTrace();

                }
                System.out.println("Mohammad Rafi song list download after enqueueing request");
            } else {
                getAllPermissionsFromUser();
            }
        }
    }

    private void downloadKishorKumarSongList() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadCertificates() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        // From https://www.washington.edu/itconnect/security/ca/load-der.crt
        //InputStream caInput = new BufferedInputStream(new FileInputStream("res/raw/publickey.cer"));
        InputStream in = this.getResources().openRawResource(R.raw.publickey);

        //String keyStoreType = KeyStore.getDefaultType();
        //KeyStore keyStore = KeyStore.getInstance("PKCS12");
        //keyStore.load(in, "musicstore".toCharArray());

        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);

        Certificate ca = null;
        Certificate cert = null;
        try {

            //ca = keyStore.getCertificate("mykey");
            cert = cf.generateCertificate(in);
            //System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            System.out.println("cert=" + ((X509Certificate) cert).getSubjectDN());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            in.close();
        }

        //keyStore.setCertificateEntry("mykey", ca);
        keyStore.setCertificateEntry("cert", cert);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        GlobalDefinitions.g_sslContext = SSLContext.getInstance("SSL");
        GlobalDefinitions.g_sslContext.init(null, tmf.getTrustManagers(), null);


        //shantanu test
        System.out.println("@@@@@@@@@shantanu before asynctask");
        new test().execute((String) null);

    }
    private void getAllPermissionsFromUser() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Shantanu requesting for write permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_WRITE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Shantanu requesting for read permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE_READ);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Shantanu requesting for internet permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PERMISSION_INTERNET);
        }
    }

    private boolean hasAllRequiredPermissions() {
        boolean status = false;
        boolean status1 = false, status2 = false, status3 = false;

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            System.out.println("Shantanu has write permissions");
            status1 |= true;
        } else {
            status1 |= false;
            System.out.println("Shantanu has no write permissions");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("Shantanu has read permissions");
            status2 |= true;
        } else {
            status2 |= false;
            System.out.println("Shantanu has no read permissions");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            status3 |= true;
            System.out.println("Shantanu has internet permissions");
        } else {
            status3 |= false;
            System.out.println("Shantanu has no internet permissions");
        }

        status = status1 & status2 & status3;

        Toast.makeText(getApplicationContext(), "hasWritePermissions : "+status1, Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "hasReadPermissions : "+status2, Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "hasInternetPermissions : "+status3, Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), " hasWritePermissions : "+status1 +
                " hasReadPermissions : "+status2 +
                        " hasInternetPermissions : "+status3 +
                " hasAllPermissions : "+status, Toast.LENGTH_SHORT).show();

        return status;
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_EXTERNAL_STORAGE_WRITE : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getApplicationContext(), "Write Permission granted", Toast.LENGTH_SHORT).show();
                    downloadMohammadRafiSongList();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Write Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case PERMISSION_EXTERNAL_STORAGE_READ:
            {

            }
            break;
            case PERMISSION_INTERNET:
            {

            }
            break;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

//==========================================================================
// New class
class test extends AsyncTask<String, Void, String> {

    HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            HostnameVerifier hv =
                    HttpsURLConnection.getDefaultHostnameVerifier();
            System.out.println("!!!!!!!!!!!!!!!!!Shantanu hostname verified");
            return hv.verify("ec2-13-234-37-59.ap-south-1.compute.amazonaws.com", session);

        }
    };

    protected String doInBackground(String... urls) {
        try {

            // Tell the URLConnection to use a SocketFactory from our SSLContext
            URL url = new URL(g_mohammadRafiDownloadPath + g_mohammadRafiSongListName);
            HttpsURLConnection urlConnection =
                    (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(GlobalDefinitions.g_sslContext.getSocketFactory());
            urlConnection.setHostnameVerifier(hostnameVerifier);
            InputStream inputStream = urlConnection.getInputStream();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.copy(inputStream, Paths.get(g_mohammadRafiSongListName));
                Files.copy(inputStream, Paths.get(Environment.DIRECTORY_DOWNLOADS + g_mohammadRafiSongListName));
                System.out.println("*******************shantanu here");
            }
            System.out.println("*******************shantanu here did not enter build version if");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }

}