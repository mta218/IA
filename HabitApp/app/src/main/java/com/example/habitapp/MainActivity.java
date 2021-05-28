package com.example.habitapp;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

/**
 * This is the MainActivity, has the AppBar used to hold the fragments
 *
 * @author Maximilian Ta
 * @version 0.1
 */
public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //System.out.println("email haa: " + user.getEmail());
        //System.out.println("uid be like? " + user.getUid());

    }

    /**
     * Returns the user to the SignInActivity if they are not logged in.
     *
     */
    @Override
    protected void onResume() {
        if(mAuth.getCurrentUser() == null){
            finish();
        }
        super.onResume();
    }
}