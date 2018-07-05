package com.edu.uni.augsburg.uniatron.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public interface CardView {
    void update(CardView cardView);

    void onBindView(Context context, RecyclerView.ViewHolder viewHolder);

    int getType();

    RecyclerView.ViewHolder onCreateViewHolder(Context context, ViewGroup viewGroup);
}
