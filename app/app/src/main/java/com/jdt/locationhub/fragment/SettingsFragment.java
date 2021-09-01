package com.jdt.locationhub.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jdt.locationhub.R;
import com.jdt.locationhub.viewmodel.MainViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    private MainViewModel mainViewModel;

    private SwitchMaterial privacySwitch;
    private TextView rangeTextV;
    private Slider rangeSlider;

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

        privacySwitch = v.findViewById(R.id.privacy_Switch_FragmentSettings);
        rangeTextV = v.findViewById(R.id.range_TextView_FragmentSettings);
        rangeSlider = v.findViewById(R.id.range_Slider_FragmentSettings);

        privacySwitch.setChecked(!mainViewModel.getPrivacyStatus());
        privacySwitch.setOnCheckedChangeListener((compoundButton, b) -> mainViewModel.setPrivacy(b));

        rangeTextV.setText(getResources().getString(R.string.clientRange, rangeSlider.getValue()));

        rangeSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                rangeTextV.setText(getResources().getString(R.string.clientRange, slider.getValue()));
                mainViewModel.setClientsRange((long) slider.getValue());
            }
        });

        return v;
    }
}