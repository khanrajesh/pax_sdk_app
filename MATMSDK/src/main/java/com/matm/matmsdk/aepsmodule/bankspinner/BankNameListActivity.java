package com.matm.matmsdk.aepsmodule.bankspinner;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import isumatm.androidsdk.equitas.R;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.matm.matmsdk.CustomThemes;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.AEPS2HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class BankNameListActivity extends AppCompatActivity implements BankNameContract.View{
    private List<BankNameModel> bankNameModelList = new ArrayList<>();
    private RecyclerView bankNameRecyclerView;
    private BankNameListAdapter bankNameListAdapter;
    private BankNameListPresenter bankNameListPresenter;
    ShimmerFrameLayout bankNameShimmer;
    EditText searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        new CustomThemes(this);

        if (SdkConstants.bankList == 0) {
            setContentView(R.layout.activity_bank_list_spinner);
        } else {
            setContentView(SdkConstants.bankList);
        }

        bankNameShimmer = findViewById(R.id.bankNameShimmerLayoout);

        // setToolbar ();

        bankNameRecyclerView = (RecyclerView) findViewById ( R.id.bankNameRecyclerView );
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( getApplicationContext () );
        bankNameRecyclerView.setLayoutManager ( mLayoutManager );
        bankNameRecyclerView.setItemAnimator ( new DefaultItemAnimator() );
        //  bankNameRecyclerView.addItemDecoration(new DividerItemDecoration(this));

        bankNameListPresenter = new BankNameListPresenter(BankNameListActivity.this);
        bankNameListPresenter.loadBankNamesList(BankNameListActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bankNameShimmer.isShimmerStarted()) {
                    onBackPressed();
                }else{
                    onBackPressed();
                }
            }
        });

        searchView=(EditText) findViewById(R.id.searchView);
        // searchView.setIconifiedByDefault(true);
        // searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setFocusable(true);
        //  searchView.setIconified(false);
        searchView.clearFocus();
        searchView.requestFocusFromTouch();

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null){
                    if(!TextUtils.isEmpty(s) && bankNameListAdapter!= null) {
                        bankNameListAdapter.getFilter().filter(s);
                    }
                }
            }
        });


    }



    @Override
    public void bankNameListReady(ArrayList<BankNameModel> bankNameModelArrayList) {
        if (bankNameModelArrayList!=null && bankNameModelArrayList.size() > 0){
            bankNameModelList = bankNameModelArrayList;
        }
    }

    @Override
    public void showBankNames() {
        if (bankNameModelList!=null && bankNameModelList.size() > 0){
            bankNameListAdapter = new BankNameListAdapter(bankNameModelList, new BankNameListAdapter.RecyclerViewClickListener() {
                @Override
                public void recyclerViewListClicked(View v, int position) {
                    try{
                        Intent intent = new Intent();
                        intent.putExtra(SdkConstants.IIN_KEY, bankNameListAdapter.getItem(position));
                        setResult(RESULT_OK, intent);
                        finish();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
            bankNameRecyclerView.setAdapter ( bankNameListAdapter );
        }
    }

    @Override
    public void showLoader() {

        if (!bankNameShimmer.isShimmerStarted()) {
            bankNameShimmer.startShimmer();
            bankNameShimmer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideLoader() {

        if (bankNameShimmer.isShimmerStarted()) {
            bankNameShimmer.stopShimmer();
        }
        bankNameShimmer.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public ActionBar getSupportActionBar() {
        return super.getSupportActionBar();
    }

    @Override
    public void emptyBanks() {

    }
   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK)
            return false;

        return false;
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void showAlert(String msg){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(BankNameListActivity.this);
            builder.setTitle("Alert!!");
            builder.setMessage(msg);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}