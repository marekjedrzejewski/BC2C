package jmm.bc2c;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.googlecode.leptonica.android.AdaptiveMap;
import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Convert;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.Scale;
import com.googlecode.leptonica.android.WriteFile;

/**
 * Created by marek on 25.01.16.
 */
public class ImagePrep {
    public static Bitmap Prepare(Bitmap image) {
        return binarize(toGrayscale(image));
    }

//    public static Bitmap toGrayscale(Bitmap image)
//    {
//        int height = image.getHeight();
//        int width = image.getWidth();
//        Bitmap imgGreyscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(imgGreyscale);
//        Paint paint = new Paint();
//        ColorMatrix colorMatrix = new ColorMatrix();
//        colorMatrix.setSaturation(0);
//        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
//        paint.setColorFilter(filter);
//        canvas.drawBitmap(image, 0, 0, paint);
//        return imgGreyscale;
//    }


    public static Bitmap toGrayscale(Bitmap image) {
        Pix imagepix = ReadFile.readBitmap(image);
        imagepix = Convert.convertTo8(imagepix);
        return WriteFile.writeBitmap(imagepix);
    }

    public static Bitmap binarize(Bitmap image) {
        Pix imagepix = ReadFile.readBitmap(image);
        imagepix = Binarize.otsuAdaptiveThreshold(imagepix);
        return WriteFile.writeBitmap(imagepix);
    }



    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap DecodeSampleBitmapFile(String imagePath,
                                                         int maxWidth, int maxHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int reqWidth = maxWidth;
        int reqHeight = maxHeight;
        int ratio = options.outWidth/options.outHeight;
        if(options.outWidth > options.outHeight){
            reqHeight = reqWidth/ratio;
        }
        else{
            reqWidth = reqHeight*ratio;
        }
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }
}
