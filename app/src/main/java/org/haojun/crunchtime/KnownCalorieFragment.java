package org.haojun.crunchtime;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class KnownCalorieFragment extends Fragment {


    public KnownCalorieFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    getContext().getResources().getAssets().open("activity_calorie")));
            String line = reader.readLine();
            while (line != null) {
                String[] data = line.split(",");
                this._unitDic.put(data[0], data[1]);
                _converterDic.put(data[0], Double.parseDouble(data[2]));
                _iconSrc.put(data[0], data[3]);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        int weight = Integer.parseInt(pref.getString("weight", "150"));
        _converter = new Converter(weight, _converterDic);
        _activitiesList.addAll(_unitDic.keySet());
        for (int i = 0; i < _activitiesList.size(); i++) {
            int convertedCal = 0;
            _convertedList.add(String.format("%d", convertedCal));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.known_calorie_fragment, container, false);
        final EditText num = (EditText) rootView.findViewById(R.id.calorie_num);
        final ListView activities = (ListView) rootView.findViewById(R.id.activity_list_calorie);

        // Setting Adapters
        final ActivityAdapter activityAdapter = new ActivityAdapter(getContext(),
                R.layout.list_item_activity, _convertedList, _activitiesList, _unitDic,
                _iconSrc);
        activities.setAdapter(activityAdapter);

        // Setting Listeners
        num.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                int reps = num.length() > 0 ? Integer.parseInt(s.toString()) : 0;
                updateAll(reps);
                activityAdapter.notifyDataSetChanged();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        _preferenceListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                  String key) {
                int weight = Integer.parseInt(sharedPreferences.getString(key, "150"));
                _converter = new Converter(weight, _converterDic);
                int reps = num.length() > 0 ?
                        Integer.parseInt(num.getText().toString()) : 0;
                updateAll(reps);
                activityAdapter.notifyDataSetChanged();
            }
        };
        pref.registerOnSharedPreferenceChangeListener(_preferenceListener);
        return rootView;
    }

    /** This method updates the converted dictionary to the most current values.
     * It does not, however, notify adapters of this change.
     * @param calorie -- the amount of Calories the user want.
     */
    private void updateAll(int calorie) {
        for (int i = 0; i < _activitiesList.size(); i++) {
            int reps = _converter.toUnit((double) calorie, _activitiesList.get(i));
            _convertedList.set(i, String.format("%d", reps));
        }
    }

    private SharedPreferences.OnSharedPreferenceChangeListener _preferenceListener;
    private HashMap<String, Double> _converterDic = new HashMap<>();
    private HashMap<String, String> _unitDic = new HashMap<>();
    private HashMap<String, String> _iconSrc = new HashMap<>();
    private List<String> _activitiesList = new ArrayList<>();
    private List<String> _convertedList = new ArrayList<>();
    private Converter _converter;
}
