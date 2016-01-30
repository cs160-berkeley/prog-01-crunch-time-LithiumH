package org.haojun.crunchtime;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class KnownAmountFragment extends Fragment {

    public KnownAmountFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            initiate(150, getContext().getResources().getAssets().open("activity_calorie"));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _rootView = inflater.inflate(R.layout.known_amount_fragment, container, false);
        // Selecting all Widgets
        final Spinner spinner = (Spinner) _rootView.findViewById(R.id.activity_spinner);
        final EditText num = (EditText) _rootView.findViewById(R.id.num);
        final ListView activities = (ListView) _rootView.findViewById(R.id.activity_list);

        // Setting Adapters
        spinner.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_item,
                R.id.spinner_item_textview, new ArrayList<>(_activitiesList)));
        final ActivityAdapter activityAdapter = new ActivityAdapter(getContext(),
                R.layout.list_item_activity);
        activities.setAdapter(activityAdapter);

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
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        num.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                int reps = num.length() > 0 ? Integer.parseInt(s.toString()) : 0;
                String selection = spinner.getSelectedItem().toString();
                updateCalorie(reps, selection);
                updateAll(reps, selection);
                activityAdapter.notifyDataSetChanged();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        // FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        // fab.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                 .setAction("Action", null).show();
        //     }
        // });
        return _rootView;
    }

    /** This method updates the calorie amount.
     *  @param num -- the number of mins/reps the user did.
     *  @param activity -- the activity the user was doing.
     */
    private void updateCalorie(int num, String activity) {
        double calorie = _converter.toCalorie(num, activity);
        TextView cal = (TextView) _rootView.findViewById(R.id.cal);
        cal.setText(String.format("%f",calorie));
    }

    /** Ths method updates the unit given an activity.
     *  @param activity -- the activity user was doing.
     */
    private void updateUnit(String activity) {
        TextView unit = (TextView) _rootView.findViewById(R.id.unit);
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

    /** This is the method that reads the configuration file and creates 2
     * database of activity/min(reps)/kg converter. One for the converter one
     * for the unit (reps/min) conversion.
     * @param stream -- the input stream of the configuration file.
     */
    private void initiate(int weight, InputStream stream) {
        HashMap<String, Double> converterDic = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line = reader.readLine();
            while (line != null) {
                String[] data = line.split(",");
                this._unitDic.put(data[0], data[1]);
                converterDic.put(data[0], Double.parseDouble(data[2]));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        _converter = new Converter(weight, converterDic);
        _activitiesList.addAll(_unitDic.keySet());
        for (String activity : _activitiesList) {
            int convertedCal = 0; // TODO
            _convertedList.add(String.format("%d", convertedCal));
        }
    }

    private class ActivityAdapter extends ArrayAdapter<String> {
        public ActivityAdapter(Context context, int resource) {
            super(context, resource, _convertedList);
            this._resource = resource;
        }

        public View getView(int pos, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(this._resource, parent, false);
            }
            String activity = _activitiesList.get(pos);
            if (activity != null) {
                TextView name = (TextView) convertView.findViewById(R.id.activity_name);
                TextView calorie = (TextView) convertView.findViewById(R.id.activity_calorie);
                if (name != null && calorie != null) {
                    name.setText(activity);
                    calorie.setText(_convertedList.get(pos));
                    int dark = Color.rgb(0x8C, 0x46, 0x46);
                    int light = Color.rgb(0xF2, 0xAE, 0x72);
                    convertView.setBackgroundColor(pos % 2 == 0 ? light : dark);
                    name.setTextColor(pos % 2 == 0 ? dark : light);
                    calorie.setTextColor(pos % 2 == 0 ? dark : light);
                }
            }
            return convertView;
        }

        private int _resource;
    }
    private final HashMap<String, String> _unitDic = new HashMap<>();
    private final List<String> _activitiesList = new ArrayList<>();
    private final List<String> _convertedList = new ArrayList<>();
    private Converter _converter;
    private View _rootView;
}
