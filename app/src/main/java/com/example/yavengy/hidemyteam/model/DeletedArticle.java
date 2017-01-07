package com.example.yavengy.hidemyteam.model;

/**
 * Created by yavengy on 1/7/17.
 */

public class DeletedArticle {
    private String title;
    private String permalink;

    public DeletedArticle(){
    }

    public DeletedArticle(String title, String permalink) {
        this.title = title;
        this.permalink = permalink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }
}
