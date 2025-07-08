
// HistoryActivity.java
package com.example.biblereminder;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HistoryActivity extends AppCompatActivity {

    private ListView historyList;
    private SQLiteDatabase db;
    private BibleDbHelper dbHelper;
    private BottomNavigationView bottomNav;
    private Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyList = findViewById(R.id.historyList);
        clearButton = findViewById(R.id.clearButton);
        bottomNav = findViewById(R.id.bottom_navigation);
        dbHelper = new BibleDbHelper(this);
        db = dbHelper.getWritableDatabase();

        loadHistory();

        clearButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to clear all history?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.delete("readings", null, null);
                            loadHistory();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        bottomNav.setSelectedItemId(R.id.nav_history);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                }
                return item.getItemId() == R.id.nav_history;
            }
        });
    }

    private void loadHistory() {
        Cursor cursor = db.query("readings", null, null, null, null, null, "date DESC");
        String[] from = {"date", "book", "chapter", "start_verse", "end_verse"};
        int[] to = {R.id.dateView, R.id.bookView, R.id.chapterView, R.id.startVerseView, R.id.endVerseView};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this, R.layout.history_item, cursor, from, to, 0);

        historyList.setAdapter(adapter);
    }
}