package jmm.bc2c;

import android.graphics.Bitmap;

import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Convert;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.WriteFile;

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

}
