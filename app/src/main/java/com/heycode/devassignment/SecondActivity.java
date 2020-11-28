package com.heycode.devassignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SecondActivity extends AppCompatActivity {

    com.heycode.devassignment.SecondActivity binding;
    int selectedOption;
    TextView textViewResult;
    TextInputLayout textInputLayout1, textInputLayout2;
    TextInputEditText scNo, customerNo;
    ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent bundle = getIntent();
        selectedOption = bundle.getIntExtra("getOption", 0);
        textViewResult = findViewById(R.id.textViewResult);
        textInputLayout1 = findViewById(R.id.filledTextField1);
        textInputLayout2 = findViewById(R.id.filledTextField2);
        scNo = findViewById(R.id.sc_number);
        customerNo = findViewById(R.id.customer_id);
        progress_bar = findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-staging.bankaks.com/task/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<Opt1> call = jsonPlaceHolderApi.getTasks();

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
            }

        });

    }
}