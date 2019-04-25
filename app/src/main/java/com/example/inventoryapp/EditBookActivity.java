package com.example.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.Loader;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.inventoryapp.data.BookContract;

public class EditBookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText mBookNameEdit;
    EditText mBookPriceEdit;
    EditText mBookQuantityEdit;
    EditText mSupplierNameEdit;
    EditText mSupplierPhoneEdit;
    Uri mCurrentBookUri;
    boolean mBookDataHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        mBookNameEdit = findViewById(R.id.edit_book_name);
        mBookPriceEdit = findViewById(R.id.edit_book_price);
        mBookQuantityEdit = findViewById(R.id.edit_book_quantity);
        mSupplierNameEdit = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneEdit = findViewById(R.id.edit_supplier_phone);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        if(mCurrentBookUri == null){
            setTitle(getString(R.string.activity_title_insert));
        }
        else {
            setTitle(getString(R.string.activity_title_edit));
            getLoaderManager().initLoader(0, null, this);
        }
        mBookNameEdit.setOnTouchListener(mTouchListener);
        mBookPriceEdit.setOnTouchListener(mTouchListener);
        mBookQuantityEdit.setOnTouchListener(mTouchListener);
        mSupplierNameEdit.setOnTouchListener(mTouchListener);
        mSupplierPhoneEdit.setOnTouchListener(mTouchListener);
    }
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mBookDataHasChanged = true;
            return false;
        }
    };

    boolean saveBook(){

        String bookName = mBookNameEdit.getText().toString().trim();
        String bookPrice = mBookPriceEdit.getText().toString().trim();
        String bookQuantity = mBookQuantityEdit.getText().toString().trim();
        String supplierName = mSupplierNameEdit.getText().toString().trim();
        String supplierPhone = mSupplierPhoneEdit.getText().toString().trim();

        if(mCurrentBookUri == null && TextUtils.isEmpty(bookName)
                && TextUtils.isEmpty(bookPrice)&& TextUtils.isEmpty(bookQuantity)
                && TextUtils.isEmpty(supplierName) && TextUtils.isEmpty(supplierPhone))
        {
            return true;
        }
        Book book = new Book(bookName, bookPrice, bookQuantity, supplierName, supplierPhone);

        if(book.getProductName().isEmpty()){
            Toast.makeText(this, "Book requires a name", Toast.LENGTH_SHORT).show();
            mBookNameEdit.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
            return false;
        }
        if (book.getPrice() == null) {
            Toast.makeText(this, "Book requires a price", Toast.LENGTH_SHORT).show();
            mBookPriceEdit.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
            return false;
        }
        if (book.getQuantity() < 0) {
            Toast.makeText(this, "Book requires valid quantity\"", Toast.LENGTH_SHORT).show();
            mBookQuantityEdit.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
            return false;
        }
        ContentValues bookData = new ContentValues();
        bookData.put(BookContract.BookEntry.COLUMN_PRODUCT_NAME, book.getProductName());
        bookData.put(BookContract.BookEntry.COLUMN_PRICE, book.getPrice());
        bookData.put(BookContract.BookEntry.COLUMN_QUANTITY, book.getQuantity());
        bookData.put(BookContract.BookEntry.COLUMN_SUPPLIER_NAME, book.getSupplierName());
        bookData.put(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, book.getSupplierPhoneNumber());


        if(mCurrentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, bookData);
            if(newUri != null){
                Toast.makeText(this, "Inserted Successfully", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        else{
            int rowsUpdated = getContentResolver().update(mCurrentBookUri, bookData, null, null);
            if(rowsUpdated != 0){
                Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_book:
                if(saveBook()){
                    finish();
                }
                return true;
            case android.R.id.home:
                if(!mBookDataHasChanged){
                    finish();
                }
                else {
                    showUnsavedChangesDialog();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(!mBookDataHasChanged){
            super.onBackPressed();
            return;
        }
        showUnsavedChangesDialog();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {
                BookContract.BookEntry.COLUMN_ID,
                BookContract.BookEntry.COLUMN_PRODUCT_NAME,
                BookContract.BookEntry.COLUMN_PRICE,
                BookContract.BookEntry.COLUMN_QUANTITY,
                BookContract.BookEntry.COLUMN_SUPPLIER_NAME,
                BookContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };
        return new CursorLoader(this, mCurrentBookUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            String supplierName = cursor.getString(supplierColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            mBookNameEdit.setText(name);
            mBookPriceEdit.setText(price);
            mBookQuantityEdit.setText(quantity);
            mSupplierNameEdit.setText(supplierName);
            mSupplierPhoneEdit.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mBookNameEdit.setText("");
        mBookPriceEdit.setText("");
        mBookQuantityEdit.setText("");
        mSupplierNameEdit.setText("");
        mSupplierPhoneEdit.setText("");
    }

    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick (DialogInterface dialogInterface,int i){
                finish();
            }
        });
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
