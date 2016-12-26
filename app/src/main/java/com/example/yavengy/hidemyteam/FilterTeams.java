package com.example.yavengy.hidemyteam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class FilterTeams extends AppCompatActivity {

    private List<Filter> filterList = new ArrayList<>();
    private RecyclerView recyclerView;
    private FilterAdaptor mAdapter;
    private Toolbar mToolbar;
    Intent filterIntent;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        filterIntent = new Intent(getApplicationContext(), MainActivity.class);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            startActivity(filterIntent);

            //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_teams);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new FilterAdaptor(filterList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareFilterData();
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
