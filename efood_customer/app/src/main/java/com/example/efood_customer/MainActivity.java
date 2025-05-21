package com.example.efood_customer;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button enterButton;
    private EditText longitude;
    private EditText latitude;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        enterButton = findViewById(R.id.enter);
        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);
        errorMessage = findViewById(R.id.error_text);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reset username and password fields
        longitude.setText("");
        latitude.setText("");

        // Reset error message
        errorMessage.setText(""); // Hides the error message
    }

    @Override
    public void navigateToCustomerHomeScreen(){
        //Intent intent = new Intent(this, ...);
        //startActivity(intent);
    }
}