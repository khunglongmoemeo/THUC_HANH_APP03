package com.example.warehousemanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.warehousemanagement.R;
import com.example.warehousemanagement.model.Product;

import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends BaseAdapter {

    private Context context;
    private List<Product> productList;
    private LayoutInflater inflater;
    private DecimalFormat currencyFormat;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.inflater = LayoutInflater.from(context);
        this.currencyFormat = new DecimalFormat("#,### đ");
    }

    public void updateData(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return productList != null ? productList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productList.get(position).getId();
    }

    static class ViewHolder {
        TextView txtName;
        TextView txtPrice;
        TextView txtQuantity;
        TextView txtCategory;
        TextView txtWarningBadge;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_product, parent, false);
            holder = new ViewHolder();
            holder.txtName = convertView.findViewById(R.id.txtProductName);
            holder.txtPrice = convertView.findViewById(R.id.txtProductPrice);
            holder.txtQuantity = convertView.findViewById(R.id.txtProductQuantity);
            holder.txtCategory = convertView.findViewById(R.id.txtProductCategory);
            holder.txtWarningBadge = convertView.findViewById(R.id.txtWarningBadge);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = productList.get(position);

        holder.txtName.setText(product.getName());
        holder.txtPrice.setText("Giá bán: " + currencyFormat.format(product.getPrice()));
        holder.txtQuantity.setText("SL: " + product.getQuantity());

        String category = product.getCategory() != null ? product.getCategory() : "Khác";
        holder.txtCategory.setText(category);

        if (product.getQuantity() < 5) {
            holder.txtWarningBadge.setVisibility(View.VISIBLE);
            holder.txtQuantity.setTextColor(context.getResources().getColor(R.color.warning_red, null));
        } else {
            holder.txtWarningBadge.setVisibility(View.GONE);
            holder.txtQuantity.setTextColor(context.getResources().getColor(R.color.text_primary, null));
        }

        return convertView;
    }
}
