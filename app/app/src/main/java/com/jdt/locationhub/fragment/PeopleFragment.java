package com.jdt.locationhub.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jdt.locationhub.R;
import com.jdt.locationhub.adapter.UserAdapter;
import com.jdt.locationhub.model.User;
import com.jdt.locationhub.viewmodel.MainViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PeopleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PeopleFragment extends Fragment {
    private MainViewModel mainViewModel;

    private RecyclerView userRecyclerV;
    private UserAdapter userRVAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PeopleFragment.
     */
    public static PeopleFragment newInstance() {
        PeopleFragment fragment = new PeopleFragment();
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_people, container, false);

        userRecyclerV = v.findViewById(R.id.user_RecyclerView);

        mainViewModel.getAllClientsPosition().observe(getViewLifecycleOwner(), (Observer<List<User>>) users ->
                userRVAdapter.notifyDataSetChanged());

        initRecyclerView();

        return v;
    }

    private void initRecyclerView() {
        //Setting a layout manager for the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        userRecyclerV.setLayoutManager(linearLayoutManager);

        //Initializing the adapter class and passing data to it
        userRVAdapter = new UserAdapter(requireContext(), mainViewModel.getAllClientsPosition().getValue());
        userRecyclerV.setAdapter(userRVAdapter);
    }

}