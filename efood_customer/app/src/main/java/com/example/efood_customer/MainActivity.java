package com.example.efood_customer;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button buttonRefresh;
    private Button buttonSearchStores;
    private Button buttonNewPurchase;
    private Button buttonGradeStore;

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

        buttonRefresh = findViewById(R.id.b_main_refresh);
        buttonSearchStores = findViewById(R.id.b_main_search_stores);
        buttonNewPurchase = findViewById(R.id.b_main_new_purchase);
        buttonGradeStore = findViewById(R.id.b_main_grade_store);

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Refresh();
            }
        });

        buttonSearchStores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchStores();
            }
        });

        buttonNewPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewPurchase();
            }
        });

        buttonGradeStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GradeStore();
            }
        });
    }

    public void Refresh() {
        // Refersh request
    }

    public void SearchStores() {
        Intent intent = new Intent(this, SearchStoresActivity.class);
        startActivity(intent);
    }

    public void NewPurchase() {
        Intent intent = new Intent(this, NewPurchaseActivity.class);
        startActivity(intent);
    }

    public void GradeStore() {
        Intent intent = new Intent(this, GradeStoreActivity.class);
        startActivity(intent);
    }
}