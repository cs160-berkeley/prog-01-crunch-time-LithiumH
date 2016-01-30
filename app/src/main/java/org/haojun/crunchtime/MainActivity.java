package org.haojun.crunchtime;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.content.Context;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.AdapterView;
import android.widget.ListView;
import android.text.Editable;
import android.text.TextWatcher;

import static android.widget.AdapterView.OnItemSelectedListener;

import org.haojun.crunchtime.Converter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            initiate(150, this.getResources().getAssets().open("activity_calorie"));
        } catch (IOException e){
            e.printStackTrace();
        }

        // Selecting all Widgets
        final Spinner spinner = (Spinner) findViewById(R.id.activity_spinner);
        final EditText num = (EditText) findViewById(R.id.num);
        final ListView activities = (ListView) findViewById(R.id.activity_list);

        // Setting Adapters
        spinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item,
                    R.id.spinner_item_textview, new ArrayList<String>(_converted.keySet())));
        // activities.setAdapter(new ActivityAdapter(this, R.layout.list_item_activity,
        //             new ArrayList<String>(_converted.keySet())));
        activities.setAdapter(new ActivityAdapter(this, R.layout.list_item_activity,
                    new ArrayList<String>(_converted.keySet())));

        // Setting Listeners
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView,
                View view, int pos, long id) {
                String selection = (String) spinner.getItemAtPosition(pos);
                updateCalorie(num.length() > 0 ? Integer.parseInt(num.getText().toString()) : 0,
                    selection);
                updateUnit(selection);
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        num.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                updateCalorie(s.length() > 0 ? Integer.parseInt(s.toString()) : 0,
                    spinner.getSelectedItem().toString());
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** This method updates the calorie amount.
     *  @param num -- the number of mins/reps the user did.
     *  @param activity -- the activity the user was doing.
     */
    private void updateCalorie(int num, String activity) {
        double calorie = _converter.toCalorie(num, activity);
        TextView cal = (TextView) findViewById(R.id.cal);
        cal.setText(String.format("%d",Math.round(calorie)));
    }

    /** Ths method updates the unit given an activity.
     *  @param activity -- the activity user was doing.
     */
    private void updateUnit(String activity) {
        TextView unit = (TextView) findViewById(R.id.unit);
        unit.setText(_unitDic.get(activity));
    }

    /** This is the method that reads the configuration file and creates 2
      * database of activity/min(reps)/kg converter. One for the converter one
      * for the unit (reps/min) conversion.
      * @param stream -- the input stream of the configuration file.
      */
    private void initiate(int weight, InputStream stream) {
        HashMap<String, Double> converterDic = new HashMap<String, Double>();
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
        for (String activity : _unitDic.keySet()) {
            double convertedCal = 0.0; //TODO
            _converted.put(activity, convertedCal);
        }
    }

    private class ActivityAdapter extends ArrayAdapter<String> {
        public ActivityAdapter(Context context, int resource, List<String> activities) {
            super(context, resource, activities);
            this._activities = activities;
            this._resource = resource;
        }

        public View getView(int pos, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(this._resource, parent, false);
            }
            String activity = this._activities.get(pos);
            if (activity != null) {
                TextView name = (TextView) convertView.findViewById(R.id.activity_name);
                TextView calorie = (TextView) convertView.findViewById(R.id.activity_calorie);
                if (name != null && calorie != null) {
                    name.setText(activity);
                    calorie.setText(
                            String.format("%d", Math.round(_converted.get(activity))));
                    int dark = Color.rgb(0x8C, 0x46, 0x46);
                    int light = Color.rgb(0xF2, 0xAE, 0x72);
                    convertView.setBackgroundColor(pos % 2 == 0 ? light : dark);
                    name.setTextColor(pos % 2 == 0 ? dark : light);
                    calorie.setTextColor(pos % 2 == 0 ? dark : light);
                }
            }
            return convertView;
        }

        private List<String> _activities;
        private int _resource;
    }

    private final HashMap<String, String> _unitDic = new HashMap<String, String>();
    private final HashMap<String, Double> _converted = new HashMap<String, Double>();
    private Converter _converter;
}
