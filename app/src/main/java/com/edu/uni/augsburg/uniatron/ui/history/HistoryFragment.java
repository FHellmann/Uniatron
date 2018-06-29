package com.edu.uni.augsburg.uniatron.ui.history;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.domain.util.DateUtil;
import com.edu.uni.augsburg.uniatron.domain.util.SummaryGroupBy;
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.Summary;
import com.edu.uni.augsburg.uniatron.ui.MainActivity;
import com.edu.uni.augsburg.uniatron.ui.setting.SettingActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * This displays a history of all the previous data.
 *
 * @author Fabio Hellmann
 */
public class HistoryFragment extends Fragment {

    private static final int DAYS_TO_LOAD = 7;
    private static final int ANIMATION_DURATION = 500;

    @BindView(R.id.recyclerViewHistory)
    RecyclerView mRecyclerViewHistory;

    private Date mDateTo;
    private Date mDateFrom;
    private ItemAdapter mHistoryItemAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view,
                              @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final HistoryViewModel model = ViewModelProviders.of(this).get(HistoryViewModel.class);

        final LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewHistory.addItemDecoration(
                new DividerItemDecoration(getContext(), layout.getOrientation())
        );
        mRecyclerViewHistory.setLayoutManager(layout);
        mRecyclerViewHistory.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerViewHistory.getItemAnimator().setAddDuration(ANIMATION_DURATION);
        mRecyclerViewHistory.getItemAnimator().setChangeDuration(ANIMATION_DURATION);
        mRecyclerViewHistory.getItemAnimator().setMoveDuration(ANIMATION_DURATION);
        mRecyclerViewHistory.getItemAnimator().setRemoveDuration(ANIMATION_DURATION);

        mHistoryItemAdapter = new ItemAdapter(mRecyclerViewHistory);
        mHistoryItemAdapter.setGroupByOrder(SummaryGroupBy.DAY);
        mRecyclerViewHistory.setAdapter(mHistoryItemAdapter);

        mDateTo = new Date();
        mDateFrom = getPreviousDate(mDateTo, DAYS_TO_LOAD);
        model.registerDateRange(mDateFrom, mDateTo);

        model.getSummary().observe(this, mHistoryItemAdapter::addItems);

