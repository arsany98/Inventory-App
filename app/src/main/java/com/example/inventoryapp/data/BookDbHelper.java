package com.example.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.inventoryapp.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    public final static String LOG_TAG = BookDbHelper.class.getSimpleName();
    public final static String DATABASE_NAME = "books.db";
    public final static int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_PRICE + " MONEY NOT NULL, "
                + BookEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BookEntry.COLUMN_SUPPLIER_NAME + " TEXT, "
                + BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT);";
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
