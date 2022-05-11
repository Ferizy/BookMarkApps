package com.example.bookapps.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.bookapps.R;
import com.example.bookapps.model.firebase.BookVolume;
import com.example.bookapps.model.request.api.Item;
import com.example.bookapps.model.request.constant.Constant;
import com.example.bookapps.model.request.retrofit.RequestService;
import com.example.bookapps.model.request.retrofit.RetrofitClass;
import com.example.bookapps.view.activity.BookInfoActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookmarksRecyclerviewAdapter extends RecyclerView.Adapter<BookmarksRecyclerviewAdapter.ViewHolder> {
    private Context context;
    private List<BookVolume> localVolumeBooks;
    private Call<Item> itemCall, itemCall1;
    private RequestService requestService = RetrofitClass.getAPIInstance();


    public BookmarksRecyclerviewAdapter(Context context, ArrayList<BookVolume> localVolumeBooks) {
        this.context = context;
        this.localVolumeBooks = localVolumeBooks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bookmark, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookVolume bookVolume1 = localVolumeBooks.get(viewHolder.getAdapterPosition());
                itemCall1 = requestService.getBookItem(bookVolume1.getVolumeID());
                itemCall1.enqueue(new Callback<Item>() {
                    @Override
                    public void onResponse(Call<Item> call, Response<Item> response) {
                        if (response.isSuccessful()) {
                            Intent intent = new Intent(v.getContext(), BookInfoActivity.class);
                            intent.putExtra("volume_id", response.body().getId());
                            v.getContext().startActivity(intent);
                        }
                    }
                    @Override
                    public void onFailure(Call<Item> call, Throwable t) {

                    }
                });
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookVolume bookVolume = localVolumeBooks.get(position);
        itemCall = requestService.getBookItem(bookVolume.getVolumeID());

        itemCall.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                Item item = response.body();
                if (response.isSuccessful()) {
                    holder.parentLL.setVisibility(View.VISIBLE);
                    holder.titleTV.setText(item.getVolumeInfo().getTitle());
                    try{
                        holder.publisherTV.setText(item.getVolumeInfo().getPublisher());
                    }catch (Exception e) {
                        holder.publisherTV.setText(R.string.dash);
                    }

                    try {
                        Glide.with(context).load(item.getVolumeInfo().getImageLinks().getSmallThumbnail()).centerCrop().into(holder.imageView);
                    }catch (Exception e) {
                        Glide.with(context).load(Constant.N0_IMAGE_PLACEHOLDER)
                                .centerCrop().into(holder.imageView);
                    }


                    try {
                        switch (item.getVolumeInfo().getAuthors().size()) {
                            case 1:
                                holder.authorTV.setText("By "+item.getVolumeInfo().getAuthors().get(0));
                                break;
                            case 2:
                                holder.authorTV.setText("By "+item.getVolumeInfo().getAuthors().get(0)+", "+item.getVolumeInfo().getAuthors().get(1));
                                break;
                            case 3:
                                holder.authorTV.setText("By "+item.getVolumeInfo().getAuthors().get(0)+", "+item.getVolumeInfo().getAuthors().get(1)+", "+item.getVolumeInfo().getAuthors().get(2));
                                break;
                            case 4:
                                holder.authorTV.setText("By "+item.getVolumeInfo().getAuthors().get(0)+", "+
                                        item.getVolumeInfo().getAuthors().get(1)+", "+
                                        item.getVolumeInfo().getAuthors().get(2)+", "+item.getVolumeInfo().getAuthors().get(3));
                                break;
                            case 5:
                                holder.authorTV.setText("By "+item.getVolumeInfo().getAuthors().get(0)+", "+item.getVolumeInfo().getAuthors().get(1)+", "+item.getVolumeInfo().getAuthors().get(2)+
                                        ", "+item.getVolumeInfo().getAuthors().get(3)+", "+item.getVolumeInfo().getAuthors().get(4));
                                break;
                            default:
                                holder.authorTV.setText("By "+item.getVolumeInfo().getAuthors().get(0));
                        }
                    }catch (Exception e) {
                        holder.authorTV.setText(R.string.dash);
                    }
                }
            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return localVolumeBooks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView publisherTV, titleTV, authorTV;
        View parentLL;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            parentLL = itemView.findViewById(R.id.parentLL);
            publisherTV = itemView.findViewById(R.id.publisherTV);
            titleTV = itemView.findViewById(R.id.titleTV);
            authorTV = itemView.findViewById(R.id.authorTV);
        }
    }
}
