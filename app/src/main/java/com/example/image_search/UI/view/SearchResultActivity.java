package com.example.image_search.UI.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.image_search.contract.SearchResultContract;
import com.example.image_search.domain.presenter.SearchResultPresenter;
import com.example.image_search.technical_services.data.Product;
import com.example.image_search.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SearchResultActivity extends AppCompatActivity implements SearchResultContract.View {
    Button storeFilterBtn, backBtn, sortBtn;
    private SearchResultContract.Presenter searchResultPresenter;

    String[] ecommerceList;
    boolean[] checkedEcommerces;
    ArrayList<Integer> userSelectedEcommerces = new ArrayList<>();

    RecyclerView mRecyclerView;
    RecyclerViewAdapter myAdapter;

    Properties prop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_result);
        // clear FLAG_TRANSLUCENT_STATUS flag:
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark));

        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("IMAGE_PATH");
        setContentView(R.layout.activity_search_result);

        storeFilterBtn = findViewById(R.id.storeFilterBtn);
        backBtn = findViewById(R.id.backBtn);

        ecommerceList = getResources().getStringArray(R.array.ecommerce_name);
        checkedEcommerces = new boolean[ecommerceList.length];

        prop = new Properties();

        int spanCount = 2;
        int spacing = 10;
        boolean includeEdge = true;

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(SearchResultActivity.this, spanCount);

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        searchResultPresenter = new SearchResultPresenter(this);
        searchResultPresenter.uploadImage(imagePath, this);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", false);
//                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        storeFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterStoreDialog();
            }
        });
    }

    private void showFilterStoreDialog() {
        final ArrayList<Integer> tempSelectedEcommerces = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchResultActivity.this);
        builder.setTitle(R.string.filter_ecommerce_dialog_title);
        builder.setMultiChoiceItems(ecommerceList, checkedEcommerces,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            if (!userSelectedEcommerces.contains(position)) {
                                userSelectedEcommerces.add(position);
                                tempSelectedEcommerces.add(position);
                            }
                        } else {
                            if (userSelectedEcommerces.contains(position)) {
                                userSelectedEcommerces.remove(new Integer(position));
                                tempSelectedEcommerces.add(position);
                            }
                        }
                    }
                });

        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                for (int i = 0; i < userSelectedEcommerces.size(); i++) {
                    item = item + ecommerceList[userSelectedEcommerces.get(i)];
                    if(i != userSelectedEcommerces.size() - 1) {
                        item = item + ", ";
                    }
                }
                searchResultPresenter.filterSortAction(ecommerceList, userSelectedEcommerces, SearchResultActivity.this);
            }
        });

        builder.setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (Integer num : tempSelectedEcommerces) {
                    if(checkedEcommerces[num] == true){
                        userSelectedEcommerces.remove(new Integer(num));
                        checkedEcommerces[num] = false;
                    }
                    else{
                        userSelectedEcommerces.add(num);
                        checkedEcommerces[num] = true;
                    }
                }
                tempSelectedEcommerces.clear();
                dialogInterface.dismiss();
            }
        });

        builder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedEcommerces.length; i++) {
                    checkedEcommerces[i] = false;
                }
                userSelectedEcommerces.clear();
                searchResultPresenter.filterSortAction(ecommerceList, userSelectedEcommerces, SearchResultActivity.this);
            }
        });

        AlertDialog mDialog = builder.create();
        mDialog.show();
    }

    private void setResultNumber(int number){
        TextView result = findViewById(R.id.resultNumber);
        result.setText(number+" Product(s)");
    }


    @Override
    public void showProduct(List<Product> mProductList) {
        myAdapter = new RecyclerViewAdapter(SearchResultActivity.this, mProductList);
        mRecyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                myAdapter.notifyItemChanged(position);
            }
        });
        setResultNumber(mProductList.size());
    }

    @Override
    public ProgressDialog setProgressDialog() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(SearchResultActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Please wait....");
        progressDialog.setTitle("Fetching Data");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        return progressDialog;
    }

    @Override
    public void intentToEmptyActivity(String statusCode) {
        Intent mIntent = new Intent(this, EmptyResultActivity.class);

        switch(statusCode) {
            case "404":
                mIntent.putExtra("MESSAGES", new String[] { statusCode, "We couldn't find any results", "Make sure the subject is well lit and centered"});
                break;
            case "500":
                mIntent.putExtra("MESSAGES", new String[] { statusCode, "Looks like the server is taking\n too long to respond", "Please try again in sometime"});
                break;
            default:
                mIntent.putExtra("MESSAGES", new String[] { statusCode, "Looks like the server is taking\n too long to respond", "This can be caused by either poor connectivity or \nan error with our servers, please try again in a while"});
                break;
        }

        startActivity(mIntent);
    }
}
