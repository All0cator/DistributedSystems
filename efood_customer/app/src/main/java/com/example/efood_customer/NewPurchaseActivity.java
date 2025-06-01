package com.example.efood_customer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.efood_customer.Primitives.Product;
import com.example.efood_customer.Primitives.Store;

import java.util.ArrayList;

public class NewPurchaseActivity extends AppCompatActivity {

    private Button buttonBack;
    private ListView lv;
    private Button buttonComplete;
    private Button buttonAddProduct;
    private Button addProduct;
    private EditText quantity;
    private Handler handler;
    private ArrayList<Product> items = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_purchase);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Intent intent = getIntent(); //this load the araylist of products given in the search store screen
        //ArrayList<...> products = intent.getArrayListExtra("ProductList");
        // TODO: load the products list with the receivedList data

        buttonBack = findViewById(R.id.b_new_purchase_complete);
        buttonComplete = findViewById(R.id.b_new_purchase_complete);
        buttonAddProduct = findViewById(R.id.b_new_purchase_add_product);
        lv = findViewById(R.id.listView);

        handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if (message.what == 1){
                    Toast.makeText(NewPurchaseActivity.this, "Connection OK! "+items.size(), Toast.LENGTH_SHORT).show();
                    ((BaseAdapter)lv.getAdapter()).notifyDataSetChanged();
                }
                return false;
            }
        });

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_list_item_1,
//                products
//        );

//        lv.setAdapter(adapter);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Back();
            }
        });

        //TODO: add functionality to the buttonAddProduct
        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //...
                try {
                    int q = Integer.parseInt(quantity.getText().toString());
                    //...

                }catch (Exception e) {
                    CharSequence temp = "Please, decimal";
                    // Show the user to put proper input
                    Toast.makeText(NewPurchaseActivity.this, temp, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //TODO: add functionality to the buttonComplete
        buttonComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //...
                //buy()
                //...
                Back();
            }
        });
    }

    public void Back() {
        finish();
    }
}
