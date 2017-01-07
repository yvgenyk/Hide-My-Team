package com.example.yavengy.hidemyteam.adapter;

/**
 * Created by yavengy on 12/17/16.
 */

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yavengy.hidemyteam.R;
import com.example.yavengy.hidemyteam.activity.ArticleView;
import com.example.yavengy.hidemyteam.model.DeletedArticle;

import java.util.List;

import static com.example.yavengy.hidemyteam.activity.MainActivity.mainContext;

public class DeletedAdapter extends RecyclerView.Adapter<DeletedAdapter.MyViewHolder> {

    private List<DeletedArticle> deletedArticlesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.deletedTitle);
        }
    }


    public DeletedAdapter(List<DeletedArticle> deletedArticlesList) {
        this.deletedArticlesList = deletedArticlesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deleted_article_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final DeletedArticle article = deletedArticlesList.get(position);

        holder.title.setText(article.getTitle());

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainContext, ArticleView.class);

                String permalink = article.getPermalink();
                intent.putExtra("permalink", permalink);
                mainContext.startActivity(intent);

            }
        });

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return deletedArticlesList.size();
    }
}
