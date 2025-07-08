package com.example.biblereminder;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.cardview.widget.CardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class HomeFragment extends Fragment {

    private Spinner bookSpinner;
    private EditText chapterInput, startVerseInput, endVerseInput;
    private Button saveButton;
    private TextView dropdownToggle;
    private CardView formCard;
    private BibleDbHelper dbHelper;

    private FloatingActionButton settingsFab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bookSpinner = view.findViewById(R.id.bookSpinner);
        chapterInput = view.findViewById(R.id.chapterInput);
        startVerseInput = view.findViewById(R.id.startVerseInput);
        endVerseInput = view.findViewById(R.id.endVerseInput);
        saveButton = view.findViewById(R.id.saveButton);
        dropdownToggle = view.findViewById(R.id.dropdownToggle);
        formCard = view.findViewById(R.id.formCard);
        dbHelper = new BibleDbHelper(requireContext());
        settingsFab = view.findViewById(R.id.settingsFab);

        // Populate Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.bible_books,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bookSpinner.setAdapter(adapter);

        settingsFab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ReminderSettingsActivity.class);
            startActivity(intent);
                });
        
        dropdownToggle.setOnClickListener(v -> {
            dropdownToggle.setText(formCard.getVisibility() == View.GONE ? "Add Reading ▲" : "Add Reading ▼");
            formCard.setVisibility(formCard.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        });

        saveButton.setOnClickListener(v -> {
            String book = bookSpinner.getSelectedItem().toString();
            String chapter = chapterInput.getText().toString();
            String startVerse = startVerseInput.getText().toString();
            String endVerse = endVerseInput.getText().toString();

            if (book.isEmpty() || chapter.isEmpty() || startVerse.isEmpty() || endVerse.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String currentDateAndTime = sdf.format(new Date());
            values.put("date", currentDateAndTime);
            values.put("book", book);
            values.put("chapter", chapter);
            values.put("start_verse", startVerse);
            values.put("end_verse", endVerse);

            long result = db.insert("readings", null, values);
            if (result != -1) {
                Toast.makeText(requireContext(), "Reading saved", Toast.LENGTH_SHORT).show();
                chapterInput.setText("");
                startVerseInput.setText("");
                endVerseInput.setText("");
            } else {
                Toast.makeText(requireContext(), "Failed to save", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
