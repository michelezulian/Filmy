package com.michelezulian.example.filmy_projectwork;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.michelezulian.example.filmy_projectwork.fragment.HomeFragment;
import com.michelezulian.example.filmy_projectwork.fragment.SeenFragment;
import com.michelezulian.example.filmy_projectwork.fragment.WishlistFragment;
import com.michelezulian.example.filmy_projectwork.dialog.WatchedDialog;

public class MainActivity extends AppCompatActivity implements WatchedDialog.IwatchedDialog{


    //CONSTANTS
    private static final String TAG_HOME ="HomeFragment" ;
    private static final String TAG_SEEN ="SeenFragment" ;
    private static final String TAG_WISHLIST ="WishlistFragment" ;

    //Layout elements
    BottomNavigationView bottomNavigationView;

    //fragments declarations
    HomeFragment homeFragment;
    SeenFragment seenFragment;
    WishlistFragment wishlistFragment;
    int selectedTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(savedInstanceState != null){
            selectedTab = savedInstanceState.getInt("tab", 0);
        }

        //binding layouts elements
        bottomNavigationView = findViewById(R.id.bottomMenu);

        //variables instantiation
        homeFragment = new HomeFragment();
        homeFragment.setRetainInstance(true);
        wishlistFragment = new WishlistFragment();
        wishlistFragment.setRetainInstance(true);
        seenFragment = new SeenFragment();
        seenFragment.setRetainInstance(true);

        //navigation menu
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.itemHome:
                        selectedTab = 0;
                        break;
                    case R.id.itemSeen:
                        selectedTab = 1;
                        break;
                    case R.id.itemUser:
                        selectedTab = 2;
                        break;
                }

                loadTab();

                return true;
            }
        });

        loadTab();

    }

    @Override
    public void onResponse(boolean aResponse, long aId, String TAG) {
        if(TAG == TAG_HOME){
            homeFragment.onResponseDialog(aResponse, aId);
        }
        if(TAG == TAG_SEEN){
            seenFragment.onResponseDialog(aResponse, aId);
        }
        if(TAG == TAG_WISHLIST){
            wishlistFragment.onResponseDialog(aResponse, aId);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", selectedTab);
    }

    //loading switch of bottom navigation
    private void loadTab(){
        switch (selectedTab){
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, homeFragment, TAG_HOME).commit();
                //Toast.makeText(MainActivity.this, "Movies", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, seenFragment, TAG_SEEN).commit();
                //Toast.makeText(MainActivity.this, "Already Seen", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, wishlistFragment, TAG_WISHLIST).commit();
                //Toast.makeText(MainActivity.this, "Nearby", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
