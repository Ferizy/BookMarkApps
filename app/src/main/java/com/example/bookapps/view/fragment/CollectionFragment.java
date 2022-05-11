package com.example.bookapps.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.bookapps.R;


public class CollectionFragment extends Fragment implements OnBackPressed {

    private Button finish, reading, plan;
    private ConstraintLayout collectionCL;


    public CollectionFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_collection, container, false);

        finish = root.findViewById(R.id.finishBTN);
        reading = root.findViewById(R.id.readingBTN);
        plan = root.findViewById(R.id.planBTN);
        collectionCL = root.findViewById(R.id.collectionCL);

        setHasOptionsMenu(true);




        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionCL.setVisibility(View.GONE);
                callFragment(new FinishFragment());
            }
        });

        reading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionCL.setVisibility(View.GONE);
                callFragment(new ReadingFragment());
            }
        });

        plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionCL.setVisibility(View.GONE);
                callFragment(new PlanFragment());
            }
        });

        return root;
    }

    @Override
    public boolean onBackPressed() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        int count = manager.getBackStackEntryCount();

        if (count == 0) {
            //action not popBackStack
            return true;
        } else {
            return false;
        }
    }

    private void callFragment(Fragment fragment) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction().replace(R.id.collectionContainer, fragment);
        transaction.commit();

    }

}
