package com.example.inventoryapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventoryapp.data.BookContract;

public class BookCursorAdapter extends CursorAdapter {
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        CardView bookCardView = view.findViewById(R.id.book_card_view);
        TextView bookNameText = view.findViewById(R.id.book_name);
        TextView bookPriceText = view.findViewById(R.id.book_price);
        TextView bookQuantityText = view.findViewById(R.id.book_quantity);
        Button saleBtn = view.findViewById(R.id.sale_btn);

        final int bookId = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_ID));
        String bookName = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME));
        String bookPrice = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRICE));
        final int bookQuantity = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_QUANTITY));
        bookCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BookActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, bookId);
                intent.setData(currentBookUri);
                context.startActivity(intent);
            }
        });
        saleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(BookContract.BookEntry.COLUMN_QUANTITY, bookQuantity-1);
                Uri currentBookUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, bookId);
                context.getContentResolver().update(currentBookUri, values, null, null);
            }
        });

        bookNameText.setText(bookName);
        bookPriceText.setText(bookPrice);
        bookQuantityText.setText(String.valueOf(bookQuantity));
    }
}
