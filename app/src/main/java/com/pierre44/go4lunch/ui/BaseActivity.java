package com.pierre44.go4lunch.ui;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pierre44.go4lunch.R;
import com.pierre44.go4lunch.di.Injection;
import com.pierre44.go4lunch.di.ViewModelFactory;
import com.pierre44.go4lunch.ui.Auth.AuthActivity;
import com.pierre44.go4lunch.ui.listView.ListRestaurantsFragment;
import com.pierre44.go4lunch.ui.mapView.MapViewFragment;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by pmeignen on 21/10/2021.
 * Base Activity class that allow to manage all the common code for the activities
 *
 * @param <T> Should be the type of the viewBinding of your activity see more <a href="https://developer.android.com/topic/libraries/view-binding"> here </a>
 */
public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    public T viewModel;
    private MapViewFragment mMapViewFragment;
    private ListRestaurantsFragment mListRestaurantsFragment;
    private LocationManager locationManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        configureViewModel();
        setContentView(this.getLayout());

    }

    protected void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        viewModel = (T) new ViewModelProvider(this, viewModelFactory).get(getViewModelClass());
    }

    protected abstract Class getViewModelClass();

    protected abstract View getLayout();

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public T getViewModel() {
        return viewModel;
    }

    protected void backToLoginPage() {
        finishAffinity();
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }

    // --------------------
    // ERROR
    // --------------------
    protected OnFailureListener onFailureListener() {
        return e -> Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public boolean requestLocationAccess() {
        mMapViewFragment = (MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.map_view_fragment);

        boolean locationAvailable = false;
        String PERMS = ACCESS_FINE_LOCATION;
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this,
                    "This app requires access to your location",
                    LOCATION_PERMISSION_REQUEST_CODE, PERMS);
        } else if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            new AlertDialog.Builder(this)
                    .setMessage("Location is not enabled")
                    .setPositiveButton("Authorize shared location", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BaseActivity.this.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_PERMISSION_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else
            locationAvailable = true;
        return locationAvailable;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE)
            onLocationAccessGranted();
    }

    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (mMapViewFragment != null && mMapViewFragment.isVisible())
            mMapViewFragment.onPermissionsDenied();
        else if (mListRestaurantsFragment != null && mListRestaurantsFragment.isVisible())
            mListRestaurantsFragment.onPermissionsDenied();
    }

    private void onLocationAccessGranted() {
        if (mMapViewFragment != null && mMapViewFragment.isVisible())
            mMapViewFragment.onPermissionsGranted();
        else if (mListRestaurantsFragment != null && mListRestaurantsFragment.isVisible())
            mListRestaurantsFragment.onPermissionsGranted();
    }
}
