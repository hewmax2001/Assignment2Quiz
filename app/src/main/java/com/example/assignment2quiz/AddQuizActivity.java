package com.example.assignment2quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class AddQuizActivity extends AppCompatActivity {

    private AutoCompleteTextView autCategory, autDifficulty, autType;
    private ArrayAdapter<String> catAdapter, difAdapter, typeAdapter;
    private Button btnGet, btnStartDate, btnEndDate;
    private TextView txtStart, txtEnd;
    private EditText txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quiz);
        setElements();
        Handler.start();
        TDBAPI.start(getApplicationContext());
        TDBAPI.setCategorySchema(new APICallback() {
            @Override
            public void onCallback(JSONObject response) {
                updateCategories();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    protected void onResume() {
        super.onResume();
        setDefaults();
    }

    private void setElements() {
        txtStart = findViewById(R.id.txt_start_date);
        txtEnd = findViewById(R.id.txt_end_date);

        txtName = findViewById(R.id.txt_quiz_name);

        btnGet = findViewById(R.id.btn_JSON);
        // False until categories have been gotten
        btnGet.setEnabled(false);
        btnStartDate = findViewById(R.id.btn_start_date);
        btnEndDate = findViewById(R.id.btn_end_date);

        autCategory = findViewById(R.id.aut_category);
        autDifficulty = findViewById(R.id.aut_difficulty);
        autType = findViewById(R.id.aut_type);

        difAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.answer_dropdown_item, Quiz.difficulties);
        typeAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.answer_dropdown_item, Quiz.types);

        autDifficulty.setAdapter(difAdapter);
        autType.setAdapter(typeAdapter);

        autCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Data: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        setButtonEvents();
    }

    private void setButtonEvents() {
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!compareDates()) {
                    Toast.makeText(getApplicationContext(), "Start date cannot be greater than end date!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!validation()) {
                    Toast.makeText(getApplicationContext(), "Quiz must have a name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String amount = "10";
                String name = txtName.getText().toString();
                String cat = autCategory.getText().toString();
                String dif = autDifficulty.getText().toString();
                String type = autType.getText().toString();
                String start = txtStart.getText().toString();
                String end = txtEnd.getText().toString();

                Handler.createQuiz(new Callback() {
                    @Override
                    public void onCallback(DataSnapshot snap) {

                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(getApplicationContext(), "Quiz with the same name already exists!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }, name, amount, cat, dif, type, start, end);
                setDefaults();
            }

        });

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(AddQuizActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar c = new GregorianCalendar(year, month, dayOfMonth);
                        Date date = c.getTime();
                        SimpleDateFormat format = new SimpleDateFormat(Handler.DATE_FORMAT);
                        String f = format.format(date);
                        txtStart.setText(f);
                    }
                }, year, month, day);

                dialog.show();
            }
        });

        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(AddQuizActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar c = new GregorianCalendar(year, month, dayOfMonth);
                        Date date = c.getTime();
                        SimpleDateFormat format = new SimpleDateFormat(Handler.DATE_FORMAT);
                        String f = format.format(date);
                        txtEnd.setText(f);
                    }
                }, year, month, day);

                dialog.show();
            }
        });
    }

    private void setDefaults() {
        try {
            txtStart.setText(Handler.getCurrentDate());
            SimpleDateFormat formatter = new SimpleDateFormat(Handler.DATE_FORMAT);
            Date start = formatter.parse(Handler.getCurrentDate());
            Calendar c = Calendar.getInstance();
            c.setTime(start);
            c = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH) + 1);
            Date end = c.getTime();
            String e = formatter.format(end);
            txtEnd.setText(e);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        txtName.setText("");

        autCategory.setText("Any");
        autDifficulty.setText("Any");
        autType.setText("Any");

        difAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.answer_dropdown_item, Quiz.difficulties);
        typeAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.answer_dropdown_item, Quiz.types);

        autDifficulty.setAdapter(difAdapter);
        autType.setAdapter(typeAdapter);

        setCategoryBox();
    }

    private boolean validation() {
        return !txtName.getText().equals("");
    }

    private boolean compareDates() {
        SimpleDateFormat formatter = new SimpleDateFormat(Handler.DATE_FORMAT);
        try {
            Date start = formatter.parse(txtStart.getText().toString());
            Date end = formatter.parse(txtEnd.getText().toString());
            if (start.getTime() < end.getTime())
                return true;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private void updateCategories() {
        btnGet.setEnabled(true);
        setCategoryBox();
    }

    private void setCategoryBox() {
        autCategory.clearListSelection();
        List<String> data = new ArrayList<>();
        data.add("Any");
        for (Map.Entry<Integer, String> entry : Quiz.categorySchema.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();
            data.add(value);
            System.out.println("Key=" + key + ", Value=" + value);
        }
        catAdapter = new ArrayAdapter<>(this, R.layout.answer_dropdown_item, data);

        autCategory.setAdapter(catAdapter);
    }
}