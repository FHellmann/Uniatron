package com.edu.uni.augsburg.uniatron.ui.onboarding;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edu.uni.augsburg.uniatron.R;

import agency.tango.materialintroscreen.SlideFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The onboarding step to select the apps for blacklisting.
 *
 * @author Fabio Hellmann
 */
public class BlacklistSelectionFragment extends SlideFragment {
    @BindView(R.id.listSelection)
    RecyclerView mListSelection;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_onboarding_multi_selection, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final BlacklistViewModel model = ViewModelProviders.of(this).get(BlacklistViewModel.class);

        final BlacklistSelectionListAdapter adapter = new BlacklistSelectionListAdapter(
                getContext(),
                model::addAppToBlacklist,
                model::isAppBlacklisted
        );
        mListSelection.setAdapter(adapter);
        mListSelection.setLayoutManager(new GridLayoutManager(
                getContext(),
                4,
                LinearLayoutManager.VERTICAL,
                false
        ));

        model.getInstalledApps().observe(this, adapter::setData);
    }

    @Override
    public int backgroundColor() {
        return R.color.onboardingBackground6;
    }

    @Override
    public int buttonsColor() {
        return android.R.color.transparent;
    }
}
