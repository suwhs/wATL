package su.whs.watl.samples;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements wATLApp.StateListener {
    private static boolean hyphenatorReady = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView lv = (ListView) findViewById(R.id.listView);
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        data.add(getRowData(0));
        data.add(getRowData(1));
        data.add(getRowData(2));



        SimpleAdapter adapter = new SimpleAdapter(this,data,R.layout.samples_list_item, new String[]  {
            "img", "title", "description"
        }, new int[] { R.id.imageView, R.id.itemTitle, R.id.itemSnippet }) {

        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
                switch(position) {
                    case 0:
                        i.setClass(getBaseContext(), TextViewWSActivity.class);
                        break;
                    case 1:
                        i.setClass(getBaseContext(), TextViewExScrollActivity.class);
                        break;
                    case 2:
                        if (hyphenatorReady)
                            i.setClass(getBaseContext(), HyphenTextViewExActivity.class);
                        else {
                            Toast.makeText(getBaseContext(), "please wait - hyphenator loading...", Toast.LENGTH_LONG).show();
                            return;
                        }
                        break;
                    default:
                        return;
                }
                startActivity(i);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        hyphenatorReady = false;
        ((wATLApp)getApplication()).addStateListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((wATLApp)getApplication()).removeStateListener(this);
    }

    private Map<String,Object> getRowData(int index) {
        Map<String,Object> result = new HashMap<String,Object>();
        int img = 0;
        String title = "Untitled";
        String description = "no description";
        switch(index) {
            case 0:
                title = "Selection TextView Sample";
                description = "su.whs.watl.ui.TextViewWS";
                break;
            case 1:
                title = "TextViewEx in ScrollView";
                description = "justification enabled";
                break;
            case 2:
                title = "Hyphenation Test";
                description = "custom LineBreaker example";
                break;

        }
        result.put("img",img);
        result.put("title",title);
        result.put("description",description);
        return result;
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

    @Override
    public void onHyphenatorLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
                if (pb!=null) {
                    pb.setIndeterminate(false);
                    pb.setProgress(100);
                    hyphenatorReady = true;
                }
            }
        });
    }
}
