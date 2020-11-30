package com.heycode.devassignment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    Spinner mSpinner;
    RelativeLayout relative_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] arrayOption = {"Select one opt", "Option 1", "Option 2", "Option 3"};

        mSpinner = findViewById(R.id.spinner_option);

        relative_lay = findViewById(R.id.relative_lay);
        //Select option features
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_menu_popup_item, arrayOption);

        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(0);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Toast.makeText(MainActivity.this, adapter.getItem(position), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    intent.putExtra("getOption", position);
                    startActivity(intent);
                } else {
                    Snackbar snackbar = Snackbar.make(relative_lay, "Please select One", Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.RED)
                            .setTextColor(Color.WHITE);
                    snackbar.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}