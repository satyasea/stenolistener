package com.blake;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
//imnports the R file generated in the package on the manifest
import com.nuance.nmdp.sample.*;

public class HelloActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_yo);
        Toast.makeText(getBaseContext(), "Yo dude HelloActivity........", Toast.LENGTH_LONG).show();
    }
}
