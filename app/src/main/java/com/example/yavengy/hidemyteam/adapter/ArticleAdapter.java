package com.example.yavengy.hidemyteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yavengy.hidemyteam.R;
import com.example.yavengy.hidemyteam.Util.DataBase;
import com.example.yavengy.hidemyteam.Parallax.InterpolatorSelector;
import com.example.yavengy.hidemyteam.Parallax.PEWImageView;
import com.example.yavengy.hidemyteam.helper.OnLoadMoreListener;
import com.example.yavengy.hidemyteam.model.Article;

import java.util.List;

import static com.example.yavengy.hidemyteam.activity.MainActivity.mainContext;
import static com.example.yavengy.hidemyteam.activity.MainActivity.screenWidth;
import com.example.yavengy.hidemyteam.helper.ItemTouchHelperAdapter;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter {

    private Context mContext;
    private List<Article> articleList;
    private DataBase myDataBase;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading = false, isMoreDataAvailable = true;
    private int visibleThreshold = 10;


    public void updateList(List<Article> newList){
        articleList = newList;
        myDataBase = new DataBase();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public PEWImageView image;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            image = (PEWImageView) view.findViewById(R.id.image);
        }

        void bindData(Article article){

            title.setText(article.getTitle());
            image.setImageBitmap(article.getImage());

            int width = screenWidth - (int) mainContext.getResources().getDimension(R.dimen.article_margin) * 2;

            int totalHeight = (int) (width / 1.618);
            int imageHeight = (int) (totalHeight * 0.8);
            int textHeight = totalHeight - imageHeight;

            title.getLayoutParams().height = textHeight;
            image.getLayoutParams().height = imageHeight;

            image.setInterpolator(InterpolatorSelector.interpolatorId(6));

        }
    }

    public class LoadHolder extends RecyclerView.ViewHolder{
        public LoadHolder(View itemView){
            super(itemView);
        }
    }

    public ArticleAdapter(Context mContext, List<Article> articleList) {
        this.mContext = mContext;
        this.articleList = articleList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if(viewType == VIEW_TYPE_ITEM){
            View itemView = inflater.inflate(R.layout.article_card, parent, false);
            return new MyViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.row_load,parent,false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Article article = articleList.get(position);

        if(position >= getItemCount() - 1 && isMoreDataAvailable && !isLoading && mOnLoadMoreListener != null){
            isLoading = true;
            mOnLoadMoreListener.onLoadMore();
        }

        if(getItemViewType(position) == VIEW_TYPE_ITEM){
            ((MyViewHolder)holder).bindData(article);
        }
    }

    @Override
    public int getItemViewType(int position){
        if(articleList.get(position).getTitle() != null){
            return VIEW_TYPE_ITEM;
        } else {
            return VIEW_TYPE_LOADING;
        }
    }

    public void setMoreDataAvailable(boolean moreDataAvailable){
        this.isMoreDataAvailable = moreDataAvailable;
    }

    public void notifyDataChanged(){
        notifyDataSetChanged();
        isLoading = false;
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public void onItemDismiss(int position) {
        myDataBase.markDeleted(articleList.get(position).getTitle());

        articleList.remove(position);
        notifyItemRemoved(position);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener){
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

}