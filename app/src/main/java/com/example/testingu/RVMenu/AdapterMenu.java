package com.example.testingu.RVMenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testingu.FilterMenu;
import com.example.testingu.R;
import com.example.testingu.FilterMenu;

import java.util.ArrayList;

public class AdapterMenu extends RecyclerView.Adapter<AdapterMenu.HolderMenu> implements Filterable {
    private Context context;
    //change to public
    public ArrayList<ModelMenu> menuList, filterList;
    private FilterMenu filter;

    public AdapterMenu(Context context,
                       ArrayList<ModelMenu> menuList) {
        this.context = context;
        this.menuList = menuList;
        //add this line
        this.filterList= menuList;
    }

    @NonNull
    @Override
    public HolderMenu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate row
        View view= LayoutInflater.from(context).inflate(R.layout.row_menu, parent, false);
        return new HolderMenu(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMenu holder, int position) {
        //get
        ModelMenu modelMenu=menuList.get(position);
        String menuName=modelMenu.getMenuName();
        String menuPrice= modelMenu.getPrice();
        String menuId=modelMenu.getMenuId();

        //set
        holder.menuName.setText(menuName);
        holder.menuPrice.setText(menuPrice);
    }

    @Override
    public int getItemCount() {

            return menuList.size();
    }

    //filter
    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new FilterMenu(this, filterList);
        }
        return filter;
    }

    class HolderMenu extends RecyclerView.ViewHolder{
        private TextView menuName, menuPrice;


        public HolderMenu(@NonNull View itemView) {
            super(itemView);

            menuName=itemView.findViewById(R.id.menuNameTv);
            menuPrice=itemView.findViewById(R.id.menuPriceTv);

        }
    }
}
