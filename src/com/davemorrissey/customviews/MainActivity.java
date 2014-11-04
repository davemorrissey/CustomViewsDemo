package com.davemorrissey.customviews;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.ripples).setOnClickListener(this);
        findViewById(R.id.missileCommand).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ripples) {
            Intent intent = new Intent(this, RipplesActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.missileCommand) {
            Intent intent = new Intent(this, MissileCommandActivity.class);
            startActivity(intent);
        }
    }
}
