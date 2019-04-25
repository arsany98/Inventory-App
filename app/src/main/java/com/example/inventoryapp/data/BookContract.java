package com.example.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BookContract {
        public final static String CONTENT_AUTHORITY = "com.example.inventoryapp";
        public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public final static String PATH_BOOKS = "books";
    public static class BookEntry implements BaseColumns{
        public final static String TABLE_NAME = "books";

        public final static String COLUMN_ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "product_name";
        public final static String COLUMN_PRICE = "price";
        public final static String COLUMN_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

        public final static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);
        public final static String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        public final static String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
    }

}
