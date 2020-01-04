import android.app.IntentService;
/*
public class DownloadSongService extends IntentService {
    private static final String DOWNLOAD_PATH = "https://drive.google.com/open?id=1iKjUCuEoW82Du85OreHas0fjelU9KHfc";
    private static final String DESTINATION_PATH = ""; //"com.spartons.androiddownloadmanager_DownloadSongService_Destination_path";
    public DownloadSongService() {
        super("DownloadSongService");
    }
    public static Intent getDownloadService(final Context callingClassContext, final String downloadPath, final String destinationPath) {
        return new Intent(callingClassContext, DownloadSongService.class)
                .putExtra(DOWNLOAD_PATH, downloadPath)
                .putExtra(DESTINATION_PATH, destinationPath);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        String downloadPath = intent.getStringExtra(DOWNLOAD_PATH);
        String destinationPath = intent.getStringExtra(DESTINATION_PATH);
        startDownload(downloadPath, destinationPath);
    }
    private void startDownload(String downloadPath, String destinationPath) {
        Uri uri = Uri.parse(downloadPath); // Path where you want to download file.
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
        request.setTitle("Downloading a file"); // Title for notification.
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(destinationPath, uri.getLastPathSegment());  // Storage directory path
        ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading
    }
}
*/