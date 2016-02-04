/** This class is the activity adapter which will update the ListView when
  * the underlying data is changed (or initialized)
  */

package org.haojun.crunchtime;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import java.util.HashMap;

class ActivityAdapter extends ArrayAdapter<String> {
    public ActivityAdapter(Context context, int resource, List<String> convertedList,
            List<String> activitiesList, HashMap<String, String> unitDic) {
        super(context, resource, convertedList);
        this._resource = resource;
        this._activitiesList = activitiesList;
        this._convertedList = convertedList;
        this._unitDic = unitDic;
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
            TextView unit = (TextView) convertView.findViewById(R.id.unit);
            if (name != null && calorie != null && unit != null) {
                name.setText(activity);
                calorie.setText(_convertedList.get(pos));
                unit.setText(_unitDic.get(activity));
                int dark = Color.rgb(0x8C, 0x46, 0x46);
                int light = Color.rgb(0xF2, 0xAE, 0x72);
                convertView.setBackgroundColor(pos % 2 == 1 ? light : dark);
                name.setTextColor(pos % 2 == 1 ? dark : light);
                calorie.setTextColor(pos % 2 == 1 ? dark : light);
            }
        }
        return convertView;
    }

    private int _resource;
    private List<String> _activitiesList;
    private List<String> _convertedList;
    private HashMap<String, String> _unitDic;
}

