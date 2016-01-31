package jmm.bc2c;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class Instructions extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        webView = (WebView)findViewById(R.id.instructions);
        String instructions = "<html><body>" +
                         "<b>Press back to close this screen</b>" +
                         "</body></html>";
        webView.loadData(instructions, "text/html", null);

    }

}
