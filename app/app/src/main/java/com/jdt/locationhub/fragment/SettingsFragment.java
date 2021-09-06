package com.jdt.locationhub.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jdt.locationhub.R;
import com.jdt.locationhub.exception.NoInternetConnectionException;
import com.jdt.locationhub.exception.ServerResponseException;
import com.jdt.locationhub.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    private MainViewModel mainViewModel;

    private SwitchMaterial sharePositionSwitch;
    private TextView rangeTextV;
    private RangeSlider rangeSlider;
    private TextView rangeMinMaxTextV;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (getArguments() != null) { }
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        sharePositionSwitch = v.findViewById(R.id.privacy_Switch_FragmentSettings);
        rangeTextV = v.findViewById(R.id.range_TextView_FragmentSettings);
        rangeSlider = v.findViewById(R.id.range_Slider_FragmentSettings);
        rangeMinMaxTextV = v.findViewById(R.id.rangeMinMax_TextView_FragmentSettings);

        sharePositionSwitch.setChecked(!mainViewModel.isPrivacyEnabled().getValue());
        sharePositionSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            try {
                mainViewModel.setPrivacyEnabled(!b);
            } catch (ServerResponseException | NoInternetConnectionException e) {
                e.printStackTrace(); //TODO a better feedback
            }
        });
        mainViewModel.isPrivacyEnabled().observe(getViewLifecycleOwner(), p -> sharePositionSwitch.setChecked(!p));

        mainViewModel.getClientsDiscoveryRange().observe(getViewLifecycleOwner(), r -> {
            rangeTextV.setText(getResources().getString(R.string.clientRange, r.get(1)-r.get(0)));
            rangeSlider.setValues(r);
            rangeMinMaxTextV.setText(getResources().getString(R.string.rangeMinMaxDistance, r.get(0), r.get(1)));
        });

        rangeSlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                mainViewModel.setClientsDiscoveryRange(slider.getValues());
            }
        });

        return v;
    }
}