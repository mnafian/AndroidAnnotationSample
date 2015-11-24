package net.mnafian.androidannotationsample.MainPage;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.OkHttpClient;

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

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewById(R.id.countResponse)
    TextView countResponse;
    @ViewById(R.id.news_rcview)
    RecyclerView rcView;
    @ViewById(R.id.pilih_method)
    Spinner spinnerView;
    @SystemService
    WindowManager windowManager;
    @RestService
    RestDataSample newsFeedService;

    private RequestQueue requestToServerVolley;
    private OkHttpClient requestRestOkHttp;

    private List<NewsFeedItem> newsList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private long startTime;


    @AfterViews
    void bindAdapter() {
        rcView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rcView.setLayoutManager(llm);

        String spinnerArray[] = {"Volley", "Spring-Android", "Ok-Http"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerView.setAdapter(spinnerArrayAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        windowManager.getDefaultDisplay();
        requestToServerVolley = Volley.newRequestQueue(this);
        requestRestOkHttp = new OkHttpClient();
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

        if (spinnerView.getSelectedItem().equals("Volley")){
            getDataVolley();
        } else if (spinnerView.getSelectedItem().equals("Spring-Android")){
            getDataSpringAndroid();
        } else {
            getDataOkHttp();
        }
    }

    @UiThread
    void updateUi(String response, long timeReponse) {
        countResponse.setText("Response Time: " + String.valueOf(timeReponse) + "ms");
        parseJSON(response);
        progressDialog.dismiss();
    }

    @Background
    void getDataSpringAndroid() {
        String response = newsFeedService.getDataSample();
        long elapsedTime = System.currentTimeMillis() - startTime;
        Log.d("response", response);
        updateUi(response, elapsedTime);
    }

    @Background
    void getDataVolley(){
        StringRequest myReq = new StringRequest(Request.Method.GET,
                Constan.URL_MAIN + "/?json=1",
                createMyReqSuccessListener(),
                createMyReqErrorListener()) {
        };
        requestToServerVolley.add(myReq);
    }

    @Background
    void getDataOkHttp() {
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder().url(Constan.URL_MAIN + "/?json=1")
                .build();
        com.squareup.okhttp.Response responseFromServer;
        try {
            responseFromServer = requestRestOkHttp.newCall(request).execute();
            String response = responseFromServer.body().string();
            long elapsedTime = System.currentTimeMillis() - startTime;
            Log.d("response", response);
            updateUi(response, elapsedTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                Log.d("response", response);
                updateUi(response, elapsedTime);
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error req to server", error.toString());
            }
        };
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
                    String linkTumb = "http://null";
                    if (postTotal.has("thumbnail_images")){
                        JSONObject thumbnail = postTotal.getJSONObject("thumbnail_images");
                        JSONObject fullTumb = thumbnail.getJSONObject("full");
                        linkTumb = fullTumb.getString("url");
                    }
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
