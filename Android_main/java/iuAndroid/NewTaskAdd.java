package iuAndroid;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import timetracker.iuandroid.R;
import utils.tools;

/**
 * Gestion de la opcion de agregar nueva tarea
 */
public class NewTaskAdd extends Activity {
    public static final String AFEGIR_TASCA =
            "es.uab.es2.TimeTracker.iuAndroid.afegir_tasca";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task_add);

        //habilito que se puede realizar hacia atras en la action bar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button btnAdd = (Button) findViewById(R.id.btnNewTask_Save);
        btnAdd.setOnClickListener(saveNewTask);

        CheckBox DateReporDateLimited = (CheckBox) findViewById(R.id.chkboxNewTask_DateLimited);
        // si has habilitado el check aparece a continuacion para que puedas colocar la fecha
        DateReporDateLimited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = ((CheckBox)view).isChecked();
                EditText Date = (EditText) findViewById(R.id.txtNewTask_DateLimited);
                //si el boton esta presionado lo pones en invisible sino en visible
                if (!isChecked) {
                    Date.setVisibility(View.INVISIBLE);
                } else {
                    // Carga el formato de fecha
                    Date.setHint(tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "formatdate"));
                    Date.setTextColor(Color.BLACK);
                    Date.setOnClickListener(dialogTaskDateLimited);
                    //el valor lo limpia, si hay cargado otro valor
                    Date.setText("");
                    Date.setVisibility(View.VISIBLE);
                }
            }
        });
        CheckBox DateReportPreprogramed = (CheckBox) findViewById(R.id.chkboxNewTask_Preprogrammed);
        // si has habilitado el check aparece a continuacion para que puedas colocar la fecha
        DateReportPreprogramed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = ((CheckBox)view).isChecked();
                EditText Date = (EditText) findViewById(R.id.txtNewTask_DateProgramed);
                if (!isChecked) {
                    Date.setVisibility(View.INVISIBLE);
                } else {
                    Date.setHint(tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "formatdate"));
                    Date.setTextColor(Color.BLACK);
                    Date.setOnClickListener(dialogTaskDatePreprogramed);
                    Date.setText("");
                    Date.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //al presionar sobre el input del programed o limited aparece un cuadro de dialogo
    private View.OnClickListener dialogTaskDateLimited = new View.OnClickListener(){
        public void onClick(View v){
            showDialog(999);
        }
    };
    private View.OnClickListener dialogTaskDatePreprogramed = new View.OnClickListener(){
        public void onClick(View v){
            showDialog(998);
        }
    };
    /**
     * Funcion para guardar toda la informacion que hay en la actividad
     *
     */
    private View.OnClickListener saveNewTask = new View.OnClickListener(){
        public void onClick(View v) {
            EditText editTextName = (EditText)findViewById(R.id.txtNewTask_Name);
            EditText editTextDescription = (EditText)findViewById(R.id.txtNewTask_Description);
            Intent intent = new Intent(NewTaskAdd.this, LlistaActivitatsActivity.class);
            Intent broadcast = new Intent(NewTaskAdd.AFEGIR_TASCA);
            broadcast.putExtra("Nombre", editTextName.getText().toString());
            broadcast.putExtra("Descripcion", editTextDescription.getText().toString());
            sendBroadcast(broadcast);
            finish();
        }
    };

    /**
     * Gestion del menu de la action bar
     * @param item
     * @return true
     */
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                this.setResult(RESULT_CANCELED);
                NavUtils.navigateUpFromSameTask(this);
                //onBackPressed();
                return true;
            case R.id.action_NewProject:
                this.setResult(RESULT_CANCELED);
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_NewTask:
                this.setResult(RESULT_CANCELED);
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     *  Segun que id entra mostrara un dialog totlametne diferente
      * @param id al id que entras
     * @return Dialog
     */
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            Calendar calendarStart = Calendar.getInstance();
            return new DatePickerDialog(this, DateListenerTaskLimited, calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH));
        }else if (id == 998) {
            Calendar calendarStart = Calendar.getInstance();
            return new DatePickerDialog(this, DateListenerTaskProgramed, calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    /**
     * DateListenerTaskLimited y DateListenerTaskProgramed
     * Muestra un Dialogo para seleccionar la fecha, de forma giratoria
     */

    private DatePickerDialog.OnDateSetListener DateListenerTaskLimited = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // arg1 = year  arg2 = month  arg3 = day
            SimpleDateFormat thisDateFormat = new SimpleDateFormat(tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "formatdate"));
            Calendar date = Calendar.getInstance();
            date.set(arg1,arg2,arg3);
            EditText dateStart = (EditText) findViewById(R.id.txtNewTask_DateLimited);
            dateStart.setText(thisDateFormat.format(date.getTime()));
        }
    };

    private DatePickerDialog.OnDateSetListener DateListenerTaskProgramed = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // arg1 = year  arg2 = month  arg3 = day
            //utiliza el formato guardado en el fichero de configruacion
            SimpleDateFormat thisDateFormat = new SimpleDateFormat(tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "formatdate"));
            Calendar date = Calendar.getInstance();
            date.set(arg1,arg2,arg3);
            EditText dateStart = (EditText) findViewById(R.id.txtNewTask_DateProgramed);
            dateStart.setText(thisDateFormat.format(date.getTime()));
        }
    };


}
