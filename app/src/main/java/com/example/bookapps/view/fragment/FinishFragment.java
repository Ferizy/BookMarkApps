package com.example.bookapps.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.bookapps.R;
import com.example.bookapps.adapter.BookmarksRecyclerviewAdapter;
import com.example.bookapps.model.firebase.BookVolume;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FinishFragment extends Fragment {

    TextView finishTV;
    RecyclerView finishRV;
    BookmarksRecyclerviewAdapter bookmarksAdapter;
    ArrayList<BookVolume> finishList;
    DatabaseReference myRef;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    String userPath;
    ConstraintLayout collectionCL;

    public FinishFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_finish, container, false);

        finishRV = root.findViewById(R.id.readingRV);
        finishTV = root.findViewById(R.id.finishTV);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userPath = firebaseUser.getUid();

        myRef = firebaseDatabase.getReference(userPath);

        setHasOptionsMenu(true);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                finishList = new ArrayList<>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String readStatus = String.valueOf(snapshot.child("readStatus").getValue());
                    String volumeID = String.valueOf(snapshot.child("volumeID").getValue());
                    if(readStatus.equals("FINISH")){
                        finishList.add(new BookVolume(volumeID,readStatus));
                    }
                }

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                finishRV.setLayoutManager(layoutManager);
                bookmarksAdapter = new BookmarksRecyclerviewAdapter(getContext(), finishList);
                finishRV.setAdapter(bookmarksAdapter);
                bookmarksAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.backOption){
            callFragment(new CollectionFragment());
        }
        return super.onOptionsItemSelected(item);
    }


    private void callFragment(Fragment fragment) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction().replace(R.id.collectionContainer, fragment);
        transaction.commit();

    }









}