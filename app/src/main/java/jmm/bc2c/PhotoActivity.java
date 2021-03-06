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
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoActivity extends AppCompatActivity {

    Bitmap photoFileBitmap;
    Bitmap photoFileScaledBitmap;

    ImageView photoImageView;
    Bitmap photoImageBitmap;
    Canvas photoImageCanvas;

    ImageView paintImageView;
    Bitmap paintImageBitmap;
    Canvas paintImageCanvas;

    int startX, currentX;
    int startY, currentY;
    int selectionLineWidth;

    Boolean croppedNameFlag = false;
    Boolean croppedPhoneFlag = false;

    ProgressDialog progressDialog;

    public static int ImageSize;
    public static float ImageRatio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        ImageSize = Math.max(height, width);

        loadCurrentPhoto();
        bindImageViews();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("startX", startX);
        outState.putInt("startY", startY);
        outState.putInt("currentX", currentX);
        outState.putInt("currentY", currentY);
        outState.putInt("selectionLineWidth", selectionLineWidth);
        outState.putBoolean("croppedNameFlag", this.croppedNameFlag);
        outState.putBoolean("croppedPhoneFlag", this.croppedPhoneFlag);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        startX = savedState.getInt("startX");
        startY = savedState.getInt("startY");
        currentX = savedState.getInt("currentX");
        currentY = savedState.getInt("currentY");
        selectionLineWidth = savedState.getInt("selectionLineWidth");
        croppedNameFlag = savedState.getBoolean("croppedNameFlag");
        croppedPhoneFlag = savedState.getBoolean("croppedPhoneFlag");
        redrawSelections();
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

        photoFileBitmap = BitmapFactory.decodeFile(IntentStorage.CurrentPhotoPath);
        ImageRatio = Math.min(
                (float) ImageSize / photoFileBitmap.getWidth(),
                (float) ImageSize / photoFileBitmap.getHeight());
        photoFileScaledBitmap = Bitmap.createScaledBitmap(photoFileBitmap,
                Math.round(ImageRatio * photoFileBitmap.getWidth()),
                Math.round(ImageRatio * photoFileBitmap.getHeight()), true);

        photoImageBitmap = Bitmap.createBitmap(photoFileScaledBitmap.getWidth(),
                photoFileScaledBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        photoImageCanvas = new Canvas(photoImageBitmap);
        photoImageCanvas.drawBitmap(photoFileScaledBitmap, 0, 0, null);
        photoImageView.setImageBitmap(photoImageBitmap);

        paintImageBitmap = Bitmap.createBitmap(photoFileScaledBitmap.getWidth(),
                photoFileScaledBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        paintImageCanvas = new Canvas(paintImageBitmap);
        paintImageView.setImageBitmap(paintImageBitmap);

        paintImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View iv, MotionEvent event) {

                int action = event.getAction();
                int x = (int) event.getX();
                int y = (int) event.getY();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        //Log.d("Event", "ACTION_DOWN - " + x + " : " + y);
                        setStartPoints((ImageView) iv, photoImageBitmap, x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Log.d("Event", "ACTION_MOVE - " + x + " : " + y);
                        drawSelection((ImageView) iv, photoImageBitmap, x, y);
                        break;
                    case MotionEvent.ACTION_UP:
                        //Log.d("Event", "ACTION_UP - " + x + " : " + y);
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
            startX = (int)((double)x * ((double)bm.getWidth() / (double)iv.getWidth()));
            startY = (int)((double)y * ((double)bm.getHeight() / (double)iv.getHeight()));
            selectionLineWidth = (int)(3.0 * ((double)bm.getHeight() / (double)iv.getHeight()));
        }
    }

    private void drawSelection(ImageView iv, Bitmap bm, int x, int y) {
        if (x >= 0 && x <= iv.getWidth() && y >= 0 && y <= iv.getHeight()) {
            currentX = (int)((double)x * ((double)bm.getWidth() / (double)iv.getWidth()));
            currentY = (int)((double)y * ((double)bm.getHeight() / (double)iv.getHeight()));
        } else {
            return;
        }

        redrawSelections();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(selectionLineWidth);

        paintImageCanvas.drawRect(startX, startY, currentX, currentY, paint);
        paintImageView.invalidate();

        //Log.d("Event", x + " : " + y + " / " + iv.getWidth() + " : " + iv.getHeight() + "\n" +
                //startX + "-" + currentX + " : " + startY + "-" + currentY + " / " + bm.getWidth() + " : " + bm.getHeight());
    }

    private void redrawSelections() {
        paintImageCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(selectionLineWidth);

        if (croppedNameFlag) {
            int name_x1 = Math.round(IntentStorage.CroppedNameX * ImageRatio);
            int name_y1 = Math.round(IntentStorage.CroppedNameY * ImageRatio);
            int name_x2 = Math.round((IntentStorage.CroppedNameX + IntentStorage.CroppedNameW) * ImageRatio);
            int name_y2 = Math.round((IntentStorage.CroppedNameY + IntentStorage.CroppedNameH) * ImageRatio);
            paintImageCanvas.drawRect(name_x1, name_y1, name_x2, name_y2, paint);
        }

        if (croppedPhoneFlag) {
            int phone_x1 = Math.round(IntentStorage.CroppedPhoneX * ImageRatio);
            int phone_y1 = Math.round(IntentStorage.CroppedPhoneY * ImageRatio);
            int phone_x2 = Math.round((IntentStorage.CroppedPhoneX + IntentStorage.CroppedPhoneW) * ImageRatio);
            int phone_y2 = Math.round((IntentStorage.CroppedPhoneY + IntentStorage.CroppedPhoneH) * ImageRatio);
            paintImageCanvas.drawRect(phone_x1, phone_y1, phone_x2, phone_y2, paint);
        }

        paintImageView.invalidate();
    }

    private void finalizeSelection() {
        if (currentX - startX < 10 || currentY - startY < 10) {
            redrawSelections();
            return;
        }

        SelectionDialogFragment sdf = new SelectionDialogFragment();
        sdf.show(getSupportFragmentManager(), "");
    }

    public void saveSelection(int whichItem) {
        Bitmap croppedFileBitmap;
        File croppedFile;

        int x = Math.round(startX / ImageRatio);
        int y = Math.round(startY / ImageRatio);
        int w = Math.round((currentX - startX) / ImageRatio);
        int h = Math.round((currentY - startY) / ImageRatio);

        switch (whichItem) {
            case 1:
                croppedFileBitmap = Bitmap.createBitmap(photoFileBitmap, x, y, w, h);
                croppedFileBitmap = ImagePrep.Prepare(croppedFileBitmap);
                croppedFile = saveBitmap(croppedFileBitmap, IntentStorage.CroppedNameFile);
                IntentStorage.CroppedNamePath = croppedFile.getAbsolutePath();
                croppedNameFlag = true;
                IntentStorage.CroppedNameX = x;
                IntentStorage.CroppedNameY = y;
                IntentStorage.CroppedNameW = w;
                IntentStorage.CroppedNameH = h;
                break;

            case 2:
                croppedFileBitmap = Bitmap.createBitmap(photoFileBitmap, x, y, w, h);
                croppedFileBitmap = ImagePrep.Prepare(croppedFileBitmap);
                croppedFile = saveBitmap(croppedFileBitmap, IntentStorage.CroppedPhoneFile);
                IntentStorage.CroppedPhonePath = croppedFile.getAbsolutePath();
                croppedPhoneFlag = true;
                IntentStorage.CroppedPhoneX = x;
                IntentStorage.CroppedPhoneY = y;
                IntentStorage.CroppedPhoneW = w;
                IntentStorage.CroppedPhoneH = h;
                break;
        }

        redrawSelections();
    }

    private File saveBitmap(Bitmap bitmap, String filename) {
        File file = new File(getExternalFilesDir(null), filename);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            //Log.e("Exception", "File not found: " + e.getMessage());
        } catch (IOException e) {
            //Log.e("Exception", "Error accessing file: " + e.getMessage());
        }

        return file;
    }

    public void BackButtonClick(View v) {
        this.finish();
    }

    public void DoOCRButtonClick(View v) {
        if (!croppedNameFlag) {
            Toast.makeText(this, getString(R.string.select_name), Toast.LENGTH_LONG).show();
            return;
        } else if (!croppedPhoneFlag) {
            Toast.makeText(this, getString(R.string.select_phone), Toast.LENGTH_LONG).show();
            return;
        }

        OCRTask ocrt = new OCRTask(this);
        ocrt.execute();
    }

    public void ShowResult(String[] results) {
        Toast.makeText(this,"Im: "+ results[0]+ " nr: " +results[1],Toast.LENGTH_LONG).show();
    }

    public void LaunchProgressDialog(String waitText, String reasonText) {
        progressDialog = ProgressDialog.show(PhotoActivity.this, waitText, reasonText, true);
        progressDialog.setCancelable(false);
    }

    public void KillProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
