package jmm.bc2c;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class PhotoActivity extends AppCompatActivity {

    String imgDecodableString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Intent mainIntent = getIntent();
        int loadMode = mainIntent.getIntExtra("LOAD_MODE", 0);

         switch (loadMode) {
            case MainActivity.LOAD_FROM_GALLERY:

                Uri selectedImage = IntentStorage.PhotoIntentData.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                ImageView imgView = (ImageView) findViewById(R.id.photoImageView);
                imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));

                break;

            case MainActivity.LOAD_FROM_CAMERA:

                break;
        }
    }
}
