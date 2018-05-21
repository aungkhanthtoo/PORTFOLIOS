package com.lay.htoo.tinykeeper.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lay.htoo.tinykeeper.data.InventoryContract.*;


/**
 * This class helps to create database firstly and then open connection.
 * Ease of updating database schema and consequently version increases.
 */

public class InventoryDBHelper extends SQLiteOpenHelper {
    private static final String TAG = InventoryDBHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 6;

    public InventoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String sqlCreateItems = "CREATE TABLE " + ItemEntry.TABLE_NAME + "( "
                + ItemEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_BUY_PRICE + " INTEGER NOT NULL, "
                + ItemEntry.COLUMN_SALE_PRICE + " INTEGER NOT NULL, "
                + ItemEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ItemEntry.COLUMN_IS_DELETED + " INTEGER DEFAULT 0, "
                + ItemEntry.COLUMN_DATE + " TEXT, "
                + ItemEntry.COLUMN_SUPPLIER_ID + " INTEGER NOT NULL, "
                + ItemEntry.COLUMN_PICTURE + " BLOB, "
                + "FOREIGN KEY(" + ItemEntry.COLUMN_SUPPLIER_ID + ") REFERENCES " + SupplierEntry.TABLE_NAME + "(" + SupplierEntry.COLUMN_ID + "));";

        final String sqlCreateSuppliers = "CREATE TABLE " + SupplierEntry.TABLE_NAME + "( "
                + SupplierEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SupplierEntry.COLUMN_NAME + " TEXT, "
                + SupplierEntry.COLUMN_PHONE + " TEXT, "
                + SupplierEntry.COLUMN_DATE + " TEXT);";

        final String sqlCreatePurchases = "CREATE TABLE " + PurchasesEntry.TABLE_NAME + "( "
                + PurchasesEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PurchasesEntry.COLUMN_ITEM_ID + " INTEGER NOT NULL, "
                + PurchasesEntry.COLUMN_SUPPLIER_ID + " INTEGER NOT NULL, "
                + PurchasesEntry.COLUMN_DATE + " TEXT, "
                + PurchasesEntry.COLUMN_PRICE + " INTEGER, "
                + PurchasesEntry.COLUMN_QUANTITY + " INTEGER, "
                + "FOREIGN KEY(" + PurchasesEntry.COLUMN_ITEM_ID + ") REFERENCES " + ItemEntry.TABLE_NAME + "(" + ItemEntry.COLUMN_ID + "), "
                + "FOREIGN KEY(" + PurchasesEntry.COLUMN_SUPPLIER_ID + ") REFERENCES " + SupplierEntry.TABLE_NAME + "(" + SupplierEntry.COLUMN_ID + "));";

        final String sqlCreateSales = "CREATE TABLE " + SalesEntry.TABLE_NAME + "( "
                + SalesEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SalesEntry.COLUMN_ITEM_ID + " INTEGER NOT NULL, "
                + SalesEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + SalesEntry.COLUMN_DATE + " TEXT, "
                + SalesEntry.COLUMN_QUANTITY + " INTEGER, "
                + "FOREIGN KEY(" + SalesEntry.COLUMN_ITEM_ID + ") REFERENCES " + ItemEntry.TABLE_NAME + "(" + ItemEntry.COLUMN_ID + "));";

        final String sqlCreateViewPurchase = "CREATE VIEW " + PurchaseReport.TABLE_NAME + " As " +
                "SELECT " +
                PurchaseReport.COLUMN_ITEM_NAME + ", " +
                PurchaseReport.COLUMN_QTY + ", " +
                PurchaseReport.COLUMN_PRICE + ", " +
                PurchaseReport.COLUMN_DATE +
                " FROM " +
                ItemEntry.TABLE_NAME + " INNER JOIN " + PurchasesEntry.TABLE_NAME + " ON " + ItemEntry.COLUMN_ID + " = purchases.item_id;";

        final String sqlCreateViewSale = "CREATE VIEW " + SalReport.TABLE_NAME + " As " +
                "SELECT " +
                SalReport.COLUMN_ITEM_NAME + ", " +
                SalReport.COLUMN_QTY + ", " +
                SalReport.COLUMN_PRICE + ", " +
                SalReport.COLUMN_DATE +
                " FROM " +
                ItemEntry.TABLE_NAME + " INNER JOIN " + SalesEntry.TABLE_NAME + " ON " + ItemEntry.COLUMN_ID + " = sales.item_id;";

        try {
            sqLiteDatabase.execSQL(sqlCreateSuppliers);
            sqLiteDatabase.execSQL(sqlCreateItems);
            sqLiteDatabase.execSQL(sqlCreatePurchases);
            sqLiteDatabase.execSQL(sqlCreateSales);
            sqLiteDatabase.execSQL(sqlCreateViewPurchase);
            sqLiteDatabase.execSQL(sqlCreateViewSale);
        } catch (SQLException e) {
            Log.d(TAG, "onCreate: ", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
