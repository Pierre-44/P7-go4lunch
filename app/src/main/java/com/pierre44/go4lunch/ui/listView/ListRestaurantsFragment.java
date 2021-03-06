package com.pierre44.go4lunch.ui.listView;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.pierre44.go4lunch.MainViewModel;
import com.pierre44.go4lunch.R;
import com.pierre44.go4lunch.databinding.ActivityMainBinding;
import com.pierre44.go4lunch.databinding.FragmentListRestaurantsBinding;
import com.pierre44.go4lunch.ui.MainActivity;

public class ListRestaurantsFragment extends Fragment {

    private ListRestaurantViewModel mListRestaurantViewModel;
    private FragmentListRestaurantsBinding binding;
    private MainActivity mainActivity;
    private ActivityMainBinding mMainBinding;
    private MainViewModel mMainViewModel;
    private boolean locationTaskSuccessful = false;
    private Location currentLocation;

    public static Fragment newInstance() {
        return new ListRestaurantsFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListRestaurantsBinding.inflate(inflater, container, false);
        mListRestaurantViewModel = new ViewModelProvider(this).get(ListRestaurantViewModel.class);

        //if (getActivity() != null) {
        //    mainActivity = ((MainActivity)getActivity());
        //    mMainBinding = mainActivity.getMainActivityBinding();
        //}

        //binding.progressBar.setVisibility(View.VISIBLE);
        initSearchBar();
        configureRecyclerView();

        return binding.getRoot();
    }
    private void initSearchBar() {
    }

    private void configureRecyclerView() {
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onPermissionsGranted() {
        if (getCurrentLocationFailed())
            Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
    }

    private boolean getCurrentLocationFailed() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mainActivity);
        //TODO : check for SuppressLint
        @SuppressLint("MissingPermission")
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnCompleteListener(getLocationTask -> {
            if (getLocationTask.isSuccessful()) {
                currentLocation = getLocationTask.getResult();
                locationTaskSuccessful = true;
            } else
                locationTaskSuccessful = false;
        });
        return !locationTaskSuccessful;
    }

    public void onPermissionsDenied() {
        Snackbar.make(binding.getRoot(), "Location unavailable", BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setTextColor(getResources().getColor(R.color.go4lunchWhite)).setDuration(5000).show();
    }
}