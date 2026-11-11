package com.example.warehousemanagement.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.warehousemanagement.R;
import com.example.warehousemanagement.adapter.ProductAdapter;
import com.example.warehousemanagement.database.DatabaseHelper;
import com.example.warehousemanagement.model.Product;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "PRODUCT_ID";

    private ListView lvProducts;
    private TextView txtEmpty, txtTotalProducts, txtTotalValue, txtLowStockCount;
    private ImageButton btnLogout;
    private Button fabAddProduct;

    private DatabaseHelper dbHelper;
    private ProductAdapter adapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        productList = new ArrayList<>();

        initViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    private void initViews() {
        lvProducts = findViewById(R.id.lvProducts);
        txtEmpty = findViewById(R.id.txtEmpty);
        txtTotalProducts = findViewById(R.id.txtTotalProducts);
        txtTotalValue = findViewById(R.id.txtTotalValue);
        txtLowStockCount = findViewById(R.id.txtLowStockCount);
        btnLogout = findViewById(R.id.btnLogout);
        fabAddProduct = findViewById(R.id.fabAddProduct);

        adapter = new ProductAdapter(this, productList);
        lvProducts.setAdapter(adapter);
    }

    private void setupListeners() {
        lvProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = (Product) adapter.getItem(position);
                if (product != null) {
                    Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                    intent.putExtra(EXTRA_PRODUCT_ID, product.getId());
                    startActivity(intent);
                }
            }
        });

        fabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddProductActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
    }

    private void loadProducts() {
        productList = dbHelper.getAllProducts();
        updateUI(productList);
    }

    private void updateUI(List<Product> list) {
        adapter.updateData(list);
        if (list == null || list.isEmpty()) {
            txtEmpty.setVisibility(View.VISIBLE);
            lvProducts.setVisibility(View.GONE);
        } else {
            txtEmpty.setVisibility(View.GONE);
            lvProducts.setVisibility(View.VISIBLE);
        }

        int total = list != null ? list.size() : 0;
        int lowStock = dbHelper.countLowStock();
        double totalValue = dbHelper.getTotalValue();
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,### đ");

        txtTotalProducts.setText(String.valueOf(total));
        txtTotalValue.setText(df.format(totalValue));
        txtLowStockCount.setText(String.valueOf(lowStock));
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
