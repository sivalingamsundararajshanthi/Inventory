package com.example.sivalingam.inventory;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sivalingam.inventory.R;
import com.example.sivalingam.inventory.data.InventoryContract;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.text.DecimalFormat;

public class AddItem extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Edit text
    private EditText nameEt, priceEt, quantityEt;

    //Image view
    private ImageView imageView;

    //Buttons
    private ImageButton takeBtn;

    //Constant for selecting or capturing image
    private static final int SELECT_PHOTO = 1;
    private static final int CAPTURE_PHOTO = 2;

    //To store the image
    private Bitmap thumbNail;

    //This Uri variable is used to recieve the data from the Main activity intent
    private Uri uri;

    //boolean to check if the item was modified
    private boolean itemHasChanged;

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     *
     * This overriden function is used to check the result of the user's response
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode){
            case CAPTURE_PHOTO:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this, "Permission granted!!!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if(intent.resolveActivity(getPackageManager()) != null){
                        startActivityForResult(intent, CAPTURE_PHOTO);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        //Enabling the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Connecting to the xml
        nameEt = findViewById(R.id.nameET);
        priceEt = findViewById(R.id.priceET);
        quantityEt = findViewById(R.id.quantityET);
        imageView = findViewById(R.id.imageViewId);
        takeBtn = findViewById(R.id.photoID);

        Intent intent = getIntent();
        uri = intent.getData();

        if(uri != null){
            setTitle("Edit item");
            Log.d("URISTRING", uri.toString());
            getLoaderManager().initLoader(0, null, this);
        } else {
            setTitle("Add an item");
            invalidateOptionsMenu();
        }

        //Onclick listener for take photo button
        takeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check for permission
                if (ContextCompat.checkSelfPermission(AddItem.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    Toast.makeText(AddItem.this, "Permission not granted", Toast.LENGTH_SHORT).show();

                    //Permission was previously denied
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddItem.this,
                            Manifest.permission.CAMERA)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        //User did not previously deny permission
                        ActivityCompat.requestPermissions(AddItem.this,
                                new String[]{Manifest.permission.CAMERA},
                                CAPTURE_PHOTO);
                    }
                } else {
                    //Permission has already been granted
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if(intent.resolveActivity(getPackageManager()) != null){
                        startActivityForResult(intent, CAPTURE_PHOTO);
                    }
                }
            }
        });

        nameEt.setOnTouchListener(mTouchListener);
        priceEt.setOnTouchListener(mTouchListener);
        quantityEt.setOnTouchListener(mTouchListener);
        takeBtn.setOnTouchListener(mTouchListener);
    }

    /**
     * This function is used to update or insert the item based on the variable uri.
     */
    private void insertPet(){
        //If the uri is not null
        if(uri != null){

            //Get name, quantity, price and image
            String name = nameEt.getText().toString();
            String quantity = quantityEt.getText().toString();
            String price = priceEt.getText().toString();
            byte[] bArray;

            //Get image fom image view and convert it to bitmap and store it in a variable
            imageView.buildDrawingCache();
            thumbNail = imageView.getDrawingCache();

            //If user took a picture
            if(thumbNail != null){
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                thumbNail.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bArray = bos.toByteArray();
            }
            //The user did not take a picture
            else {
                Bitmap bitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_action_plus, null))
                        .getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                bArray = stream.toByteArray();
            }

            //If user did not enter all the values
            if(name.equals("") || quantity.equals("") || price.equals("")){
                Toast.makeText(AddItem.this, "Please enter name, quantity, price", Toast.LENGTH_SHORT)
                        .show();
            }
            //The user entered all the values
            else {
                Integer quantityInt = Integer.parseInt(quantity);
                Integer priceInt = Integer.parseInt(price);

                //Make content values
                ContentValues contentValues = new ContentValues();
                contentValues.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME, name);
                contentValues.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantityInt);
                contentValues.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE, priceInt);
                contentValues.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PHOTO, bArray);

                //Update the selected value
                int mUri = getContentResolver().update(uri, contentValues, null, null);

                if(mUri != 0){
                    //Insertion successful
                    Toast.makeText(AddItem.this, "Update successful", Toast.LENGTH_SHORT).show();
                    //Close the activity
                    finish();
                }
                else
                    Toast.makeText(AddItem.this, "Update unsuccessful", Toast.LENGTH_SHORT).show();
            }
        }
        //If the uri is null
        else {
            //Get name, quantity, price and image
            String name = nameEt.getText().toString();
            String quantity = quantityEt.getText().toString();
            String price = priceEt.getText().toString();
            byte[] bArray;

            //If user took a picture
            if(thumbNail != null){
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                thumbNail.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bArray = bos.toByteArray();
            }

            //The user did not take a picture
            else {
                Bitmap bitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_action_plus, null))
                        .getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                bArray = stream.toByteArray();
            }

            //If fields are not empty
            if(name.equals("") || quantity.equals("") || price.equals("")){
                Toast.makeText(AddItem.this, "Please enter name, quantity, price", Toast.LENGTH_SHORT)
                        .show();
            }

            //All the fields are entered
            else {
                Integer quantityInt = Integer.parseInt(quantity);
                Integer priceInt = Integer.parseInt(price);

                //Create content value
                ContentValues contentValues = new ContentValues();
                contentValues.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME, name);
                contentValues.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantityInt);
                contentValues.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE, priceInt);
                contentValues.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PHOTO, bArray);

                //Insert the data into the database
                Uri uri = getContentResolver().insert(InventoryContract.CONTENT_URI, contentValues);

                if(uri != null){

                    //Insertion was successful
                    Toast.makeText(AddItem.this, "Insertion successful", Toast.LENGTH_SHORT).show();

                    //Finish the activity
                    finish();
                }

                //Insertion was not successful
                else
                    Toast.makeText(AddItem.this, "Insertion unsuccessful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     *
     * This overriden function is used to recieve the image back from the camera intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("RESULT", "RESULT");
//        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAPTURE_PHOTO && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            thumbNail = (Bitmap) bundle.get("data");
            imageView.setImageBitmap(thumbNail);
        }
    }

    /**
     *
     * @param menu
     * @return boolean
     *
     * This overriden function is used to hide delete.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if(uri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }

        return true;
    }

    /**
     *
     * @param menu
     * @return
     *
     * This is used to inflate the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     *
     * @param item
     * @return
     *
     * This is used to handle the options selection
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_save:
                insertPet();
                break;

            case R.id.action_delete:
                showAlertDialog();
                break;

            case R.id.home:

                if(!itemHasChanged){
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(AddItem.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param discardButtonClickListener
     *
     * This function is used to display an alert box which lets the user to keep editing or discard
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.back_message);
        builder.setPositiveButton(R.string.cancel, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null)
                    dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     *
     * @param id
     * @param args
     * @return
     *
     * This is used to initialize a cursor loader and return it
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //Projection which has the id, name, quantity, price, photo
        String[] projection = {InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PHOTO

        };

        //selection which has the id
        String selection = InventoryContract.InventoryEntry._ID;

        //selection args
        String[] selectionArgs = {String.valueOf(ContentUris.parseId(uri))};

        //return the cursor
        return new CursorLoader(this, uri, projection, selection, selectionArgs, null);
    }

    /**
     *
     * @param loader
     * @param cursor
     *
     * This is used to access the name, quantity, price and image from the database and display them.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE));
            byte[] imageBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PHOTO));

            nameEt.setText(name);
            priceEt.setText(String.valueOf(price));
            quantityEt.setText(String.valueOf(quantity));

            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * This function dispays an alert dialog which asks confirmation from the user before deleting.
     * If user clicks delete the item will be deleted
     * else the dialog is dismissed
     */
    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem();
                        finish();
                    }
                });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * This overriden function checks if the user has made any changes, if so an alert dialog is displayed
     * else the activity is closed.
     */
    @Override
    public void onBackPressed() {
        if(!itemHasChanged){
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * This is an onTouchListener to indicate changes to the edit text.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            itemHasChanged = true;
            return false;
        }
    };

    /**
     * This function is used to delete data from the database.
     */
    private void deleteItem(){
        int rows = getContentResolver().delete(uri, null, null);

        if(rows != 0)
            Toast.makeText(this, "Deletion successful", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Deletion unsuccessful", Toast.LENGTH_SHORT).show();
    }
}
