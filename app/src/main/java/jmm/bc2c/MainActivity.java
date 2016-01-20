package jmm.bc2c;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public final static int LOAD_FROM_CAMERA = 1;
    public final static int LOAD_FROM_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void useCamera(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, LOAD_FROM_CAMERA);
        //startActivity(Contact.CreateContactIntent("Robert Marszałek", "691 163 899"));
    }

    public void browsePictures(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, LOAD_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (resultCode == RESULT_OK && data != null) {

                IntentStorage.PhotoIntentData = data;

                Intent photoIntent = new Intent(this, PhotoActivity.class);
                photoIntent.putExtra("LOAD_MODE", requestCode);
                startActivity(photoIntent);

            } else {
                Toast.makeText(this, "You haven't picked the image", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

    }
}
