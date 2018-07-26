package com.edu.uni.augsburg.uniatron.ui;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.button.MaterialButton;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.domain.util.DateFormatter;
import com.edu.uni.augsburg.uniatron.notification.NotificationChannels;
import com.edu.uni.augsburg.uniatron.service.StickyAppService;
import com.edu.uni.augsburg.uniatron.ui.about.AboutActivity;
import com.edu.uni.augsburg.uniatron.ui.card.appusage.AppUsageViewModel;
import com.edu.uni.augsburg.uniatron.ui.card.coinbag.CoinBagViewModel;
import com.edu.uni.augsburg.uniatron.ui.card.summary.SummaryViewModel;
import com.edu.uni.augsburg.uniatron.ui.card.timeaccount.TimeAccountViewModel;
import com.edu.uni.augsburg.uniatron.ui.onboarding.OnBoardingActivity;
import com.edu.uni.augsburg.uniatron.ui.setting.SettingActivity;
import com.edu.uni.augsburg.uniatron.ui.shop.TimeCreditShopActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
    private MainActivityCardListAdapter mAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mBottomAppBar.replaceMenu(R.menu.nav_bottom_bar_menu);
        mBottomAppBar.setOnMenuItemClickListener(this);

        final StaggeredGridLayoutManager layout =
                new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layout);

        mAdapter = new MainActivityCardListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        setupCardModels();
        startOnBoarding();

        NotificationChannels.setupChannels(this);
        startService(new Intent(this, StickyAppService.class));
    }

    private void setupCardModels() {
        final SummaryViewModel modelSummary = ViewModelProviders.of(this).get(SummaryViewModel.class);
        modelSummary.getSummaryCard().observe(this, mAdapter::addOrUpdateCard);

        final AppUsageViewModel modelAppStatistics = ViewModelProviders.of(this)
                .get(AppUsageViewModel.class);
        modelAppStatistics.getAppUsageCard(this).observe(this, mAdapter::addOrUpdateCard);

        final CoinBagViewModel coinBagModel = ViewModelProviders.of(this).get(CoinBagViewModel.class);
        coinBagModel.getRemainingCoins().observe(this, mAdapter::addOrUpdateCard);

        final TimeAccountViewModel timeAccountModel = ViewModelProviders.of(this).get(TimeAccountViewModel.class);
        timeAccountModel.getRemainingAppUsageTime().observe(this, mAdapter::addOrUpdateCard);

        mModelNavigation = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mModelNavigation.registerCardViewModel(modelSummary);
        mModelNavigation.registerCardViewModel(modelAppStatistics);
        mModelNavigation.registerCardViewModel(coinBagModel);
        mModelNavigation.registerCardViewModel(timeAccountModel);
        mModelNavigation.getCurrentDate().observe(this, date -> {
            if (date != null) {
                mDateDisplayButton.setText(getDateFormatByGroupStrategy(date.getTime()));
            }
            mNextDateButton.setEnabled(mModelNavigation.isNextAvailable());
            mPrevDateButton.setEnabled(mModelNavigation.isPrevAvailable());
            mRecyclerView.smoothScrollToPosition(0);
        });
    }

    private void startOnBoarding() {
        if (mModelNavigation.isIntroNeeded(this)) {
            startActivity(new Intent(this, OnBoardingActivity.class));
            mModelNavigation.setIntroDone();
        }
    }

    /**
     * Called when the button to step to the previous date is clicked.
     */
    @OnClick(R.id.prevDateButton)
    public void onPrevClicked() {
        mAdapter.clear();
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
        mAdapter.clear();
        mModelNavigation.nextData();
    }

    /**
     * Is called when the fab is clicked.
     */
    @OnClick(R.id.fab)
    public void onFabClicked() {
        startActivityWithParentStack(new Intent(this, TimeCreditShopActivity.class));
    }

    private String getDateFormatByGroupStrategy(@NonNull final Date date) {
        switch (mModelNavigation.getGroupByStrategy()) {
            case MONTH:
                return DateFormatter.MM_YYYY.format(date);
            case YEAR:
                return DateFormatter.YYYY.format(date);
            case DATE:
            default:
                return DateFormatter.DD_MM_YYYY.format(date);
        }
    }

    @Override
    public boolean onMenuItemClick(@NonNull final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.today:
                mModelNavigation.setDate(new Date());
                return true;
            case R.id.group_by_day:
            case R.id.group_by_month:
            case R.id.group_by_year:
                menuItem.setChecked(true);
                return handleGrouping(menuItem.getItemId());
            case R.id.setting:
                startActivityWithParentStack(new Intent(this, SettingActivity.class));
                return true;
            case R.id.about:
                startActivityWithParentStack(new Intent(this, AboutActivity.class));
                return true;
            case R.id.feedback:
                startFeedbackDialog();
                return true;
            default:
                return false;
        }
    }

    private boolean handleGrouping(@IdRes final int menuItemId) {
        switch (menuItemId) {
            case R.id.group_by_day:
                mModelNavigation.setGroupByStrategy(MainActivityViewModel.GroupBy.DATE);
                return true;
            case R.id.group_by_month:
                mModelNavigation.setGroupByStrategy(MainActivityViewModel.GroupBy.MONTH);
                return true;
            case R.id.group_by_year:
                mModelNavigation.setGroupByStrategy(MainActivityViewModel.GroupBy.YEAR);
                return true;
            default:
                return false;
        }
    }

    private void startFeedbackDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.feedback)
                .content(R.string.feedback_question)
                .positiveText(R.string.yes)
                .positiveColor(getResources().getColor(R.color.secondaryColor))
                .onPositive((dialog, which) -> {
                    // Open Play Store for rating
                    startActivityWithParentStack(getPlayStoreIntent(getPackageName()));
                })
                .negativeText(R.string.no)
                .negativeColor(getResources().getColor(android.R.color.darker_gray))
                .onNegative((dialog, which) -> {
                    // Open feedback web page
                    final Intent feedbackIntent = new Intent(Intent.ACTION_SEND);
                    feedbackIntent.setType("text/html");
                    feedbackIntent.putExtra(Intent.EXTRA_EMAIL, "info@fabio-hellmann.de");
                    feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback: UNIAtron");
                    startActivityWithParentStack(feedbackIntent);
                })
                .show();
    }

    @NonNull
    private Intent getPlayStoreIntent(@NonNull final String appPackageName) {
        try {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
        } catch (android.content.ActivityNotFoundException anfe) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
        }
    }

    private void startActivityWithParentStack(@NonNull final Intent intent) {
        TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(intent)
                .startActivities();
    }
}
