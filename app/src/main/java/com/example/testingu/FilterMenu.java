package com.example.testingu;

import android.widget.Filter;

import com.example.testingu.RVMenu.AdapterMenu;
import com.example.testingu.RVMenu.ModelMenu;
import com.example.testingu.RVMenu.AdapterMenu;
import com.example.testingu.RVMenu.ModelMenu;

import java.util.ArrayList;
import java.util.Locale;

public class FilterMenu extends Filter {

    private AdapterMenu adapter;
    private ArrayList<ModelMenu> filterList;

    public FilterMenu(AdapterMenu adapter, ArrayList<ModelMenu> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        if (constraint != null && constraint.length()>0){
            //search not empty
            //change uppercase
            constraint=constraint.toString().toUpperCase(Locale.ROOT);
            //store filtered list
            ArrayList<ModelMenu> filteredModels=new ArrayList<>();
            for(int i=0; i<filterList.size(); i++){
                //check, search by title and category
                if(filterList.get(i).getMenuName().toUpperCase().contains(constraint)){
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count=filteredModels.size();
            results.values=filteredModels;

        }
        else{
            results.count= filterList.size();
            results.values=filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.menuList=(ArrayList<ModelMenu>) filterResults.values;
        //refresh adapter
        adapter.notifyDataSetChanged();
    }
}
