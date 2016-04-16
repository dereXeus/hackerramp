package com.parse.starter.services.models;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.starter.R;

import com.squareup.picasso.Picasso;

/**
 * Created by 11162 on 16/07/15.
 */
public class ProductListAdapter extends RecyclerView.Adapter {
    private SearchModel searchResult;
    private MyItemClickListener listener;
    public ProductListAdapter(SearchModel searchResult, MyItemClickListener listener){
        this.searchResult = searchResult;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View myProductView = inflater.inflate(R.layout.product_item,parent,false);
        return new MyCustomViewHolder(myProductView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Product product = searchResult.data.results.products.get(position);
        ((MyCustomViewHolder)holder).onBind(product);

    }

    @Override
    public int getItemCount() {
        return searchResult.data.results.products.size();
    }

    private class MyCustomViewHolder extends RecyclerView.ViewHolder{
        private ImageView productImage;
        private TextView productName;
        private TextView price;
        public MyCustomViewHolder(View itemView) {
            super(itemView);
            productImage = (ImageView) itemView.findViewById(R.id.productImage);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            price = (TextView) itemView.findViewById(R.id.price);
        }

        public void onBind(final Product product){
            Picasso.with(itemView.getContext()).load(product.search_image).into(productImage);
            productName.setText(product.product);
            price.setText(product.price);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(listener!=null){
                        listener.onItemClicked(product);
                    }
                }
            });
        }

    }
    public interface MyItemClickListener{
        void onItemClicked(Product product);
    }
}
