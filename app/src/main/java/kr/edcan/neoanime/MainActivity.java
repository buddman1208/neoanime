package kr.edcan.neoanime;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    String url, s;
    Elements music;
    ArrayList<CData> arrayList;
    ListView main_ListView;
    int count, link_count, file_count;
    String[] music_file, music_link;
    Runnable asdf, task;
    private static Thread thread = null;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadMusicList();
    }

    void setData() {
        main_ListView = (ListView) findViewById(R.id.main_listview);
        arrayList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            arrayList.add(new CData(getApplicationContext(), music_link[i]));
        }
        DataAdapter adapter = new DataAdapter(getApplicationContext(), arrayList);
        main_ListView.setAdapter(adapter);
        progressDialog.dismiss();
    }

    public void loadMusicList() {
        progressDialog = ProgressDialog.show(MainActivity.this, "음악 목록 로딩중", "잠시만 기다려주세요", true);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                url = "http://www.neoanime.co.kr/index.php?mid=ost&page=1";
                music_file = new String[20];
                music_link = new String[20];
                arrayList = new ArrayList<CData>();
                try {
                    Document doc = Jsoup.connect(url).get();
                    music = doc.select("li div a");
                    for (Element link : music) {
                        if(count%2==0) {
                            music_file[file_count] = link.absUrl("href");
                            file_count+=1;
                        } else {
                            music_link[link_count] = link.absUrl("href");
                            link_count+=1;
                        }
                        count += 1;
                        runOnUiThread(asdf);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread = new Thread(task);
        thread.start();
        asdf = new Runnable() {
            @Override
            public void run() {
                setData();
            }
        };
    }

    public void ShortToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }


//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
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
