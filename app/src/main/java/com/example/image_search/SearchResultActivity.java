package com.example.image_search;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

        Properties prop = new Properties();

        try {
            //load a properties file
            prop.load(new FileInputStream("config.properties"));

            //get the property value and print it out
            System.out.println(prop.getProperty("IP"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

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
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchResultActivity.this);
        builder.setTitle(R.string.filter_ecommerce_dialog_title);
        builder.setMultiChoiceItems(ecommerceList, checkedEcommerces,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            if (!userSelectedEcommerces.contains(position)) {
                                userSelectedEcommerces.add(position);
                            }
                        } else {
                            if (userSelectedEcommerces.contains(position)) {
                                userSelectedEcommerces.remove(new Integer(position));
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
            }
        });

        builder.setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
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
            }
        });

        AlertDialog mDialog = builder.create();
        mDialog.show();
    }

    private void uploadImage(String imagePath) {

        Product product1 = new Product(1, "Long Dress", "Berry Benka","https://berrybenka.com/clothing/tops/100855/dale-top?trc_sale=clothing+blouse", 234500, "https://i.ibb.co/qYGmHyR/281021-febry-basic-shirt-beige-cream-1-PUV2.jpg");
        Product product2 = new Product(2, "Pure Cotton Polo Shirt", "Berry Benka", "https://berrybenka.com/clothing/tops/102345/chuwa-top?trc_sale=clothing+tank-top", 234500, "https://i.ibb.co/n03T2qx/280735-fimanda-flare-dress-brown-brown-IMW9-B.jpg");
        Product product3 = new Product(3, "Long Sleeve Top", "MapeMall", "https://berrybenka.com/clothing/tops/102347/chuv-top?trc_sale=clothing+tank-top", 234500, "https://i.ibb.co/KsQ2D7K/280370-denice-linen-blouse-cream-cream-AR0-HG.jpg");
        Product product4 = new Product(4, "Floral Print Waisted", "Zalora", "https://berrybenka.com/clothing/tops/104726/ellie-blue-pleats-top?trc_sale=clothing+blouse", 234500, "https://i.ibb.co/0Y073M1/112219-gw-freital-top-in-cream-wheat-E0-Y3-J.jpg");
        Product product5 = new Product(5, "Dogtooth Print Jersey", "Berry Benka", "https://berrybenka.com/clothing/tops/104727/ellie-grey-pleats-top?trc_sale=clothing+blouse", 234500, "https://i.ibb.co/VVyMDfg/272915-xickey-long-sleeves-upper-cream-cream-GGC3-X.jpg");
        Product product6 = new Product(6, "Pure Linen Popover", "Zalora", "https://berrybenka.com/clothing/tops/104728/ellie-red-pleats-top?trc_sale=clothing+blouse",234500, "https://i.ibb.co/Cz1gXhd/279731-misty-button-blouse-nude-cream-XP18-X.jpg");

        mProductList = new ArrayList<>();
        mProductList.add(product1);
        mProductList.add(product2);
        mProductList.add(product3);
        mProductList.add(product4);
        mProductList.add(product5);
        mProductList.add(product6);

        myAdapter = new RecyclerViewAdapter(SearchResultActivity.this, mProductList);
        mRecyclerView.setAdapter(myAdapter);

        myAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                myAdapter.notifyItemChanged(position);
            }
        });
    }


}
