package com.edu.uni.augsburg.uniatron.ui.home.shop;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * The time credit shop to select the time credit and emotion.
 *
 * @author Fabio Hellmann
 */
public class TimeCreditShopActivity extends AppCompatActivity {
    private static final int ANIMATION_DURATION = 500;

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
    private SharedPreferencesHandler mPrefHandler;
    private MenuItem mMenuTrade;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_credit_shop);

        ButterKnife.bind(this);

        setupActionBar();

        mPrefHandler = new SharedPreferencesHandler(this);

        mModel = ViewModelProviders.of(this)
                .get(TimeCreditShopViewModel.class);
        mModel.setShopChangeListener(empty -> mMenuTrade.setVisible(!empty));

        setupRecyclerView();

        final TimeCreditListAdapter adapter = new TimeCreditListAdapter();

        mModel.getLatestLearningAidTimePassed().observe(this, learningAid -> {
            if (learningAid.isActive()) {
                mScrollView.setVisibility(View.GONE);
                mLayoutLearningAid.setVisibility(View.VISIBLE);
                mTextLearningAidInfo.setText(getString(
                        R.string.learning_aid_active,
                        learningAid.getLeftTime()
                ));
            } else {
                mScrollView.setVisibility(View.VISIBLE);
                mLayoutLearningAid.setVisibility(View.GONE);
            }
        });
        mModel.getRemainingStepCountToday().observe(this, stepCount -> {
            adapter.setStepCount(stepCount);
            adapter.notifyDataSetChanged();
        });
        mRecyclerViewCreditList.setAdapter(adapter);
    }

    private void setupActionBar() {
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.time_credit_shop_title));
    }

    private void setupRecyclerView() {
        final GridLayoutManager layout = new GridLayoutManager(this, 2);
        layout.setOrientation(GridLayoutManager.VERTICAL);

        mRecyclerViewCreditList.setHasFixedSize(true);
        mRecyclerViewCreditList.setLayoutFrozen(true);
        mRecyclerViewCreditList.setLayoutManager(layout);
        mRecyclerViewCreditList.setItemAnimator(new SlideInUpAnimator());
        mRecyclerViewCreditList.getItemAnimator().setAddDuration(ANIMATION_DURATION);
        mRecyclerViewCreditList.getItemAnimator().setChangeDuration(ANIMATION_DURATION);
        mRecyclerViewCreditList.getItemAnimator().setMoveDuration(ANIMATION_DURATION);
        mRecyclerViewCreditList.getItemAnimator().setRemoveDuration(ANIMATION_DURATION);
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
                finish();
                return true;
            case R.id.trade:
                trade();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void trade() {
        // Get selected emotion
        final int checkedRadioButtonId = mRadioGroupEmotion.getCheckedRadioButtonId();
        final View checkedRadioButton = mRadioGroupEmotion.findViewById(checkedRadioButtonId);
        final int checkedIndex = mRadioGroupEmotion.indexOfChild(checkedRadioButton);

        // Buy the time credit
        mModel.buy(Emotions.values()[checkedIndex]);
        finish();
    }

    final class TimeCreditListAdapter extends
            RecyclerView.Adapter<TimeCreditListAdapter.ViewHolder> {
        private static final int VIEW_TYPE_LEARNING_AID = 1;
        private int mStepCount;

        @NonNull
        @Override
        public TimeCreditListAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                                   final int viewType) {
            return new TimeCreditListAdapter.ViewHolder(getViewByType(parent, viewType));
        }

        private View getViewByType(@NonNull final ViewGroup parent, final int viewType) {
            if (viewType == VIEW_TYPE_LEARNING_AID) {
                return LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.card_time_credit_shop_learning_aid, parent, false);
            } else {
                return LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.card_time_credit_shop_credit, parent, false);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull final TimeCreditListAdapter.ViewHolder holder,
                                     final int position) {
            final TimeCredits timeCredits = Stream.of(TimeCredits.values())
                    .sortBy(TimeCredits::getStepCount)
                    .collect(Collectors.toList())
                    .get(position);

            if (mModel.isInShoppingCart(timeCredits)) {
                final int color = getResources().getColor(R.color.secondaryLightColor);
                holder.mTextViewTradeOffer.setBackgroundColor(color);
            } else {
                final int color = getResources().getColor(android.R.color.transparent);
                holder.mTextViewTradeOffer.setBackgroundColor(color);
            }
            holder.mValue = timeCredits;

            if (timeCredits == TimeCredits.CREDIT_LEARNING) {
                holder.mTextViewTradeOffer.setText(getString(
                        R.string.card_time_credit_learning,
                        timeCredits.getTime(),
                        timeCredits.getBlockedMinutes())
                );
            } else {
                holder.mTextViewTradeOffer.setText(getString(
                        R.string.card_time_credit_steps,
                        timeCredits.getTime(),
                        (int) (mPrefHandler.getStepsFactor() * timeCredits.getStepCount()))
                );
            }
        }

        @Override
        public int getItemViewType(final int position) {
            return position == 0 ? VIEW_TYPE_LEARNING_AID : 0;
        }

        @Override
        public int getItemCount() {
            return (int) Stream.of(TimeCredits.values())
                    .filter(credit -> mPrefHandler.getStepsFactor()
                            * credit.getStepCount() <= mStepCount)
                    .count();
        }

        void setStepCount(final int stepCount) {
            this.mStepCount = stepCount;
        }

        /**
         * The view holder for the time credit item.
         *
         * @author Fabio Hellmann
         */
        public final class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.textViewTradeOffer)
            TextView mTextViewTradeOffer;
            private TimeCredits mValue;

            ViewHolder(final View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @OnClick(R.id.textViewTradeOffer)
            public void onClick() {
                if (mModel.isInShoppingCart(mValue)) {
                    mModel.removeFromShoppingCart(mValue);
                } else {
                    mModel.addToShoppingCart(mValue);
                }
                notifyDataSetChanged();
            }
        }
    }
}
