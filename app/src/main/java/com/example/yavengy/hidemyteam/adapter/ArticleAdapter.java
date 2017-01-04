package com.example.yavengy.hidemyteam.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yavengy.hidemyteam.R;
import com.example.yavengy.hidemyteam.Util.DataBase;
import com.example.yavengy.hidemyteam.Util.TopCropImageView;
import com.example.yavengy.hidemyteam.activity.MainActivity;
import com.example.yavengy.hidemyteam.model.Article;

import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.yavengy.hidemyteam.activity.MainActivity.getMainContext;
import static com.example.yavengy.hidemyteam.activity.MainActivity.mainContext;
import static com.example.yavengy.hidemyteam.activity.MainActivity.screenWidth;
import com.example.yavengy.hidemyteam.helper.ItemTouchHelperAdapter;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter {

    private Context mContext;
    private List<Article> articleList;
    private DataBase myDataBase;

    public void updateList(List<Article> newList){
        articleList = newList;
        myDataBase = new DataBase();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TopCropImageView image;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            image = (TopCropImageView) view.findViewById(R.id.image);
        }
    }

    public ArticleAdapter(Context mContext, List<Article> articleList) {
        this.mContext = mContext;
        this.articleList = articleList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.title.setText(article.getTitle());
        holder.image.setImageBitmap(article.getImage());

        int width = screenWidth - (int) mainContext.getResources().getDimension(R.dimen.article_margin)*2;

        int totalHeight = (int) (width/1.618);
        int imageHeight = (int) (totalHeight*0.8);
        int textHeight = totalHeight - imageHeight;

        holder.title.getLayoutParams().height = textHeight;
        holder.image.getLayoutParams().height = imageHeight;

    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    //@Override
    public void onItemDismiss(int position) {
        myDataBase.markDeleted(articleList.get(position).getTitle());

        articleList.remove(position);
        notifyItemRemoved(position);

    }

}