package com.example.efood_customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.efood_customer.Primitives.MyRunnable;
import com.example.efood_customer.Primitives.Store;

import java.util.ArrayList;


public class SearchStoresActivity extends AppCompatActivity {

    private ListView listView;
    private Button buttonBack;
    private Button buttonGradeStore;
    private Button buttonNewPurchase;
    private Button buttonSearch;
    private Handler handler;
    private Store selectedStore = null;
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

//        new Thread(new MyRunnable(handler,items)).start(); //maybe this code snippet isn't supposed to be here

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
                //...
                //search()
                //...

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

//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new MyThread(handler,items).start();
//            }
//        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                selectedStore = items.get(i);

                Toast.makeText(SearchStoresActivity.this, "Clicked: "+selectedStore.GetName(), Toast.LENGTH_SHORT).show();
            }
        });

//        listView.setAdapter(new BaseAdapter() {
//            @Override
//            public int getCount() {
//                return items.size();
//            }
//
//            @Override
//            public Object getItem(int i) {
//                return items.get(i);
//            }
//
//            @Override
//            public long getItemId(int i) {
//                return i;
//            }
//
//
//            @Override
//            public View getView(int i, View view, ViewGroup viewGroup) {
//
//                View itemView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_search_stores,viewGroup,false);
//
//                TextView name = itemView.findViewById(R.id.);
//
//                TextView text = itemView.findViewById(R.id.text);
//
//                title.setText(items.get(i).getName());
//
//                text.setText(items.get(i).getText());
//
//                return itemView;
//            }
//        });
    }

    public void Back() {
        finish();
    }
}
