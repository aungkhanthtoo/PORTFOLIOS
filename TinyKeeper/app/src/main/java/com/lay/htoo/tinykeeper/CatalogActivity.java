package com.lay.htoo.tinykeeper;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lay.htoo.tinykeeper.data.InventoryContract;
import com.lay.htoo.tinykeeper.data.InventoryContract.ItemEntry;
import com.lay.htoo.tinykeeper.data.InventoryDBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CatalogActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG =
            CatalogActivity.class.getSimpleName();
    private ItemCursorAdapter mCursorAdapter;
    private ImageView mEmptyImage;
    private TextView mEmptyTitle, mEmptySubtitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        mEmptyImage = (ImageView) findViewById(R.id.imageView_empty_view);
        mEmptyTitle = (TextView) findViewById(R.id.textView_title_emptyView);
        mEmptySubtitle = (TextView) findViewById(R.id.textView_subtitle_emptyView);

        getSupportActionBar().setTitle("Inventory");
        setUpListView();


    }

    private void setUpListView() {
        View emptyView = findViewById(R.id.empty_view);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setEmptyView(emptyView);
        mCursorAdapter = new ItemCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);
        listView.smoothScrollToPosition(mCursorAdapter.getCount());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CardView cardView = (CardView) view;
                cardView.setCardElevation(32f);
                saleItem(id);
            }
        });
        getLoaderManager().initLoader(0, null, this);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                CardView cardView = (CardView) view;
                cardView.setCardElevation(32f);
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                intent.setData(ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id));
                startActivity(intent);
                return true;
            }
        });


    }

    public void openEditor(View view) {
        Intent intent = new Intent(this, EditorActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.catalog_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case R.id.action_deleteAll:
                deleteAllItems();
                return true;
            case R.id.action_show_report:
                Intent intent = new Intent(this, ReportActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAllItems() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_delete_all)
                .setTitle("Delete All Items")
                .setMessage("Are you sure to delete.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delete();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void delete() {
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_IS_DELETED, 1);

        int id = getContentResolver().update(ItemEntry.CONTENT_URI,
                values,
                null,
                null);
        if (id != 0) {
            Snackbar.make((CoordinatorLayout) findViewById(R.id.coordinator_layout),
                    "All items deleted.",
                    Snackbar.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(CatalogActivity.this, "Deleting fail.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDummyData() {

        InventoryDBHelper dbHelper = new InventoryDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                ItemEntry.TABLE_NAME,
                null,
                ItemEntry.COLUMN_IS_DELETED + "=?",
                new String[]{"0"},
                null,
                null,
                null
        );
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                String currentItem = "";
                while (cursor.moveToNext()) {
                    currentItem += cursor.getString(0) + "     :   " +
                            cursor.getString(1) + "     :   " +
                            cursor.getString(2) + "     :   " +
                            cursor.getString(3) + "     :   " +
                            cursor.getString(4) + "     :   " +
                            cursor.getString(5) + "     :   " +
                            cursor.getString(6) + "     :   " +
                            cursor.getString(7) + "     :   " +
                            cursor.getBlob(8).toString() + "\n";
                }
                Log.d(TAG, "showDummyData: " + currentItem);
                cursor.close();
            } else {
                Log.d(TAG, "showDummyData: Your table has empty row...");
            }
        } else {
            Log.d(TAG, "showDummyData: error in cursor");
        }

        Cursor cursorSupplier = db.query(
                InventoryContract.SupplierEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        if (cursorSupplier != null) {
            if (cursorSupplier.getCount() > 0) {
                String currentSupplier = "";
                while (cursorSupplier.moveToNext()) {
                    currentSupplier += cursorSupplier.getString(0) + "     :   " +
                            cursorSupplier.getString(1) + "     :   " +
                            cursorSupplier.getString(2) + "     :   " +
                            cursorSupplier.getString(3) + "\n";
                }
                Log.d(TAG, "showSupplierData: " + currentSupplier);
                cursorSupplier.close();
            } else {
                Log.d(TAG, "showDummyData: Your Supplier has empty row...");
            }
        } else {
            Log.d(TAG, "showDummyData: error in cursorSupplier");
        }

        Cursor cursorPurchases = db.query(
                InventoryContract.PurchaseReport.TABLE_NAME,
                null,
                InventoryContract.PurchasesEntry.COLUMN_DATE + " LIKE ?",
                new String[]{"%-10-%"},
                null,
                null,
                null
        );
        if (cursorPurchases != null) {
            if (cursorPurchases.getCount() > 0) {
                String currentPurchases = "";
                while (cursorPurchases.moveToNext()) {
                    currentPurchases += cursorPurchases.getString(0) + "     :   " +
                            cursorPurchases.getString(1) + "     :   " +
                            cursorPurchases.getString(2) + "     :   " +
                            cursorPurchases.getString(3) + "\n";
                }
                Log.d(TAG, "showPurchaseData: " + currentPurchases);
                cursorPurchases.close();
            } else {
                Log.d(TAG, "showDummyData: Your Purchase has empty row...");
            }
        } else {
            Log.d(TAG, "showDummyData: error in cursorPurchase");
        }
        Cursor cursorSale = db.query(
                InventoryContract.SalReport.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        if (cursorSale != null) {
            String currentSales = "";
            while (cursorSale.moveToNext()) {
                currentSales += cursorSale.getString(0) + "     :   " +
                        cursorSale.getString(1) + "     :   " +
                        cursorSale.getString(2) + "     :   " +
                        cursorSale.getString(3) + "\n";
            }
            Log.d(TAG, "showSaleData: " + currentSales);
            cursorSale.close();
        } else {
            Log.d(TAG, "showDummyData: error in cursorSale");
        }

    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(this,
                ItemEntry.CONTENT_URI,
                null,
                ItemEntry.COLUMN_IS_DELETED + " =?",
                new String[]{"0"},
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        mEmptyImage.setImageResource(R.drawable.store);
        mEmptyTitle.setText(R.string.label_brand_new_shop);
        mEmptySubtitle.setText(R.string.label_put_new_items);

        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private void saleItem(long id) {
        Uri uri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);
        Cursor cursor = getContentResolver().query(
                uri,
                new String[]
                        {
                                ItemEntry.COLUMN_ID,
                                ItemEntry.COLUMN_QUANTITY,
                                ItemEntry.COLUMN_SALE_PRICE
                        },
                null,
                null,
                null
        );
        int currentQty = 0;
        int itemId = 0;
        int itemPrice = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            currentQty = cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_QUANTITY));
            itemId = cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_ID));
            itemPrice = cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_SALE_PRICE));
            cursor.close();
        }

        ContentValues itemValues = new ContentValues();
        if (currentQty != 0) {
            ContentValues values = new ContentValues();
            values.put(InventoryContract.SalesEntry.COLUMN_ITEM_ID, itemId);
            values.put(InventoryContract.SalesEntry.COLUMN_PRICE, itemPrice);
            values.put(InventoryContract.SalesEntry.COLUMN_QUANTITY, 1);
            values.put(InventoryContract.SalesEntry.COLUMN_DATE, getCurrentDate());
            getContentResolver().insert(
                    InventoryContract.SalesEntry.CONTENT_URI,
                    values
            );
            itemValues.put(ItemEntry.COLUMN_QUANTITY, currentQty - 1);
        } else {
            Toast.makeText(this, "Zero quantity!", Toast.LENGTH_SHORT).show();
            return;
        }

        final long updatedRow = getContentResolver().update(uri, itemValues, null, null);
        if (updatedRow != 0) {
            Toast toast = Toast.makeText(this, "\nSole 1 Item.\n ", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }
    }

    private void putSaleRecord(long id) {
        Uri uri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);
        Cursor cursor = getContentResolver().query(
                uri,
                new String[]{ItemEntry.COLUMN_ID, ItemEntry.COLUMN_QUANTITY, ItemEntry.COLUMN_SALE_PRICE},
                null,
                null,
                null
        );
        int currentQty = 0;
        int itemId = 0;
        int itemPrice = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            currentQty = cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_QUANTITY));

            cursor.close();
        }
        if (currentQty != 0) {


        }


    }

    private String getCurrentDate() {
        return new SimpleDateFormat("dd-MM-yyyy").format(
                new Date(System.currentTimeMillis())
        );
    }
}
