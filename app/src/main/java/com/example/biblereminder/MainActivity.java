package com.example.biblereminder;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton settingsFab;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        settingsFab = findViewById(R.id.settingsFab);
        bottomNav = findViewById(R.id.bottom_navigation);

        TextView dropdownToggle = findViewById(R.id.dropdownToggle);
        CardView formCard = findViewById(R.id.formCard);

        dropdownToggle.setOnClickListener(v -> {
            if (formCard.getVisibility() == View.GONE) {
                formCard.setVisibility(View.VISIBLE);
                dropdownToggle.setText("Hide Form ▲");
            } else {
                formCard.setVisibility(View.GONE);
                dropdownToggle.setText("Add Reading ▼");
            }
        });


        settingsFab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReminderSettingsActivity.class);
            startActivity(intent);
        });

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    return true; // already on home
                } else if (item.getItemId() == R.id.nav_history) {
                    Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
}
