package jmm.bc2c;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

public class Instructions extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        webView = (WebView)findViewById(R.id.instructions);

        webView.getSettings();
        webView.setBackgroundColor(0x212121);
        webView.loadData(getString(R.string.instructions), "text/html", null);

        Toast.makeText(this, getString(R.string.back_to_close), Toast.LENGTH_SHORT).show();
    }

}
