package com.asinenko.carcalendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
//import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.asinenko.carcalendar.items.MeasurementItem;
import com.asinenko.carcalendar.items.OdometerItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class CarActivity extends AppCompatActivity {

    private TextView carVin;
    private TextView carNumber;

    private String carName;

    private Realm realm;
    private RealmResults<OdometerItem> allOdometerListResult;
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
        activity = this;
        setContentView(R.layout.activity_car);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        carVin = (TextView)findViewById(R.id.vinId);
        carNumber = (TextView)findViewById(R.id.numberId);

        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String vin = intent.getStringExtra("vin");
        String number = intent.getStringExtra("number");

        carName = name;


        carVin.setText(vin);
        carNumber.setText(number);
        setTitle(name);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddMeasureDialog();
            }
        });
        realm = Realm.getDefaultInstance();
        realm.addChangeListener(realmCarListener);
        allOdometerListResult = realm.where(OdometerItem.class).findAll();
        adapter = new OdometerArrayAdapter(this, allOdometerListResult);
        listview = (ListView) findViewById(R.id.carOdometerListView);
        listview.setAdapter(adapter);
        registerForContextMenu(listview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //editMeasure(position);
            }
        });
    }

    private EditText odometerValueEditView;
    private TextView odometerDateEditView;
    private Button odometerButton;

    public void showAddMeasureDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_km_dialog, null);

        odometerValueEditView = (EditText) view.findViewById(R.id.odometerValueEdit);
        odometerDateEditView = (TextView) view.findViewById(R.id.odometerDateEdit);
        odometerButton = (Button) view.findViewById(R.id.setDateButton);

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        final Calendar c = Calendar.getInstance();
        String str = format.format(c.getTime());
        odometerDateEditView.setText(str);

        odometerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(odometerDateEditView);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        final AlertDialog dialog = builder.setTitle("Добавить показаниe").setView(view).
                setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int id) {
                        realm.beginTransaction();

                        OdometerItem item = realm.createObject(OdometerItem.class); // Create a new object

                        item.setValue(odometerValueEditView.getText().toString());
//                        item.setDate(new Date() odometerDateEditView.getText().toString());
                        item.setCarname(carName);
                        realm.commitTransaction();
                    }
                }).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int id) {

            }
        }).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });

        odometerValueEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                RealmResults<MeasurementItem> result = realm.where(MeasurementItem.class).equalTo("name", text).findAll();
                if (result.size() > 0 || text.equalsIgnoreCase("")) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });
        dialog.show();
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
            View rowView = inflater.inflate(R.layout.list_odometer_item, parent, false);
            TextView dateView = (TextView) rowView.findViewById(R.id.kmDateTextView);
            TextView valueView = (TextView) rowView.findViewById(R.id.kmValueTextView);
            OdometerItem item = valuesResult.get(position);
            dateView.setText(item.getDate().toString());
            valueView.setText(item.getValue());
            return rowView;
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private TextView editText;

        @SuppressLint("ValidFragment")
        public DatePickerFragment(TextView edit_text){
            editText = edit_text;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            editText.setText("Idi na huj");



            String str = (day < 10 ? "0" + day : day) + "." + (month < 10 ? "0" + month : month) + "." + year;
            editText.setText(str);
        }
    }
}
