package com.edu.uni.augsburg.uniatron.ui.day;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Fabio Hellmann
 */
public class DayFragment extends Fragment {

    private static final String CALENDAR_DATE = "calendar_date";
    private static final String CALENDAR_MONTH = "calendar_month";
    private static final String CALENDAR_YEAR = "calendar_year";
    private static final String CALENDAR_TYPE = "calendar_type";

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_day, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view,
                              @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() == null) {
            throw new NullPointerException("The bundle with calendar data is missing!");
        }

        final DayViewModel model = ViewModelProviders.of(this).get(DayViewModel.class);
        model.setup(getCalendar(getArguments()).getTime(), getCalendarType(getArguments()));

        final LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layout);

        final CardListAdapter adapter = new CardListAdapter(getContext());
        mRecyclerView.setAdapter(adapter);

        model.getSummary().observe(this, adapter::addOrUpdateCard);
    }

    private Calendar getCalendar(@NonNull final Bundle bundle) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(
                bundle.getInt(CALENDAR_YEAR),
                bundle.getInt(CALENDAR_MONTH),
                bundle.getInt(CALENDAR_DATE)
        );
        return calendar;
    }

    private int getCalendarType(@NonNull final Bundle bundle) {
        return bundle.getInt(CALENDAR_TYPE);
    }

    public static Fragment createInstance(@NonNull final Calendar calendar,
                                          final int calendarType) {
        final DayFragment fragment = new DayFragment();
        final Bundle bundle = new Bundle();
        bundle.putInt(CALENDAR_DATE, calendar.get(Calendar.DATE));
        bundle.putInt(CALENDAR_MONTH, calendar.get(Calendar.MONTH));
        bundle.putInt(CALENDAR_YEAR, calendar.get(Calendar.YEAR));
        bundle.putInt(CALENDAR_TYPE, calendarType);
        fragment.setArguments(bundle);
        return fragment;
    }

    private static final class CardListAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final List<ModelCard> mModelCardList = new ArrayList<>();
        @NonNull
        private final Context mContext;

        CardListAdapter(@NonNull final Context context) {
            mContext = context;
        }

        void addOrUpdateCard(@NonNull final ModelCard modelCard) {
            // Remove the card if it does already exists
            final Optional<ModelCard> cardOptional = Stream.of(mModelCardList)
                    .filter(card -> card.getType() == modelCard.getType())
                    .findFirst();
            if (cardOptional.isPresent()) {
                cardOptional.get().update(modelCard);
                notifyItemChanged(mModelCardList.indexOf(cardOptional.get()));
                notifyDataSetChanged();
            } else {
                mModelCardList.add(modelCard);
                Collections.sort(
                        mModelCardList,
                        (modelCard1, t1) -> Integer.compare(modelCard1.getType(), t1.getType())
                );
                notifyItemInserted(mModelCardList.indexOf(modelCard));
                notifyDataSetChanged();
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup,
                                                          final int type) {
            return Stream.of(mModelCardList)
                    .filter(card -> card.getType() == type)
                    .findFirst()
                    .map(card -> card.onCreateViewHolder(mContext, viewGroup))
                    .orElseThrow(() -> new IllegalStateException("The card needs to " +
                            "create a view holder!"));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            mModelCardList.get(position).onBindView(mContext, viewHolder);
        }

        @Override
        public int getItemViewType(int position) {
            return mModelCardList.get(position).getType();
        }

        @Override
        public int getItemCount() {
            return mModelCardList.size();
        }
    }
}
