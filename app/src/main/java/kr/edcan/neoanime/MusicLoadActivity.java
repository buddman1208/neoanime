package kr.edcan.neoanime;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;


public class MusicLoadActivity extends ActionBarActivity {

    ImageView imageView;
    Intent intent;
    Elements titles;
    MediaPlayer mediaPlayer;
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
    MaterialDialog down;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_load);
        setDefault();
        Parse();
        Stream();

    }

    void setDefault() {
        intent = getIntent();
        url = intent.getStringExtra("Link");
        filelink = intent.getStringExtra("FileLink");
        imglink = intent.getStringExtra("ImgLink");
        titleText = (TextView) findViewById(R.id.title);
        imageView = (ImageView) findViewById(R.id.image);
        new DownloadImageTask(imageView).execute(imglink);
        download = (Button) findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                down = new MaterialDialog.Builder(MusicLoadActivity.this)
                        .title("다운로드")
                        .content(titleText.getText().toString() + "를 다운로드합니다.")
                        .positiveText("확인")
                        .negativeText("취소")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                Download(filelink);
                            }
                        })
                        .show();
            }
        });
    }

    void Stream() {
        String url = filelink; // your URL here
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void Parse() {
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
                MainActivity.onClickLoad.dismiss();
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

    public void onPause() {
        super.onPause();
        mediaPlayer.release();
    }

    public void onDestroy() {
        super.onDestroy();
        mediaPlayer=null;
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
