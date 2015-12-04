package com.asinenko.carcalendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.asinenko.carcalendar.items.MeasurementItem;
import com.asinenko.carcalendar.items.OdometerItem;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class CarActivity extends AppCompatActivity {

    private TextView carVin;
    private TextView carNumber;

    private Realm realm;
    private RealmResults<MeasurementItem> allMeasuresListResult;
    private Activity activity;
    private ListView listview;
    private OdometerArrayAdapter adapter;

    private RealmChangeListener realmCarListener = new RealmChangeListener() {
        @Override
        public void onChange() {
            adapter.notifyDataSetChanged();
            listview.invalidateViews();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        carVin = (TextView)findViewById(R.id.vinId);
        carNumber = (TextView)findViewById(R.id.numberId);

        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String vin = intent.getStringExtra("vin");
        String number = intent.getStringExtra("number");

        carVin.setText(vin);
        carNumber.setText(number);
        setTitle(name);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        realm = Realm.getDefaultInstance();
        realm.addChangeListener(realmCarListener);
        allMeasuresListResult = realm.where(MeasurementItem.class).findAll();

        adapter = new OdometerArrayAdapter(this, allMeasuresListResult);
        listview = (ListView) findViewById(R.id.measureListView);
        listview.setAdapter(adapter);
        registerForContextMenu(listview);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //editMeasure(position);
            }
        });
    }

    public class OdometerArrayAdapter extends BaseAdapter {
        private final Context context;
        private RealmResults<OdometerItem> valuesResult;

        public OdometerArrayAdapter(Context context, RealmResults<OdometerItem> values) {
            this.context = context;
            this.valuesResult = values;
        }

        @Override
        public int getCount() {
            return valuesResult.size();
        }

        @Override
        public Object getItem(int position) {
            return valuesResult.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_measure_item, parent, false);
            TextView nameView = (TextView) rowView.findViewById(R.id.measureNameTextView);
            TextView typeView = (TextView) rowView.findViewById(R.id.measureTypeTextView);
            TextView intervalView = (TextView) rowView.findViewById(R.id.measureIntervalTextView);
//            MeasurementItem measure = valuesResult.get(position);
//            nameView.setText(measure.getName());
//
//            if (measure.getTimeType() != null && measure.getTimeType().equalsIgnoreCase("Время")){
//                typeView.setText(" дн");
//            }else if (measure.getTimeType() != null && measure.getTimeType().equalsIgnoreCase("Дистанция")){
//                typeView.setText(" км");
//            }
//            intervalView.setText(String.valueOf(measure.getStandartInterval()));
            return rowView;
        }
    }

}
