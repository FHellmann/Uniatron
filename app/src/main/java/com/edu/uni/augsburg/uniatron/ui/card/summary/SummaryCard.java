package com.edu.uni.augsburg.uniatron.ui.card.summary;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.Summary;
import com.edu.uni.augsburg.uniatron.ui.CardViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The card which displays the summary data.
 *
 * @author Fabio Hellmann
 */
public class SummaryCard implements CardViewHolder {
    private static final int TYPE = Integer.MAX_VALUE - 1;
    private Summary mSummary;

    SummaryCard(@NonNull final Summary summary) {
        mSummary = summary;
    }

    @Override
    public void update(@NonNull final CardViewHolder cardViewHolder) {
        mSummary = ((SummaryCard) cardViewHolder).mSummary;
    }

    @Override
    public void onBindView(@NonNull final Context context,
                           @NonNull final RecyclerView.ViewHolder viewHolder) {
        final StaggeredGridLayoutManager.LayoutParams layoutParams =
                (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
        layoutParams.setFullSpan(true);

        final ViewHolder itemViewHolder = (ViewHolder) viewHolder;

        final Emotions emotion = Emotions.getAverage(mSummary.getEmotionAvg());
        final Drawable drawable = context.getResources().getDrawable(getEmoticonDrawable(emotion));

        itemViewHolder.mImageViewEmoticon.setImageDrawable(drawable);
        itemViewHolder.mTextViewSteps.setText(context.getString(R.string.card_summary_steps,
                mSummary.getSteps(),
                context.getString(getEmotionText(emotion))
        ));
    }

    @Override
    public int getType() {
        return TYPE;
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
            case NEUTRAL:
            default:
                return R.string.emotion_neutral;
        }
    }

    @DrawableRes
    private int getEmoticonDrawable(@NonNull final Emotions emotion) {
        switch (emotion) {
            case ANGRY:
                return R.drawable.ic_emoticon_angry_selected;
            case SADNESS:
                return R.drawable.ic_emoticon_sad_selected;
            case HAPPINESS:
                return R.drawable.ic_emoticon_happy_selected;
            case FANTASTIC:
                return R.drawable.ic_emoticon_fantastic_selected;
            case NEUTRAL:
            default:
                return R.drawable.ic_emoticon_neutral_selected;
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
