package com.example.image_search;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class SearchResult extends AppCompatActivity {
    Button storeFilterBtn;
    TextView testFilter;

    String[] ecommerceList;
    boolean[] checkedEcommerces;
    ArrayList<Integer> userSelectedEcommerces = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        storeFilterBtn = findViewById(R.id.storeFilterBtn);
        testFilter = findViewById(R.id.testFilter);

        ecommerceList = getResources().getStringArray(R.array.ecommerce_name);
        checkedEcommerces = new boolean[ecommerceList.length];

        Properties prop = new Properties();

        try {
            //load a properties file
            prop.load(new FileInputStream("config.properties"));

            //get the property value and print it out
            System.out.println(prop.getProperty("IP"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        storeFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchResult.this);
                builder.setTitle(R.string.filter_ecommerce_dialog_title);
                builder.setMultiChoiceItems(ecommerceList, checkedEcommerces,
                    new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        Toast.makeText(getApplicationContext(), (position+": "+ecommerceList[position]+" is "+isChecked), Toast.LENGTH_LONG).show();
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

                        testFilter.setText(item);
                    }
                });

                builder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
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
                            userSelectedEcommerces.clear();
                            testFilter.setText("");
                        }
                    }
                });

                AlertDialog mDialog = builder.create();
                mDialog.show();
            }
        });
    }
}
