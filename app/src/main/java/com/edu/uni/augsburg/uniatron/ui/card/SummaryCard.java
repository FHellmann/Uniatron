package com.edu.uni.augsburg.uniatron.ui.card;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.Summary;
import com.edu.uni.augsburg.uniatron.ui.CardView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SummaryCard implements CardView {
    private static final int TYPE = 1;
    private Summary mSummary;

    public SummaryCard(@NonNull final Summary summary) {
        mSummary = summary;
    }

    @Override
    public void update(CardView cardView) {
        mSummary = ((SummaryCard) cardView).mSummary;
    }

    @Override
    public void onBindView(@NonNull final Context context,
                           @NonNull final RecyclerView.ViewHolder viewHolder) {

        final String textSteps = String.valueOf(mSummary.getSteps());
        final String usageTime = String.format(
                Locale.getDefault(),
                "%d:%02d",
                mSummary.getAppUsageTime() / 60,
                mSummary.getAppUsageTime() % 60
        );

        final ViewHolder itemViewHolder = (ViewHolder) viewHolder;
        itemViewHolder.mTextViewSteps.setText(textSteps);
        itemViewHolder.mTextViewUsageTime.setText(usageTime);

        final Emotions emotion = Emotions.getAverage(mSummary.getEmotionAvg());
        final Drawable drawable = getEmoticonDrawable(context, emotion);
        itemViewHolder.mImageViewEmoticon.setImageDrawable(drawable);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final Context context,
                                                      @NonNull final ViewGroup viewGroup) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.card_summary, viewGroup, false));
    }

    private Drawable getEmoticonDrawable(@NonNull final Context context,
                                         @NonNull final Emotions emotion) {
        switch (emotion) {
            case ANGRY:
                return context.getResources()
                        .getDrawable(R.drawable.ic_emoticon_angry_selected);
            case SADNESS:
                return context.getResources()
                        .getDrawable(R.drawable.ic_emoticon_sad_selected);
            case HAPPINESS:
                return context.getResources()
                        .getDrawable(R.drawable.ic_emoticon_happy_selected);
            case FANTASTIC:
                return context.getResources()
                        .getDrawable(R.drawable.ic_emoticon_fantastic_selected);
            case NEUTRAL:
            default:
                return context.getResources()
                        .getDrawable(R.drawable.ic_emoticon_neutral_selected);
        }
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textViewSteps)
        TextView mTextViewSteps;
        @BindView(R.id.textViewUsageTime)
        TextView mTextViewUsageTime;
        @BindView(R.id.imageViewEmoticon)
        ImageView mImageViewEmoticon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
