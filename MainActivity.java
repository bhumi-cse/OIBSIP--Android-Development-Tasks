package com.example.myapplication;
import static java.lang.String.*;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Spinner catSpinner, fromSpinner, toSpinner;
    private EditText inputVal;
    private TextView resultText;

    private final String[] categories = {"Length", "Weight", "Temperature"};
    private final String[] lengthUnits = {"Centimeter (cm)", "Meter (m)", "Inch (in)"};
    private final String[] weightUnits = {"Gram (g)", "Kilogram (kg)", "Pound (lb)"};
    private final String[] tempUnits = {"Celsius (°C)", "Fahrenheit (°F)"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind Views
        catSpinner = findViewById(R.id.catSpinner);
        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);
        inputVal = findViewById(R.id.inputVal);
        Button convertBtn = findViewById(R.id.convertBtn);
        resultText = findViewById(R.id.resultText);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSpinner.setAdapter(catAdapter);

        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                bindUnits(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        convertBtn.setOnClickListener(v -> processConversion());
    }

    private void bindUnits(int categoryIndex) {
        String[] selectedUnits;
        switch (categoryIndex) {
            case 0: selectedUnits = lengthUnits; break;
            case 1: selectedUnits = weightUnits; break;
            default: selectedUnits = tempUnits; break;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, selectedUnits);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);
    }

    @SuppressLint("DefaultLocale")
    private void processConversion() {
        String inputStr = inputVal.getText().toString().trim();

        if (inputStr.isEmpty()) {
            Toast.makeText(this, "Enter a value first", Toast.LENGTH_SHORT).show();
            return;
        }

        double value;
        try {
            value = Double.parseDouble(inputStr);
        } catch (Exception e) {
            Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show();
            return;
        }

        int catPos = catSpinner.getSelectedItemPosition();
        String srcUnit = fromSpinner.getSelectedItem().toString();
        String targetUnit = toSpinner.getSelectedItem().toString();
        double output = 0;

        if (catPos == 0) {
            output = calcLength(value, srcUnit, targetUnit);
        } else if (catPos == 1) {
            output = calcWeight(value, srcUnit, targetUnit);
        } else {
            output = calcTemp(value, srcUnit, targetUnit);
        }

        resultText.setText(format("%.2f %s", output, targetUnit));
    }

    private double calcLength(double val, String src, String target) {
        double meters = val;
        if (src.contains("cm")) meters = val / 100.0;
        else if (src.contains("in")) meters = val * 0.0254;

        if (target.contains("cm")) return meters * 100.0;
        if (target.contains("in")) return meters / 0.0254;
        return meters;
    }

    private double calcWeight(double val, String src, String target) {
        double grams = val;
        if (src.contains("kg")) grams = val * 1000.0;
        else if (src.contains("lb")) grams = val * 453.592;

        if (target.contains("kg")) return grams / 1000.0;
        if (target.contains("lb")) return grams / 453.592;
        return grams;
    }

    private double calcTemp(double val, String src, String target) {
        if (src.equals(target)) return val;
        return src.contains("°C") ? (val * 9 / 5) + 32 : (val - 32) * 5 / 9;
    }
}