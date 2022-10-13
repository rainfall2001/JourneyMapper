package com.example.mapper;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mapper.activity.MapsActivity;
import com.example.mapper.location.GPS;
import com.example.mapper.location.LocationUpdateService;
import com.example.mapper.model.CommuteDatabase;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mapper.databinding.ActivityBaseBinding;

/**
 * This class is responsible for creating the side navigation drawer, checking location permissions and checking
 * GPS is enabled. The static method of isMyServieRunning is in this class as everytime the user opens the application
 * this activity will launch.
 * @author Rhane Mercado
 */
public class BaseActivity extends AppCompatActivity {

    public static CommuteDatabase CD;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityBaseBinding binding;
    private int backgroundCode = 1;
    private int fineLocationCode = 2;

    public static String tag = "HelloWorld";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //the commute database will be used by multiple classes, so instantiate first
        CD = new CommuteDatabase(this);

        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarBase.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_address, R.id.nav_schedule, R.id.nav_one_trip, R.id.nav_upload, R.id.nav_credits)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_base);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //ask for permission
            //the background locations as the phone needs to track the location in the background
            checkEnabledPermissions(Manifest.permission.ACCESS_BACKGROUND_LOCATION, backgroundCode, getString(R.string.permission_background));
            //fine location as we need to track where the user is going
            checkEnabledPermissions(Manifest.permission.ACCESS_FINE_LOCATION, fineLocationCode, getString(R.string.permission_fine));

            //prompt the user to turn on the GPS on the device if it is not already turned on
            GPS.getInstance().turnOnGPS(this);

            if(isMyServiceRunning(LocationUpdateService.class, getApplicationContext())){
                //the service is running, take the user to the maps activity
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
            }
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_base);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    /**
     * This method prompts the user to accept the location permissions.
     * It will show an alert dialog outlining why the permission is needed and asking the user to
     * accept.
     * @param permission The permission that is to be requested
     * @param requestCode The uniqueRequest code for that permissions
     * @param reason The reason why the permission is needed
     */
    private void checkEnabledPermissions(String permission, int requestCode, String reason){
        if(ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED){
            //permission has not been granted
            //inform the user why the permission is needed
            ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
            new AlertDialog.Builder(this)
                    .setTitle("Permission")
                    .setMessage("This permission is needed " + reason)
                    .setPositiveButton("okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //directly ask for the permission
                            ActivityCompat.requestPermissions(BaseActivity.this, new String[] {permission}, requestCode);
                        }
                    })
                    .create().show();
        }
    }


    /**
     * This method is called to prompt the user to accept the permission on their device.
     * It will show the android prompts which the user can chose to accept or decline.
     * The outcome will be shown to the user via a toast.
     * @param requestCode The code for the permission
     * @param permissions The permission that will be asked
     * @param grantResults The result of the permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //make sure that the user has a choice of which permission they can want on or off
        if(requestCode == backgroundCode) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(BaseActivity.this, "Background location permission: GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(BaseActivity.this, "Background location permission: DENIED", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == fineLocationCode){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(BaseActivity.this, "Fine location permission: GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(BaseActivity.this, "Fine location permission: DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This method checks if the service class passed in is running
     * @param serviceClass Class
     * @param c Context
     * @return boolean
     */
    public static boolean isMyServiceRunning(Class<?> serviceClass, Context c) {
        ActivityManager manager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}