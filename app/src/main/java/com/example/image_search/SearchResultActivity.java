package com.example.image_search;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchResultActivity extends AppCompatActivity {
    Button storeFilterBtn, backBtn, sortBtn;
    TextView testFilter;

    String[] ecommerceList;
    boolean[] checkedEcommerces;
    ArrayList<Integer> userSelectedEcommerces = new ArrayList<>();

    String[] sortList;
    int selectedSort;
    int userSelectedSort;

    RecyclerView mRecyclerView;
    List<Product> mProductList;
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
        testFilter = findViewById(R.id.testFilter);
        sortBtn =  findViewById(R.id.sortBtn);

        ecommerceList = getResources().getStringArray(R.array.ecommerce_name);
        checkedEcommerces = new boolean[ecommerceList.length];

        sortList = getResources().getStringArray(R.array.sort_choices);
        selectedSort = 0;

        prop = new Properties();

        int spanCount = 2;
        int spacing = 10;
        boolean includeEdge = true;

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(SearchResultActivity.this, spanCount);

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        uploadImage(imagePath);

        testFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchResultActivity.this, EmptyResultActivity.class);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", false);
                setResult(Activity.RESULT_CANCELED, returnIntent);

                finish();
            }
        });

        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortAlertDialog();
            }
        });

        storeFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterStoreDialog();
            }
        });
    }

    private void showSortAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchResultActivity.this);
        alertDialog.setTitle(R.string.sort_product_dialog_title);
        alertDialog.setSingleChoiceItems(sortList, selectedSort, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userSelectedSort = which;
            }
        });

        alertDialog.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                selectedSort = userSelectedSort;
                switch (userSelectedSort) {
                    case 0:
                        Toast.makeText(SearchResultActivity.this, "best match", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(SearchResultActivity.this, "highest price", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(SearchResultActivity.this, "lowest price", Toast.LENGTH_LONG).show();
                        break;
                }
                filterSortAction();
            }
        });

        alertDialog.setCancelable(false);

        alertDialog.setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
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
                filterSortAction();
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
                filterSortAction();
            }
        });

        AlertDialog mDialog = builder.create();
        mDialog.show();
    }

    // Filter and Sort Action
    private void filterSortAction(){
        List<Product> productFilterList;

        if(userSelectedEcommerces.size() == 0 || userSelectedEcommerces.size() == 3) {
            productFilterList = new ArrayList<>(mProductList);
        }
        else{
            productFilterList = new ArrayList<>();
            for (Product p : mProductList) {
                for (int i = 0; i < userSelectedEcommerces.size(); i++) {
                    if (p.getSiteName() != null && p.getSiteName().contains(ecommerceList[userSelectedEcommerces.get(i)])) {
                        productFilterList.add(p);
                    }
                }
            }
        }

        switch (selectedSort) {
            case 1:
                sort(productFilterList, false);
                break;
            case 2:
                sort(productFilterList, true);
                break;
        }
        //set result number
        setResultNumber(productFilterList.size());
        updateRecyclerView(productFilterList);
    }

    public void sort(List list, final boolean asc){
        Collections.sort(list, new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                if(asc)// jika pengurutan secara ascending
                    return p1.getPrice() - p2.getPrice();
                else // jika pengurutan secara descending
                    return p2.getPrice() - p1.getPrice();
            }

        });
    }

    private void updateRecyclerView(List productList){
        myAdapter = new RecyclerViewAdapter(SearchResultActivity.this, productList);
        mRecyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                myAdapter.notifyItemChanged(position);
            }
        });
    }

    private void uploadImage(String imagePath) {
        String BASE_URL = "";
        try {
            BASE_URL = "http://" + Util.getProperty("IP",getApplicationContext()) + "/";
        } catch (IOException e) {
            e.printStackTrace();
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        //Create a file object using file path
        File file = new File(imagePath);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);

        Log.d("image", file.getName());

//        showDataDummy();
        // Set up progress before call
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(SearchResultActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Please wait....");
        progressDialog.setTitle("Fetching Data");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        progressDialog.show();
        Call<Result> call = service.uploadImage(part);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.code() == 200) {
                    //get response
                    Log.d("message", "succes");
                    // close it after response
                    mProductList = response.body().getResult();
                    //set result number
                    setResultNumber(mProductList.size());
                    updateRecyclerView(mProductList);
                } else {
                    intentToEmptyActivity();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                intentToEmptyActivity();
            }
        });
    }

    private void setResultNumber(int number){
        TextView result = findViewById(R.id.resultNumber);
        result.setText(number+" Product(s)");
    }

    private void intentToEmptyActivity(){
        Intent intent = new Intent(SearchResultActivity.this, EmptyResultActivity.class);
        startActivity(intent);
    }

    private void showDataDummy() {
        Product product1 = new Product(1, "Long Dress", "Berry Benka", "https://berrybenka.com/clothing/tops/100855/dale-top?trc_sale=clothing+blouse", 200500, "281021-febry-basic-shirt-beige-cream-1-PUV2.jpg", "https://i.ibb.co/qYGmHyR/281021-febry-basic-shirt-beige-cream-1-PUV2.jpg");
        Product product2 = new Product(2, "Pure Cotton Polo Shirt", "Berry Benka", "https://berrybenka.com/clothing/tops/102345/chuwa-top?trc_sale=clothing+tank-top", 230500, "280735-fimanda-flare-dress-brown-brown-IMW9-B.jpg","https://i.ibb.co/n03T2qx/280735-fimanda-flare-dress-brown-brown-IMW9-B.jpg");
        Product product3 = new Product(3, "Long Sleeve Top", "MapeMall", "https://berrybenka.com/clothing/tops/102347/chuv-top?trc_sale=clothing+tank-top", 204500, "280370-denice-linen-blouse-cream-cream-AR0-HG.jpg","https://i.ibb.co/KsQ2D7K/280370-denice-linen-blouse-cream-cream-AR0-HG.jpg");
        Product product4 = new Product(4, "Floral Print Waisted", "Zalora", "https://berrybenka.com/clothing/tops/104726/ellie-blue-pleats-top?trc_sale=clothing+blouse", 134500, "112219-gw-freital-top-in-cream-wheat-E0-Y3-J.jpg","https://i.ibb.co/0Y073M1/112219-gw-freital-top-in-cream-wheat-E0-Y3-J.jpg");
        Product product5 = new Product(5, "Dogtooth Print Jersey", "Berry Benka", "https://berrybenka.com/clothing/tops/104727/ellie-grey-pleats-top?trc_sale=clothing+blouse", 334500, "VVyMDfg/272915-xickey-long-sleeves-upper-cream-cream-GGC3-X.jpg","https://i.ibb.co/VVyMDfg/272915-xickey-long-sleeves-upper-cream-cream-GGC3-X.jpg");
        Product product6 = new Product(6, "Pure Linen Popover", "Zalora", "https://berrybenka.com/clothing/tops/104728/ellie-red-pleats-top?trc_sale=clothing+blouse", 434500, "279731-misty-button-blouse-nude-cream-XP18-X.jpg", "https://i.ibb.co/Cz1gXhd/279731-misty-button-blouse-nude-cream-XP18-X.jpg");

        mProductList = new ArrayList<>();
        mProductList.add(product1);
        mProductList.add(product2);
        mProductList.add(product3);
        mProductList.add(product4);
        mProductList.add(product5);
        mProductList.add(product6);

        updateRecyclerView(mProductList);
    }


}
