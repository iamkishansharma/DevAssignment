package com.heycode.devassignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] arrayOption = {"Select one opt", "Option 1", "Option 2", "Option 3"};

        mSpinner = findViewById(R.id.spinner_option);

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
                    switch (position) {
                        case 1:
                            Toast.makeText(MainActivity.this, adapter.getItem(position), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                            intent.putExtra("getOption", position);
                            startActivity(intent);
                            break;
                        case 2:
                            //TODO::2nd
                            break;
                        case 3:
                            //TODO::3rd
                            break;
                        default:
                            Toast.makeText(MainActivity.this, "Choose any option", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please select one", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}