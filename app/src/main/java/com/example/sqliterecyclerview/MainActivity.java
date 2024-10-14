package com.example.sqliterecyclerview;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseHelper databaseHelper;
    ArrayList<String> studentList;
    StudentAdapter adapter;
    EditText editTextName;
    Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        editTextName = findViewById(R.id.editTextName);
        buttonAdd = findViewById(R.id.buttonAdd);

        databaseHelper = new DatabaseHelper(this);
        studentList = new ArrayList<>();

        // Load data from database
        loadData();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(this, studentList);
        recyclerView.setAdapter(adapter);

        // Menambahkan data ketika buttonAdd diklik
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                if (!name.isEmpty()) {
                    boolean isInserted = databaseHelper.addStudent(name);
                    if (isInserted) {
                        // Data berhasil ditambahkan, refresh RecyclerView
                        studentList.clear(); // Hapus data lama dari list
                        loadData(); // Ambil data terbaru dari database
                        adapter.notifyDataSetChanged(); // Beritahu adapter untuk memperbarui tampilan
                        editTextName.setText(""); // Kosongkan input setelah menambah
                        Toast.makeText(MainActivity.this, "Data Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Data not added", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handling window insets for proper padding (if required)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Method untuk memuat data dari database ke dalam ArrayList studentList
    private void loadData() {
        Cursor cursor = databaseHelper.getAllStudents();
        if (cursor.moveToFirst()) {
            do {
                studentList.add(cursor.getString(1)); // Kolom 1 adalah nama
            } while (cursor.moveToNext());
        }
    }
}
