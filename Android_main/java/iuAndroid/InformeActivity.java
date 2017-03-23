package iuAndroid;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.NavUtils;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EmptyStackException;

import timetracker.iuandroid.R;
import utils.tools;

public class InformeActivity extends Activity {
    public static final String REPORT = "es.uab.es2.TimeTracker.iuAndroid.Report";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informe);

        SimpleDateFormat thisDateFormat = new SimpleDateFormat(tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "formatdate"));

        EditText dateStart = (EditText) findViewById(R.id.Report_DataStart);
        dateStart.setHint(tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "formatdate"));
        dateStart.setTextColor(Color.BLACK);
        dateStart.setOnClickListener(dialogDateStart);


        EditText dateEnd = (EditText) findViewById(R.id.Report_DataEnd);
        dateEnd.setText(thisDateFormat.format(new Date()));
        dateEnd.setOnClickListener(dialogDateEnd);

        Button saveButton = (Button) findViewById(R.id.btnReport_Save);
        saveButton.setOnClickListener(save);


        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        Spinner DateReport = (Spinner) findViewById(R.id.SpinerReport_DateItems);
        //Para mostrar la fecha al seleccionar otros, aparece los elementos a rellenar
        DateReport.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout Date= (LinearLayout) findViewById(R.id.Llayou_OtherDate);
                LinearLayout contentDate= (LinearLayout) findViewById(R.id.Layout_ContentDate);

                if(position == 7){
                    //si se ha seleccionado otros hago visible la fecha y amplio el layout para que se pueda ver
                    contentDate.getLayoutParams().height = 370;
                    Date.setVisibility(View.VISIBLE);

                }else{
                    //si se ha seleccionado cualqueir otro pongo invisible y lo pongo a 170 la longitud
                    contentDate.getLayoutParams().height = 170;
                    Date.setVisibility(View.GONE);                }
            }

            @Override
            public void  onNothingSelected(AdapterView<?> parentView){
                return;
            }
        });
    }
    //la creacion del dialogo que despues se rellanara
    private View.OnClickListener dialogDateStart = new View.OnClickListener(){
        public void onClick(View v){
            showDialog(999);
        }
    };
    //la creacion del dialogo que despues se rellanara
    private View.OnClickListener dialogDateEnd = new View.OnClickListener(){
        public void onClick(View v){
            showDialog(998);
        }
    };

    //al crear un report llamo esta accion para crear el infrome correspondiente
    private View.OnClickListener save = new View.OnClickListener(){
        public void onClick(View v){
            Calendar dateStartReport = Calendar.getInstance();

            Calendar  dateEndReport = Calendar.getInstance();
            dateEndReport.setTime(new Date());

            //recono el dato sefun la fecha que se ha seleccionado
            Spinner spiDateItems = (Spinner) findViewById(R.id.SpinerReport_DateItems);
            switch (spiDateItems.getSelectedItemPosition()){
                case 0: //hoy
                    dateStartReport.setTime(new Date());
                    break;
                case 1: //ayer
                    dateStartReport.setTime(new Date());
                    dateStartReport.add(Calendar.DAY_OF_YEAR, -1);
                    break;
                case 2: //antes de ayer
                    dateStartReport.setTime(new Date());
                    dateStartReport.add(Calendar.DAY_OF_YEAR, -2);
                    break;
                case 3: //la semana pasada
                    dateStartReport.setTime(new Date());
                    dateStartReport.add(Calendar.DAY_OF_YEAR, -7);
                    break;
                case 4: //hace dos semanas
                    dateStartReport.setTime(new Date());
                    dateStartReport.add(Calendar.DAY_OF_YEAR, -14);
                    break;
                case 5: //El mes pasado
                    dateStartReport.setTime(new Date());
                    dateStartReport.add(Calendar.MONTH, -1);
                    break;
                case 6: //hace dos meses
                    dateStartReport.setTime(new Date());
                    dateStartReport.add(Calendar.MONTH, -2);
                    break;
                case 7: //otros
                    SimpleDateFormat formatDateStart= new SimpleDateFormat(tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "formatdate"));
                    SimpleDateFormat formatDateEnd = new SimpleDateFormat(tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "formatdate"));
                    try {
                        //recojo los datos y lo paso y lso guardo en un Calendar
                        EditText dateStart = (EditText) findViewById(R.id.Report_DataStart);
                        formatDateStart.parse(dateStart.getText().toString());

                        EditText dateEnd = (EditText) findViewById(R.id.Report_DataEnd);
                        formatDateEnd.parse(dateEnd.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    dateStartReport = formatDateStart.getCalendar();
                    dateEndReport =  formatDateEnd.getCalendar();
                    break;
                default:
                    throw new EmptyStackException();
            }
            //asigno que la fecha sea 12am para fecha inicail y 23hora para la fecha final
            dateStartReport.set(Calendar.HOUR_OF_DAY, 0);
            dateEndReport.set(Calendar.HOUR_OF_DAY, 23);
            //Miro que tipo de infrome ha pedido
            Spinner spiReportTypeItems = (Spinner) findViewById(R.id.SpinerReport_TypeItems);
            String typeReport;
            //return 0 detallado 1 Breve
            switch (spiReportTypeItems.getSelectedItemPosition()){
                case 0:
                    typeReport = "breve";
                    break;
                case 1:
                    typeReport = "detallado";
                    break;
                default:throw new EmptyStackException();
            }

            //Revicion del formato del informe
            Spinner spiFormatItems = (Spinner) findViewById(R.id.SpinerReport_HtmlTxtItems);
            String formatReport;
            //RETORNA 0 HTML report 1 TXT
            switch (spiFormatItems.getSelectedItemPosition()){
                case 0:
                    formatReport = "txt";
                    break;
                case 1:
                    formatReport = "html";
                    break;
                default:throw new EmptyStackException();
            }

            //guardo en el intent todos los tatos y envio una accion GestorArbreActivitat
            Intent in =  new Intent(InformeActivity.REPORT);
            in.putExtra("formato",formatReport);
            in.putExtra("tipo",typeReport);
            in.putExtra("dataInici", dateStartReport);
            in.putExtra("dataFinal", dateEndReport);
            sendBroadcast(in);
            //Envio un mensaje de report creado
            AlertMessage();

        }

    };

    // Dialogo box, que habilito para crear la entrada fe fecha en formato rueda
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            Calendar calendarStart = Calendar.getInstance();
            return new DatePickerDialog(this, DateListenerStart, calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH));
        }else if(id == 998){
            SimpleDateFormat formatDateEnd = new SimpleDateFormat(tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "formatdate"));
            try {

                EditText dateEnd = (EditText) findViewById(R.id.Report_DataEnd);
                //recojo el dato y lo parseo a la fecha correspondietne
                formatDateEnd.parse(dateEnd.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendarEnd = formatDateEnd.getCalendar();
            return new DatePickerDialog(this,DateListenerEnd,calendarEnd.get(Calendar.YEAR),calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    /**
     * DateListenerStart como DateListenerEnd, al colocar la fecha recoje lso datos y los cola en el input
     */

    private DatePickerDialog.OnDateSetListener DateListenerStart = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // arg1 = year  arg2 = month  arg3 = day
            SimpleDateFormat thisDateFormat = new SimpleDateFormat(tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "formatdate"));
            Calendar date = Calendar.getInstance();
            //guardo loo datos, pasandolos a un calendario
            date.set(arg1,arg2,arg3);
            EditText dateStart = (EditText) findViewById(R.id.Report_DataStart);
            //Muestro la fecha del calendario
            dateStart.setText(thisDateFormat.format(date.getTime()));
        }
    };

    private DatePickerDialog.OnDateSetListener DateListenerEnd = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // arg1 = year  arg2 = month  arg3 = day
            SimpleDateFormat thisDateFormat = new SimpleDateFormat(tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "formatdate"));
            Calendar date = Calendar.getInstance();
            date.set(arg1,arg2,arg3);
            EditText dateEnd = (EditText) findViewById(R.id.Report_DataEnd);
            dateEnd.setText(thisDateFormat.format(date.getTime()));
        }
    };

    /**
     * Gestion de la action bar
     * @param item
     * @return
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
     * Mensaje conforme el report ha sido creado
     */
    private void AlertMessage() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.Report_DialogBox_Title);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder
                .setMessage(R.string.Report_DialogBox_Message)
                .setCancelable(false)
                .setPositiveButton(R.string.Report_DialogBox_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
