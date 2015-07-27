package kr.edcan.neoanime;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;


public class MusicLoadActivity extends ActionBarActivity {

    ImageView imageView;
    Intent intent;
    Elements titles;
    String url, filelink, imglink;
    String songTitle;
    Button download;
    Runnable setLayout, task;
    TextView titleText;
    private long latestId = -1;
    private DownloadManager downloadManager;
    private DownloadManager.Request request;
    private Uri urlToDownload;
    private static Thread thread = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_load);
        setDefault();
        Parse();
    }

    void setDefault(){
        intent = getIntent();
        url = intent.getStringExtra("Link");
        filelink = intent.getStringExtra("FileLink");
        imglink = intent.getStringExtra("ImgLink");
        titleText = (TextView)findViewById(R.id.title);
        imageView = (ImageView)findViewById(R.id.image);
        new DownloadImageTask(imageView).execute(imglink);
        download = (Button)findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Download(filelink);
            }
        });
    }
    void Parse(){
        task = new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(url).get();
                    titles = doc.select("div>h1");
                    songTitle = titles.get(2).text().toString();
                    runOnUiThread(setLayout);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread = new Thread(task);
        thread.start();
        setLayout = new Runnable() {
            @Override
            public void run() {
                titleText.setText(songTitle);
            }
        };
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
