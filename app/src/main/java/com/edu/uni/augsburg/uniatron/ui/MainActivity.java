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
import android.support.v7.widget.Toolbar;
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
import com.edu.uni.augsburg.uniatron.ui.card.AppUsageViewModel;
import com.edu.uni.augsburg.uniatron.ui.card.EmptyCard;
import com.edu.uni.augsburg.uniatron.ui.card.SummaryViewModel;
import com.edu.uni.augsburg.uniatron.ui.setting.SettingActivity;
import com.edu.uni.augsburg.uniatron.ui.shop.TimeCreditShopActivity;
import com.rvalerio.fgchecker.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

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

    private MainActivityViewModel mModelNavigation;

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

        setupCardModels(adapter);

        NotificationChannels.setupChannels(this);
        requestAppPermissions();
        startServices();
    }

    private void setupCardModels(@NonNull final CardListAdapter adapter) {
        final SummaryViewModel modelSummary = ViewModelProviders.of(this).get(SummaryViewModel.class);
        modelSummary.getSummaryCard().observe(this, adapter::addOrUpdateCard);

        final AppUsageViewModel modelAppStatistics = ViewModelProviders.of(this)
                .get(AppUsageViewModel.class);
        modelAppStatistics.getAppStatisticsCard().observe(this, adapter::addOrUpdateCard);

        mModelNavigation = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mModelNavigation.registerCardViewModel(modelSummary);
        mModelNavigation.registerCardViewModel(modelAppStatistics);
        mModelNavigation.getCurrentDate().observe(this, date -> {
            adapter.clear();
            mDateDisplayButton.setText(getDateFormatByGroupStrategy(date.getTime()));
            final int calendarType = mModelNavigation.getGroupByStrategy().getCalendarType();
            modelSummary.setup(date.getTime(), calendarType);
            mNextDateButton.setEnabled(mModelNavigation.isNextAvailable());
            mPrevDateButton.setEnabled(mModelNavigation.isPrevAvailable());
            mRecyclerView.smoothScrollToPosition(0);
        });
    }

    /**
     * Called when the button to step to the previous date is clicked.
     */
    @OnClick(R.id.prevDateButton)
    public void onPrevClicked() {
        mModelNavigation.prevData();
    }

    /**
     * Called when the button which displays the date is clicked.
     */
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

    /**
     * Called when the button to step to the next date is clicked.
     */
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
        return powerManager != null && !powerManager.isIgnoringBatteryOptimizations(packageName);
    }

    private String getDateFormatByGroupStrategy(@NonNull final Date date) {
        switch (mModelNavigation.getGroupByStrategy()) {
            case MONTH:
                return DateUtil.formatForMonth(date);
            case YEAR:
                return DateUtil.formatForYear(date);
            case DATE:
            default:
                return DateUtil.formatForDate(date);
        }
    }

    @Override
    public boolean onMenuItemClick(@NonNull final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.setting:
                final Intent nextIntent = new Intent(this, SettingActivity.class);
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(nextIntent)
                        .startActivities();
                return true;
            case R.id.group_by_day:
                menuItem.setChecked(true);
                mModelNavigation.setGroupByStrategy(MainActivityViewModel.GroupBy.DATE);
                return true;
            case R.id.group_by_month:
                menuItem.setChecked(true);
                mModelNavigation.setGroupByStrategy(MainActivityViewModel.GroupBy.MONTH);
                return true;
            case R.id.group_by_year:
                menuItem.setChecked(true);
                mModelNavigation.setGroupByStrategy(MainActivityViewModel.GroupBy.YEAR);
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
            super();
            mContext = context;
        }

        void addOrUpdateCard(@Nullable final CardView cardView) {
            if (cardView == null) {
                return;
            } else if (mCardViewList.isEmpty()) {
                mCardViewList.add(new EmptyCard());
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
                        (card1, card2) -> Integer.compare(card1.getType(), card2.getType())
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
                    .orElseThrow(() -> new IllegalStateException("The card needs to "
                            + "create a view holder!"));
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder,
                                     final int position) {
            mCardViewList.get(position).onBindView(mContext, viewHolder);
        }

        @Override
        public int getItemViewType(final int position) {
            return mCardViewList.get(position).getType();
        }

        @Override
        public int getItemCount() {
            return mCardViewList.size();
        }
    }
}
