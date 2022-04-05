package com.matm.matmsdk.aepsmodule.bankspinner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;

import androidx.recyclerview.widget.RecyclerView;


import com.matm.matmsdk.Utils.SdkConstants;

import isumatm.androidsdk.equitas.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 6/27/2018.
 */

public class BankNameListAdapter extends RecyclerView.Adapter<BankNameListAdapter.BankViewHolder> implements Filterable {
    private int lastSelectedPosition = -1;

    public List<BankNameModel> bankNameModelList,filterList;
    RecyclerViewClickListener recyclerViewClickListener;

    public interface RecyclerViewClickListener
    {

        public void recyclerViewListClicked(View v, int position);
    }
    public class BankViewHolder extends RecyclerView.ViewHolder {
        public RadioButton bankName;

        public BankViewHolder(View view) {
            super ( view );
            bankName = (RadioButton) view.findViewById ( R.id.bankName );
            bankName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastSelectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                    recyclerViewClickListener.recyclerViewListClicked(v,lastSelectedPosition);

                }
            });
        }
    }
    public BankNameModel getItem(int position) {
        return filterList.get(position);

    }
    public BankNameListAdapter(List<BankNameModel> bankNameModelList, RecyclerViewClickListener recyclerViewClickListener) {
        this.bankNameModelList = bankNameModelList;
        this.filterList = bankNameModelList;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    @Override
    public BankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.bank_list_item;
        if (SdkConstants.bankItem != 0){
            layout = SdkConstants.bankItem;
        }
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new BankViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BankViewHolder holder, int position) {
        BankNameModel bankNameModel = filterList.get(position);
        holder.bankName.setText(bankNameModel.getBankName());

        holder.bankName.setChecked(lastSelectedPosition == position);

    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {


                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filterList = bankNameModelList;
                } else {
                    List<BankNameModel> filteredList = new ArrayList<>();
                    for (BankNameModel row : bankNameModelList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getBankName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filterList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if(filterResults.values!= null){

                    filterList = (ArrayList<BankNameModel>) filterResults.values;
                    notifyDataSetChanged();
                }
            }
        };
    }
}

