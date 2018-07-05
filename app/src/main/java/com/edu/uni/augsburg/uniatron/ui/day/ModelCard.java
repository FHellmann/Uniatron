package com.edu.uni.augsburg.uniatron.ui.day;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public interface ModelCard {
    void update(ModelCard modelCard);

    void onBindView(Context context, RecyclerView.ViewHolder viewHolder);

    int getType();

    RecyclerView.ViewHolder onCreateViewHolder(Context context, ViewGroup viewGroup);
}
