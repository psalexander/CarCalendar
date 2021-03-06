package com.asinenko.carcalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.asinenko.carcalendar.items.CarItem;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class CarCalendarMainActivity extends AppCompatActivity {

    private Realm realm;
    private RealmResults<CarItem> allCarListResult;
    private Activity activity;
    private ListView listview;
    private CarArrayAdapter adapter;

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

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getDefaultInstance();
        realm.addChangeListener(realmCarListener);
        allCarListResult = realm.where(CarItem.class).findAll();

        setContentView(R.layout.activity_car_calendar_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCarDialog();
            }
        });

        adapter = new CarArrayAdapter(this, allCarListResult);
        listview = (ListView) findViewById(R.id.carListView);
        listview.setAdapter(adapter);
        registerForContextMenu(listview);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, CarActivity.class);
                intent.putExtra("name", allCarListResult.get(position).getName());
                intent.putExtra("vin", allCarListResult.get(position).getVin());
                intent.putExtra("number", allCarListResult.get(position).getNumber());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.carListView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("Действия");
            menu.add(Menu.NONE, 0, 0, "Удалить");
            menu.add(Menu.NONE, 1, 1, "Редактировать");
        }
    }

    public void deleteCar(int position){
        final int pos = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("Вы действительно хотите удалить автомобиль?").setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int id) {

            }
        }).setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                realm.beginTransaction();
                CarItem car = allCarListResult.get(pos);
                car.removeFromRealm();
                realm.commitTransaction();
            }
        }).show();
    }

    public void editCar(int position){
        final int pos = position;
        showEditCarDialog(pos);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        switch (menuItemIndex){
            case 0:
                deleteCar(info.position);
                break;
            case 1:
                editCar(info.position);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_car_calendar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_new) {
            showAddCarDialog();
            return true;
        }else if (id == R.id.action_delete) {
            return true;
        }else if (id == R.id.action_prop) {
            showMeasureActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private EditText carNameEditView;
    private EditText carNumberEditView;
    private EditText carVINEditView;

    public void showAddCarDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_car_dialog, null);

        carNameEditView = (EditText) view.findViewById(R.id.carNameEdit);
        carNumberEditView = (EditText) view.findViewById(R.id.carNumberEdit);
        carVINEditView = (EditText) view.findViewById(R.id.vinEdit);

        final AlertDialog dialog = builder.setTitle("Новое авто").setView(view).
                setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int id) {
                        realm.beginTransaction();
                        CarItem car = realm.createObject(CarItem.class); // Create a new object
                        car.setName(carNameEditView.getText().toString());
                        car.setNumber(carNumberEditView.getText().toString());
                        car.setVin(carVINEditView.getText().toString());
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
                ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });

        carNameEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                RealmResults<CarItem> result = realm.where(CarItem.class).equalTo("name", text).findAll();
                if (result.size() > 0 || text.equalsIgnoreCase("")) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });
        dialog.show();
    }

    private EditText editCarNameEditView;
    private EditText editCarNumberEditView;
    private EditText editCarVINEditView;

    public void showEditCarDialog(final int carIndex){
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_car_dialog, null);

        editCarNameEditView = (EditText) view.findViewById(R.id.carNameEdit);
        editCarNumberEditView = (EditText) view.findViewById(R.id.carNumberEdit);
        editCarVINEditView = (EditText) view.findViewById(R.id.vinEdit);
        editCarNameEditView.setText(allCarListResult.get(carIndex).getName());
        editCarNumberEditView.setText(allCarListResult.get(carIndex).getNumber());
        editCarVINEditView.setText(allCarListResult.get(carIndex).getVin());

        final AlertDialog dialog = builder.setTitle("Редактирование").setView(view).
                setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int id) {
                        realm.beginTransaction();
                        CarItem ci = allCarListResult.get(carIndex);
                        ci.setName(editCarNameEditView.getText().toString());
                        ci.setNumber(editCarNumberEditView.getText().toString());
                        ci.setVin(editCarVINEditView.getText().toString());
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
                ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }
        });

        editCarNameEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!allCarListResult.get(carIndex).getName().equalsIgnoreCase(text)) {
                    RealmResults<CarItem> result = realm.where(CarItem.class).equalTo("name", text).findAll();
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

    public void showMeasureActivity(){
        Intent intent = new Intent(this, CarCalendarMeasureActivity.class);
        startActivity(intent);
    }

    public class CarArrayAdapter extends BaseAdapter {
        private final Context context;
        private RealmResults<CarItem> valuesResult;

        public CarArrayAdapter(Context context, RealmResults<CarItem> values) {
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
            View rowView = inflater.inflate(R.layout.list_car_item, parent, false);
            TextView nameView = (TextView) rowView.findViewById(R.id.carNameTextView);
            TextView numberView = (TextView) rowView.findViewById(R.id.carNumberTextView);
            CarItem car = valuesResult.get(position);
            nameView.setText(car.getName());
            numberView.setText(car.getNumber());
            return rowView;
        }
    }
}
