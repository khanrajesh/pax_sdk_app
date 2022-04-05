package com.matm.matmsdk.aepsmodule.ministatement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.unifiedaeps.UnifiedTxnStatusModel;

import java.util.ArrayList;

import isumatm.androidsdk.equitas.R;


public class StatementList_Adapter extends RecyclerView.Adapter<StatementList_Adapter.RecyclerViewHolder> {

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView date_txt,amount_txt,remark_txt,crdr_txt;
        RecyclerViewHolder(View view) {
            super(view);
            date_txt = (TextView) view.findViewById(R.id.date_txt);
            amount_txt = (TextView) view.findViewById(R.id.amount_txt);
            remark_txt = (TextView) view.findViewById(R.id.remark_txt);
            crdr_txt = (TextView) view.findViewById(R.id.crdr_txt);

        }

    }

    ArrayList<TransactionList> list = new ArrayList<>();
    private Context context;
    private int selectedPosition = -1;

    public StatementList_Adapter(Context context, ArrayList<TransactionList> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int layout = R.layout.statement_list_items;
        if (SdkConstants.statementItem != 0){
            layout = SdkConstants.statementItem;
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);

        final RecyclerViewHolder mViewHolder = new RecyclerViewHolder(v);


        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int pos) {

        try{


            holder.date_txt.setText(list.get(pos).date);

            String amount = list.get(pos).amount;
            String type = list.get(pos).d_c;

            if(type.equalsIgnoreCase("Credit")){


                holder.crdr_txt.setTextColor(context.getResources().getColor(R.color.green));
                holder.crdr_txt.setText("Cr");
                holder.amount_txt.setText(amount);
            }else{

                holder.crdr_txt.setTextColor(context.getResources().getColor(R.color.red));
                holder.crdr_txt.setText("Dr");
                holder.amount_txt.setText(amount);
            }

            holder.remark_txt.setText(list.get(pos).transaction_remark);



        }catch (Exception e){

        }

    }

    //On selecting any view set the current position to selectedPositon and notify adapter
    private void itemCheckChanged(View v) {

    }


    @Override
    public int getItemCount() {
        return list.size();
    }



    //Delete the selected position from the arrayList
    public void deleteSelectedPosition() {
        if (selectedPosition != -1) {
            list.remove(selectedPosition);
            selectedPosition = -1;//after removing selectedPosition set it back to -1
            notifyDataSetChanged();
        }
    }
}
