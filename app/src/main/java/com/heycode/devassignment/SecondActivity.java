package com.heycode.devassignment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SecondActivity extends AppCompatActivity {

    TextInputLayout textInputLayout1, textInputLayout2, textInputLayout3, textInputLayout4, textInputLayout5;
    TextInputEditText scNo, customerNo, phoneNo, name;
    AutoCompleteTextView monthSpinner;
    ProgressBar progress_bar;
    Retrofit retrofit;
    RelativeLayout relative_lay;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        textInputLayout1 = findViewById(R.id.filledTextField1);
        textInputLayout2 = findViewById(R.id.filledTextField2);
        textInputLayout3 = findViewById(R.id.filledTextField3);
        textInputLayout4 = findViewById(R.id.filledTextField4);
        textInputLayout5 = findViewById(R.id.filledTextField5);

        fab = findViewById(R.id.fab);
        fab.setEnabled(false);

        scNo = findViewById(R.id.sc_number);
        customerNo = findViewById(R.id.customer_id);
        monthSpinner = findViewById(R.id.selectDate);
        phoneNo = findViewById(R.id.phone_no);
        name = findViewById(R.id.full_name);

        progress_bar = findViewById(R.id.progress_bar);
        relative_lay = findViewById(R.id.relative_lay);
        progress_bar.setVisibility(View.VISIBLE);

        fab.setOnClickListener(v -> {
            ProgressDialog dialog = new ProgressDialog(SecondActivity.this, android.R.style.Theme_Material_Dialog);
            dialog.setMessage("Loading Bill\nInformation...");
            dialog.setIndeterminateDrawable(new FadingCircle());
            dialog.show();
        });

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api-staging.bankaks.com/task/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        switch (getIntent().getIntExtra("getOption", 0)) {
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
                textInputLayout4.setVisibility(View.VISIBLE);
                textInputLayout5.setVisibility(View.VISIBLE);
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
                    return;
                }
                Opt1 optData = response.body();
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
                    textInputLayout1.setHelperText(fieldObj[0].optString("hint_text"));
                    textInputLayout1.setHint(fieldObj[0].optString("placeholder"));

                    textInputLayout2.setHelperText(fieldObj[1].optString("hint_text"));
                    textInputLayout2.setHint(fieldObj[1].optString("placeholder"));
                    customerNo.setInputType(InputType.TYPE_CLASS_NUMBER);

                    //setting regex for SC No
                    scNo.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String inputScNo = scNo.getText().toString().trim();
//                            fab.setEnabled(fieldObj[0].optString("regex").matches(inputScNo));
                            fab.setEnabled((!inputScNo.isEmpty() && customerNo.getText().length() > 0));
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                    //setting regex customer ID
                    customerNo.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String inputCustomerId = customerNo.getText().toString().trim();
//                            fab.setEnabled(fieldObj[1].optString("regex").matches(inputCustomerId));
                            fab.setEnabled(!inputCustomerId.isEmpty() && scNo.getText().length() > 0);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    progress_bar.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                    return;
                }
                Opt1 optData = response.body();
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
                    customerNo.setInputType(InputType.TYPE_CLASS_NUMBER);

                    //setting regex Month
                    monthSpinner.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String inputMonth = monthSpinner.getText().toString().trim();
//                            fab.setEnabled((Pattern.compile(fieldObj[0].optString("regex"))).matcher(inputMonth).matches());
                            fab.setEnabled(!inputMonth.isEmpty() && customerNo.getText().length() > 0);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                    //setting regex for Customer ID
                    customerNo.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String inputCustomerId = customerNo.getText().toString().trim();
