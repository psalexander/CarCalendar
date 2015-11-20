package com.asinenko.carcalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.asinenko.carcalendar.items.MeasurementItem;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class CarCalendarMeasureActivity extends AppCompatActivity {

    private Realm realm;
    private RealmResults<MeasurementItem> allMeasuresListResult;
    private Activity activity;
    private ListView listview;
    private MeasuresArrayAdapter adapter;

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
        setContentView(R.layout.activity_car_calendar_measure);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddMeasureDialog();
            }
        });

        realm = Realm.getDefaultInstance();
        realm.addChangeListener(realmCarListener);
        allMeasuresListResult = realm.where(MeasurementItem.class).findAll();

        adapter = new MeasuresArrayAdapter(this, allMeasuresListResult);
        listview = (ListView) findViewById(R.id.measureListView);
        listview.setAdapter(adapter);
        registerForContextMenu(listview);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editMeasure(position);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.measureListView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("Действия");
            menu.add(Menu.NONE, 0, 0, "Удалить");
//            menu.add(Menu.NONE, 1, 1, "Редактировать");
        }
    }

    public void deleteMeasure(int position){
        final int pos = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("Вы действительно хотите удалить измерение?").setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int id) {

            }
        }).setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                realm.beginTransaction();
                MeasurementItem car = allMeasuresListResult.get(pos);
                car.removeFromRealm();
                realm.commitTransaction();
            }
        }).show();
    }

    public void editMeasure(int position){
        final int pos = position;
        showEditMeasureDialog(pos);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        switch (menuItemIndex){
            case 0:
                deleteMeasure(info.position);
                break;
            case 1:
                editMeasure(info.position);
                break;
            default:
                break;
        }
        return true;
    }

    private EditText measureNameEditView;
    private EditText measureIntervalEditView;
    private Spinner spinner;

    public void showAddMeasureDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_measure_dialog, null);

        measureNameEditView = (EditText) view.findViewById(R.id.measureNameEdit);
        measureIntervalEditView = (EditText) view.findViewById(R.id.measureIntervalEdit);
        spinner = (Spinner) view.findViewById(R.id.measure_type_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.measure_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final AlertDialog dialog = builder.setTitle("Добавить измерение").setView(view).
                setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int id) {
                        realm.beginTransaction();
                        MeasurementItem item = realm.createObject(MeasurementItem.class); // Create a new object
                        item.setName(measureNameEditView.getText().toString());
                        String[] types = getResources().getStringArray(R.array.measure_type_array);
                        item.setTimeType( types[((int) spinner.getSelectedItemId())]);
                        item.setStandartInterval(Integer.valueOf(measureIntervalEditView.getText().toString()));
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

        measureNameEditView.addTextChangedListener(new TextWatcher() {
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

    private EditText editMeasureNameEditView;
    private EditText editMeasureIntervalEditView;
    private Spinner editSpinner;

    public void showEditMeasureDialog(final int measureIndex){
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_measure_dialog, null);

        editMeasureNameEditView = (EditText) view.findViewById(R.id.measureNameEdit);
        editMeasureIntervalEditView = (EditText) view.findViewById(R.id.measureIntervalEdit);
        editSpinner = (Spinner) view.findViewById(R.id.measure_type_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.measure_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editSpinner.setAdapter(adapter);

        editMeasureNameEditView.setText(allMeasuresListResult.get(measureIndex).getName());
        editMeasureIntervalEditView.setText(String.valueOf(allMeasuresListResult.get(measureIndex).getStandartInterval()));
        if(allMeasuresListResult.get(measureIndex).getTimeType().equalsIgnoreCase("Время")){
            editSpinner.setSelection(0);
        }else if(allMeasuresListResult.get(measureIndex).getTimeType().equalsIgnoreCase("Дистанция")){
            editSpinner.setSelection(1);
        }

        final AlertDialog dialog = builder.setTitle("Редактировать измерение").setView(view).
                setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int id) {
                        realm.beginTransaction();
                        MeasurementItem item = allMeasuresListResult.get(measureIndex); // Create a new object
                        item.setName(editMeasureNameEditView.getText().toString());
                        String[] types = getResources().getStringArray(R.array.measure_type_array);
                        item.setTimeType( types[((int) editSpinner.getSelectedItemId())]);
                        item.setStandartInterval(Integer.valueOf(editMeasureIntervalEditView.getText().toString()));
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
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }
        });

        editMeasureNameEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!allMeasuresListResult.get(measureIndex).getName().equalsIgnoreCase(text)) {
                    RealmResults<MeasurementItem> result = realm.where(MeasurementItem.class).equalTo("name", text).findAll();
                    if (result.size() > 0 || text.equalsIgnoreCase("")) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    } else {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                }

            }
        });
        dialog.show();
    }

    public class MeasuresArrayAdapter extends BaseAdapter {
        private final Context context;
        private RealmResults<MeasurementItem> valuesResult;

        public MeasuresArrayAdapter(Context context, RealmResults<MeasurementItem> values) {
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
            MeasurementItem measure = valuesResult.get(position);
            nameView.setText(measure.getName());

            if (measure.getTimeType() != null && measure.getTimeType().equalsIgnoreCase("Время")){
                typeView.setText(" дн");
            }else if (measure.getTimeType() != null && measure.getTimeType().equalsIgnoreCase("Дистанция")){
                typeView.setText(" км");
            }
            intervalView.setText(String.valueOf(measure.getStandartInterval()));
            return rowView;
        }
    }
}