        mHistoryItemAdapter.setOnLoadMoreListener(() -> {
            // define next interval to load
            mDateTo = mDateFrom;
            mDateFrom = getPreviousDate(mDateTo, DAYS_TO_LOAD);

            model.registerDateRange(mDateFrom, mDateTo);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        final MainActivity activity = (MainActivity) getActivity();
        final BottomAppBar bottomAppBar = activity.getBottomAppBar();
        bottomAppBar.post(() -> setupBottomAppBar(bottomAppBar));
    }

    private void setupBottomAppBar(@NonNull final BottomAppBar bottomAppBar) {
        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
        bottomAppBar.replaceMenu(R.menu.history);
        bottomAppBar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.setting:
                    final Intent nextIntent = new Intent(getContext(), SettingActivity.class);
                    TaskStackBuilder.create(getContext())
                            .addNextIntentWithParentStack(nextIntent)
                            .startActivities();
                    return true;
                case R.id.group_by_day:
                    mHistoryItemAdapter.setGroupByOrder(SummaryGroupBy.DAY);
                    menuItem.setChecked(true);
                    return true;
                case R.id.group_by_month:
                    mHistoryItemAdapter.setGroupByOrder(SummaryGroupBy.MONTH);
                    menuItem.setChecked(true);
                    return true;
                case R.id.group_by_year:
                    mHistoryItemAdapter.setGroupByOrder(SummaryGroupBy.YEAR);
                    menuItem.setChecked(true);
                    return true;
                default:
                    return false;
            }
        });
    }

    private static Date getPreviousDate(final Date date, final int days) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -days);
        return calendar.getTime();
    }

    /**
     * The view holder for the item view.
     *
     * @author Fabio Hellmann
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textViewDate)
        TextView mTextViewDate;
        @BindView(R.id.textViewSteps)
        TextView mTextViewSteps;
        @BindView(R.id.textViewUsageTime)
        TextView mTextViewUsageTime;
        @BindView(R.id.imageViewEmoticon)
        ImageView mImageViewEmoticon;

        ItemViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * The view holder for the loading view.
     *
     * @author Fabio Hellmann
     */
    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.progressBar)
        ProgressBar mProgressBar;

        LoadingViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static final class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_ITEM = 0;
        private static final int VIEW_TYPE_LOADING = 1;
        private static final int VISIBLE_THRESHOLD = 5;

        private final Map<Date, Summary> mSummaryMap = new ConcurrentHashMap<>();
        private List<Summary> mContentList = new ArrayList<>();
        private final Context mContext;

        private OnLoadMoreListener mOnLoadMoreListener;

        private boolean isLoading;
        private SummaryGroupBy mGroupByOrder;

        ItemAdapter(@NonNull final RecyclerView recyclerView) {
            super();
            mContext = recyclerView.getContext();
            final LinearLayoutManager linearLayoutManager =
                    (LinearLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(final RecyclerView recyclerView,
                                       final int dScrollX,
                                       final int dScrollY) {
                    super.onScrolled(recyclerView, dScrollX, dScrollY);

                    final int totalItemCount = linearLayoutManager.getItemCount();
                    final int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (getItemCount() > 0 && !isLoading
                            && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                        recyclerView.post(() -> notifyItemInserted(getItemCount()));
                    }
                }
            });
        }

        private void setGroupByOrder(@NonNull final SummaryGroupBy groupByOrder) {
            mGroupByOrder = groupByOrder;
            addItems(Collections.emptyList());
        }

        private void setOnLoadMoreListener(@NonNull final OnLoadMoreListener onLoadMoreListener) {
            this.mOnLoadMoreListener = onLoadMoreListener;
        }

        private void addItems(@NonNull final Collection<Summary> newItems) {
            if (isLoading) {
                notifyItemRemoved(getItemCount());
                isLoading = false;
            }
            // Add the new items to the map, if their not already existing
            Stream.of(newItems).forEach(item -> {
                final Date date = DateUtil.extractMinTimeOfDate(item.getTimestamp());
                mSummaryMap.put(date, item);
            });
            // Group all the entries by the selected group by rule
            mContentList = mGroupByOrder.groupBy(mSummaryMap.values());
            // Notify the view to update
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                          final int viewType) {
            if (viewType == VIEW_TYPE_LOADING) {
                final View view = LayoutInflater.from(mContext)
                        .inflate(R.layout.fragment_history_loading, parent, false);
                return new LoadingViewHolder(view);
            }
            final View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.fragment_history_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder,
                                     final int position) {
            if (holder instanceof ItemViewHolder) {
                final Summary summary = mContentList.get(position);

                final String timestamp = mGroupByOrder.formatTimestamp(summary.getTimestamp());
                final String steps = String.valueOf(summary.getSteps());
                final String usageTime = String.format(
                        Locale.getDefault(),
                        "%d:%02d",
                        summary.getAppUsageTime() / 60,
                        summary.getAppUsageTime() % 60
                );

                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                itemViewHolder.mTextViewDate.setText(timestamp);
                itemViewHolder.mTextViewSteps.setText(steps);
                itemViewHolder.mTextViewUsageTime.setText(usageTime);

                final Emotions emotion = Emotions.getAverage(summary.getEmotionAvg());
                final Drawable drawable = getEmoticonDrawable(emotion);
                itemViewHolder.mImageViewEmoticon.setImageDrawable(drawable);
            } else if (holder instanceof LoadingViewHolder) {
                final LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.mProgressBar.setIndeterminate(true);
            }
        }

        private Drawable getEmoticonDrawable(@NonNull final Emotions emotion) {
            switch (emotion) {
                case ANGRY:
                    return mContext.getResources()
                            .getDrawable(R.drawable.ic_emoticon_angry_selected);
                case SADNESS:
                    return mContext.getResources()
                            .getDrawable(R.drawable.ic_emoticon_sad_selected);
                case HAPPINESS:
                    return mContext.getResources()
                            .getDrawable(R.drawable.ic_emoticon_happy_selected);
                case FANTASTIC:
                    return mContext.getResources()
                            .getDrawable(R.drawable.ic_emoticon_fantastic_selected);
                case NEUTRAL:
                default:
                    return mContext.getResources()
                            .getDrawable(R.drawable.ic_emoticon_neutral_selected);
            }
        }

        @Override
        public int getItemCount() {
            return mContentList.size() + (isLoading ? 1 : 0);
        }

        @Override
        public int getItemViewType(final int position) {
            return getItemCount() - 1 == position && isLoading ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }
    }

    private interface OnLoadMoreListener {
        void onLoadMore();
    }
}
