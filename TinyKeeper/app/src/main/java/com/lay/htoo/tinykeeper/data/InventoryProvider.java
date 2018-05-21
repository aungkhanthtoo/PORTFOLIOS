package com.lay.htoo.tinykeeper.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.lay.htoo.tinykeeper.data.InventoryContract.ItemEntry;

import static android.content.ContentValues.TAG;

/**
 * Created by Lenovo on 9/25/2017.
 */

public class InventoryProvider extends ContentProvider {

    private static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    private InventoryDBHelper mDbHelper;
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int ITEMS = 100;
    private static final int ITEM_ID = 101;
    private static final int SUPPLIERS = 200;
    private static final int SUPPLIER_ID = 201;
    private static final int PURCHASES = 300;
    private static final int SALES = 400;
    private static final int PURCHASES_REPORT = 500;
    private static final int SALES_REPORT = 600;

    public static final String EMPTY_NAME = "provide a name";
    public static final String EMPTY_PRICE = "provide a price";
    public static final String EMPTY_SUPPLIER = "provide a supplier";

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                ItemEntry.PATH_ITEMS,
                ITEMS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                ItemEntry.PATH_ITEMS + "/#",
                ITEM_ID);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.SupplierEntry.PATH_SUPPLIERS,
                SUPPLIERS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.SupplierEntry.PATH_SUPPLIERS + "/#",
                SUPPLIER_ID);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.PurchasesEntry.PATH_PURCHASES,
                PURCHASES);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.SalesEntry.PATH_SALES,
                SALES);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.PurchaseReport.PATH_PURCHASE_REPORT,
                PURCHASES_REPORT);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.SalReport.PATH_SALE_REPORT,
                SALES_REPORT);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projections, @Nullable String selection, @Nullable String[] selectArgs, @Nullable String sortOrder) {
        final int match = sUriMatcher.match(uri);
        long id;
        switch (match) {
            case ITEMS:
                return queryItem(uri, projections, selection, selectArgs, sortOrder);
            case ITEM_ID:
                id = ContentUris.parseId(uri);
                selection = ItemEntry.COLUMN_ID + "=?";
                selectArgs = new String[]
                        {String.valueOf(id)};
                return queryItem(uri, projections, selection, selectArgs, sortOrder);
            case SUPPLIER_ID:
                id = ContentUris.parseId(uri);
                selection = InventoryContract.SupplierEntry.COLUMN_ID + "=?";
                selectArgs = new String[]
                        {String.valueOf(id)};
                return querySupplier(projections, selection, selectArgs, sortOrder);
            case PURCHASES_REPORT:
                return queryPurchaseReport(projections, selection, selectArgs, sortOrder);
            case SALES_REPORT:
                return querySaleReport(projections, selection, selectArgs, sortOrder);
            default:
                throw new IllegalArgumentException("Querying is not supported for : " + uri);
        }

    }

    private Cursor querySaleReport(String[] projections, String selection, String[] selectArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        return database.query(
                InventoryContract.SalReport.TABLE_NAME,
                projections,
                selection,
                selectArgs,
                ItemEntry.COLUMN_NAME,
                null,
                sortOrder
        );
    }

    private Cursor queryPurchaseReport(String[] projections, String selection, String[] selectArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        return database.query(
                InventoryContract.PurchaseReport.TABLE_NAME,
                projections,
                selection,
                selectArgs,
                ItemEntry.COLUMN_NAME,
                null,
                sortOrder
        );
    }

    private Cursor querySupplier(String[] projections, String selection, String[] selectArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        return database.query(
                InventoryContract.SupplierEntry.TABLE_NAME,
                projections,
                selection,
                selectArgs,
                null, null, sortOrder
        );

    }

    private Cursor queryItem(Uri uri, String[] projections, String selection, String[] selectArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = database.query(
                ItemEntry.TABLE_NAME, projections,
                selection,
                selectArgs,
                null,
                null,
                sortOrder
        );
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return ItemEntry.CONTENT_DIR_TYPE; // vnd.android.cursor.dir/com.lay.htoo.tinykeeper/items
            case ITEM_ID:
                return ItemEntry.CONTENT_ITEM_TYPE; // vnd.android.cursor.item/com.lay.htoo.tinykeeper/items
            default:
                throw new IllegalArgumentException("Unknown Uri : " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri, contentValues);
            case SUPPLIERS:
                return insertSupplier(uri, contentValues);
            case PURCHASES:
                return insertPurchase(uri, contentValues);
            case SALES:
                return insertSale(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for this : " + uri);
        }
    }

    private Uri insertSale(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long newRow = db.insert(
                InventoryContract.SalesEntry.TABLE_NAME,
                null,
                contentValues
        );
        if (newRow == -1) {
            Log.d(TAG, "insertSale: Error in inserting Sales");
            throw new IllegalArgumentException("Insertion failed for Uri : " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, newRow);
    }

    private Uri insertPurchase(Uri uri, ContentValues contentValues) {
        if (contentValues == null) {
            throw new IllegalArgumentException("ContentValue == null");
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long purchaseId = db.insert(InventoryContract.PurchasesEntry.TABLE_NAME, null, contentValues);
        if (purchaseId == -1) {
            throw new IllegalArgumentException("Insertion failed for Uri : " + uri);
        }
        return ContentUris.withAppendedId(uri, purchaseId);
    }

    private Uri insertSupplier(Uri uri, ContentValues contentValues) {
        if (contentValues == null) {
            throw new IllegalArgumentException("ContentValue == null");
        }
        String supplier = contentValues.getAsString(InventoryContract.SupplierEntry.COLUMN_NAME);
        if (TextUtils.isEmpty(supplier)) {
            throw new IllegalArgumentException(EMPTY_SUPPLIER);
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long supplierId = db.insert(
                InventoryContract.SupplierEntry.TABLE_NAME,
                null,
                contentValues);
        if (supplierId == -1) {
            throw new IllegalArgumentException("Insertion failed for Uri : " + uri);
        }
        return ContentUris.withAppendedId(uri, supplierId);
    }

    private Uri insertItem(Uri uri, ContentValues contentValues) {

        if (contentValues == null) {
            throw new IllegalArgumentException("ContentValue == null");
        }
        String name = contentValues.getAsString(ItemEntry.COLUMN_NAME);
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException(EMPTY_NAME);
        }
        String price = contentValues.getAsString(ItemEntry.COLUMN_BUY_PRICE);
        String salePrice = contentValues.getAsString(ItemEntry.COLUMN_SALE_PRICE);
        if (TextUtils.isEmpty(price) || TextUtils.isEmpty(salePrice)) {
            throw new IllegalArgumentException(EMPTY_PRICE);
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final long newRowId = database.insert(ItemEntry.TABLE_NAME, null, contentValues);
        if (newRowId == -1) {
            throw new IllegalArgumentException("Insertion fail for Uri : " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, newRowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return deleteItem(uri, selection, selectionArgs);
            case ITEM_ID:
                selection = ItemEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]
                        {String.valueOf(ContentUris.parseId(uri))};
                return deleteItem(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Unsupported Uri : " + uri);
        }
    }

    private int deleteItem(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int numberOfRows = database.delete(
                ItemEntry.TABLE_NAME,
                selection,
                selectionArgs
        );
        if (numberOfRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                selection = ItemEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                return updateItem(uri, contentValues, selection, selectionArgs);
            case SUPPLIER_ID:
                selection = InventoryContract.SupplierEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                return updateSupplier(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update fail for unsupported Uri : " + uri);
        }
    }

    private int updateSupplier(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues == null) {
            throw new IllegalArgumentException("ContentValues == null");
        }
        if (contentValues.containsKey(InventoryContract.SupplierEntry.COLUMN_NAME)) {
            String name = contentValues.getAsString(InventoryContract.SupplierEntry.COLUMN_NAME);
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException(EMPTY_SUPPLIER);
            }
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        return db.update(
                InventoryContract.SupplierEntry.TABLE_NAME,
                contentValues,
                selection,
                selectionArgs
        );
    }

    private int updateItem(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        if (contentValues == null) {
            throw new IllegalArgumentException("ContentValues == null");
        }
        if (contentValues.containsKey(ItemEntry.COLUMN_NAME)) {
            String name = contentValues.getAsString(ItemEntry.COLUMN_NAME);
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException(EMPTY_NAME);
            }
        }
        if (contentValues.containsKey(ItemEntry.COLUMN_BUY_PRICE)) {
            String price = contentValues.getAsString(ItemEntry.COLUMN_BUY_PRICE);
            if (TextUtils.isEmpty(price)) {
                throw new IllegalArgumentException(EMPTY_PRICE);
            }
        }
        if (contentValues.containsKey(ItemEntry.COLUMN_SALE_PRICE)) {
            String price = contentValues.getAsString(ItemEntry.COLUMN_SALE_PRICE);
            if (TextUtils.isEmpty(price)) {
                throw new IllegalArgumentException(EMPTY_PRICE);
            }
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int numberOfRows = database.update(
                ItemEntry.TABLE_NAME,
                contentValues,
                selection,
                selectionArgs);
        if (numberOfRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRows;
     }
}