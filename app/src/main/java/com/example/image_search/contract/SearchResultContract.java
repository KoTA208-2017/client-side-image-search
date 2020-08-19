package com.example.image_search.contract;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.image_search.technical_services.data.Product;

import java.util.ArrayList;
import java.util.List;

public interface SearchResultContract {
    interface View{
        void showProduct(List<Product> mProductList);
        ProgressDialog setProgressDialog();
        void intentToEmptyActivity(String statusCode);
    }

    interface Interactor{
        void filterAction(String[] ecommerceList, ArrayList<Integer> userSelectedEcommerces);
        void setProduct(List<Product> mProductList);
        List<Product> getProduct();
        List<Product> getProductFilter();
    }

    interface Presenter{
        void uploadImage(String imagePath, Context context);
        void filterAction(String[] ecommerceList, ArrayList<Integer> userSelectedEcommerces, Context context);
    }
}
