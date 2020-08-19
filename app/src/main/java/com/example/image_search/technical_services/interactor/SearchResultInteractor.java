package com.example.image_search.technical_services.interactor;

import com.example.image_search.contract.SearchResultContract;
import com.example.image_search.technical_services.data.Product;

import java.util.ArrayList;
import java.util.List;


public class SearchResultInteractor implements SearchResultContract.Interactor {
    private List<Product> productList = new ArrayList<>();
    private List<Product> productFilter;

    @Override
    public void filterAction(String[] ecommerceList, ArrayList<Integer> userSelectedEcommerces) {

        if(userSelectedEcommerces.size() == 0 || userSelectedEcommerces.size() == 3) {
            productFilter = new ArrayList<>(productList);
        }
        else{
            productFilter = new ArrayList<>();
            for (Product p : productList) {
                for (int i = 0; i < userSelectedEcommerces.size(); i++) {
                    if (p.getSiteName() != null && p.getSiteName().contains(ecommerceList[userSelectedEcommerces.get(i)])) {
                        productFilter.add(p);
                    }
                }
            }
        }
    }

    @Override
    public void setProduct(List<Product> mProductList) {
        productList = mProductList;
    }

    @Override
    public List<Product> getProduct(){
        return productList;
    }

    @Override
    public List<Product> getProductFilter() {
        return productFilter;
    }

}

