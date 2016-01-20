package jmm.bc2c;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;

public class PhotoActivity extends AppCompatActivity {

    ImageView photoImageView;
    Bitmap photoImageBitmap;
    String imgDecodableString;

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

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();

                    photoImageBitmap = BitmapFactory.decodeFile(imgDecodableString);
                    photoImageView.setImageBitmap(photoImageBitmap);
                }

                break;

            case MainActivity.LOAD_FROM_CAMERA:

                //Bundle extras = IntentStorage.PhotoIntentData.getExtras();
                //photoImageBitmap = (Bitmap) extras.get("data");

                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals(IntentStorage.CameraTempFile)) {
                        f = temp;
                        break;
                    }
                }

                photoImageBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                photoImageView.setImageBitmap(photoImageBitmap);

                break;
        }
    }
}
