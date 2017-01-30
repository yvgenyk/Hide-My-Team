package com.example.yavengy.hidemyteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yavengy.hidemyteam.Util.BrContentDownload;
import com.example.yavengy.hidemyteam.Util.DataBase;
import com.example.yavengy.hidemyteam.helper.OnLoadMoreListener;
import com.example.yavengy.hidemyteam.helper.SimpleItemTouchHelperCallback;
import com.example.yavengy.hidemyteam.model.Article;
import com.example.yavengy.hidemyteam.adapter.ArticleAdapter;
import com.example.yavengy.hidemyteam.R;
import com.example.yavengy.hidemyteam.Util.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.yavengy.hidemyteam.activity.MainActivity.fab;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private List<Article> articleList;
    private List<Article> displayedArticles;
    private boolean downloadingArticles = false;

    public ImageView loadingIcon = null;
    private AnimationDrawable loadingViewAnim = null;

    public static int currentMaxIndex = 0;

    DataBase myDataBaseClass;

    BrContentDownload brContentDownload;

    Intent intent;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        brContentDownload = new BrContentDownload();

        articleList = new ArrayList<>();
        displayedArticles = new ArrayList<>();

        adapter = new ArticleAdapter(getActivity(), displayedArticles);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        if(currentMaxIndex != articleList.size()) {
                            loadMoreArticles();
                        }
                    }
                });

            }
        });

        intent = new Intent(getActivity().getApplicationContext(), ArticleView.class);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new HomeFragment.GridSpacingItemDecoration(2, dpToPx(0), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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

                if(!downloadingArticles) {
                    downloadingArticles = true;
                    getArticlesFromBR();
                    Toast.makeText(getActivity(), "Updating", Toast.LENGTH_SHORT).show();
                }
            }
        });

        prepareArticles();
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

    public void updateList(){
        recyclerView.setAdapter(adapter);
    }

    public void getArticlesFromBR() {
        brContentDownload.getArticles();
        currentMaxIndex = 0;
        prepareArticles();
        downloadingArticles = false;
    }

    private void loadMoreArticles(){
        displayedArticles.add(new Article(null, null));
        adapter.notifyItemInserted(displayedArticles.size() - 1);
        displayedArticles.remove(displayedArticles.size() - 1);

        if(currentMaxIndex + 10 >= articleList.size()){
            for(int i = currentMaxIndex; i < articleList.size(); i++){
                displayedArticles.add(articleList.get(i));
            }
            currentMaxIndex = articleList.size();
        } else {
            for(int i = currentMaxIndex; i < currentMaxIndex + 10; i++){
                displayedArticles.add(articleList.get(i));
            }
            currentMaxIndex += 10;
        }

        adapter.notifyDataChanged();

    }

    private void prepareArticles() {

        updateList();

        articleList = myDataBaseClass.getArticles();
        if (currentMaxIndex + 10 > articleList.size()){
            displayedArticles = new ArrayList<>(articleList.subList(currentMaxIndex, articleList.size()));
            currentMaxIndex = articleList.size();
        } else {
            displayedArticles = new ArrayList<>(articleList.subList(currentMaxIndex, currentMaxIndex + 10));
            currentMaxIndex +=10;
        }

        adapter.updateList(displayedArticles);

        adapter.notifyDataSetChanged();

        loadingIcon.setVisibility(View.GONE);
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