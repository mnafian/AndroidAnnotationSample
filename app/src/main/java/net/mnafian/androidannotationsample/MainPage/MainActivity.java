package net.mnafian.androidannotationsample.MainPage;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import net.mnafian.androidannotationsample.Adapter.NewsFeedAdapter;
import net.mnafian.androidannotationsample.Item.NewsFeedItem;
import net.mnafian.androidannotationsample.R;
import net.mnafian.androidannotationsample.Service.RestDataSample;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewById(R.id.countResponse)
    TextView countResponse;
    @ViewById(R.id.news_rcview)
    RecyclerView rcView;
    @SystemService
    WindowManager windowManager;
    @RestService
    RestDataSample newsFeedService;

    private List<NewsFeedItem> newsList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private long startTime;


    @AfterViews
    void bindAdapter() {
        rcView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rcView.setLayoutManager(llm);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        windowManager.getDefaultDisplay();
    }

    @Click
    void klikSaya() {
        startTime = System.currentTimeMillis();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data. Please wait...");
        progressDialog.show();
        if (newsList.size()>0){
            newsList.clear();
        }
        getUserInBackground();
    }

    @UiThread
    void updateUi(String response, long timeReponse) {
        countResponse.setText("Response Time: " + String.valueOf(timeReponse) +"ms");
        parseJSON(response);
        progressDialog.dismiss();
    }

    @Background
    void getUserInBackground() {
        String string = newsFeedService.getDataSample();
        long elapsedTime = System.currentTimeMillis() - startTime;
        Log.d("response", string);
        updateUi(string, elapsedTime);
    }

    public void parseJSON(String response) {
        try {
            JSONObject rootObj = new JSONObject(response);
            JSONArray postData = rootObj.getJSONArray(Constan.GET_POST);
            if (postData != null) {
                for (int i = 0; i < postData.length(); i++) {
                    JSONObject postTotal = postData.getJSONObject(i);
                    String url = postTotal.getString(Constan.GET_URL_FEED);
                    String title = postTotal.getString(Constan.GET_TITLE);
                    String content = postTotal.getString(Constan.GET_CONTENT);
                    String date = postTotal.getString(Constan.GET_DATE);
                    JSONObject thumbnail = postTotal.getJSONObject("thumbnail_images");
                    JSONObject fullTumb = thumbnail.getJSONObject("full");
                    String linkTumb = fullTumb.getString("url");

                    NewsFeedItem nfi = new NewsFeedItem();
                    nfi.setNewsTittle(title);
                    nfi.setNewsContent(stripHtml(content.substring(0, 150)) + "[Baca Selengkapnya]");
                    nfi.setNewsDate(date);
                    nfi.setNewsUrl(url);
                    nfi.setNewsImageUrl(linkTumb);
                    newsList.add(nfi);
                }
            }
            NewsFeedAdapter nfa = new NewsFeedAdapter(this, newsList);
            rcView.setAdapter(nfa);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }
}
