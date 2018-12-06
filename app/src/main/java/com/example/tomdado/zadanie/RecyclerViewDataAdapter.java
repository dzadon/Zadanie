package com.example.tomdado.zadanie;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class RecyclerViewDataAdapter extends RecyclerView.Adapter<RecyclerViewDataAdapter.ItemRowHolder> {

    private ArrayList<SectionDataModel> dataList;
    private Context mContext;
    private RecyclerView.RecycledViewPool recycledViewPool;

    public RecyclerViewDataAdapter(ArrayList<SectionDataModel> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder holder, int position) {
        ArrayList singleSectionItems = dataList.get(position).getAllItemInSection();
        SectionListDataAdapter adapter = new SectionListDataAdapter(singleSectionItems, mContext);
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        holder.recyclerView.setAdapter(adapter);
        holder.recyclerView.setRecycledViewPool(recycledViewPool);
        holder.recyclerView.scrollToPosition(1);
        SnapHelper snapHelper = new PagerSnapHelper();
        holder.recyclerView.setOnFlingListener(null);
        snapHelper.attachToRecyclerView(holder.recyclerView);
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        protected RecyclerView recyclerView;

        public ItemRowHolder(View itemView) {
            super(itemView);
            this.recyclerView = itemView.findViewById(R.id.recycler_view_list);
        }
    }
}