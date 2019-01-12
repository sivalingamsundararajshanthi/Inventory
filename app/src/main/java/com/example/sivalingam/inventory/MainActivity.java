package com.example.sivalingam.inventory;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sivalingam.inventory.data.InventoryAdapter;
import com.example.sivalingam.inventory.data.InventoryContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 0;

    //Floating action bar
    private FloatingActionButton fab;

    //Adapter for list view
    private InventoryAdapter inventoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Connecting with XML
        fab = findViewById(R.id.floatingActionButton);

        //Onclick listener for floating action bar
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Start the add item activity
                Intent intent = new Intent(MainActivity.this, AddItem.class);
                startActivity(intent);
            }
        });

        //Calling the loader call backs
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    /**
     *
     * @param menu
     * @return
     *
     * This overriden function is used to inflate the menu from the xml.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    /**
     *
     * @param item
     * @return
     *
     * This function is used to do some action on click of a menu item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Switch to determine which menu item was clicked
        switch (item.getItemId()){

            //Insert menu option was clicked
            case R.id.action_insert:
                //To start the add item activity
                Intent intent = new Intent(MainActivity.this, AddItem.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param i
     * @param bundle
     * @return
     *
     * This function returns a cursor loader by querying all the details from the table.
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {

        //Fetch the cursor by accessing the table
        return new CursorLoader(this, InventoryContract.CONTENT_URI, null, null, null, null);
    }

    /**
     *
     * @param loader
     * @param cursor
     *
     * This function is called when the cursor loader is finished loading.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        //Set the inventory adapter
        inventoryAdapter = new InventoryAdapter(this, cursor, 0);

        //List view
        ListView listView = findViewById(R.id.listViewId);

        //Setting an empty view
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        //Set adapter for list view
        listView.setAdapter(inventoryAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Declaring a new intent to go from main activity to add activity
                Intent intent = new Intent(MainActivity.this, AddItem.class);

                //Obtaining the uri
                Uri uri = Uri.withAppendedPath(InventoryContract.CONTENT_URI, String.valueOf(id));

                //Set the intent with uri
                intent.setData(uri);

                //start the intent
                startActivity(intent);
            }
        });

        //Swap the cursor when data changes
        inventoryAdapter.swapCursor(cursor);
    }

    /**
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        inventoryAdapter.swapCursor(null);
    }
}
