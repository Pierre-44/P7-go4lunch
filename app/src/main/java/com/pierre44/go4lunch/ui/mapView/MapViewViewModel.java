package com.pierre44.go4lunch.ui.mapView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapViewViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MapViewViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is mapview fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}