package com.example.sivalingam.inventory.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class InventoryDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = InventoryDBHelper.class.getSimpleName();

    //DB name
    private static final String DATABASE_NAME = "inventory.db";

    //DB version
    private static final int DATABASE_VERSION = 1;

    //Constructor
    public InventoryDBHelper(Context context) {
        //Calling the super class Constructor
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //This overriden method is used to create the table

    /**
     *
     * @param db
     *
     * The columns are
     * ID       - Primary key Integer not null
     * Name     - Text Not null
     * Price    - Integer not null
     * Quantity - Integer not null
     * Photo    - BLOB can be null
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + InventoryContract.InventoryEntry.TABLE_NAME + " ("
                + InventoryContract.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME + " TEXT NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE + " INTEGER NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY + " INTEGER NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_INVENTORY_PHOTO + " BLOB);";

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }























}
