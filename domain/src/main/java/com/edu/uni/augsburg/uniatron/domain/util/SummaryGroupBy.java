package com.edu.uni.augsburg.uniatron.domain.util;

import android.support.annotation.NonNull;

import com.annimon.stream.function.Function;
import com.edu.uni.augsburg.uniatron.domain.model.SummaryEntity;
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.Summary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A group by algorithm to group by different date categories.
 *
 * @author Fabio Hellmann
 */
public enum SummaryGroupBy {
    /**
     * Group the summaries by date.
     */
    DAY(
            DateUtil::extractMinTimeOfDate,
            date -> String.format(Locale.getDefault(), "%te. %tb %tY", date, date, date)
    ),
    /**
     * Group the summaries by month.
     */
    MONTH(
            DateUtil::extractMinDateOfMonth,
            date -> String.format(Locale.getDefault(), "%tB %tY", date, date)
    ),
    /**
     * Group the summaries by year.
     */
    YEAR(
            DateUtil::extractMinMonthOfYear,
            date -> String.format(Locale.getDefault(), "%tY", date)
    );

    private final Function<Date, Date> mDateConverterFunction;
    private final Function<Date, String> mTimestampFormatter;

    SummaryGroupBy(final Function<Date, Date> dateConverterFunction,
                   final Function<Date, String> timestampFormatter) {
        mDateConverterFunction = dateConverterFunction;
        mTimestampFormatter = timestampFormatter;
    }

    /**
     * Format the timestamp to the group by date.
     *
     * @param date The date to format.
     * @return the formatted date.
     */
    public String formatTimestamp(@NonNull final Date date) {
        return mTimestampFormatter.apply(date);
    }

    /**
     * Convert the date to the group by date.
     *
     * @param date The date to convert.
     * @return the converted date.
     */
    public Date toGroupDate(@NonNull final Date date) {
        return mDateConverterFunction.apply(date);
    }

    /**
     * Apply the group by function on the given set of summaries.
     *
     * @param input The summaries to group by.
     * @return the grouped by summaries.
     */
    public List<Summary> groupBy(@NonNull final Collection<Summary> input) {
        final List<Summary> result = new ArrayList<>();
        if (!input.isEmpty()) {
            Date date = null;
            long appUsageTime = 0;
            double emotionAvg = 0;
            long steps = 0;
            int count = 0;
            for (Summary entity : input) {
                Date tmpDate = toGroupDate(entity.getTimestamp());
                if (date != null && tmpDate.before(date)) {
                    result.add(createGroupedBySummary(
                            date,
                            appUsageTime,
                            emotionAvg / count,
                            steps
                    ));
                    appUsageTime = 0;
                    emotionAvg = 0;
                    steps = 0;
                    count = 0;
                }
                appUsageTime += entity.getAppUsageTime();
                emotionAvg += entity.getEmotionAvg() >= 0 ? entity.getEmotionAvg()
                        : Emotions.NEUTRAL.ordinal();
                steps += entity.getSteps();
                date = tmpDate;
                count++;
            }
            result.add(createGroupedBySummary(
                    date,
                    appUsageTime,
                    emotionAvg / count,
                    steps
            ));
        }
        return result;
    }

    private static Summary createGroupedBySummary(final Date timestamp,
                                                  final long appUsage,
                                                  final double emotionAvg,
                                                  final long steps) {
        final SummaryEntity summary = new SummaryEntity();
        summary.setAppUsageTime(appUsage);
        summary.setEmotionAvg(emotionAvg);
        summary.setSteps(steps);
        summary.setTimestamp(timestamp);
        return summary;
    }
}
