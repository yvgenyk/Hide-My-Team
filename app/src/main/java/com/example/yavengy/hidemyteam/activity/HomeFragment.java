package com.example.yavengy.hidemyteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yavengy.hidemyteam.Util.DataBase;
import com.example.yavengy.hidemyteam.Util.TagNFilters;
import com.example.yavengy.hidemyteam.helper.SimpleItemTouchHelperCallback;
import com.example.yavengy.hidemyteam.model.Article;
import com.example.yavengy.hidemyteam.adapter.ArticleAdapter;
import com.example.yavengy.hidemyteam.R;
import com.example.yavengy.hidemyteam.Util.RecyclerTouchListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.yavengy.hidemyteam.Util.DbBitmapUtility.getBytes;
import static com.example.yavengy.hidemyteam.Util.TagNFilters.filterArray;
import static com.example.yavengy.hidemyteam.activity.MainActivity.fab;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private List<Article> articleList;
    boolean emptyArray;

    public ImageView loadingIcon = null;
    private AnimationDrawable loadingViewAnim = null;

    DataBase myDataBaseClass;

    TagNFilters tagsNFilters;

    Intent intent;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tagsNFilters = new TagNFilters(getActivity().getString(R.string.tags_api_call));

        emptyArray = true;

        for(int i = 0; i < filterArray.length; i++){
            if(filterArray[i] == 1){
                emptyArray = false;
                break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        loadingIcon = (ImageView) rootView.findViewById(R.id.loadingView);

        loadingIcon.setBackgroundResource(R.drawable.loading_animation);
        loadingViewAnim = (AnimationDrawable) loadingIcon.getBackground();
        loadingIcon.setVisibility(View.VISIBLE);

        loadingViewAnim.start();

        myDataBaseClass = new DataBase();

        articleList = new ArrayList<>();
        adapter = new ArticleAdapter(getActivity(), articleList);

        intent = new Intent(getActivity().getApplicationContext(), ArticleView.class);

        getArticles();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new HomeFragment.GridSpacingItemDecoration(2, dpToPx(0), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        updateList();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new MainActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String permalink = myDataBaseClass.getArticleLink(articleList.get(position).getTitle());
                intent.putExtra("permalink", permalink);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                for(int i = 0; i < filterArray.length; i++){
                    if(filterArray[i] == 1){
                        emptyArray = false;
                        break;
                    }
                }

                if(!emptyArray) {
                    getArticles();
                }

                Toast.makeText(getActivity(), "Updating", Toast.LENGTH_SHORT).show();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    public class DownloadContext extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            saveToDb(result);

        }

    }

    public void updateList(){
        recyclerView.setAdapter(adapter);
    }

    public void getArticles() {

        String filteredTags = tagsNFilters.getTags();;

        String url = "http://bleacherreport.com/api/front/lead_articles.json?tags=" +
                filteredTags + "&appversion=1.4&perpage=40";

        HomeFragment.DownloadContext task = new HomeFragment.DownloadContext();

        task.execute(url);

    }

    private void prepareArticles() {

        updateList();

        articleList = myDataBaseClass.getArticles();

        adapter.updateList(articleList);

        adapter.notifyDataSetChanged();

        loadingIcon.setVisibility(View.GONE);

        //loadingViewAnim.stop();

    }


    public void saveToDb (String result){

        String title;

        if (result != null) {

            String allArticles = myDataBaseClass.getAllPermalinks();

            try {

                JSONArray arr = new JSONArray(result);

                for (int i = 0; i < arr.length(); i++) {


                    if (!arr.get(i).toString().equals("null")) {
                        if (arr.getJSONObject(i).has("tag")) {

                            title = arr.getJSONObject(i).getString("title");

                            title = title.replaceAll(":", " -");
                            title = title.replaceAll("'", "");

                            if (!allArticles.contains(arr.getJSONObject(i).getString("permalink"))) {

                                myDataBaseClass.saveArticle(title,
                                        getBytes(new HomeFragment.DownloadImage().execute(arr.getJSONObject(i).getString("primary_image_650x440")).get()),
                                        arr.getJSONObject(i).getString("permalink"));
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        prepareArticles();

    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}