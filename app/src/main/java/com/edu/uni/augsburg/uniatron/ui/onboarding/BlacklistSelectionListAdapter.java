package com.edu.uni.augsburg.uniatron.ui.onboarding;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.edu.uni.augsburg.uniatron.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The onboarding selection adapter manages the apps to be displayed.
 *
 * @author Fabio Hellmann
 */
class BlacklistSelectionListAdapter extends RecyclerView.Adapter<BlacklistSelectionListAdapter.ViewHolder> {
    private final Context mContext;
    private final Consumer<String> mSelector;
    private final Function<String, Boolean> mSelectionCheck;
    private final List<LaunchableAppDetails> mList = new ArrayList<>();

    /**
     * Ctr.
     *
     * @param context        The context.
     * @param selector       The function which is called on selection.
     * @param selectionCheck The function which is called to check if
     *                       an item is already slected.
     */
    BlacklistSelectionListAdapter(@NonNull final Context context,
                                  @NonNull final Consumer<String> selector,
                                  @NonNull final Function<String, Boolean> selectionCheck) {
        super();
        mContext = context;
        mSelector = selector;
        mSelectionCheck = selectionCheck;
    }

    @NonNull
    @Override
    public BlacklistSelectionListAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int type) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.multi_selection_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final BlacklistSelectionListAdapter.ViewHolder viewHolder, final int position) {
        final LaunchableAppDetails appDetails = mList.get(position);
        viewHolder.mImageAppIcon.setBackground(appDetails.getApplicationIcon());
        setForegroundDrawable(viewHolder, appDetails);
        viewHolder.mImageAppIcon.setOnClickListener(view -> {
            mSelector.accept(appDetails.getPackageName());
            setForegroundDrawable(viewHolder, appDetails);
        });
    }

    private void setForegroundDrawable(final @NonNull ViewHolder viewHolder, final LaunchableAppDetails appDetails) {
        if (mSelectionCheck.apply(appDetails.getPackageName())) {
            viewHolder.select(mContext.getResources().getDrawable(R.drawable.ic_add_black_24dp));
        } else {
            viewHolder.unselect();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * Sets the data which should be displayed.
     *
     * @param list The data.
     */
    public void setData(@NonNull final List<LaunchableAppDetails> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageAppIcon)
        ImageView mImageAppIcon;

        /**
         * Ctr.
         *
         * @param itemView The view of one item.
         */
        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void select(@Nullable final Drawable drawable) {
            mImageAppIcon.setImageDrawable(drawable);
        }

        private void unselect() {
            mImageAppIcon.setImageDrawable(null);
        }
    }
}
