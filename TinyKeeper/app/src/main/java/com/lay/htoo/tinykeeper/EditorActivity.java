package com.lay.htoo.tinykeeper;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.lay.htoo.tinykeeper.data.InventoryContract;
import com.lay.htoo.tinykeeper.data.InventoryDBHelper;
import com.lay.htoo.tinykeeper.data.InventoryProvider;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.widget.Toast.makeText;

public class EditorActivity extends AppCompatActivity implements View.OnTouchListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = EditorActivity.class.getSimpleName();
    private static final int REQUEST_CAMERA_CODE = 1, REQUEST_GALLERY_CODE = 2;
    private static final int REQUEST_CALL = 10;
    private ImageView mImage;
    private Bitmap mBitmap;
    PopupMenu popup;
    private EditText mNameField, mBuyPriceField, mSellPriceField, mQtyField, mSupplierField, mPhoneField, mQtyModifyField;
    private Uri mUri;
    private int mValue;
    private boolean mItemEdited = false;
    private int mSupplierId;
    private String mSupplierPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mValue = 0;
        mImage = (ImageView) findViewById(R.id.imageView_item_pic);
        mImage.setOnTouchListener(this);
        mNameField = (EditText) findViewById(R.id.textField_name);
        mNameField.setOnTouchListener(this);
        mBuyPriceField = (EditText) findViewById(R.id.textField_buy_price);
        mBuyPriceField.setOnTouchListener(this);
        mSellPriceField = (EditText) findViewById(R.id.textField_sell_price);
        mSellPriceField.setOnTouchListener(this);
        mQtyField = (EditText) findViewById(R.id.textField_qty);
        mQtyField.setOnTouchListener(this);
        mQtyModifyField = (EditText) findViewById(R.id.textField_change_qty);
        mQtyModifyField.setOnTouchListener(this);
        mSupplierField = (EditText) findViewById(R.id.textField_supplier);
        mSupplierField.setOnTouchListener(this);
        mPhoneField = (EditText) findViewById(R.id.textField_supplier_phone);
        mPhoneField.setOnTouchListener(this);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cart);
        mUri = getIntent().getData();
        if (mUri == null) {

            View modifyQtyView = findViewById(R.id.modify_qty_view);
            modifyQtyView.setVisibility(View.GONE);
            View orderView = findViewById(R.id.button_order_more);
            orderView.setVisibility(View.GONE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add an item");
            }
            invalidateOptionsMenu();
        } else {
            //showCurrentItem();
            mQtyField.setFocusable(false);
            getLoaderManager().initLoader(0, null, this);
        }
        setUpPopup();


    }

    private void showCurrentItem() {
        
    }

    private void setUpPopup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            popup = new PopupMenu(EditorActivity.this, mImage, Gravity.CENTER);
            getMenuInflater().inflate(R.menu.add_pic_menu, popup.getMenu());
            mImage.setOnTouchListener(popup.getDragToOpenListener());
        }
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.show();
            }
        });
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                final int id = menuItem.getItemId();
                switch (id) {
                    case R.id.action_choose_pic:
                        Intent open = new Intent(Intent.ACTION_GET_CONTENT);
                        open.setType("image/*");
                        if (open.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(open, REQUEST_GALLERY_CODE);
                            return true;
                        }
                        return false;
                    case R.id.action_take_pic:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, REQUEST_CAMERA_CODE);
                            return true;
                        }
                        return false;
                    default:
                        return false;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_CODE) {
                mBitmap = data.getParcelableExtra("data");
                mImage.setImageBitmap(mBitmap);
            } else if (requestCode == REQUEST_GALLERY_CODE) {
                //Bitmap thumbnail = data.getParcelableExtra("data");
                Uri fullPhotoUri = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(fullPhotoUri);
                    mBitmap = BitmapFactory.decodeStream(inputStream);
                    if (mBitmap.getHeight() > 3000) {
                        Toast toast = Toast.makeText(this, "Image can't be saved because of its big size.\n Please select another image.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();
                        return;
                    }
                        mImage.setImageBitmap(mBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mUri == null) {
            MenuItem item = menu.getItem(1);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (mItemEdited) {
                    showExitConfirmation(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        }
                    });
                    return true;
                }
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_save:
                insertOrUpdateItem();
                //insertByDBHelper();
                return true;
            case R.id.action_deleteItem:
                deleteItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void insertByDBHelper() {
        InventoryDBHelper dbHelper = new InventoryDBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        makeText(this, "DB successfully created...", Toast.LENGTH_SHORT).show();

        String itemName = mNameField.getText().toString().trim();
        String itemBuyPrice = mBuyPriceField.getText().toString().trim();
        String itemSellPrice = mSellPriceField.getText().toString().trim();
        String itemQty = mQtyField.getText().toString().trim();
        String supplierName = mSupplierField.getText().toString().trim();
        String supplierPhone = mPhoneField.getText().toString().trim();
        byte[] imageInBytes = InventoryContract.ItemEntry.getBytesArray(mBitmap);

        ContentValues values = new ContentValues();
        values.put(InventoryContract.SupplierEntry.COLUMN_NAME, supplierName);
        values.put(InventoryContract.SupplierEntry.COLUMN_PHONE, supplierPhone);
        values.put(InventoryContract.SupplierEntry.COLUMN_DATE, "25-10-2017");
        long supplierId = db.insert(
                InventoryContract.SupplierEntry.TABLE_NAME,
                null,
                values);
        if (supplierId
         != -1) {
            values.clear();
            values.put(InventoryContract.ItemEntry.COLUMN_NAME, itemName);
            values.put(InventoryContract.ItemEntry.COLUMN_BUY_PRICE, itemBuyPrice);
            values.put(InventoryContract.ItemEntry.COLUMN_SALE_PRICE, itemSellPrice);
            values.put(InventoryContract.ItemEntry.COLUMN_QUANTITY, itemQty);
            values.put(InventoryContract.ItemEntry.COLUMN_SUPPLIER_ID, supplierId);
            values.put(InventoryContract.ItemEntry.COLUMN_PICTURE, imageInBytes);
            values.put(InventoryContract.ItemEntry.COLUMN_DATE, "25-10-2017");
            long itemId = db.insert(
                    InventoryContract.ItemEntry.TABLE_NAME,
                    null,
                    values
            );
            if (itemId != -1) {
                values.clear();
                values.put(InventoryContract.PurchasesEntry.COLUMN_ITEM_ID, itemId);
                values.put(InventoryContract.PurchasesEntry.COLUMN_SUPPLIER_ID, supplierId);
                values.put(InventoryContract.PurchasesEntry.COLUMN_PRICE, itemBuyPrice);
                values.put(InventoryContract.PurchasesEntry.COLUMN_QUANTITY, itemQty);
                values.put(InventoryContract.PurchasesEntry.COLUMN_DATE, "25-10-2017");
                if (db.insert(
                        InventoryContract.PurchasesEntry.TABLE_NAME,
                        null,
                        values
                ) != -1) {
                    makeText(this, "Successfully saved.", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d(TAG, "insertByDBHelper: Error inserting purchases...");
                }
            }else{
                Log.d(TAG, "insertByDBHelper: Error inserting items");
            }
            
        }else{
            Log.d(TAG, "insertByDBHelper: Error inserting suppliers");
        }
    }

    private void deleteItem() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ContentValues contentValue = new ContentValues();
                        contentValue.put(InventoryContract.ItemEntry.COLUMN_IS_DELETED, 1);
                        int row = getContentResolver().update(mUri,
                                contentValue,
                                null, null);
                        if (row > 0) {
                            makeText(EditorActivity.this, "Item Deleted.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            makeText(EditorActivity.this, "Deleting Fail!", Toast.LENGTH_SHORT).show();
                        }
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

    private void insertOrUpdateItem() {

        String itemName = mNameField.getText().toString().trim();
        String itemBuyPrice = mBuyPriceField.getText().toString().trim();
        String itemSellPrice = mSellPriceField.getText().toString().trim();
        String itemQty = mQtyField.getText().toString().trim();
        String supplierName = mSupplierField.getText().toString().trim();
        String supplierPhone = mPhoneField.getText().toString().trim();
        byte[] imageInBytes = InventoryContract.ItemEntry.getBytesArray(mBitmap);
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(System.currentTimeMillis()));

        if (
                TextUtils.isEmpty(itemName) && TextUtils.isEmpty(itemBuyPrice) && TextUtils.isEmpty(itemSellPrice) &&
                        TextUtils.isEmpty(itemQty) && TextUtils.isEmpty(supplierName) &&
                        TextUtils.isEmpty(supplierPhone)
                ) {
            return;
        }

        Integer buyPrice = null;
        if (!TextUtils.isEmpty(itemBuyPrice)) {
            buyPrice = Integer.parseInt(itemBuyPrice);
        }

        Integer sellPrice = null;
        if (!TextUtils.isEmpty(itemBuyPrice)) {
            sellPrice = Integer.parseInt(itemSellPrice);
        }

        int qty = 0;
        if (!TextUtils.isEmpty(itemQty)) {
            qty = Integer.parseInt(itemQty);
        }

        ContentValues supplierValues = new ContentValues();
        supplierValues.put(InventoryContract.SupplierEntry.COLUMN_NAME, supplierName);
        supplierValues.put(InventoryContract.SupplierEntry.COLUMN_PHONE, supplierPhone);
        supplierValues.put(InventoryContract.SupplierEntry.COLUMN_DATE, date);

        try {
            if (mUri == null) {
                Uri newSupplierUri = getContentResolver().insert(InventoryContract.SupplierEntry.CONTENT_URI, supplierValues);
                long newSupId = ContentUris.parseId(newSupplierUri);
                ContentValues itemValues = new ContentValues();
                itemValues.put(InventoryContract.ItemEntry.COLUMN_SUPPLIER_ID, newSupId);
                itemValues.put(InventoryContract.ItemEntry.COLUMN_NAME, itemName);
                itemValues.put(InventoryContract.ItemEntry.COLUMN_BUY_PRICE, buyPrice);
                itemValues.put(InventoryContract.ItemEntry.COLUMN_SALE_PRICE, sellPrice);
                itemValues.put(InventoryContract.ItemEntry.COLUMN_QUANTITY, qty);
                itemValues.put(InventoryContract.ItemEntry.COLUMN_PICTURE, imageInBytes);
                itemValues.put(InventoryContract.ItemEntry.COLUMN_DATE, date);
                Uri newItemUri = getContentResolver().insert(InventoryContract.ItemEntry.CONTENT_URI, itemValues);
                long newItemId = ContentUris.parseId(newItemUri);
                ContentValues purchaseValues = new ContentValues();
                purchaseValues.put(InventoryContract.PurchasesEntry.COLUMN_ITEM_ID, newItemId);
                purchaseValues.put(InventoryContract.PurchasesEntry.COLUMN_SUPPLIER_ID, newSupId);
                purchaseValues.put(InventoryContract.PurchasesEntry.COLUMN_PRICE, buyPrice);
                purchaseValues.put(InventoryContract.PurchasesEntry.COLUMN_QUANTITY, qty);
                purchaseValues.put(InventoryContract.PurchasesEntry.COLUMN_DATE, date);
                getContentResolver().insert(InventoryContract.PurchasesEntry.CONTENT_URI, purchaseValues);
                makeText(this, "Item saved.", Toast.LENGTH_SHORT).show();
            } else {
                if (getContentResolver().update(
                        ContentUris.withAppendedId(InventoryContract.SupplierEntry.CONTENT_URI, mSupplierId),
                        supplierValues,
                        null,
                        null
                ) == 0) {
                    Log.d(TAG, "insertOrUpdateItem: Error in Updating Supplier");
                }

                ContentValues itemValues = new ContentValues();
                itemValues.put(InventoryContract.ItemEntry.COLUMN_SUPPLIER_ID, mSupplierId);
                itemValues.put(InventoryContract.ItemEntry.COLUMN_NAME, itemName);
                itemValues.put(InventoryContract.ItemEntry.COLUMN_BUY_PRICE, buyPrice);
                itemValues.put(InventoryContract.ItemEntry.COLUMN_SALE_PRICE, sellPrice);
                itemValues.put(InventoryContract.ItemEntry.COLUMN_QUANTITY, qty);
                itemValues.put(InventoryContract.ItemEntry.COLUMN_PICTURE, imageInBytes);
                itemValues.put(InventoryContract.ItemEntry.COLUMN_DATE, date);
                if (getContentResolver().update(
                        mUri,
                        itemValues,
                        null,
                        null
                ) == 0) {
                    Log.d(TAG, "insertOrUpdateItem: Error in Updating Item");
                }
                if (mValue > 0) {
                    ContentValues purchaseValues = new ContentValues();
                    purchaseValues.put(InventoryContract.PurchasesEntry.COLUMN_ITEM_ID, ContentUris.parseId(mUri));
                    purchaseValues.put(InventoryContract.PurchasesEntry.COLUMN_SUPPLIER_ID, mSupplierId);
                    purchaseValues.put(InventoryContract.PurchasesEntry.COLUMN_PRICE, buyPrice);
                    purchaseValues.put(InventoryContract.PurchasesEntry.COLUMN_QUANTITY, mValue);
                    purchaseValues.put(InventoryContract.PurchasesEntry.COLUMN_DATE, date);
                    getContentResolver().insert(InventoryContract.PurchasesEntry.CONTENT_URI, purchaseValues);
                }
                makeText(this, "Item updated.", Toast.LENGTH_SHORT).show();
            }
            finish();
        } catch (Exception e) {
            String message = e.getLocalizedMessage();
            if (message.equalsIgnoreCase(InventoryProvider.EMPTY_NAME)) {
                makeText(this, "Please, enter your item name.", Toast.LENGTH_SHORT).show();
            } else if (message.equalsIgnoreCase(InventoryProvider.EMPTY_PRICE)) {
                makeText(this, "Please, enter your item price.", Toast.LENGTH_SHORT).show();
            } else if (message.equalsIgnoreCase(InventoryProvider.EMPTY_SUPPLIER)) {
                makeText(this, "Please, provide supplier name.", Toast.LENGTH_SHORT).show();
            }
            Log.d(TAG, "Insert Fail!", e);
        }
    }

    public void decreaseQty(View view) {
        int currentValue = 0;
        String present = mQtyModifyField.getText().toString().trim();
        if (!TextUtils.isEmpty(present)) {
            currentValue = Integer.parseInt(present);
        }
        int presentQty = Integer.parseInt(mQtyField.getText().toString().trim());
        if (presentQty >= currentValue) {
            presentQty -= currentValue;
            mValue -= currentValue;
            mQtyModifyField.setText("");
            mQtyModifyField.setHint(String.format("           %d   ", mValue));
            mQtyField.setText(String.format("%d", presentQty));
        } else {
            makeText(this, "Can't decrease.", Toast.LENGTH_SHORT).show();
        }
    }

    public void increaseQty(View view) {
        int currentValue = 0;
        String present = mQtyModifyField.getText().toString().trim();
        if (!TextUtils.isEmpty(present)) {
            currentValue = Integer.parseInt(present);
        }
        int presentQty = Integer.parseInt(mQtyField.getText().toString().trim());
        presentQty += currentValue;
        mValue += currentValue;
        mQtyModifyField.setText("");
        mQtyModifyField.setHint(String.format("           %d   ", mValue));
        mQtyField.setText(String.format("%d", presentQty));

    }

    public void orderMore(View view) {
        mSupplierPhone = mPhoneField.getText().toString();
        if (!TextUtils.isEmpty(mSupplierPhone)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    callPhone(mSupplierPhone);
                }else{
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                        makeText(this, "Call Permission is needed to call the Supplier to order more.", Toast.LENGTH_LONG).show();
                    }
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                }
            }


        }
    }

    private void callPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel://"+phone));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhone(mSupplierPhone);
            }else{
                makeText(this, "Permission was not granted.", Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }

    private void showExitConfirmation(DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.discard_message)
                .setPositiveButton("Discard", onClickListener)
                .setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mItemEdited = true;
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!mItemEdited) {
            super.onBackPressed();
            return;
        }
        showExitConfirmation(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(
                this,
                mUri,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor != null) {
            cursor.moveToFirst();
            mNameField.setText(cursor.getString(
                    cursor.getColumnIndex(
                            InventoryContract.ItemEntry.COLUMN_NAME)));
            mBuyPriceField.setText(cursor.getString(
                    cursor.getColumnIndex(
                            InventoryContract.ItemEntry.COLUMN_BUY_PRICE)));
            mSellPriceField.setText(cursor.getString(
                    cursor.getColumnIndex(
                            InventoryContract.ItemEntry.COLUMN_SALE_PRICE)));
            mQtyField.setText(cursor.getString(
                    cursor.getColumnIndex(
                            InventoryContract.ItemEntry.COLUMN_QUANTITY)));
            mSupplierId = cursor.getInt(cursor.getColumnIndex(InventoryContract.ItemEntry.COLUMN_SUPPLIER_ID));
            Cursor cursorSupplier = getContentResolver().query(
                    ContentUris.withAppendedId(
                            InventoryContract.SupplierEntry.CONTENT_URI, mSupplierId
                    ), null, null, null, null);
            if (cursorSupplier != null) {
                cursorSupplier.moveToFirst();
                mSupplierField.setText(cursorSupplier.getString(
                        cursorSupplier.getColumnIndex(
                                InventoryContract.SupplierEntry.COLUMN_NAME)));
                mPhoneField.setText(cursorSupplier.getString(
                        cursorSupplier.getColumnIndex(
                                InventoryContract.SupplierEntry.COLUMN_PHONE)));
                cursorSupplier.close();
            }else{
                Log.d(TAG, "onLoadFinished: Error in Querying supplier cursor.");
            }
            mBitmap = InventoryContract.ItemEntry.getBitmap(
                    cursor.getBlob(
                            cursor.getColumnIndex(
                                    InventoryContract.ItemEntry.COLUMN_PICTURE)));
            mImage.setImageBitmap(mBitmap);
            cursor.close();
        } else {
            Log.d(TAG, "showCurrentItem: Fail");
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameField.setText(null);
        mBuyPriceField.setText(null);
        mSellPriceField.setText(null);
        mQtyField.setText(null);
        mQtyModifyField.setText(null);
        mQtyModifyField.setHint(null);
        mSupplierField.setText(null);
        mPhoneField.setText(null);

    }
}
