package com.example.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import com.example.inventoryapp.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {
    public static final String LOG_TAG = BookProvider.class.getSimpleName();
    private static final int BOOKS = 100;
    private static final int BOOK_ID = 101;
    BookDbHelper mDbHelper;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY,BookContract.PATH_BOOKS,BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY,BookContract.PATH_BOOKS + "/#",BOOK_ID);
    }
    @Override
    public boolean onCreate() {
         mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor;
        switch (match)
        {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, null, null, null, null, null, null);
                break;
            case BOOK_ID:
                selection = BookEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int match = sUriMatcher.match(uri);
        switch (match){
            case BOOKS:
                return insertBook(uri,values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }
    private Uri insertBook(Uri uri, ContentValues values) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long newRowId = database.insert(BookEntry.TABLE_NAME, null, values);
        if(newRowId == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri,newRowId);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case BOOK_ID:
                selection = BookEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        if(values.containsKey(BookEntry.COLUMN_QUANTITY)){
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                Toast.makeText(getContext(), "Not Enough Items", Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
        if(values.size() == 0){
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(BookEntry.TABLE_NAME,values,selection,selectionArgs);
        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case BOOKS:
                return deleteBook(uri,selection,selectionArgs);
            case BOOK_ID:
                selection = BookEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteBook(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    int deleteBook(Uri uri, String selection, String[] selectionArgs)
    {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);

        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

}
