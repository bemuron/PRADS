package bruca.prads.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bruca.prads.R;
import bruca.prads.adapters.GalleryAdapter;
import bruca.prads.app.AppController;
import bruca.prads.fragments.SlideshowDialogFragment;
import bruca.prads.models.Image;

public class PositiveConfessionsActivity extends AppCompatActivity {

    private String TAG = PositiveConfessionsActivity.class.getSimpleName();
    private static String endpoint;
    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positive_confessions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        //get the intent from which this activity is called
        Intent intent = getIntent();
        int category_id = intent.getIntExtra("item-ID",0);
        switch (category_id){
            case 1:
                endpoint = "http://www2.macs.hw.ac.uk/~bae1/focus_app/json/whendepressed.json";
                break;
            case 2:
                endpoint = "http://www2.macs.hw.ac.uk/~bae1/focus_app/json/whenanxious.json";
                break;
            case 3:
                endpoint = "http://www2.macs.hw.ac.uk/~bae1/focus_app/json/whenhappy.json";
                break;
            case 4:
                endpoint = "http://www2.macs.hw.ac.uk/~bae1/focus_app/json/onhealth.json";
                break;
            case 5:
                endpoint = "http://www2.macs.hw.ac.uk/~bae1/focus_app/json/onlove.json";
                break;
            case 6:
                endpoint = "http://www2.macs.hw.ac.uk/~bae1/focus_app/json/onfamily.json";
                break;
            default:
                endpoint = "http://www2.macs.hw.ac.uk/~bae1/focus_app/json/confessions.json";
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        fetchImages();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void fetchImages() {

        //progress dialog
        pDialog.setMessage("Downloading file...");
        pDialog.show();

        //download the json file by making volley request
        JsonArrayRequest req = new JsonArrayRequest(endpoint,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();

                        //parse json file and add the images to the array list
                        images.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Image image = new Image();
                                image.setName(object.getString("name"));

                                JSONObject url = object.getJSONObject("url");
                                image.setSmall(url.getString("small"));
                                image.setMedium(url.getString("medium"));
                                image.setLarge(url.getString("large"));
                                image.setVerse(object.getString("verse"));

                                images.add(image);

                            } catch (JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                pDialog.hide();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
}