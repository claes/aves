package net.holmerson.aves;

import org.lucasr.smoothie.SimpleItemLoader;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore.Images.ImageColumns;
import android.view.View;
import android.widget.Adapter;

public class BirdListLoader extends SimpleItemLoader<Long, Bitmap> {
    private final Context mContext;

    public BirdListLoader (Context context) {
        mContext = context;
    }

    @Override
    public Long getItemParams(Adapter adapter, int position) {
        Cursor c = (Cursor) adapter.getItem(position);
        return c.getLong(c.getColumnIndex(ImageColumns._ID));
    }


    @Override
    public Bitmap loadItem(Long id) {
//        Uri imageUri = Uri.withAppendedPath(Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
//
//        Resources res = mContext.getResources();
//        int width = res.getDimensionPixelSize(R.dimen.image_width);
//        int height = res.getDimensionPixelSize(R.dimen.image_height);
//
//        Bitmap bitmap = decodeSampledBitmapFromResource(imageUri, width, height);
//        if (bitmap != null) {
//            mMemCache.put(id, bitmap);
//        }
//
//        return bitmap;
        return null;
    }

    @Override
    public Bitmap loadItemFromMemory(Long aLong) {
        return null;
    }

    @Override
    public void displayItem(View itemView, Bitmap result, boolean fromMemory) {
//        if (result == null) {
//            return;
//        }
//
//        ViewHolder holder = (ViewHolder) itemView.getTag();
//
//        BitmapDrawable imageDrawable = new BitmapDrawable(itemView.getResources(), result);
//
//        if (fromMemory) {
//            holder.image.setImageDrawable(imageDrawable);
//        } else {
//            BitmapDrawable emptyDrawable = new BitmapDrawable(itemView.getResources());
//
//            TransitionDrawable fadeInDrawable =
//                    new TransitionDrawable(new Drawable[] { emptyDrawable, imageDrawable });
//
//            holder.image.setImageDrawable(fadeInDrawable);
//            fadeInDrawable.startTransition(200);
//        }
    }
}
