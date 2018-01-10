package com.example.hanan.movielistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements TwoPaneListener {
   static boolean IsTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // moviesFragment main= new moviesFragment();
        //   main.setListener(this);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new moviesFragment())
                    .commit();

            if (null != findViewById(R.id.fragment_detail)) {
                IsTwoPane = true;
            }
        }
    }

    @Override
    public void setSelectedData(String data) {
        Toast.makeText(getApplicationContext(), " pane = "+ IsTwoPane, Toast.LENGTH_SHORT).show();
        if (!IsTwoPane) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, data);

            startActivity(intent);
        } else {
           DetailActivity.DetailFragment Dfragment = new DetailActivity.DetailFragment();
            Bundle extras = new Bundle();
            extras.putString(Intent.EXTRA_TEXT, data);
            Dfragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_detail, Dfragment).commit();
        }
    }


}
