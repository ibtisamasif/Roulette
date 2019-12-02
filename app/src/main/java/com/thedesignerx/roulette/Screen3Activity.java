package com.thedesignerx.roulette;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class Screen3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen3);

        initializeView();
    }

    private void initializeView() {
        findViewById(R.id.button_won).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Screen3Activity.this, "won", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.button_lost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Screen3Activity.this, "lost", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
