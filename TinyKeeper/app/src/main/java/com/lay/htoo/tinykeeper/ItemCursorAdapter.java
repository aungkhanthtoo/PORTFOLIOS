package com.lay.htoo.tinykeeper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lay.htoo.tinykeeper.data.InventoryContract.ItemEntry;

import java.util.Locale;

/**
 * Created by Lenovo on 9/25/2017.
 */

 class ItemCursorAdapter extends CursorAdapter {
    private ViewHolder mViewHolder;
    private Cursor mCursor;

    /**
     * @param context
     * @param c
     * @deprecated
     */
    ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mCursor = c;
    }

    @Override
    public int getCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return super.getCount();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
        mViewHolder = new ViewHolder(view);
        view.setTag(mViewHolder);
        return view;
    }


    private static class ViewHolder{
        private TextView textName, textPrice, textQty;
        private ImageView image;
        ViewHolder(View view){
            textName = (TextView) view.findViewById(R.id.textView_list_name);
            textPrice = (TextView) view.findViewById(R.id.textView_list_price);
            textQty = (TextView) view.findViewById(R.id.textView_list_qty);
            image = (ImageView) view.findViewById(R.id.imageView_list_image);
        }
    }
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        if (view != null) {
            mViewHolder = (ViewHolder) view.getTag();
        }

        mViewHolder.textName.setText(cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_NAME)));

        mViewHolder.textPrice.setText(String.format("%s Ks", cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_SALE_PRICE))));

        int currentQty = cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_QUANTITY));
        if (currentQty <= 10) {
            mViewHolder.textQty.setTextColor(context.getResources().getColor(R.color.colorAccent));
        } else if (currentQty <= 20) {
            mViewHolder.textQty.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            mViewHolder.textQty.setTextColor(Color.BLUE);
        }
        mViewHolder.textQty.setText(String.format(Locale.getDefault(), "%d", currentQty));

        byte[] bytesImage = cursor.getBlob(cursor.getColumnIndex(ItemEntry.COLUMN_PICTURE));
        mViewHolder.image.setImageBitmap(ItemEntry.getBitmap(bytesImage));

    }
}
