package jmm.bc2c;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public final static int LOAD_FROM_CAMERA = 1;
    public final static int LOAD_FROM_GALLERY = 2;

    ProgressDialog progressDialog = null;

    public static String LANG = "eng";
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().getPath() +
            "/Android/data/jmm.bc2c/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LANG = getString(R.string.tess_lang);

        CheckAssetsTask checkAssetsTaskTask = new CheckAssetsTask(this);
        checkAssetsTaskTask.execute();

        checkFirstRunAndShowInstructions();
    }

    public void useCamera(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File cameraTemp = new File(getExternalFilesDir(null), IntentStorage.CameraTempFile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraTemp));
        startActivityForResult(cameraIntent, LOAD_FROM_CAMERA);
        //startActivity(Contact.CreateContactIntent("Robert Marszałek", "691 163 899"));
    }

    public void browsePictures(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, LOAD_FROM_GALLERY);
    }

    public void getInstructions(View view) {
        showInstructions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == RESULT_OK) {
                IntentStorage.PhotoIntentData = data;
                Intent photoIntent = new Intent(this, PhotoActivity.class);
                photoIntent.putExtra("LOAD_MODE", requestCode);
                startActivity(photoIntent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

    }

    public void checkFirstRunAndShowInstructions() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
                showInstructions();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    private void showInstructions() {
        Intent instructions = new Intent(this, Instructions.class);
        startActivity(instructions);
    }

    public void PerformCopyingAssets(){
        CopyAssetsTask copyAssetsTask = new CopyAssetsTask(this);
        copyAssetsTask.execute();
    }

    public void LaunchProgressDialog(String waitText, String reasonText) {
        progressDialog = ProgressDialog.show(MainActivity.this, waitText, reasonText, true);
        progressDialog.setCancelable(false);
    }

    public void KillProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
