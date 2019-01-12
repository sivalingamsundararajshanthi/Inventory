package com.example.sivalingam.inventory.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.sivalingam.inventory.R;

public class InventoryAdapter extends CursorAdapter {

    //Constructor
    public InventoryAdapter(Context context, Cursor c, int flags) {
        //Call to super class constructor
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Inflating the list view
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Textview for name, price, quantity
        TextView nameTv = view.findViewById(R.id.name);
        TextView priceTv = view.findViewById(R.id.price);
        TextView quantityTv = view.findViewById(R.id.quantity);

        //Retrieving the values from the cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME));
        Integer price = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE));
        Integer quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY));

        //Setting the values in the textview
        nameTv.setText(name);
        priceTv.setText(String.valueOf(price));
        quantityTv.setText(String.valueOf(quantity));
    }
}
