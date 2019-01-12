package com.example.sivalingam.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {

    //String for CONTENT_AUTHORITY which will be used to form the URI
    public static final String CONTENT_AUTHORITY = "com.example.sivalingam.inventory";

    //The base URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //To specify the path for the table
    public static final String PATH_INVENTORY = "inventory";

    //The content uri which will be used to fetch all the data from the table
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

    //Private constructor
    private InventoryContract(){}

    //This inner class is used to represent the inventory table
    public static final class InventoryEntry implements BaseColumns{

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY
                + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_INVENTORY;

        //This is the table name
        public static final String TABLE_NAME = "inventory";

        //Table columns
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_INVENTORY_NAME = "Name";
        public static final String COLUMN_INVENTORY_QUANTITY = "Quantity";
        public static final String COLUMN_INVENTORY_PRICE = "Price";
        public static final String COLUMN_INVENTORY_PHOTO = "Photo";
    }
}
