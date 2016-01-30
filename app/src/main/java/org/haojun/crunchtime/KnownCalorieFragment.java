package org.haojun.crunchtime;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.haojun.crunchtime.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class KnownCalorieFragment extends Fragment {


    public KnownCalorieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.known_calorie_fragment, container, false);
    }

}
