package com.example.image_search.search_result;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.image_search.Product;

import java.util.ArrayList;
import java.util.List;

public interface SearchResultContract {
    interface View{
        void showProduct(List<Product> mProductList);
        ProgressDialog setProgressDialog();
        void intentToEmptyActivity();
    }

    interface Interactor{
        void filterSortAction(String[] ecommerceList, ArrayList<Integer> userSelectedEcommerces, int selectedSort);
        void sort(boolean ascending);
        void setProduct(List<Product> mProductList);
        List<Product> getProduct();
        List<Product> getProductFilter();
    }

    interface Presenter{
        void uploadImage(String imagePath, Context context);
        void filterSortAction(String[] ecommerceList, ArrayList<Integer> userSelectedEcommerces, int selectedSort, Context context);
    }
}
