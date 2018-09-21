package com.edu.uni.augsburg.uniatron.ui.card.summary;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.domain.dao.model.Emotions;
import com.edu.uni.augsburg.uniatron.domain.dao.model.Summary;
import com.edu.uni.augsburg.uniatron.ui.card.BaseCardViewHolder;
import com.edu.uni.augsburg.uniatron.ui.card.CardPriority;
import com.edu.uni.augsburg.uniatron.ui.card.CardUtil;
import com.edu.uni.augsburg.uniatron.ui.card.CardViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The card which displays the summary data.
 *
 * @author Fabio Hellmann
 */
public class SummaryCard extends BaseCardViewHolder {
    private Summary mSummary;

    SummaryCard(@NonNull final Summary summary) {
        super();
        mSummary = summary;
    }

    @Override
    public void update(@NonNull final CardViewHolder cardViewHolder) {
        mSummary = ((SummaryCard) cardViewHolder).mSummary;
    }

    @Override
    public void onBindView(@NonNull final Context context,
                           @NonNull final RecyclerView.ViewHolder viewHolder) {
        CardUtil.setFullSpan(viewHolder);
        final ViewHolder itemViewHolder = (ViewHolder) viewHolder;

        final Emotions emotion = mSummary.getEmotion();
        final Drawable drawable = context.getResources().getDrawable(getEmoticonDrawable(emotion));

        itemViewHolder.mImageViewEmoticon.setImageDrawable(drawable);
        itemViewHolder.mTextViewSteps.setText(context.getString(R.string.card_summary_steps,
                mSummary.getSteps(),
                context.getString(getEmotionText(emotion))
        ));
    }

    @Override
    public CardPriority getPriority() {
        return CardPriority.VERY_LOW;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final Context context,
                                                      @NonNull final ViewGroup viewGroup) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_summary, viewGroup, false));
    }

    @StringRes
    private int getEmotionText(@NonNull final Emotions emotion) {
        switch (emotion) {
            case ANGRY:
                return R.string.emotion_angry;
            case SADNESS:
                return R.string.emotion_sad;
            case HAPPINESS:
                return R.string.emotion_happy;
            case FANTASTIC:
                return R.string.emotion_fantastic;
            default:
                return R.string.emotion_neutral;
        }
    }

    @DrawableRes
    private int getEmoticonDrawable(@NonNull final Emotions emotion) {
        switch (emotion) {
            case ANGRY:
                return R.drawable.ic_sentiment_very_dissatisfied_black_48dp;
            case SADNESS:
                return R.drawable.ic_sentiment_dissatisfied_black_48dp;
            case HAPPINESS:
                return R.drawable.ic_sentiment_satisfied_black_48dp;
            case FANTASTIC:
                return R.drawable.ic_sentiment_very_satisfied_black_48dp;
            default:
                return R.drawable.ic_sentiment_neutral_black_48dp;
        }
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textViewSteps)
        TextView mTextViewSteps;
        @BindView(R.id.imageViewEmoticon)
        ImageView mImageViewEmoticon;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
