package com.example.inventoryapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventoryapp.data.BookContract;

public class BookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    Uri mCurrentBookUri;
    TextView mBookNameText;
    TextView mBookPriceText;
    TextView mBookQuantityText;
    TextView mSupplierNameText;
    TextView mSupplierPhoneText;

    Button mDecreaseQuantity;
    EditText mAmount;
    Button mIncreaseQuantity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        mBookNameText = findViewById(R.id.book_name);
        mBookPriceText = findViewById(R.id.book_price);
        mBookQuantityText = findViewById(R.id.book_quantity);
        mSupplierNameText = findViewById(R.id.supplier_name);
        mSupplierPhoneText = findViewById(R.id.supplier_phone);

        mDecreaseQuantity = findViewById(R.id.decrease_quantity_btn);
        mAmount = findViewById(R.id.amount_edit);
        mIncreaseQuantity = findViewById(R.id.increase_quantity_btn);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        getLoaderManager().initLoader(0, null, this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_book:
                Intent intent = new Intent(this, EditBookActivity.class);
                intent.setData(mCurrentBookUri);
                startActivity(intent);
                return true;
            case R.id.action_delete_book:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_call_supplier:
                if(!mSupplierPhoneText.getText().toString().equals(getString(R.string.unknown_phone))){
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + mSupplierPhoneText.getText().toString()));
                    startActivity(callIntent);
                    return true;
                }
                else{
                    Toast.makeText(this, "Supplier Phone is Unknown", Toast.LENGTH_SHORT).show();
                }
        }
        return super.onOptionsItemSelected(item);
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
        if(cursor.moveToFirst()){
            int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            final String quantity = cursor.getString(quantityColumnIndex);
            String supplierName = cursor.getString(supplierColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            if(supplierName.isEmpty())
                supplierName = getString(R.string.uknown_supplier);
            if(supplierPhone.isEmpty())
                supplierPhone = getString(R.string.unknown_phone);

            mBookNameText.setText(name);
            mBookPriceText.setText(price);
            mBookQuantityText.setText(quantity);
            mSupplierNameText.setText(supplierName);
            mSupplierPhoneText.setText(supplierPhone);

            mDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int amount = 1;
                    if(!mAmount.getText().toString().isEmpty()){
                        amount = Integer.parseInt(mAmount.getText().toString());
                    }
                        ContentValues values = new ContentValues();
                        values.put(BookContract.BookEntry.COLUMN_QUANTITY,Integer.parseInt(quantity) - amount);
                        getContentResolver().update(mCurrentBookUri, values, null, null);
                }
            });

            mIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int amount = 1;
                    if(!mAmount.getText().toString().isEmpty()){
                        amount = Integer.parseInt(mAmount.getText().toString());
                    }
                    ContentValues values = new ContentValues();
                    values.put(BookContract.BookEntry.COLUMN_QUANTITY,Integer.parseInt(quantity) + amount);
                    getContentResolver().update(mCurrentBookUri, values, null, null);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mBookNameText.setText("");
        mBookPriceText.setText("");
        mBookQuantityText.setText("");
        mSupplierNameText.setText("");
        mSupplierPhoneText.setText("");
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    void deleteBook(){
        getContentResolver().delete(mCurrentBookUri, null, null);
        finish();
    }

}
