package app.music.musicstore;

import java.util.ArrayList;

import javax.net.ssl.SSLContext;

public class GlobalDefinitions {
    public final String staticPath = "http://ec2-13-234-37-59.ap-south-1.compute.amazonaws.com:8080/static/";
    public final static String mohammadRafiDownloadPath = "http://ec2-13-234-37-59.ap-south-1.compute.amazonaws.com:8080/static/mohammad_rafi/";
    public final static String mohammadRafiSongListName = "mohammad_rafi_song_list.txt";
    public static ArrayList<String> mohammadRafiSongList = new ArrayList<String>();


    public static SSLContext g_sslContext;
}
