package com.matm.matmsdk.aepsmodule.aeps3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.ministatement.StatementList_Adapter;
import com.matm.matmsdk.aepsmodule.ministatement.TransactionList;

import java.util.ArrayList;

import isumatm.androidsdk.equitas.R;

public class MinistatementListAdapter extends RecyclerView.Adapter<MinistatementListAdapter.RecyclerViewHolder> {

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private final TextView date_txt;
        private final TextView amount_txt;
        private final TextView remark_txt;

        RecyclerViewHolder(View view) {
            super(view);
            date_txt = (TextView) view.findViewById(R.id.date_txt);
            amount_txt = (TextView) view.findViewById(R.id.amount_txt);
            remark_txt = (TextView) view.findViewById(R.id.remark_txt);

        }

    }

    ArrayList<MinistatemenTxnList> list = new ArrayList<>();
    private final Context context;
    private final int selectedPosition = -1;

    public MinistatementListAdapter(ArrayList<MinistatemenTxnList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MinistatementListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.statement_list_items;
        if (SdkConstants.statementItem != 0) {
            layout = SdkConstants.statementItem;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        final MinistatementListAdapter.RecyclerViewHolder mViewHolder = new MinistatementListAdapter.RecyclerViewHolder(v);

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MinistatementListAdapter.RecyclerViewHolder holder, int position) {
        holder.date_txt.setText(list.get(position).date);

        Double amount = list.get(position).amount;
        String type = list.get(position).d_c;

        if (type.equalsIgnoreCase("C")) {
            amount = Double.valueOf("+" + amount);
            holder.amount_txt.setTextColor(context.getResources().getColor(R.color.green));
            holder.amount_txt.setText(String.valueOf(amount));
        } else {
            amount = Double.valueOf("-" + amount);
            holder.amount_txt.setTextColor(context.getResources().getColor(R.color.red));
            holder.amount_txt.setText(String.valueOf(amount));
        }

        holder.remark_txt.setText(list.get(position).transaction_remark);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
