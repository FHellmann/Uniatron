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

    private TimeCreditShopViewModel mModel;
    private SharedPreferencesHandler mPrefHandler;
    private MenuItem mMenuTrade;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_credit_shop);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.time_credit_shop_title));

        mPrefHandler = new SharedPreferencesHandler(this);

        mModel = ViewModelProviders.of(this)
                .get(TimeCreditShopViewModel.class);
        mModel.setShopChangeListener(empty -> mMenuTrade.setVisible(!empty));

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

        final TimeCreditListAdapter adapter = new TimeCreditListAdapter();
        mModel.getRemainingStepCountToday().observe(this, stepCount -> {
            adapter.setStepCount(10000);
            adapter.notifyDataSetChanged();
        });
        mRecyclerViewCreditList.setAdapter(adapter);
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
            RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int VIEW_TYPE_LEARNING_AID = 0;
        private static final int VIEW_TYPE_STEPS = 1;

        private int mStepCount;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                          final int viewType) {
            if (viewType == VIEW_TYPE_LEARNING_AID) {
                final View view = LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.activity_time_credit_shop_card_learning_aid, parent, false);
                return new ViewHolderSteps(view);
            }
            final View view = LayoutInflater.from(getBaseContext())
                    .inflate(R.layout.activity_time_credit_shop_card_credit, parent, false);
            return new ViewHolderSteps(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder,
                                     final int position) {
            final TimeCredits timeCredits = Stream.of(TimeCredits.values())
                    .sortBy(TimeCredits::getStepCount)
                    .collect(Collectors.toList())
                    .get(position);

            if(timeCredits == TimeCredits.CREDIT_LEARNING) {
                final ViewHolderLearningAid holderLearningAid = (ViewHolderLearningAid) holder;

            } else {
                final ViewHolderSteps holderSteps = (ViewHolderSteps) holder;

                if (mModel.isInShoppingCart(timeCredits)) {
                    final int color = getResources().getColor(R.color.secondaryLightColor);
                    holderSteps.mTextViewTradeOffer.setBackgroundColor(color);
                } else {
                    final int color = getResources().getColor(android.R.color.transparent);
                    holderSteps.mTextViewTradeOffer.setBackgroundColor(color);
                }
                holderSteps.mValue = timeCredits;
                holderSteps.mTextViewTradeOffer.setText(getString(
                        R.string.card_time_credit_title,
                        timeCredits.getTimeInMinutes(),
                        (int) (mPrefHandler.getStepsFactor() * timeCredits.getStepCount()))
                );
            }
        }

        @Override
        public int getItemViewType(final int position) {
            if (position == 0) {
                return VIEW_TYPE_LEARNING_AID;
            }
            return VIEW_TYPE_STEPS;
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

        private final class ViewHolderSteps extends RecyclerView.ViewHolder {
            @BindView(R.id.textViewTradeOffer)
            TextView mTextViewTradeOffer;
            private TimeCredits mValue;

            ViewHolderSteps(final View itemView) {
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

        private final class ViewHolderLearningAid extends RecyclerView.ViewHolder {

            public ViewHolderLearningAid(@NonNull final View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
