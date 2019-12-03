package com.thedesignerx.roulette;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class Screen3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen3);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initializeView();
    }

    private void initializeView() {
        findViewById(R.id.imageView_settingButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Screen3Activity.this, "Settings screen", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.imageView_closeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(Screen3Activity.this, FloatingViewService.class));
                finish();
            }
        });

        findViewById(R.id.button_won).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Screen3Activity.this, "Won pressed", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.button_lost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Screen3Activity.this, "Lost pressed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
