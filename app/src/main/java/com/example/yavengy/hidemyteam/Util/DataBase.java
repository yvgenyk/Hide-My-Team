package com.example.yavengy.hidemyteam.Util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.yavengy.hidemyteam.model.Article;
import com.example.yavengy.hidemyteam.model.DeletedArticle;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

import static com.example.yavengy.hidemyteam.activity.MainActivity.mainContext;

/**
 * Created by yavengy on 1/3/17.
 */

public class DataBase{
    private SQLiteDatabase myDatabase;
    private SQLiteStatement insertStmt;

    public DataBase(){
        try {
            myDatabase = mainContext.openOrCreateDatabase("Aticles", MODE_PRIVATE, null);
            //myDatabase.execSQL("DROP TABLE IF EXISTS articlesDb");
            myDatabase.execSQL("CREATE TABLE IF NOT EXISTS articlesDb (id INTEGER PRIMARY KEY, title VARCHAR, image VARCHAR, permalink VARCHAR, codedImage BLOB, deleted VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Article> getArticles() {

        List<Article> articleList = new ArrayList<>();;

        try {

            Cursor c = myDatabase.rawQuery("SELECT * FROM articlesDb", null);


            int titleIndex = c.getColumnIndex("title");
            int cImageIndex = c.getColumnIndex("codedImage");
            int deletedIndex = c.getColumnIndex("deleted");

            c.moveToLast();
            Article newArticle = new Article();
            if(c.getString(deletedIndex).equals("0")) {
                newArticle.setTitle(c.getString(titleIndex));

                newArticle.setByteArray(c.getBlob(cImageIndex));
                articleList.add(newArticle);
            }
            while (c.moveToPrevious()) {
                newArticle = new Article();
                if(c.getString(deletedIndex).equals("0")) {
                    newArticle.setTitle(c.getString(titleIndex));
                    newArticle.setByteArray(c.getBlob(cImageIndex));
                    articleList.add(newArticle);
                }
            }

            c.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return articleList;
    }

    public List<DeletedArticle> getDeletedArticles(){

        List<DeletedArticle> articleList = new ArrayList<>();;

        try {
            Cursor c = myDatabase.rawQuery("SELECT * FROM articlesDb", null);

            int titleIndex = c.getColumnIndex("title");
            int permalinkIndex = c.getColumnIndex("permalink");
            int deletedIndex = c.getColumnIndex("deleted");

            c.moveToLast();
            DeletedArticle delArticle = new DeletedArticle();
            if(c.getString(deletedIndex).equals("1")) {
                delArticle.setTitle(c.getString(titleIndex));
                delArticle.setPermalink(c.getString(permalinkIndex));

                articleList.add(delArticle);
            }
            while (c.moveToPrevious()) {
                delArticle = new DeletedArticle();
                if(c.getString(deletedIndex).equals("1")) {
                    delArticle.setTitle(c.getString(titleIndex));
                    delArticle.setPermalink(c.getString(permalinkIndex));
                    articleList.add(delArticle);
                }
            }

            c.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return articleList;

    }

    public String getArticleLink(String title){

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

    public String getAllPermalinks(){

        String allArticles = "";

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

        return allArticles;
    }

    public void saveArticle(String title, byte[] image, String permalink){

        try {

            String sql = "INSERT INTO articlesDb (title, codedImage, permalink, deleted) VALUES (?, ?, ?, ?)";

            insertStmt = myDatabase.compileStatement(sql);

            insertStmt.clearBindings();

            insertStmt.bindString(1, title);
            insertStmt.bindBlob(2, image);
            insertStmt.bindString(3, permalink);
            insertStmt.bindString(4, "0");

            insertStmt.executeInsert();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void markDeleted(String title){
        byte[] empty = null;
        ContentValues cv = new ContentValues();
        cv.put("deleted", "1");
        cv.put("codedImage", empty);

        int id = getId(title);

        myDatabase.update("articlesDb", cv, "id=" + id, null);
    }

    private int getId(String title){

        try {

            Cursor c = myDatabase.rawQuery("SELECT * FROM articlesDb", null);

            int titleIndex = c.getColumnIndex("title");
            int idIndex = c.getColumnIndex("id");

            c.moveToFirst();

            if (title.equals(c.getString(titleIndex))) {
                return c.getInt(idIndex);
            }

            while (c.moveToNext()) {

                if (title.equals(c.getString(titleIndex))) {
                    return c.getInt(idIndex);
                }
            }

            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
