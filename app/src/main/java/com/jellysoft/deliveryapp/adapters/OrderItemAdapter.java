package com.jellysoft.deliveryapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jellysoft.deliveryapp.ItemOrder;
import com.jellysoft.deliveryapp.R;
import com.jellysoft.deliveryapp.databinding.ItemProductsBinding;

import java.text.DecimalFormat;
import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ItemViewHolder> {
    private List<ItemOrder> itemDetails;
    private Context context;
    private DecimalFormat df;

    public OrderItemAdapter(List<ItemOrder> itemDetails) {
        this.itemDetails = itemDetails;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        df = new DecimalFormat("###.##");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_products, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.setModel(itemDetails.get(position));
    }

    @Override
    public int getItemCount() {
        return itemDetails.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ItemProductsBinding binding;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemProductsBinding.bind(itemView);
        }

        @SuppressLint("SetTextI18n")
        public void setModel(ItemOrder itemDetail) {
            binding.productName.setText(itemDetail.getProductName());
            binding.tvproductprice.setText("Rs. " + itemDetail.getPrice() + "/-");
            binding.produxtQuentity.setText(context.getString(R.string.quentity).concat(" : ") + itemDetail.getQuantity());
            Glide.with(binding.getRoot().getContext())
                    .load(itemDetail.getImage())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(binding.imgProduct);
        }
    }
}
