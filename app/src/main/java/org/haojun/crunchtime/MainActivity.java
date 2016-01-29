package org.haojun.crunchtime;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.haojun.crunchtime.Converter;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Converter converter = null;
        converterDic = new HashMap<String, Double>();
        unitDic = new HashMap<String, String>();
        try {
            readFile(this.getResources().getAssets().open("activity_calorie"));
            converter = new Converter(150, converterDic);
        } catch (IOException e){
            e.printStackTrace();
        }
        TextView cal = (TextView) findViewById(R.id.cal);
        cal.setText(String.format("%d",Math.round(converter.toCalorie(100, "Situp"))));

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

    /** This is the method that reads the configuration file and creates 2
      * database of activity/min(reps)/kg converter. One for the converter one
      * for the unit (reps/min) conversion.
      * @param stream -- the input stream of the configuration file.
      * @return Nothing. It initiates two databases.
      */
    HashMap<String, Double>[] readFile(InputStream stream) {
        HashMap<String, Double>[] result = new HashMap<String, Double>[];
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line = reader.readLine();
            while (line != null) {
                String[] data = line.split(",");
                unitDic.put(data[0], data[1]);
                converterDic.put(data[0], Double.parseDouble(data[2]));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private HashMap<String, Double> _converterDic;
    private HashMap<String, String> _unitDic;
}
