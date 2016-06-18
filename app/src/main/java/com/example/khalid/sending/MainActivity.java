package com.example.khalid.sending;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showMap(View view) {
        //Build the intent
        Uri location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

        //Verify if it resolves
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
        boolean intentIsSafe = activities.size() > 0;

        //Start an activity if its safe
        if (intentIsSafe) {
            startActivity(mapIntent);
        }
    }

    public void showMapChooser(View view) {
        //Build the intent
        Uri location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

        Intent chooser = Intent.createChooser(mapIntent, getResources().getString(R.string.show_map_chooser));

        //Verify intent will resolve to at least one activity
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

}
