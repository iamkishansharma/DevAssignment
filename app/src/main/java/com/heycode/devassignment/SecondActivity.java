package com.heycode.devassignment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SecondActivity extends AppCompatActivity {

    int selectedOption;
    TextView textViewResult;
    TextInputLayout textInputLayout1, textInputLayout2,textInputLayout3;
    TextInputEditText scNo, customerNo;
    ProgressBar progress_bar;
    Retrofit retrofit;
    RelativeLayout relative_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        textViewResult = findViewById(R.id.textViewResult);
        textInputLayout1 = findViewById(R.id.filledTextField1);
        textInputLayout2 = findViewById(R.id.filledTextField2);

        textInputLayout3 = findViewById(R.id.filledTextField3);

        scNo = findViewById(R.id.sc_number);
        customerNo = findViewById(R.id.customer_id);
        progress_bar = findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.VISIBLE);
        relative_lay = findViewById(R.id.relative_lay);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api-staging.bankaks.com/task/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        switch (getIntent().getIntExtra("getOption",0)) {
            case 1:
                callPageOne();
                break;
            case 2:
                textInputLayout3.setVisibility(View.VISIBLE);
                textInputLayout1.setVisibility(View.INVISIBLE);
                callPageTwo();
                break;
            case 3:
                textInputLayout3.setVisibility(View.VISIBLE);
                textInputLayout1.setVisibility(View.INVISIBLE);
                callPageThree();
                break;
            default:
                Snackbar snackbar = Snackbar.make(relative_lay, "Sorry!", Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(getResources().getColor(R.color.purple_200, null))
                        .setTextColor(Color.WHITE);
                snackbar.show();
                //
        }

    }

    void callPageOne() {
        ApiHolder apiHolder = retrofit.create(ApiHolder.class);
        Call<Opt1> call = apiHolder.getTasks1();

        call.enqueue(new Callback<Opt1>() {
            @Override
            public void onResponse(Call<Opt1> call, Response<Opt1> response) {
                if (!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }
                Opt1 optData = response.body();
                String content = "";
                try {
                    String jsonString = optData.getResult().getAsJsonObject().toString();
                    JSONObject obj = new JSONObject(jsonString);

                    //filds
                    JSONObject[] fieldObj = new JSONObject[2];
                    JSONArray jsonArray = obj.getJSONArray("fields");
                    for (int i = 0; i < obj.optInt("number_of_fields"); i++) {
                        JSONObject obj2 = new JSONObject(jsonArray.getString(i));
                        fieldObj[i] = obj2;
                    }

                    getSupportActionBar().setTitle(obj.optString("screen_title"));
                    textInputLayout1.setHelperText(fieldObj[0].optString("hint_text"));
                    textInputLayout1.setHint(fieldObj[0].optString("placeholder"));

                    textInputLayout2.setHelperText(fieldObj[1].optString("hint_text"));
                    textInputLayout2.setHint(fieldObj[1].optString("placeholder"));

                    content += "Status " + optData.getStatus() + "\nMessages: " + optData.getMessage() + "\nTitle: " + obj.optString("screen_title") + "\n\n";

                    progress_bar.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textViewResult.append(content);
            }

            @Override
            public void onFailure(Call<Opt1> call, Throwable t) {
                Toast.makeText(SecondActivity.this, "Failed To load", Toast.LENGTH_SHORT).show();
                progress_bar.setVisibility(View.INVISIBLE);
            }

        });
    }

    void callPageTwo() {
        ApiHolder apiHolder = retrofit.create(ApiHolder.class);
        Call<Opt1> call = apiHolder.getTasks2();

        call.enqueue(new Callback<Opt1>() {
            @Override
            public void onResponse(Call<Opt1> call, Response<Opt1> response) {
                if (!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }
                Opt1 optData = response.body();
                String content = "";
                try {
                    String jsonString = optData.getResult().getAsJsonObject().toString();
                    JSONObject obj = new JSONObject(jsonString);

                    //fields
                    JSONObject[] fieldObj = new JSONObject[2];
                    JSONArray jsonArray = obj.getJSONArray("fields");
                    for (int i = 0; i < obj.optInt("number_of_fields"); i++) {
                        JSONObject obj2 = new JSONObject(jsonArray.getString(i));
                        fieldObj[i] = obj2;
                    }

                    getSupportActionBar().setTitle(obj.optString("screen_title"));
                    textInputLayout3.setHelperText(fieldObj[0].optString("hint_text"));
                    textInputLayout3.setHint(fieldObj[0].optString("placeholder"));

                    textInputLayout2.setHelperText(fieldObj[1].optString("hint_text"));
                    textInputLayout2.setHint(fieldObj[1].optString("placeholder"));

                    JSONObject uiType = fieldObj[0].getJSONObject("ui_type");
                    JSONArray dateValues = uiType.getJSONArray("values");
                    ArrayList<String> dates = new ArrayList<>();
                    dates.add("Select the Month");

                    for(int i=0;i<dateValues.length();i++){
                        JSONObject obj2 = new JSONObject(dateValues.getString(i));
                        dates.add(obj2.optString("name"));
                    }

                    //Select option features
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(
                                    SecondActivity.this,
                                    R.layout.dropdown_menu_popup_item, dates);

                    AutoCompleteTextView mSpinner = findViewById(R.id.selectDate);
                    mSpinner.setAdapter(adapter);
                    mSpinner.setSelection(0);
                    mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position > 0) {
                                Toast.makeText(SecondActivity.this, adapter.getItem(position), Toast.LENGTH_LONG).show();
                            } else {
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });






                    content += "Status " + optData.getStatus() + "\nMessages: " + optData.getMessage() + "\nTitle: " + obj.optString("screen_title") + "\n\n";

                    progress_bar.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textViewResult.append(content);
            }

            @Override
            public void onFailure(Call<Opt1> call, Throwable t) {
                Toast.makeText(SecondActivity.this, "Failed To load", Toast.LENGTH_SHORT).show();
                progress_bar.setVisibility(View.INVISIBLE);
            }

        });
    }

    void callPageThree() {
        ApiHolder apiHolder = retrofit.create(ApiHolder.class);
        Call<Opt1> call = apiHolder.getTasks3();
        call.enqueue(new Callback<Opt1>() {
            @Override
            public void onResponse(Call<Opt1> call, Response<Opt1> response) {
                if (!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }
                Opt1 optData = response.body();
                String content = "";
                try {
                    String jsonString = optData.getResult().getAsJsonObject().toString();
                    JSONObject obj = new JSONObject(jsonString);

                    //fields
                    JSONObject[] fieldObj = new JSONObject[4];
                    JSONArray jsonArray = obj.getJSONArray("fields");
                    for (int i = 0; i < obj.optInt("number_of_fields"); i++) {
                        JSONObject obj2 = new JSONObject(jsonArray.getString(i));
                        fieldObj[i] = obj2;
                    }

                    getSupportActionBar().setTitle(obj.optString("screen_title"));
                    textInputLayout3.setHelperText(fieldObj[0].optString("hint_text"));
                    textInputLayout3.setHint(fieldObj[0].optString("placeholder"));

                    textInputLayout2.setHelperText(fieldObj[1].optString("hint_text"));
                    textInputLayout2.setHint(fieldObj[1].optString("placeholder"));

                    JSONObject uiType = fieldObj[0].getJSONObject("ui_type");
                    JSONArray dateValues = uiType.getJSONArray("values");
                    ArrayList<String> dates = new ArrayList<>();
                    dates.add("Select the Month");

                    for(int i=0;i<dateValues.length();i++){
                        JSONObject obj2 = new JSONObject(dateValues.getString(i));
                        dates.add(obj2.optString("name"));
                    }

                    //Select option features
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(
                                    SecondActivity.this,
                                    android.R.layout.simple_expandable_list_item_1, dates);

                    AutoCompleteTextView mSpinner = findViewById(R.id.selectDate);
                    mSpinner.setAdapter(adapter);
                    mSpinner.setSelection(0);
                    mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position > 0) {
                                Toast.makeText(SecondActivity.this, adapter.getItem(position), Toast.LENGTH_LONG).show();
                            } else {
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });


                    content += "Status " + optData.getStatus() + "\nMessages: " + optData.getMessage() + "\nTitle: " + obj.optString("screen_title") + "\n\n";

                    progress_bar.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textViewResult.append(content);
            }

            @Override
            public void onFailure(Call<Opt1> call, Throwable t) {
                Toast.makeText(SecondActivity.this, "Failed To load", Toast.LENGTH_SHORT).show();
                progress_bar.setVisibility(View.INVISIBLE);
            }

        });
    }
}