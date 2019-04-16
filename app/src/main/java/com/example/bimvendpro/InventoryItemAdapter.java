package com.example.bimvendpro;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class InventoryItemAdapter extends RecyclerView.Adapter<InventoryItemAdapter.InventoryViewHolder>{
    private List<InventoryItem> itemList;
    public class InventoryViewHolder extends RecyclerView.ViewHolder {
        private TextView codeTextView,inMachineTextView,inStock,inWareHouseTextView,lastCostTextView,productNameTextView,productTypeTextView,unitPerCaseTextView;


        public InventoryViewHolder(View view) {
            super(view);
           // title = (TextView) view.findViewById(R.id.title);
        //    genre = (TextView) view.findViewById(R.id.genre);
          //  year = (TextView) view.findViewById(R.id.year);
        }
    }


    public InventoryItemAdapter(List<InventoryItem> moviesList) {
        this.itemList = moviesList;
    }

    @Override
    public InventoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inventory_item_layout, parent, false);

        return new InventoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InventoryViewHolder holder, int position) {
        InventoryItem item = itemList.get(position);
        holder.codeTextView.setText(item.getCode());
        holder.inMachineTextView.setText(String.valueOf(item.getInMachine()));
        holder.inStock.setText(String.valueOf(item.getInStock()));
        holder.inWareHouseTextView.setText(String.valueOf(item.getInWarehouse()));
        holder.lastCostTextView.setText(String.valueOf(item.getLastCost()));
        holder.productNameTextView.setText(item.getProductName());
        holder.productTypeTextView.setText(item.getProductType());
        holder.unitPerCaseTextView.setText(String.valueOf(item.getUnitPerCase()));

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}