package com.example.bookapps.view.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.example.bookapps.R;
import com.example.bookapps.model.firebase.BookVolume;
import com.example.bookapps.model.request.api.Item;
import com.example.bookapps.model.request.api.VolumeInfo;
import com.example.bookapps.model.request.constant.Constant;
import com.example.bookapps.model.request.retrofit.RequestService;
import com.example.bookapps.model.request.retrofit.RetrofitClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView bookImageIV;
    private TextView publisherTV, titleTV, authorTV, descriptionTV, categoriesTV,
            publishedDateTV, pageCountTV, languageTV, isbnsTV;
    private Button activeBookmark, inactiveBookmark;
    private RequestService requestService;
    private Call<Item> itemCall;
    private String title="",volume_id="";
    private FirebaseDatabase firebaseDataBase;
    private DatabaseReference myRef;
    private FirebaseUser firebaseUser;
    private String userPath;
    private AutoCompleteTextView autoTV;
    private ArrayAdapter<String> dropDownAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        // Initialize
        requestService = RetrofitClass.getAPIInstance();
        bookImageIV = findViewById(R.id.bookImageIV);
        publisherTV = findViewById(R.id.publisherInfoTV);
        titleTV = findViewById(R.id.bookTitleTV);
        authorTV = findViewById(R.id.bookAuthorTV);
        descriptionTV = findViewById(R.id.descriptionTV);
        categoriesTV = findViewById(R.id.categoryTV);
        publishedDateTV = findViewById(R.id.publishedTV);
        isbnsTV = findViewById(R.id.isbnTV);
        pageCountTV = findViewById(R.id.pageCountTV);
        languageTV = findViewById(R.id.languageTV);
        activeBookmark = findViewById(R.id.activeBookmark);
        inactiveBookmark = findViewById(R.id.inactiveBookmark);
        autoTV = findViewById(R.id.autoTV);

        //Firebase setup

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDataBase = FirebaseDatabase.getInstance();
        userPath = firebaseUser.getUid();


        //Array Adapter
        String [] readingActivity = {"FINISH", "READING", "PLAN TO READ"};
        dropDownAdapter = new ArrayAdapter<>(BookInfoActivity.this, R.layout.dropdown_item, readingActivity);
        autoTV.setAdapter(dropDownAdapter);


        //setup and getBook info from API
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            volume_id = bundle.getString("volume_id");
            displayBookItem(volume_id);

        }
        // onclick listener
        activeBookmark.setOnClickListener(this);
        inactiveBookmark.setOnClickListener(this);


        autoTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String status = parent.getItemAtPosition(position).toString();
                myRef = firebaseDataBase.getReference(userPath);
                myRef.child(volume_id).setValue(new BookVolume(volume_id,status));
                addToBookmark();
            }
        });

    }

    private void setIsBookmark(){
        myRef = firebaseDataBase.getReference(userPath);

        myRef.child(volume_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        DataSnapshot dataSnapshot = task.getResult();
                        String volumeID = String.valueOf(dataSnapshot.child("volumeID").getValue());
                        if(volumeID.equals(volume_id)){
                            activeBookmark.setVisibility(View.VISIBLE);
                            inactiveBookmark.setVisibility(View.GONE);
                        }else{
                            activeBookmark.setVisibility(View.GONE);
                            inactiveBookmark.setVisibility(View.VISIBLE);
                        }
                    }else{
                        activeBookmark.setVisibility(View.GONE);
                        inactiveBookmark.setVisibility(View.VISIBLE);
                    }
                }else{
                    activeBookmark.setVisibility(View.GONE);
                    inactiveBookmark.setVisibility(View.VISIBLE);
                }
            }
        });
    }




    private void addToBookmark() {
        myRef = firebaseDataBase.getReference(userPath);
        if(autoTV.getText().toString().isEmpty()){
            myRef.child(volume_id).setValue(new BookVolume(volume_id, "READING"));
        }else{
            myRef.child(volume_id).setValue(new BookVolume(volume_id, autoTV.getText().toString()));
        }
        inactiveBookmark.setVisibility(View.GONE);
        activeBookmark.setVisibility(View.VISIBLE);
    }

    private void removeFromBookmark() {
        myRef = firebaseDataBase.getReference(userPath);
        myRef.child(volume_id).removeValue();
        inactiveBookmark.setVisibility(View.VISIBLE);
        activeBookmark.setVisibility(View.GONE);

    }
    private void setReadingStatus(){
        myRef = firebaseDataBase.getReference(userPath);

        myRef.child(volume_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        DataSnapshot dataSnapshot = task.getResult();
                        String readStatus = String.valueOf(dataSnapshot.child("readStatus").getValue());
                        String volumeID = String.valueOf(dataSnapshot.child("volumeID").getValue());
                        if(volumeID.equals(volume_id)){
                           autoTV.setHint(readStatus);
                        }else{
                        }
                    }else{
                    }
                }else{
                }
            }
        });

    }

    void displayBookItem(String id) {
        itemCall = requestService.getBookItem(id);
        itemCall.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                VolumeInfo volume = response.body().getVolumeInfo();
                if (response.isSuccessful()) {

                    setIsBookmark();
                    setReadingStatus();

                    title = volume.getTitle();
                    setTitle(volume.getTitle()+"");
                    titleTV.setText(volume.getTitle());
                    languageTV.setText(volume.getLanguage().toUpperCase());
                    try {
                        publisherTV.setText(volume.getPublisher());
                    }catch (Exception e) {
                        publisherTV.setText(R.string.dash);
                    }
                    try {
                        publishedDateTV.setText(volume.getPublishedDate());
                    }catch (Exception e) {
                        publishedDateTV.setText(R.string.dash);
                    }
                    try {
                        pageCountTV.setText(volume.getPageCount()+" pages");
                    }catch (Exception e) {
                        pageCountTV.setText(R.string.dash);
                    }

                    String categories="", authors="", isbns="";

                    try {
                        for (int i=0; i<volume.getCategories().size(); i++) {
                            categories = ""+volume.getCategories().get(i)+"\n";
                            categoriesTV.append(""+categories);
                        }
                    }catch (Exception e) {
                        categoriesTV.setText(R.string.dash);
                    }


                    try {
                        for (int j=0; j<volume.getAuthors().size(); j++) {
                            if (volume.getAuthors().size()==1) {
                                authors = ""+volume.getAuthors().get(j);
                            }else {
                                authors = ""+volume.getAuthors().get(j)+", ";
                            }
                        }
                        authorTV.setText("By "+ authors);


                    }catch (Exception e) {
                        authorTV.setText(R.string.dash);
                    }
                    try {
                        Glide.with(BookInfoActivity.this).load(volume.getImageLinks().getThumbnail()).placeholder(R.color.grey).into(bookImageIV);
                    }catch (Exception e) {
                        Glide.with(BookInfoActivity.this).load(Constant.N0_IMAGE_PLACEHOLDER)
                                .into(bookImageIV);
                    }

                    try {
                        for (int k=0;k<volume.getIndustryIdentifiers().size(); k++) {
                            isbns = ""+volume.getIndustryIdentifiers().get(k).getIdentifier()+"\n";
                            isbnsTV.append(""+isbns);
                        }
                    }catch (Exception e) {
                        isbnsTV.setText(R.string.dash);
                    }

                }


                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        descriptionTV.setText(Html.fromHtml(volume.getDescription(), Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        descriptionTV.setText(Html.fromHtml(volume.getDescription()));
                    }
                }catch (Exception e) {
                    descriptionTV.setText(R.string.dash);
                }

            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activeBookmark:
                removeFromBookmark();
                break;
            case R.id.inactiveBookmark:
                addToBookmark();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

}
