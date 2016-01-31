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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.widget.Toast;

import java.io.File;

public class PhotoActivity extends AppCompatActivity {

    ImageView photoImageView;
    Bitmap photoImageBitmap;
    Bitmap tempCanvasBitmap;
    Canvas photoImageCanvas;

    ProgressDialog progressDialog;

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

    public void BackButtonClick(View v){
        this.finish();
    }

    public void DoOCRButtonClick(View v){

        OCRTask ocrt = new OCRTask(this);
        ocrt.execute();

    }

    public void ShowResult(String result){
        Toast.makeText(this,result,Toast.LENGTH_LONG).show();
    }

    public void LaunchProgressDialog(String waitText, String reasonText) {
        progressDialog = ProgressDialog.show(PhotoActivity.this, waitText, reasonText, true);
        progressDialog.setCancelable(false);
    }

    public void KillProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
