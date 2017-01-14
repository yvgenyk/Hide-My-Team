package com.example.yavengy.hidemyteam.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yavengy.hidemyteam.R;
import com.example.yavengy.hidemyteam.Util.DataBase;
import com.example.yavengy.hidemyteam.Util.SimpleDividerItemDecorator;
import com.example.yavengy.hidemyteam.adapter.DeletedAdapter;
import com.example.yavengy.hidemyteam.adapter.FilterAdaptor;
import com.example.yavengy.hidemyteam.model.DeletedArticle;
import com.example.yavengy.hidemyteam.model.Filter;

import java.util.ArrayList;
import java.util.List;

public class DeletedArticlesFragment extends android.support.v4.app.Fragment {

    private List<DeletedArticle> deletedList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DeletedAdapter mAdapter;

    public DeletedArticlesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_deleted_articles, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SimpleDividerItemDecorator(getActivity()));

        getArticles();

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

    public void getArticles(){

        DataBase myDB = new DataBase();

        deletedList = myDB.getDeletedArticles();
        mAdapter = new DeletedAdapter(deletedList);
        recyclerView.setAdapter(mAdapter);
    }

}
