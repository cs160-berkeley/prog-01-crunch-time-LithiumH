/** This class is a calories converter from any of the activity to another
  * given certain parameters. This is an object which needs to be initiated.
  */
package org.haojun.crunchtime;

import android.renderscript.ScriptGroup;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Converter {

    /** The initiation of the convertor requires the weight in (lb.) of the person. */
    Converter(int weight, HashMap<String, Double> activities) {
        this._weight = weight;
        this._activities = activities;
    }

    Converter(int weight) {
        this._weight = weight;
        this._activities = new HashMap<String, Double>();
    }

    /** This is the convert method that takes in a NUM of either repetitions or
      * minutes, and an activity as a String. The unit is automatic.
      * It returns a integer of Calories of the activity.
      * @param num -- the number of UNIT done (see unit param)
      * @param activity -- the activity as a String.
      * @return the amount of calories burned
      */
    double toCalorie(int num, String activity) {
        double calPerUnit = this._activities.get(activity);
        return num * calPerUnit * this._weight * 0.453592;
    }

    /** This is the convert method that takes in a CALorie, an activity, and
      * returns the number of either minutes or repetitions depending on the
      * activity.
      * @param cal -- the amount of calories
      * @param activity -- the activity we want to convert to
      * @return the reps or mins we need to complete the ACTIVITY to achieve CAL
      */
    int toUnit(double cal, String activity) {
        double calPerUnit = this._activities.get(activity);
        return (int) Math.round(cal / (this._weight * 0.453592 * calPerUnit));
    }


    /** This is the weight of the person. */
    private int _weight;
    /** This is the hashmap of data. */
    private HashMap<String, Double> _activities;
}
