package com.edu.uni.augsburg.uniatron.ui;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.button.MaterialButton;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.domain.util.DateUtil;
import com.edu.uni.augsburg.uniatron.notification.NotificationChannels;
import com.edu.uni.augsburg.uniatron.service.AppTrackingService;
import com.edu.uni.augsburg.uniatron.service.BroadcastService;
import com.edu.uni.augsburg.uniatron.service.StepCountService;
import com.edu.uni.augsburg.uniatron.ui.card.AppStatisticsViewModel;
import com.edu.uni.augsburg.uniatron.ui.card.EmptyCard;
import com.edu.uni.augsburg.uniatron.ui.card.SummaryViewModel;
import com.edu.uni.augsburg.uniatron.ui.shop.TimeCreditShopActivity;
import com.rvalerio.fgchecker.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The main activity is the entry point of the app.
 *
 * @author Fabio Hellmann
 */
public class MainActivity extends AppCompatActivity implements android.support.v7.widget.Toolbar.OnMenuItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.bar)
    BottomAppBar mBottomAppBar;
    @BindView(R.id.prevDateButton)
    MaterialButton mPrevDateButton;
    @BindView(R.id.dateDisplayButton)
    MaterialButton mDateDisplayButton;
    @BindView(R.id.nextDateButton)
    MaterialButton mNextDateButton;

    private BasicViewModel mModelNavigation;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mBottomAppBar.replaceMenu(R.menu.nav_bottom_bar_menu);
        mBottomAppBar.setOnMenuItemClickListener(this);

        final LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layout);

        final CardListAdapter adapter = new CardListAdapter(this);
        mRecyclerView.setAdapter(adapter);
        adapter.addOrUpdateCard(new EmptyCard());

        final SummaryViewModel modelSummary = ViewModelProviders.of(this).get(SummaryViewModel.class);
        modelSummary.getSummaryCard().observe(this, adapter::addOrUpdateCard);

        final AppStatisticsViewModel modelAppStatistics = ViewModelProviders.of(this)
                .get(AppStatisticsViewModel.class);
        modelAppStatistics.getAppStatisticsCard().observe(this, adapter::addOrUpdateCard);

        mModelNavigation = ViewModelProviders.of(this).get(BasicViewModel.class);
        mModelNavigation.registerCardViewModel(modelSummary);
        mModelNavigation.registerCardViewModel(modelAppStatistics);
        mModelNavigation.getCurrentDate().observe(this, date -> {
            adapter.clear();
            adapter.addOrUpdateCard(new EmptyCard());
            switch (mModelNavigation.getGroupByStrategy()) {
                case MONTH:
                    mDateDisplayButton.setText(DateUtil.formatForMonth(date.getTime()));
                    break;
                case YEAR:
                    mDateDisplayButton.setText(DateUtil.formatForYear(date.getTime()));
                    break;
                case DATE:
                default:
                    mDateDisplayButton.setText(DateUtil.formatForDate(date.getTime()));
                    break;
            }
            final int calendarType = mModelNavigation.getGroupByStrategy().getCalendarType();
            modelSummary.setup(date.getTime(), calendarType);
            mNextDateButton.setEnabled(mModelNavigation.isNextAvailable());
            mPrevDateButton.setEnabled(mModelNavigation.isPrevAvailable());
            mRecyclerView.smoothScrollToPosition(0);
        });

        NotificationChannels.setupChannels(this);
        requestAppPermissions();
        startServices();
    }

    @OnClick(R.id.prevDateButton)
    public void onPrevClicked() {
        mModelNavigation.prevData();
    }

    @OnClick(R.id.dateDisplayButton)
    public void onDateDisplayClicked() {
        new DatePickerDialog(
                this,
                (datePicker, year, month, date) -> {
                    final Calendar calendar = GregorianCalendar.getInstance();
                    calendar.set(year, month, date);
                    mModelNavigation.setDate(calendar.getTime());
                },
                mModelNavigation.getCurrentDateValue(Calendar.YEAR),
                mModelNavigation.getCurrentDateValue(Calendar.MONTH),
                mModelNavigation.getCurrentDateValue(Calendar.DATE)
        ).show();
    }

    @OnClick(R.id.nextDateButton)
    public void onNextClicked() {
        mModelNavigation.nextData();
    }

    /**
     * Is called when the fab is clicked.
     */
    @OnClick(R.id.fab)
    public void onFabClicked() {
        final Intent nextIntent = new Intent(this, TimeCreditShopActivity.class);
        TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(nextIntent)
                .startActivities();
    }

    private void startServices() {
        startService(new Intent(this, BroadcastService.class));
        startService(new Intent(this, StepCountService.class));
        startService(new Intent(this, AppTrackingService.class));
    }

    private void requestAppPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !Utils.hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkBatteryOptimized()) {
            startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkBatteryOptimized() {
        final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        final String packageName = getApplicationContext().getPackageName();
        return !powerManager.isIgnoringBatteryOptimizations(packageName);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.setting:
                return true;
            case R.id.group_by_day:
                menuItem.setChecked(true);
                mModelNavigation.setGroupByStrategy(BasicViewModel.GroupBy.DATE);
                return true;
            case R.id.group_by_month:
                menuItem.setChecked(true);
                mModelNavigation.setGroupByStrategy(BasicViewModel.GroupBy.MONTH);
                return true;
            case R.id.group_by_year:
                menuItem.setChecked(true);
                mModelNavigation.setGroupByStrategy(BasicViewModel.GroupBy.YEAR);
                return true;
            default:
                return false;
        }
    }

    private static final class CardListAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final List<CardView> mCardViewList = new ArrayList<>();
        @NonNull
        private final Context mContext;

        CardListAdapter(@NonNull final Context context) {
            mContext = context;
        }

        void addOrUpdateCard(@Nullable final CardView cardView) {
            if (cardView == null) {
                return;
            }
            // Remove the card if it does already exists
            final Optional<CardView> cardOptional = Stream.of(mCardViewList)
                    .filter(card -> card.getType() == cardView.getType())
                    .findFirst();
            if (cardOptional.isPresent()) {
                cardOptional.get().update(cardView);
                notifyItemChanged(mCardViewList.indexOf(cardOptional.get()));
                notifyDataSetChanged();
            } else {
                mCardViewList.add(cardView);
                Collections.sort(
                        mCardViewList,
                        (cardView1, t1) -> Integer.compare(cardView1.getType(), t1.getType())
                );
                notifyItemInserted(mCardViewList.indexOf(cardView));
                notifyDataSetChanged();
            }
        }

        void clear() {
            final int size = mCardViewList.size();
            mCardViewList.clear();
            notifyItemRangeRemoved(0, size);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup,
                                                          final int type) {
            return Stream.of(mCardViewList)
                    .filter(card -> card.getType() == type)
                    .findFirst()
                    .map(card -> card.onCreateViewHolder(mContext, viewGroup))
                    .orElseThrow(() -> new IllegalStateException("The card needs to " +
                            "create a view holder!"));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            mCardViewList.get(position).onBindView(mContext, viewHolder);
        }

        @Override
        public int getItemViewType(int position) {
            return mCardViewList.get(position).getType();
        }

        @Override
        public int getItemCount() {
            return mCardViewList.size();
        }
    }
}
