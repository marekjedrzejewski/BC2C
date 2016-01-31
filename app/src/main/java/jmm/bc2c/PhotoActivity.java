package jmm.bc2c;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;

public class PhotoActivity extends AppCompatActivity {

    ImageView photoImageView;
    Bitmap photoImageBitmap;
    Bitmap tempCanvasBitmap;
    Canvas photoImageCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        photoImageView = (ImageView) findViewById(R.id.photoImageView);

        Intent mainIntent = getIntent();
        int loadMode = mainIntent.getIntExtra("LOAD_MODE", 0);

         switch (loadMode) {

            case MainActivity.LOAD_FROM_GALLERY:

                if (IntentStorage.PhotoIntentData != null) {
                    Uri selectedImage = IntentStorage.PhotoIntentData.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

                    if (cursor != null) {
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        IntentStorage.CurrentPhotoPath = cursor.getString(columnIndex);
                        cursor.close();

                        photoImageBitmap = BitmapFactory.decodeFile(IntentStorage.CurrentPhotoPath);

                        tempCanvasBitmap = Bitmap.createBitmap(photoImageBitmap.getWidth(), photoImageBitmap.getHeight(), Bitmap.Config.RGB_565);
                        photoImageCanvas = new Canvas(tempCanvasBitmap);
                        photoImageCanvas.drawBitmap(ImagePrep.Prepare(photoImageBitmap), 0, 0, null);

                        photoImageView.setImageDrawable(new BitmapDrawable(getResources(), tempCanvasBitmap));
                    }
                }

                break;

            case MainActivity.LOAD_FROM_CAMERA:

                File cameraTemp = new File(getExternalFilesDir(null), IntentStorage.CameraTempFile);
                IntentStorage.CurrentPhotoPath = cameraTemp.getAbsolutePath();

                photoImageBitmap = BitmapFactory.decodeFile(IntentStorage.CurrentPhotoPath);

                tempCanvasBitmap = Bitmap.createBitmap(photoImageBitmap.getWidth(), photoImageBitmap.getHeight(), Bitmap.Config.RGB_565);
                photoImageCanvas = new Canvas(tempCanvasBitmap);
                photoImageCanvas.drawBitmap(photoImageBitmap, 0, 0, null);

                photoImageView.setImageDrawable(new BitmapDrawable(getResources(), tempCanvasBitmap));

                break;

        }
    }
}
