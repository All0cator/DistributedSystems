package com.example.efood_customer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.efood_customer.Primitives.Store;

import java.util.ArrayList;


public class SearchStoresActivity extends AppCompatActivity {

    private ListView listView;
    private Button buttonBack;
    private Button buttonGradeStore;
    private Button buttonNewPurchase;
    private Button buttonSearch;
    private Handler handler;
    ArrayList<Store> items = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_stores);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if (message.what == 1){
                    Toast.makeText(SearchStoresActivity.this, "Connection OK! "+items.size(), Toast.LENGTH_SHORT).show();
                    ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                }
                return false;
            }
        });

        buttonBack = findViewById(R.id.b_search_stores_back);
        buttonSearch = findViewById(R.id.b_search_stores_search);
        buttonGradeStore = findViewById(R.id.b_search_stores_grade_store);
        buttonNewPurchase = findViewById(R.id.b_search_stores_new_purchase);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Back();
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Back();
            }
        });

        buttonGradeStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Back();
            }
        });

        buttonNewPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Back();
            }
        });
    }

    public void Back() {
        finish();
    }
}
