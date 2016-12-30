package com.example.yavengy.hidemyteam.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yavengy.hidemyteam.model.Filter;
import com.example.yavengy.hidemyteam.adapter.FilterAdaptor;
import com.example.yavengy.hidemyteam.R;

import java.util.ArrayList;
import java.util.List;

public class FilterFragment extends Fragment {

    private List<Filter> filterList = new ArrayList<>();
    private RecyclerView recyclerView;
    private FilterAdaptor mAdapter;

    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filter, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        mAdapter = new FilterAdaptor(filterList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareFilterData();

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

    private void prepareFilterData(){


        filterList.add(new Filter(null, "Atlantic"));
        filterList.add(new Filter("Boston Celtics", null));
        filterList.add(new Filter("Brooklyn Nets", null));
        filterList.add(new Filter("New York Knicks", null));
        filterList.add(new Filter("Philadelphia 76ers", null));
        filterList.add(new Filter("Toronto Raptors", null));

        filterList.add(new Filter(null, "Pacific"));
        filterList.add(new Filter("Golden State Warriors", null));
        filterList.add(new Filter("La Clippers", null));
        filterList.add(new Filter("La Lakers", null));
        filterList.add(new Filter("Phoenix Suns", null));
        filterList.add(new Filter("Sacramento Kings", null));

        filterList.add(new Filter(null, "Central"));
        filterList.add(new Filter("Chicago Bulls", null));
        filterList.add(new Filter("Cleveland Cavaliers", null));
        filterList.add(new Filter("Detroit Pistons", null));
        filterList.add(new Filter("Indiana Pacers", null));
        filterList.add(new Filter("Milwaukee Bucks", null));

        filterList.add(new Filter(null, "Southeast"));
        filterList.add(new Filter("Atlanta Hawks", null));
        filterList.add(new Filter("Charlotte Hornets", null));
        filterList.add(new Filter("Miami Heat", null));
        filterList.add(new Filter("Orlando Magic", null));
        filterList.add(new Filter("Washington Wizards", null));

        filterList.add(new Filter(null, "Northwest"));
        filterList.add(new Filter("Denver Nuggets", null));
        filterList.add(new Filter("Minnesota Timberwolves", null));
        filterList.add(new Filter("Oklahoma City Thunders", null));
        filterList.add(new Filter("Portland Trailblazers", null));
        filterList.add(new Filter("Utah Jazz", null));

        filterList.add(new Filter(null, "Southwest"));
        filterList.add(new Filter("Dallas Mavericks", null));
        filterList.add(new Filter("Houston Rockets", null));
        filterList.add(new Filter("Memphis Grizzlies", null));
        filterList.add(new Filter("New Orleans Pelicans", null));
        filterList.add(new Filter("San Antonio Spurs", null));

        mAdapter.notifyDataSetChanged();

    }

}