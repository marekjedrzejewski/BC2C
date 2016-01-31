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
                         "<p>Follow these instructions to get the best results.</p>" +
                         "<h2>Camera position</h2>" +
                         "<p>Camera should look straight at the surface. Change of perspective" +
                         " or rotation may lead to weird results. </p>" +
                         "<h2>Light</h2>" +
                         "<p>It's best to take photograph in good lighting conditions to avoid" +
                         " extensive noise.</p>" +
                         "<h2>Contrast</h2>" +
                         "<p>If text is embossed or otherwise hard to read on the photo" +
                         " it probably also won't be readable by app.</p>" +
                         "</body></html>";
        webView.loadData(instructions, "text/html", null);

    }

}
