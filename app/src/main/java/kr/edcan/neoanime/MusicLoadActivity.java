package kr.edcan.neoanime;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;


public class MusicLoadActivity extends ActionBarActivity {
    Elements titles;
    String url;
    String ss;
    String[] arr, arr2;
    private long latestId = -1;
    private DownloadManager downloadManager;
    private DownloadManager.Request request;
    private Uri urlToDownload;
    private static Thread thread = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_load);
        String s = "<a class=\"hx fixed\" href=\"http://www.neoanime.co.kr/index.php?mid=ost&amp;page=1&amp;document_srl=4475410\" data-viewer=\"http://www.neoanime.co.kr/index.php?mid=ost&amp;document_srl=4475410&amp;listStyle=viewer\">";
        arr = s.split("\"");
        arr2 = arr[3].split("=");
        Runnable task = new Runnable() {
            @Override
            public void run() {
                url = "http://www.neoanime.co.kr/" + arr2[3];
                try {
                    Document doc = Jsoup.connect(url).get();
                    titles = doc.select("source[src]");
                    for (Element link : titles) {
                        ss = link.attr("abs:src");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread = new Thread(task);
        thread.start();
    }

    public void Download(String DownloadUrl) {
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        urlToDownload = Uri.parse(DownloadUrl);
        List<String> pathSegments = urlToDownload.getPathSegments();
        request = new DownloadManager.Request(urlToDownload);
        request.setTitle("선택한 파일을 다운로드합니다");
        request.setDescription("기다려주세요");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, pathSegments.get(pathSegments.size() - 1));
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();
        latestId = downloadManager.enqueue(request);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_music_load, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
