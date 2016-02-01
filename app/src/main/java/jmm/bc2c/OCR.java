package jmm.bc2c;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;
import java.io.IOException;

public class OCR {

    public String PerformOCR(String path)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);


        try {
            ExifInterface exif = new ExifInterface(path);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v("OCRed", "Orient: " + exifOrientation);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v("OCRed", "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }


        } catch (IOException e) {
            Log.e("OCRed", "Couldn't correct orientation: " + e.toString());
        }

        // Convert to ARGB_8888, required by tess
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        // _image.setImageBitmap( bitmap );

        Log.v("OCRed", "Before baseApi");
        bitmap = ImagePrep.Prepare(bitmap);
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(MainActivity.DATA_PATH, MainActivity.LANG);
        baseApi.setImage(bitmap);

        String recognizedText = baseApi.getUTF8Text();

        baseApi.end();

        // You now have the text in recognizedText var, you can do anything with it.
        // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
        // so that garbage doesn't make it to the display.

        Log.v("OCRed", "OCRED TEXT: " + recognizedText);

        if ( MainActivity.LANG.equalsIgnoreCase("eng") ) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }

        return recognizedText.trim();
    }

}

class OCRTask extends AsyncTask<Void,Void,String>{

    PhotoActivity photoActivity;

    public OCRTask(PhotoActivity pa){
        this.photoActivity = pa;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        String waitText = photoActivity.getString(R.string.wait_progress);
        String reasonText = photoActivity.getString(R.string.ocr_progress);
        photoActivity.LaunchProgressDialog(waitText,reasonText);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        photoActivity.KillProgressDialog();
        photoActivity.ShowResult(s);
    }

    @Override
    protected String doInBackground(Void... params) {
        OCR ocr = new OCR();
        Log.d("OCT","Before OCR");
        String result = ocr.PerformOCR(IntentStorage.CurrentPhotoPath);
        Log.d("OCT", "After OCR");
        return result;
    }
}
