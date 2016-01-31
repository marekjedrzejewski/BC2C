package jmm.bc2c;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Created by marek on 25.01.16.
 */
public class ImagePrep {
    public static Bitmap Prepare(Bitmap image) {
        return toGrayscale(image);
    }

    public static Bitmap toGrayscale(Bitmap image)
    {
        int height = image.getHeight();
        int width = image.getWidth();
        Bitmap imgGreyscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(imgGreyscale);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(image, 0, 0, paint);
        return imgGreyscale;
    }
}
