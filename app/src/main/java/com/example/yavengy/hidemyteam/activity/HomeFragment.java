package com.example.yavengy.hidemyteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import static android.content.Context.MODE_PRIVATE;
import static com.example.yavengy.hidemyteam.Util.DbBitmapUtility.getBytes;
import static com.example.yavengy.hidemyteam.Util.DbBitmapUtility.getImage;
import static com.example.yavengy.hidemyteam.activity.MainActivity.filterArray;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private List<Article> articleList;
    boolean emptyArray;

    Intent intent;

    SQLiteDatabase myDatabase;

    SQLiteStatement insertStmt;

    final String tags[] = {"boston-celtics", "brooklyn-nets", "new-york-knicks", "philadelphia-76ers",
            "toronto-raptors", "golden-state-warriors", "los-angeles-clippers",
            "los-angeles-lakers", "phoenix-suns", "sacramento-kings", "chicago-bulls",
            "cleveland-cavaliers", "detroit-pistons", "indiana-pacers", "milwaukee-bucks",
            "atlanta-hawks", "charlotte-hornets", "miami-heat", "orlando-magic",
            "washington-wizards", "denver-nuggets", "minnesota-timberwolves",
            "oklahoma-city-thunder", "portland-trail-blazers", "utah-jazz",
            "dallas-mavericks", "houston-rockets", "memphis-grizzlies",
            "new-orleans-pelicans", "san-antonio-spurs"};

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        emptyArray = true;

        for(int i = 0; i < filterArray.length; i++){
            if(filterArray[i] == 1){
                emptyArray = false;
                break;
            }
        }

        try {
            myDatabase = getActivity().openOrCreateDatabase("Aticles", MODE_PRIVATE, null);
            //myDatabase.execSQL("DROP TABLE IF EXISTS articlesDb");
            myDatabase.execSQL("CREATE TABLE IF NOT EXISTS articlesDb (id INTEGER PRIMARY KEY, title VARCHAR, image VARCHAR, permalink VARCHAR, codedImage BLOB)");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //prepareArticles();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        intent = new Intent(getActivity().getApplicationContext(), ArticleView.class);

        getArticles();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new HomeFragment.GridSpacingItemDecoration(2, dpToPx(0), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        updateList();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new MainActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String permalink = findPermalink(articleList.get(position).getTitle());
                intent.putExtra("permalink", permalink);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
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

        //prepareArticles();

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

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
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

        articleList = new ArrayList<>();
        adapter = new ArticleAdapter(getActivity(), articleList);

        recyclerView.setAdapter(adapter);

    }

    public void getArticles() {

        int correctIndex = 0;

        String filteredTags = "";

        for(int i = 0; i < filterArray.length; i++){

            if(i != 0 && i != 6 && i != 12 && i != 18 && i != 24 && i != 30){

                if (filterArray[i] == 1) {
                    filteredTags += tags[correctIndex] + ",";
                }
                correctIndex++;
            }
        }

        if (filteredTags.length() > 1) {
            filteredTags = filteredTags.substring(0, filteredTags.length() - 1);
        }

        String url = "http://bleacherreport.com/api/front/lead_articles.json?tags=" +
                filteredTags + "&appversion=1.4&perpage=40";

        HomeFragment.DownloadContext task = new HomeFragment.DownloadContext();

        task.execute(url);

    }

    private void prepareArticles() {

        updateList();

        try {

            Cursor c = myDatabase.rawQuery("SELECT * FROM articlesDb", null);


            int titleIndex = c.getColumnIndex("title");
            int titleImage = c.getColumnIndex("image");
            int cImageIndex = c.getColumnIndex("codedImage");

            c.moveToLast();
            Article newArticle = new Article();
            newArticle.setTitle(c.getString(titleIndex));
            newArticle.setImage(getImage(c.getBlob(cImageIndex)));
            articleList.add(newArticle);

            while (c.moveToPrevious()) {
                newArticle = new Article();
                newArticle.setTitle(c.getString(titleIndex));
                newArticle.setImage(getImage(c.getBlob(cImageIndex)));
                articleList.add(newArticle);
            }

            c.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }

    public String findPermalink(String title) {

        try {

            Cursor c = myDatabase.rawQuery("SELECT * FROM articlesDb", null);

            int titleIndex = c.getColumnIndex("title");
            int permalinkIndex = c.getColumnIndex("permalink");

            c.moveToFirst();

            if (title.equals(c.getString(titleIndex))) {
                return c.getString(permalinkIndex);
            }

            while (c.moveToNext()) {

                if (title.equals(c.getString(titleIndex))) {
                    return c.getString(permalinkIndex);
                }
            }

            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error";
    }

    public void saveToDb (String result){

        String title;

        if (result != null) {

            String allArticles = "";

            try {

                try {

                    Cursor c = myDatabase.rawQuery("SELECT * FROM articlesDb", null);

                    int permalinkIndex = c.getColumnIndex("permalink");

                    c.moveToFirst();

                    allArticles += c.getString((permalinkIndex)) + " ";

                    while (c.moveToNext()) {

                        allArticles += c.getString((permalinkIndex)) + " ";

                    }

                    c.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                JSONArray arr = new JSONArray(result);

                for (int i = 0; i < arr.length(); i++) {

                    //Log.i("runs", Integer.toString(i));

                    if (!arr.get(i).toString().equals("null")) {
                        if (arr.getJSONObject(i).has("tag")) {

                            title = arr.getJSONObject(i).getString("title");

                            title = title.replaceAll(":", " -");
                            title = title.replaceAll("'", "");

                            if (!allArticles.contains(arr.getJSONObject(i).getString("permalink"))) {

                                try {

                                    String sql = "INSERT INTO articlesDb (title, codedImage, permalink) VALUES (?, ?, ?)";

                                    insertStmt = myDatabase.compileStatement(sql);

                                    insertStmt.clearBindings();

                                    insertStmt.bindString(1, title);
                                    insertStmt.bindBlob(2, getBytes(new HomeFragment.DownloadImage().execute(arr.getJSONObject(i).getString("primary_image_650x440")).get()));
                                    insertStmt.bindString(3, arr.getJSONObject(i).getString("permalink"));

                                    insertStmt.executeInsert();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                }

            } catch (JSONException e) {
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