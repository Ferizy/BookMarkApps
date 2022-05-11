package com.example.bookapps.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.bookapps.R;
import com.example.bookapps.adapter.SearchResultsRecyclerviewAdapter;
import com.example.bookapps.model.extra.EndlessRecyclerViewScrollListener;
import com.example.bookapps.model.request.api.Books;
import com.example.bookapps.model.request.api.Item;
import com.example.bookapps.model.request.retrofit.RequestService;
import com.example.bookapps.model.request.retrofit.RetrofitClass;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private EditText searchET;
    private ImageView clearBTN;
    private TextView sortRelevanceTV, sortNewest, placeholderTitleTV, placeholderTextTV;
    private RecyclerView searchResultsRV;
    private ProgressBar progressBar;
    private SearchResultsRecyclerviewAdapter searchAdapter;
    private LinearLayoutManager layoutManager;
    private String search_keyword="";
    private String orderBy="relevance";
    private RequestService requestService;
    private Call<Books> searchResultsCall;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    int page=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);


        searchET = root.findViewById(R.id.searchET);
        clearBTN = root.findViewById(R.id.clearBTN);
        sortRelevanceTV = root.findViewById(R.id.sortRelevanceTV);
        sortNewest = root.findViewById(R.id.sortNewest);
        searchResultsRV = root.findViewById(R.id.searchResultsRV);
        progressBar = root.findViewById(R.id.progressBar);
        placeholderTitleTV = root.findViewById(R.id.placeholderTitleTV);

        requestService = RetrofitClass.getNewBooksAPIInstance();

        onDestroyOptionsMenu();



        clearBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_keyword="";
                searchET.setText("");
                progressBar.setVisibility(View.GONE);
            }
        });
        sortRelevanceTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRelevantItems(page);
                orderBy="relevance";
            }
        });
        sortNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewestItems(page);
                orderBy="newest";
            }
        });


        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search_keyword = searchET.getText().toString().trim();
                    if (!searchET.getText().toString().trim().isEmpty()) {
                        loadRelevantItems(page);
                        placeholderTitleTV.setVisibility(View.GONE);
                        placeholderTextTV.setVisibility(View.GONE);
                        searchResultsRV.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }else {
                        searchResultsRV.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        placeholderTitleTV.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearBTN.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                search_keyword = searchET.getText().toString().trim();
                if (s.length() >0 && (!searchET.getText().toString().trim().isEmpty())){
                    loadRelevantItems(page);
                }else if (s.length() ==0 && searchET.getText().toString().trim().isEmpty()){
                    clearBTN.setVisibility(View.GONE);
                    placeholderTitleTV.setVisibility(View.VISIBLE);
                    searchResultsRV.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }else {
                    clearBTN.setVisibility(View.GONE);
                    placeholderTitleTV.setVisibility(View.GONE);
                    placeholderTextTV.setVisibility(View.GONE);
                    searchResultsRV.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        return root;
    }

    private void callSearchResults(String orderBy, int index) {
        String finalQuery = search_keyword.replace(" ","+");
        searchResultsCall = requestService.getSearchResults(finalQuery,index,orderBy,40);

        searchResultsCall.enqueue(new Callback<Books>() {
            @Override
            public void onResponse(Call<Books> call, Response<Books> response) {
                if (response.isSuccessful() || response.body() != null) {
                    placeholderTitleTV.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    searchResultsRV.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    for (int i=0; i<response.body().getItems().size(); i++) {
                        setUpSearchResultslist(response.body().getItems());
                    }
                }

                if (response.code()!=200) {
                    progressBar.setVisibility(View.GONE);
                    ;
                }
            }

            @Override
            public void onFailure(Call<Books> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                placeholderTitleTV.setVisibility(View.GONE);
                placeholderTextTV.setText(R.string.something_went_wrong);
                placeholderTextTV.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setUpSearchResultslist(List<Item> itemList) {
        searchAdapter = new SearchResultsRecyclerviewAdapter(getContext(),itemList);
        layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager, this::onLoadMore);
        searchResultsRV.setLayoutManager(layoutManager);
        searchResultsRV.setAdapter(searchAdapter);
    }

    void loadRelevantItems(int page) {
        sortRelevanceTV.setAlpha((float) 0.9);
        sortNewest.setAlpha((float) 0.5);
        searchResultsRV.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        callSearchResults("relevance",page);
    }

    void loadNewestItems(int page) {
        sortRelevanceTV.setAlpha((float) 0.5);
        sortNewest.setAlpha((float) 0.9);
        searchResultsRV.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        callSearchResults("newest",page);
    }


    public void onLoadMore(int page, int totalItemsCount) {
        page++;
        callSearchResults("relevance", page);
    }



}
