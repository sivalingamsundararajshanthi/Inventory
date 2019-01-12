package com.example.sivalingam.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class InventoryProvider extends ContentProvider {

    //Constants to indicate to fetch all the data from the table or just a piece of data
    private static final int INVENTORY = 100;
    private static final int INVENTORY_ID = 101;

    //URI matcher
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //Static block to specify the type of request
    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#",
                INVENTORY_ID);
    }

    private InventoryDBHelper inventoryDBHelper;

    @Override
    public boolean onCreate() {
        inventoryDBHelper = new InventoryDBHelper(getContext());
        return true;
    }

    /**
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return cursor
     *
     * This overriden function is used to fetch the table for some data.
     *
     * There are two options for querying:
     * 1. Fetch a single record.
     * 2. Fetch all the records.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = inventoryDBHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match){
            case INVENTORY:
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case INVENTORY_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case INVENTORY:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;

            case INVENTORY_ID:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     *
     * @param uri
     * @param values
     * @return uri
     *
     * This overriden function is used to insert data into the database.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = sUriMatcher.match(uri);

        switch (match){
            case INVENTORY:
                return insertItem(uri, values);


            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }
    }

    /**
     *
     * @param uri
     * @param contentValues
     * @return uri
     *
     * This private function is used to insert data into the database.
     */
    private Uri insertItem(Uri uri, ContentValues contentValues){
        //Get a writable database to insert
        SQLiteDatabase db = inventoryDBHelper.getWritableDatabase();

        //Extracting the column values from the content values
        String name = contentValues.getAsString(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
        Integer quantity = contentValues.getAsInteger(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY);
        Integer price = contentValues.getAsInteger(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);

        //If some of the columns are null dont do insertion and return null
//        if(name == null || quantity == null || price == null){
////            Toast.makeText(getContext(), "Insertion failed at provider", Toast.LENGTH_SHORT).show();
////            return null;
////        } else{
////            //Do the insertion
////
////
////        }

        long newRowId = db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, contentValues);

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, newRowId);
    }

    /**
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return int
     *
     * This overriden function is used to delete data from the database.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted;

        SQLiteDatabase database = inventoryDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);


        switch (match){
            case INVENTORY:
                rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);

                if(rowsDeleted != 0)
                    getContext().getContentResolver().notifyChange(uri, null);

                return rowsDeleted;

            case INVENTORY_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);

                if(rowsDeleted != 0)
                    getContext().getContentResolver().notifyChange(uri, null);

                return rowsDeleted;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     *
     * @param uri
     * @param contentValues
     * @param selection
     * @param selectionArgs
     * @return int - number of rows updated
     *
     * This overriden function is used to update a record or multiple records in the database
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,@Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case INVENTORY:
                return updateItem(uri, contentValues, selection, selectionArgs);

            case INVENTORY_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};


                return updateItem(uri, contentValues, selection, selectionArgs);
        }
        return 0;
    }

    /**
     *
     * @param uri
     * @param contentValues
     * @param selection
     * @param selectionArgs
     * @return int
     *
     * This private function is used to update the record.
     */
    private int updateItem(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        SQLiteDatabase database = inventoryDBHelper.getWritableDatabase();

        int numRowsUpdated = database.update(InventoryContract.InventoryEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if(numRowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return numRowsUpdated;
    }
}
