package com.edu.uni.augsburg.uniatron.ui.shop;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.domain.dao.model.Emotions;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The time credit shop to select the time credit and emotion.
 *
 * @author Fabio Hellmann
 */
public class TimeCreditShopActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recyclerViewCreditlist)
    RecyclerView mRecyclerViewCreditList;
    @BindView(R.id.radioGroupEmotion)
    RadioGroup mRadioGroupEmotion;
    @BindView(R.id.layoutLearningAidActive)
    View mLayoutLearningAid;
    @BindView(R.id.textLearningAidActive)
    TextView mTextLearningAidInfo;
    @BindView(R.id.scrollView)
    ScrollView mScrollView;

    private TimeCreditShopViewModel mModel;
    private MenuItem mMenuTrade;
    private TimeCreditShopListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_credit_shop);

        ButterKnife.bind(this);

        setupActionBar();

        final GridLayoutManager layout = new GridLayoutManager(this, 2);
        layout.setOrientation(GridLayoutManager.VERTICAL);

        mModel = ViewModelProviders.of(this)
                .get(TimeCreditShopViewModel.class);
        mModel.setShopValidListener(visible -> mMenuTrade.setVisible(visible));

        mAdapter = new TimeCreditShopListAdapter(this, mModel);
        mRecyclerViewCreditList.setAdapter(mAdapter);

        mRecyclerViewCreditList.setHasFixedSize(true);
        mRecyclerViewCreditList.setLayoutFrozen(true);
        mRecyclerViewCreditList.setLayoutManager(layout);

        setupModelObservers();
    }

    private void setupModelObservers() {
        mRadioGroupEmotion.setOnCheckedChangeListener((radioGroup, position) -> {
            final int checkedRadioButtonId = mRadioGroupEmotion.getCheckedRadioButtonId();
            final View checkedRadioButton = mRadioGroupEmotion.findViewById(checkedRadioButtonId);
            final int checkedIndex = mRadioGroupEmotion.indexOfChild(checkedRadioButton);
            mModel.setEmotion(Emotions.values()[checkedIndex]);
        });
        mModel.getLatestLearningAidTimePassed().observe(this, learningAid -> {
            if (learningAid != null && learningAid.isActive()) {
                mScrollView.setVisibility(View.GONE);
                mLayoutLearningAid.setVisibility(View.VISIBLE);
                mTextLearningAidInfo.setText(getString(
                        R.string.learning_aid_active,
                        TimeUnit.MINUTES.convert(learningAid.getLeftTime().get(), TimeUnit.MILLISECONDS)
                ));
            } else {
                mScrollView.setVisibility(View.VISIBLE);
                mLayoutLearningAid.setVisibility(View.GONE);
            }
        });
        mModel.getRemainingStepCountToday().observe(this, mAdapter::setStepCount);
    }

    private void setupActionBar() {
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.time_credit_shop_title));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.time_credit_shop, menu);
        mMenuTrade = menu.findItem(R.id.trade);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeAsUp:
                mModel.clear();
                finish();
                return true;
            case R.id.trade:
                mModel.buy();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
