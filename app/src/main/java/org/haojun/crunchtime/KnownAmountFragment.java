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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class KnownAmountFragment extends Fragment {

    public KnownAmountFragment() { }

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
        _rootView = inflater.inflate(R.layout.known_amount_fragment, container, false);
        // Selecting all Widgets
        final Spinner spinner = (Spinner) _rootView.findViewById(R.id.activity_spinner);
        final EditText num = (EditText) _rootView.findViewById(R.id.amount_num);
        final ListView activities = (ListView) _rootView.findViewById(R.id.activity_list_amount);

        // Setting Adapters
        final ActivityAdapter activityAdapter = new ActivityAdapter(getContext(),
                R.layout.list_item_activity, _convertedList, _activitiesList, _unitDic);
        activities.setAdapter(activityAdapter);
        spinner.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_item,
                R.id.spinner_item_textview, new ArrayList<>(_activitiesList)));

        // Setting Listeners
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView,
                                       View view, int pos, long id) {
                String selection = (String) spinner.getItemAtPosition(pos);
                int reps = num.length() > 0 ? Integer.parseInt(num.getText().toString()) : 0;
                updateCalorie(reps, selection);
                updateUnit(selection);
                updateAll(reps, selection);
                activityAdapter.notifyDataSetChanged();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        num.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                int reps = num.length() > 0 ? Integer.parseInt(s.toString()) : 0;
                String selection = spinner.getSelectedItem().toString();
                updateCalorie(reps, selection);
                updateAll(reps, selection);
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
                    String selection = spinner.getSelectedItem().toString();
                    int reps = num.length() > 0 ?
                            Integer.parseInt(num.getText().toString()) : 0;
                    updateCalorie(reps, selection);
                    updateAll(reps, selection);
                    activityAdapter.notifyDataSetChanged();
                }
            };
        pref.registerOnSharedPreferenceChangeListener(_preferenceListener);
        return _rootView;
    }

    /** This method updates the calorie amount.
     *  @param num -- the number of mins/reps the user did.
     *  @param activity -- the activity the user was doing.
     */
    private void updateCalorie(int num, String activity) {
        double calorie = _converter.toCalorie(num, activity);
        TextView cal = (TextView) _rootView.findViewById(R.id.cal);
        cal.setText(String.format("%f", calorie));
    }

    /** Ths method updates the unit given an activity.
     *  @param activity -- the activity user was doing.
     */
    private void updateUnit(String activity) {
        TextView unit = (TextView) _rootView.findViewById(R.id.amount_unit);
        unit.setText(_unitDic.get(activity));
    }

    /** This method updates the converted dictionary to the most current values.
     * It does not, however, notify adapters of this change.
     * @param num -- the number of mins/reps/the user did.
     * @param activity -- the activity the user was doing.
     */
    private void updateAll(int num, String activity) {
        for (int i = 0; i < _activitiesList.size(); i++) {
            double calorie = _converter.toCalorie(num, activity);
            int reps = _converter.toUnit(calorie, _activitiesList.get(i));
            _convertedList.set(i, String.format("%d", reps));
        }
    }

    private SharedPreferences.OnSharedPreferenceChangeListener _preferenceListener;
    private HashMap<String, Double> _converterDic = new HashMap<>();
    private HashMap<String, String> _unitDic = new HashMap<>();
    private List<String> _activitiesList = new ArrayList<>();
    private List<String> _convertedList = new ArrayList<>();
    private Converter _converter;
    private View _rootView;
}
