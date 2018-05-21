package com.lay.htoo.tinykeeper.data;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BaseColumns;

import java.io.ByteArrayOutputStream;

/**
 * This class provides database constants to define schema and easily update later. Removes
 * spelling errors when generating sql commands.
 */

public final class InventoryContract {

    public static final String CONTENT_AUTHORITY = "com.lay.htoo.tinykeeper";

    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class ItemEntry implements BaseColumns {

        public static final String PATH_ITEMS = ItemEntry.TABLE_NAME;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_ITEMS);

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final String TABLE_NAME = "items";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BUY_PRICE = "cost_price";
        public static final String COLUMN_SALE_PRICE = "sale_price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_IS_DELETED = "is_deleted";
        public static final String COLUMN_DATE = "updated_date";
        public static final String COLUMN_PICTURE = "picture";
        public static final String COLUMN_SUPPLIER_ID = "supplier_id";

        public static byte[] getBytesArray(Bitmap bitmap) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
            return outputStream.toByteArray();
        }

        public static Bitmap getBitmap(byte[] bytes) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
    }

    public static final class SupplierEntry implements BaseColumns {

        public static final String PATH_SUPPLIERS = SupplierEntry.TABLE_NAME;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_SUPPLIERS);

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;

        public static final String TABLE_NAME = "suppliers";
        public static final String COLUMN_ID = "supplier_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE= "phone";
        public static final String COLUMN_DATE = "updated_date";
    }

    public static final class PurchasesEntry implements BaseColumns {

        public static final String PATH_PURCHASES = PurchasesEntry.TABLE_NAME;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_PURCHASES);

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PURCHASES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PURCHASES;

        public static final String TABLE_NAME = "purchases";
        public static final String COLUMN_ID = "purchase_id";
        public static final String COLUMN_ITEM_ID = "item_id";
        public static final String COLUMN_SUPPLIER_ID = "supplier_id";
        public static final String COLUMN_DATE = "purchase_date";
        public static final String COLUMN_PRICE= "purchase_price";
        public static final String COLUMN_QUANTITY = "quantity";
    }

    public static final class SalesEntry implements BaseColumns {

        public static final String PATH_SALES = SalesEntry.TABLE_NAME;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_SALES);

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SALES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SALES;

        public static final String TABLE_NAME = "sales";
        public static final String COLUMN_ID = "sale_id";
        public static final String COLUMN_ITEM_ID = "item_id";
        public static final String COLUMN_PRICE= "sale_price";
        public static final String COLUMN_DATE = "sale_date";
        public static final String COLUMN_QUANTITY = "quantity";
    }

    public static final class PurchaseReport implements BaseColumns {

        public static final String PATH_PURCHASE_REPORT = PurchaseReport.TABLE_NAME;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_PURCHASE_REPORT);

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PURCHASE_REPORT;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PURCHASE_REPORT;

        public static final String TABLE_NAME = "view_purchases_report";
        public static final String COLUMN_ITEM_NAME = ItemEntry.TABLE_NAME+"."+ItemEntry.COLUMN_NAME;
        public static final String COLUMN_QTY = PurchasesEntry.TABLE_NAME+"."+PurchasesEntry.COLUMN_QUANTITY;
        public static final String COLUMN_PRICE= PurchasesEntry.TABLE_NAME+"."+PurchasesEntry.COLUMN_PRICE;
        public static final String COLUMN_DATE = PurchasesEntry.TABLE_NAME+"."+PurchasesEntry.COLUMN_DATE;

    }

    public static final class SalReport implements BaseColumns {

        public static final String PATH_SALE_REPORT = SalReport.TABLE_NAME;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_SALE_REPORT);

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SALE_REPORT;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SALE_REPORT;

        public static final String TABLE_NAME = "view_sales_report";
        public static final String COLUMN_ITEM_NAME = ItemEntry.TABLE_NAME+"."+ItemEntry.COLUMN_NAME;
        public static final String COLUMN_QTY = SalesEntry.TABLE_NAME+"."+SalesEntry.COLUMN_QUANTITY;
        public static final String COLUMN_PRICE= SalesEntry.TABLE_NAME+"."+SalesEntry.COLUMN_PRICE;
        public static final String COLUMN_DATE = SalesEntry.TABLE_NAME+"."+SalesEntry.COLUMN_DATE;


    }
}