//                            fab.setEnabled((Pattern.compile(fieldObj[1].optString("regex"))).matcher(inputCustomerId).matches());
                            fab.setEnabled(!inputCustomerId.isEmpty() && monthSpinner.getText().length() > 0);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    JSONObject uiType = fieldObj[0].getJSONObject("ui_type");
                    JSONArray dateValues = uiType.getJSONArray("values");
                    ArrayList<String> dates = new ArrayList<>();
                    dates.add("Select the Month");

                    for (int i = 0; i < dateValues.length(); i++) {
                        JSONObject obj2 = new JSONObject(dateValues.getString(i));
                        dates.add(obj2.optString("name"));
                    }

                    //Select option features
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(
                                    SecondActivity.this,
                                    R.layout.dropdown_menu_popup_item, dates);
                    monthSpinner.setAdapter(adapter);
                    monthSpinner.setSelection(0);
                    monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    progress_bar.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                    return;
                }
                Opt1 optData = response.body();
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
                    //for Month
                    textInputLayout3.setHelperText(fieldObj[0].optString("hint_text"));
                    textInputLayout3.setHint(fieldObj[0].optString("placeholder"));

                    //for email
                    textInputLayout2.setHelperText(fieldObj[1].optString("hint_text"));
                    textInputLayout2.setHint(fieldObj[1].optString("placeholder"));
                    customerNo.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    //for Phone Number
                    textInputLayout4.setHelperText(fieldObj[2].optString("hint_text"));
                    textInputLayout4.setHint(fieldObj[2].optString("placeholder"));
                    //for Name
                    textInputLayout5.setHelperText(fieldObj[3].optString("hint_text"));
                    textInputLayout5.setHint(fieldObj[3].optString("placeholder"));

                    //setting regex Month
                    monthSpinner.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String inputMonth = monthSpinner.getText().toString().trim();
//                            fab.setEnabled((Pattern.compile(fieldObj[0].optString("regex"))).matcher(inputMonth).matches());
                            fab.setEnabled(!inputMonth.isEmpty() && customerNo.getText().length() > 0 && phoneNo.getText().length() > 0 && name.getText().length() > 0);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                    //setting regex for Email
                    customerNo.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String inputEmail = customerNo.getText().toString().trim();
                            //the regex has an closing bracket missing so i've used the default email matcher
//                            fab.setEnabled((Pattern.compile(fieldObj[1].optString("regex"))).matcher(inputEmail).matches());
                            fab.setEnabled((Patterns.EMAIL_ADDRESS).matcher(inputEmail).matches() && monthSpinner.getText().length() > 0 && phoneNo.getText().length() > 0 && name.getText().length() > 0);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                    //setting regex for Phone Number
                    phoneNo.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String inputPhone = phoneNo.getText().toString().trim();
                            fab.setEnabled((Pattern.compile(fieldObj[2].optString("regex"))).matcher(inputPhone).matches() && monthSpinner.getText().length() > 0 && customerNo.getText().length() > 0 && name.getText().length() > 0);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                    //setting regex for Name
                    name.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String inputName = name.getText().toString().trim();
//                            fab.setEnabled((Pattern.compile(fieldObj[3].optString("regex"))).matcher(inputName).matches());
                            fab.setEnabled(!inputName.isEmpty() && monthSpinner.getText().length() > 0 && phoneNo.getText().length() > 0 && customerNo.getText().length() > 0);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    JSONObject uiType = fieldObj[0].getJSONObject("ui_type");
                    JSONArray dateValues = uiType.getJSONArray("values");
                    ArrayList<String> dates = new ArrayList<>();
                    dates.add("Select the Month");

                    for (int i = 0; i < dateValues.length(); i++) {
                        JSONObject obj2 = new JSONObject(dateValues.getString(i));
                        dates.add(obj2.optString("name"));
                    }

                    //Select option features
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(
                                    SecondActivity.this,
                                    android.R.layout.simple_expandable_list_item_1, dates);

                    monthSpinner.setAdapter(adapter);
                    monthSpinner.setSelection(0);
                    monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    progress_bar.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Opt1> call, Throwable t) {
                Toast.makeText(SecondActivity.this, "Failed To load", Toast.LENGTH_SHORT).show();
                progress_bar.setVisibility(View.INVISIBLE);
            }

        });
    }

}