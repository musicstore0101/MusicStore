package app.music.musicstore;

import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;

public class GlobalDefinitions {
    public final String g_staticPath = "http://ec2-13-234-37-59.ap-south-1.compute.amazonaws.com:8080/static/";
    public final static String g_mohammadRafiDownloadPath = "http://ec2-13-234-37-59.ap-south-1.compute.amazonaws.com:8080/static/mohammad_rafi/";
    public final static String g_mohammadRafiSongListName = "mohammad_rafi_song_list.txt";
    public static final String BASE_URL = "http://ec2-13-234-37-59.ap-south-1.compute.amazonaws.com:8085/greeting";

    //shantanu change the directory to app specific later
    public final static String g_externalStorageDownloadPath = "/storage/emulated/0/" + Environment.DIRECTORY_DOWNLOADS + File.separator;
    public static ArrayList<String> g_mohammadRafiSongList = new ArrayList<String>();

    public static SSLContext g_sslContext;
    public static boolean rafiDownloadCompleted = false;

    public static boolean authenticationDone = false;

    public static void g_parseMohammadRafiSongList() throws FileNotFoundException, IOException {
        BufferedReader reader;

        String rafiSongListFile = "";

        if (rafiDownloadCompleted)
            return;
        else {

            rafiSongListFile = g_externalStorageDownloadPath + g_mohammadRafiSongListName;

            //shantanu to delete later
            System.out.println("shantanu read path " + rafiSongListFile);
            reader = new BufferedReader(new FileReader(rafiSongListFile));
            String line = reader.readLine();
            while (line != null) {
                //shantanu to remove this later
                System.out.println("shantanu song name : " + line);
                g_mohammadRafiSongList.add(line);
                line = reader.readLine();
            }
            rafiDownloadCompleted = true;

            //shantanu to delete later
            System.out.println("shantanu list size " + g_mohammadRafiSongList.size());
            for (int i = 0; i < g_mohammadRafiSongList.size(); i++) {
                System.out.println(g_mohammadRafiSongList.get(i));
            }
        }
    }
}
