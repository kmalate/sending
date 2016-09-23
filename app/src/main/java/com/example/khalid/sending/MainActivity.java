package com.example.khalid.sending;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.ContactsContract;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int PICK_CONTACT_REQUEST = 1;
    static final int FILES_REQUEST = 2;
    static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private ShareActionProvider mShareActionProvider;

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

    public void pickContact(View view) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    public void actionSend(View view) {
        Intent actionIntent = new Intent(Intent.ACTION_SEND);
        actionIntent.putExtra(Intent.EXTRA_TEXT, "Hello Khalid");
        actionIntent.setType("text/plain");
        if (actionIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(actionIntent, 2);
        }
    }

    public void requestPermission(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            //should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void requestFile(View view) {
        Intent requestFileIntent = new Intent(Intent.ACTION_PICK);
        requestFileIntent.setType("image/jpg");
        startActivityForResult(requestFileIntent, FILES_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check which request we are responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode ==  RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                cursor.moveToFirst();
                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);
                TextView txtPhoneNumber = (TextView)findViewById(R.id.txtPhoneNumber);
                txtPhoneNumber.setText(number);
                // Do something with the phone number
            }
        }

        if (requestCode == FILES_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Get the file's content URI from the incoming Intent
                Uri returnUri = data.getData();
                ParcelFileDescriptor mInputPFD;
                  /*
             * Try to open the file for "read" access using the
             * returned URI. If the file isn't found, write to the
             * error log and return.
             */
                try {
                    /*
                 * Get the content resolver instance for this context, and use it
                 * to get a ParcelFileDescriptor for the file.
                 */
                    mInputPFD = getContentResolver().openFileDescriptor(returnUri, "r");
                    String mimeType = getContentResolver().getType(returnUri);

                    Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                     /*
                 * Get the column indexes of the data in the Cursor,
                 * move to the first row in the Cursor, get the data,
                 * and display it.
                 */
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();
                    TextView nameView = (TextView)findViewById(R.id.nameView);
                    TextView sizeView = (TextView)findViewById(R.id.sizeView);
                    nameView.setText(returnCursor.getString(nameIndex));
                    sizeView.setText(returnCursor.getString(sizeIndex));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e("MainActivity", "File not found");
                    return;
                }

                //Get the file descriptor for the file
                FileDescriptor fd = mInputPFD.getFileDescriptor();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu resources
        getMenuInflater().inflate(R.menu.share_menu, menu);

        //Locate menu item with shareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        //Fetch and store ShareActionProvider
        mShareActionProvider = new ShareActionProvider(this);
        MenuItemCompat.setActionProvider(item, mShareActionProvider);

        Uri location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            setShareIntent(mapIntent);
        }

        //Return true to display menu
        return  true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
}
