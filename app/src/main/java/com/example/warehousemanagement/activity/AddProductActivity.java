package com.example.warehousemanagement.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.warehousemanagement.R;
import com.example.warehousemanagement.database.DatabaseHelper;
import com.example.warehousemanagement.model.Product;

public class AddProductActivity extends AppCompatActivity {

    private ImageButton btnBackHeader;
    private EditText edtName, edtQuantity, edtPrice, edtDescription;
    private AutoCompleteTextView edtUnit;
    private SwitchCompat switchStatus;
    private Button btnSave;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupSpinner();

        btnBackHeader.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void initViews() {
        btnBackHeader = findViewById(R.id.btnBackHeader);
        edtName = findViewById(R.id.edtName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtPrice = findViewById(R.id.edtPrice);
        edtUnit = findViewById(R.id.edtUnit);
        switchStatus = findViewById(R.id.switchStatus);
        edtDescription = findViewById(R.id.edtDescription);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupSpinner() {
        String[] categories = new String[]{"Điện tử", "Thực phẩm", "Văn phòng phẩm", "Gia dụng", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categories
        );
        edtUnit.setAdapter(adapter);
    }

    private void saveProduct() {
        String name = edtName.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        String quantityStr = edtQuantity.getText().toString().trim();
        String category = edtUnit.getText().toString().trim();
        int active = (switchStatus != null && switchStatus.isChecked()) ? 1 : 0;
        String description = edtDescription.getText().toString().trim();

        // 1. Kiểm tra Tên sản phẩm (Không được để trống)
        if (TextUtils.isEmpty(name)) {
            edtName.setError("Tên sản phẩm không được để trống");
            edtName.requestFocus();
            return;
        }

        // 2. Kiểm tra Giá bán (Chỉ nhập số, lớn hơn 0)
        if (TextUtils.isEmpty(priceStr)) {
            edtPrice.setError("Giá bán không được để trống");
            edtPrice.requestFocus();
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                edtPrice.setError("Giá bán phải lớn hơn 0");
                edtPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtPrice.setError("Giá bán không hợp lệ");
            edtPrice.requestFocus();
            return;
        }

        // 3. Kiểm tra Số lượng (Chỉ nhập số nguyên, không âm)
        if (TextUtils.isEmpty(quantityStr)) {
            edtQuantity.setError("Số lượng không được để trống");
            edtQuantity.requestFocus();
            return;
        }
        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                edtQuantity.setError("Số lượng không được âm");
                edtQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtQuantity.setError("Số lượng không hợp lệ");
            edtQuantity.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(category)) {
            category = "Khác";
        }

        Product product = new Product(name, price, quantity, category, active, description);
        long result = dbHelper.insertProduct(product);

        if (result > 0) {
            Toast.makeText(this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Có lỗi xảy ra khi thêm sản phẩm!", Toast.LENGTH_SHORT).show();
        }
    }
}
