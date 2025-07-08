package com.example.biblereminder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class HistoryFragment extends Fragment {

    private ListView historyList;
    private Button deleteAllButton;
    private BibleDbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        historyList = view.findViewById(R.id.historyList);
        deleteAllButton = view.findViewById(R.id.clearButton);
        dbHelper = new BibleDbHelper(requireContext());
        db = dbHelper.getReadableDatabase();

        loadHistory();

        deleteAllButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Deletion")
                    .setMessage("Delete all readings?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db = dbHelper.getWritableDatabase();
                        db.delete("readings", null, null);
                        loadHistory();
                        Toast.makeText(requireContext(), "All readings deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return view;
    }

    private void loadHistory() {
        Cursor cursor = db.query("readings", null, null, null, null, null, "date DESC");
        String[] from = {"date", "book", "chapter", "start_verse", "end_verse"};
        int[] to = {R.id.dateView, R.id.bookView, R.id.chapterView, R.id.startVerseView, R.id.endVerseView};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                requireContext(), R.layout.history_item, cursor, from, to, 0);
        historyList.setAdapter(adapter);
    }
}
