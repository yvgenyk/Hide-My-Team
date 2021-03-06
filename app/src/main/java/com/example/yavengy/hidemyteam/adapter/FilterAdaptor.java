package com.example.yavengy.hidemyteam.adapter;

/**
 * Created by yavengy on 12/17/16.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.yavengy.hidemyteam.R;
import com.example.yavengy.hidemyteam.model.Filter;

import java.util.List;

import static com.example.yavengy.hidemyteam.Util.TagNFilters.filterArray;
import static com.example.yavengy.hidemyteam.activity.MainActivity.getMainContext;
import static com.example.yavengy.hidemyteam.activity.MainActivity.saveFilters;

public class FilterAdaptor extends RecyclerView.Adapter<FilterAdaptor.MyViewHolder> {

    private List<Filter> filterList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox title;
        public TextView devision;

        public MyViewHolder(View view) {
            super(view);
            title = (CheckBox) view.findViewById(R.id.checkBox);
            devision = (TextView) view.findViewById(R.id.division);
        }
    }


    public FilterAdaptor(List<Filter> filterList) {
        this.filterList = filterList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_team_row, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Filter filter = filterList.get(position);
        final int currentPosition = position;
        final MyViewHolder newHolder = holder;

        if(filter.getDevision() == null){
            holder.title.setText(filter.getTitle());
            holder.devision.setVisibility(View.INVISIBLE);
        } else {
            holder.devision.setText(filter.getDevision());
            holder.devision.setVisibility(View.VISIBLE);
            holder.title.setBackgroundColor(Integer.parseInt(String.valueOf(R.color.colorSecondary)));
            holder.title.setEnabled(false);
        }

        if (filterArray[position] == 0) {
            holder.title.setChecked(false);
        } else {
            holder.title.setChecked(true);
        }

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean isChecked = newHolder.title.isChecked();

                if(isChecked){
                    filterArray[currentPosition] = 1;
                    saveFilters(Integer.toString(currentPosition), 1, getMainContext());
                } else {
                    filterArray[currentPosition] = 0;
                    saveFilters(Integer.toString(currentPosition), 0, getMainContext());
                }
            }
        });

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }
}