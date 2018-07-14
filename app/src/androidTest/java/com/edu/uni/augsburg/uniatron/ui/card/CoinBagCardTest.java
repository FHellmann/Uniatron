package com.edu.uni.augsburg.uniatron.ui.card;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.ui.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CoinBagCardTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private CoinBagCard mCard;
    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
        mCard = new CoinBagCard();
    }

    @Test
    public void testCardSetupAndUpdate() {
        int coins = 10;

        mCard.setCoins(coins);
        mCard.setVisible(true);

        RecyclerView.ViewHolder viewHolder = mCard.onCreateViewHolder(mContext, new LinearLayout(mContext));
        assertThat(viewHolder, is(notNullValue()));
        onView(withId(R.id.textCoins)).check(matches(withText(String.valueOf(coins))));
        assertThat(mCard.isVisible(), is(true));

        final CoinBagCard coinBagCard = new CoinBagCard();
        coins = 20;
        coinBagCard.setCoins(coins);
        coinBagCard.setVisible(false);

        mCard.update(coinBagCard);

        viewHolder = mCard.onCreateViewHolder(mContext, new LinearLayout(mContext));
        assertThat(viewHolder, is(notNullValue()));
        onView(withId(R.id.textCoins)).check(matches(withText(String.valueOf(coins))));
        assertThat(mCard.isVisible(), is(false));
    }

    @Test
    public void getType() {
        assertThat(mCard.getType(), is(greaterThan(0)));
    }
}