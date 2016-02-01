package jmm.bc2c;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoActivity extends AppCompatActivity {

    Bitmap photoFileBitmap;
    Bitmap croppedFileBitmap;
    TextView previewTextView;

    ImageView photoImageView;
    Bitmap photoImageBitmap;
    Canvas photoImageCanvas;

    ImageView paintImageView;
    Bitmap paintImageBitmap;
    Canvas paintImageCanvas;

    int startX, currentX;
    int startY, currentY;
    int selectionLineWidth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        loadCurrentPhoto();
        bindImageViews();
    }

    private void loadCurrentPhoto() {
        Intent mainIntent = getIntent();
        int loadMode = mainIntent.getIntExtra("LOAD_MODE", 0);

        switch (loadMode) {

            case MainActivity.LOAD_FROM_CAMERA:

                File cameraTemp = new File(getExternalFilesDir(null), IntentStorage.CameraTempFile);
                IntentStorage.CurrentPhotoPath = cameraTemp.getAbsolutePath();

                break;

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
                    }
                }

                break;

            default:

                break;
        }
    }

    private void bindImageViews() {
        photoImageView = (ImageView) findViewById(R.id.photoImageView);
        paintImageView = (ImageView) findViewById(R.id.paintImageView);
        previewTextView = (TextView) findViewById(R.id.previewTextView);

        photoFileBitmap = BitmapFactory.decodeFile(IntentStorage.CurrentPhotoPath);

        photoImageBitmap = Bitmap.createBitmap(photoFileBitmap.getWidth(), photoFileBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        photoImageCanvas = new Canvas(photoImageBitmap);
        photoImageCanvas.drawBitmap(ImagePrep.Prepare(photoFileBitmap), 0, 0, null);
        photoImageView.setImageBitmap(photoImageBitmap);

        paintImageBitmap = Bitmap.createBitmap(photoFileBitmap.getWidth(), photoFileBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        paintImageCanvas = new Canvas(paintImageBitmap);
        paintImageView.setImageBitmap(paintImageBitmap);

        paintImageView.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View iv, MotionEvent event) {

                int action = event.getAction();
                int x = (int) event.getX();
                int y = (int) event.getY();

                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                        previewTextView.setText("ACTION_DOWN - " + x + " : " + y);
                        setStartPoints((ImageView) iv, photoImageBitmap, x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        previewTextView.setText("ACTION_MOVE - " + x + " : " + y);
                        drawSelection((ImageView) iv, photoImageBitmap, x, y);
                        break;
                    case MotionEvent.ACTION_UP:
                        previewTextView.setText("ACTION_UP^^ - " + x + " : " + y);
                        drawSelection((ImageView) iv, photoImageBitmap, x, y);
                        finalizeSelection();
                        break;
                }

                return true;
            }

        });
    }

    private void setStartPoints(ImageView iv, Bitmap bm, int x, int y) {
        if (x >= 0 && x <= iv.getWidth() && y >= 0 && y <= iv.getHeight()) {
            startX = (int)((double)x * ((double)bm.getWidth()/(double)iv.getWidth()));
            startY = (int)((double)y * ((double)bm.getHeight()/(double)iv.getHeight()));
            selectionLineWidth = (int)(3.0 * ((double)bm.getHeight()/(double)iv.getHeight()));
        }
    }

    private void drawSelection(ImageView iv, Bitmap bm, int x, int y) {
        if (x >= 0 && x <= iv.getWidth() && y >= 0 && y <= iv.getHeight()) {
            currentX = (int)((double)x * ((double)bm.getWidth()/(double)iv.getWidth()));
            currentY = (int)((double)y * ((double)bm.getHeight()/(double)iv.getHeight()));
        } else {
            return;
        }

        paintImageCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(selectionLineWidth);

        paintImageCanvas.drawRect(startX, startY, currentX, currentY, paint);
        paintImageView.invalidate();

        previewTextView.setText(x + " : " + y + " / " + iv.getWidth() + " : " + iv.getHeight() + "\n" +
            startX + "-" + currentX + " : " + startY + "-" + currentY + " / " + bm.getWidth() + " : " + bm.getHeight());
    }

    private void finalizeSelection(){
        if (currentX-startX < 10 || currentY-startY < 10) {
            return;
        }

        croppedFileBitmap = Bitmap.createBitmap(ImagePrep.Prepare(photoFileBitmap), startX, startY, currentX-startX, currentY-startY);

        File croppedFile = new File(getExternalFilesDir(null), IntentStorage.CroppedNameFile);

        try {
            FileOutputStream fos = new FileOutputStream(croppedFile);
            croppedFileBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("Error", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("Error", "Error accessing file: " + e.getMessage());
        }

        IntentStorage.CroppedNamePath = croppedFile.getAbsolutePath();
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
