package jmm.bc2c;

import android.graphics.Bitmap;

import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Convert;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.WriteFile;



public class ImagePrep {

    public final static int OTSU_SIZE_X = 32;
    public final static int OTSU_SIZE_Y = 32;
    public final static int OTSU_SMOOTH_X = 1;
    public final static int OTSU_SMOOTH_Y = 1;
    public final static float OTSU_SCORE_FRACTION = 0.1f;

    public static Bitmap Prepare(Bitmap image) {
        //return binarize(toGrayscale(image));
        return toGrayscale(image);
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
        imagepix = Binarize.otsuAdaptiveThreshold(imagepix, OTSU_SIZE_X, OTSU_SIZE_Y,
                OTSU_SMOOTH_X, OTSU_SMOOTH_Y, OTSU_SCORE_FRACTION);
        return WriteFile.writeBitmap(imagepix);
    }

}
