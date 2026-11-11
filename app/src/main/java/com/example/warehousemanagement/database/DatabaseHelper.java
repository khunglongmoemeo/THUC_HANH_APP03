package com.example.warehousemanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.warehousemanagement.model.Product;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "warehouse.db";
    private static final int DATABASE_VERSION = 1;

    // Table Users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_USERNAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";

    // Table Products
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_PRODUCT_ID = "id";
    public static final String COLUMN_PRODUCT_NAME = "name";
    public static final String COLUMN_PRODUCT_PRICE = "price";
    public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
    public static final String COLUMN_PRODUCT_CATEGORY = "category";
    public static final String COLUMN_PRODUCT_ACTIVE = "active";
    public static final String COLUMN_PRODUCT_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng users (theo bảng 3.1)
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_USERNAME + " TEXT NOT NULL UNIQUE, " +
                COLUMN_USER_PASSWORD + " TEXT NOT NULL" +
                ");";
        db.execSQL(createUsersTable);

        // Tạo bảng products (theo bảng 3.2)
        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, " +
                COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, " +
                COLUMN_PRODUCT_CATEGORY + " TEXT NOT NULL, " +
                COLUMN_PRODUCT_ACTIVE + " INTEGER NOT NULL, " +
                COLUMN_PRODUCT_DESCRIPTION + " TEXT" +
                ");";
        db.execSQL(createProductsTable);

        // Chèn tài khoản mẫu admin / 123456 bằng insertUser
        insertUser(db, "admin", "123456");

        // Chèn 3 sản phẩm mẫu ở mục 2.2 bằng insertProduct (cột active = 1)
        insertProduct(db, new Product("Chuột không dây", 150000, 20, "Điện tử", 1, ""));
        insertProduct(db, new Product("Bàn phím cơ", 450000, 3, "Điện tử", 1, ""));
        insertProduct(db, new Product("Sổ tay A5", 25000, 40, "Văn phòng phẩm", 1, ""));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }


    // =========================================================================
    // 3.3. CÁC PHƯƠNG THỨC TỐI THIỂU CỦA DatabaseHelper (ĐÚNG THEO BẢNG YÊU CẦU)
    // =========================================================================

    // 1. checkLogin(username, password): Trả về true nếu có dòng khớp trong bảng users
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_USERNAME + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID}, selection, new String[]{username, password}, null, null, null);
        boolean isValid = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return isValid;
    }

    // 2. insertUser(username, password): Thêm tài khoản mới vào bảng users
    public long insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = insertUser(db, username, password);
        db.close();
        return id;
    }

    public long insertUser(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, username);
        values.put(COLUMN_USER_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    // 3. insertProduct(Product p): Thêm sản phẩm, trả về id vừa thêm
    public long insertProduct(Product p) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = insertProduct(db, p);
        db.close();
        return id;
    }

    public long insertProduct(SQLiteDatabase db, Product p) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, p.getName());
        values.put(COLUMN_PRODUCT_PRICE, p.getPrice());
        values.put(COLUMN_PRODUCT_QUANTITY, p.getQuantity());
        values.put(COLUMN_PRODUCT_CATEGORY, p.getCategory());
        values.put(COLUMN_PRODUCT_ACTIVE, p.getActive());
        values.put(COLUMN_PRODUCT_DESCRIPTION, p.getDescription());
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    // 4. getAllProducts(): Lấy toàn bộ sản phẩm để hiển thị danh sách
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " ORDER BY " + COLUMN_PRODUCT_ID + " DESC", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ACTIVE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION))
                );
                productList.add(product);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return productList;
    }

    // 5. getProductById(int id): Lấy một sản phẩm theo id
    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        Product product = null;
        if (cursor != null && cursor.moveToFirst()) {
            product = new Product(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ACTIVE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION))
            );
            cursor.close();
        }
        db.close();
        return product;
    }

    // 6. updateQuantity(int id, int quantity): Cập nhật số lượng (dùng cho Nhập thêm / Bán)
    public int updateQuantity(int id, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_QUANTITY, quantity);
        int rows = db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    // 7. deleteProduct(int id): Xóa sản phẩm theo id
    public int deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_PRODUCTS, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    // 8. getTotalValue(): Tổng giá trị kho: SUM(price * quantity)
    public double getTotalValue() {
        double total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_PRODUCT_PRICE + " * " + COLUMN_PRODUCT_QUANTITY + ") FROM " + TABLE_PRODUCTS, null);
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getDouble(0);
            cursor.close();
        }
        db.close();
        return total;
    }

    // 9. countLowStock(): Số sản phẩm sắp hết hàng: COUNT với quantity < 5
    public int countLowStock() {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCT_QUANTITY + " < 5", null);
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }
}
