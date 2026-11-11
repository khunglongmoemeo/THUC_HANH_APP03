package com.example.warehousemanagement.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.warehousemanagement.R;
import com.example.warehousemanagement.database.DatabaseHelper;
import com.example.warehousemanagement.model.Product;

import java.text.DecimalFormat;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageButton btnBackHeader;
    private TextView txtDetailName, txtDetailCategory, txtDetailPrice;
    private TextView txtDetailQuantity, txtWarningStock, txtDetailTotalValue, txtDetailDescription;
    private Button btnAddStock, btnSellStock, btnBack, btnDelete;

    private DatabaseHelper dbHelper;
    private Product currentProduct;
    private int productId = -1;
    private DecimalFormat df = new DecimalFormat("#,### đ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        dbHelper = new DatabaseHelper(this);

        if (getIntent() != null) {
            productId = getIntent().getIntExtra(MainActivity.EXTRA_PRODUCT_ID, -1);
        }

        if (productId == -1) {
            finish();
            return;
        }

        initViews();
        loadProductDetails();
        setupListeners();
    }

    private void initViews() {
        btnBackHeader = findViewById(R.id.btnBackHeader);
        txtDetailName = findViewById(R.id.txtDetailName);
        txtDetailCategory = findViewById(R.id.txtDetailCategory);
        txtDetailPrice = findViewById(R.id.txtDetailPrice);
        txtDetailQuantity = findViewById(R.id.txtDetailQuantity);
        txtWarningStock = findViewById(R.id.txtWarningStock);
        txtDetailTotalValue = findViewById(R.id.txtDetailTotalValue);
        txtDetailDescription = findViewById(R.id.txtDetailDescription);

        btnAddStock = findViewById(R.id.btnAddStock);
        btnSellStock = findViewById(R.id.btnSellStock);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void loadProductDetails() {
        currentProduct = dbHelper.getProductById(productId);
        if (currentProduct == null) {
            finish();
            return;
        }

        txtDetailName.setText(currentProduct.getName());
        txtDetailCategory.setText(currentProduct.getCategory() != null ? currentProduct.getCategory() : "Khác");
        txtDetailPrice.setText(df.format(currentProduct.getPrice()));

        txtDetailQuantity.setText(String.valueOf(currentProduct.getQuantity()));
        double totalValue = currentProduct.getQuantity() * currentProduct.getPrice();
        txtDetailTotalValue.setText(df.format(totalValue));

        txtDetailDescription.setText(currentProduct.getDescription());

        if (currentProduct.getQuantity() < 5) {
            txtWarningStock.setVisibility(View.VISIBLE);
        } else {
            txtWarningStock.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        btnBackHeader.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());

        // Nút Nhập thêm 1: Tăng số lượng thêm 1, ghi vào csdl và cập nhật lại
        btnAddStock.setOnClickListener(v -> {
            int newQuantity = currentProduct.getQuantity() + 1;
            updateQuantity(newQuantity);
        });

        // Nút Bán 1: Giảm số lượng đi 1; nếu số lượng bằng 0 thì thông báo "Hết hàng" và không giảm nữa
        btnSellStock.setOnClickListener(v -> {
            int currentQ = currentProduct.getQuantity();
            if (currentQ == 0) {
                Toast.makeText(this, "Hết hàng", Toast.LENGTH_SHORT).show();
            } else {
                updateQuantity(currentQ - 1);
            }
        });

        // Nút Xóa sản phẩm: Xóa sản phẩm khỏi csdl, quay lại danh sách
        btnDelete.setOnClickListener(v -> confirmDeleteProduct());
    }

    private void updateQuantity(int newQuantity) {
        if (dbHelper.updateQuantity(currentProduct.getId(), newQuantity) > 0) {
            loadProductDetails(); // Tải lại thông tin cập nhật
        }
    }

    private void confirmDeleteProduct() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa sản phẩm '" + currentProduct.getName() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbHelper.deleteProduct(currentProduct.getId());
                    Toast.makeText(this, "Đã xóa sản phẩm!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
