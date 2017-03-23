package iuAndroid;

import utils.tools;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import timetracker.iuandroid.R;

/**
 * Contiene toda la logica del activity_setting.xml
 */
public class SettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //habilito que se puede realizar hacia atras en la action bar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //al boton con el simbolo + le doy el evento del onclick
        Button btnSave = (Button) findViewById(R.id.btnSetting_Save);
        btnSave.setOnClickListener(saveSetting);

        /**
         * Inicializo los valores que estan configurados en nuestra aplicacion, fichero config.xml
         */
        //al spinner lo inicializo automaticamente en el valor que tiene la aplicacion.
        Spinner SpinerSetting_LenguajeItems = (Spinner) findViewById(R.id.spinnerSetting_Language);
        SpinerSetting_LenguajeItems.setSelection(posicionLenguaje(Locale.getDefault().getLanguage()));

        String pathAutoSave =  tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "savepath");
        EditText editText_SavePath = (EditText) findViewById(R.id.txtSetting_SavePath);
        editText_SavePath.setText(pathAutoSave);

        Spinner SpinerSetting_FormatDateItems = (Spinner) findViewById(R.id.spinnerSetting_FormatDate);
        SpinerSetting_FormatDateItems.setSelection(posicionformatDate(tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "formatdate")));

        EditText editText_CoolIU = (EditText) findViewById(R.id.txtSetting_CoolIU);
        editText_CoolIU.setText(tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "cooliu"));

        EditText editText_CoolClock = (EditText) findViewById(R.id.txtSetting_CoolClock);
        editText_CoolClock.setText(tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "coolclock"));
    }

    /**
     * al guardar (+) recojo todos los datos que hay en la pantalla y los guardo, si son datos del
     *  reloj (cooliu, coolclock) reviso si hay modificacion
     */
    private View.OnClickListener saveSetting = new View.OnClickListener(){
        public void onClick(View v) {
            //acedo a todos los elementos que tengo en la interfaz
            EditText editText_SavePath = (EditText) findViewById(R.id.txtSetting_SavePath);
            Spinner SpinerSetting_LenguajeItems = (Spinner) findViewById(R.id.spinnerSetting_Language);
            Spinner SpinerSetting_FormatDateItems = (Spinner) findViewById(R.id.spinnerSetting_FormatDate);
            EditText editText_CoolIU = (EditText) findViewById(R.id.txtSetting_CoolIU);
            EditText editText_CoolClock = (EditText) findViewById(R.id.txtSetting_CoolClock);

            // guardo en el fichero los elemenos de lenguaje, fichero de save y el formato
            tools.writeFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "lang",SpinerSetting_LenguajeItems.getSelectedItem().toString());
            tools.writeFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "savepath", editText_SavePath.getText().toString());
            tools.writeFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "formatdate", SpinerSetting_FormatDateItems.getSelectedItem().toString());

            //Cargo el nuevo lenguaje
            LoadLenguaje();

            // si ha avido actualizacion de los datos los guardo sino no los guardo, en caso de
            // guardar se necesita reiniciar la aplicacion
            if(!tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "cooliu").equals(editText_CoolIU.getText().toString()) ||
               !tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "coolclock").equals(editText_CoolClock.getText().toString()) ){
                    tools.writeFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "cooliu",editText_CoolIU.getText().toString());
                    tools.writeFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "coolclock",editText_CoolClock.getText().toString());
                    //Mensaje de alertaconforme se tiene que reiniciar la palciacion
                    AlertMessage();
            }else{
                Intent intent = new Intent(SettingActivity.this, LlistaActivitatsActivity.class);
                startActivity(intent);
            }

        }
    };

    //Mensaje de alerta conforme se tiene que reiniciar la aplciacion para aplciar los cambios
    private void AlertMessage() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.setting_DialogBox_Title);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder
                .setMessage(R.string.setting_DialogBox_Message)
                .setCancelable(false)
                .setPositiveButton(R.string.setting_DialogBox_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(SettingActivity.this, LlistaActivitatsActivity.class);
                        startActivity(intent);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     *  Posiciona al spinner el valor guardado con la posicion que necesita
     * @param format entra un fornmato de fecha correcta
     * @return retorna en la posicion en el que spiner colocara el elemento
     */
    private int posicionformatDate(String format){
        int result = 0;
        String s = format.toLowerCase();
        if (s.equals("dd/mm/yyyy")) {
            result = 0;
        } else if (s.equals("mm/dd/yyyy")) {
            result = 1;
        } else if (s.equals("yyyy/mm/dd")) {
            result = 2;
        } else {
            result = 0;
        }
        return  result;

    }
    /**
     *  Posiciona al spinner del valor guardado con la posicion que necesita par amostrar el idioma correcto
     * @param lang entra un fornmato de lenguaje correcta
     * @return retorna en la posicion en el que spiner colocara el elemento
     */
    private int posicionLenguaje(String lang){
        int result = 0;
        String s = lang.toLowerCase();
        if (s.equals("en")) {
            result = 0;
        } else if (s.equals("es")) {
            result = 1;
        } else if (s.equals("ca")) {
            result = 2;
        } else if (s.equals("fr")) {
            result = 3;
        } else {
            result = 0;
        }
        return  result;
    }

    /**
     *  gestiona el menu de la action bar
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
     * Carga el lenguaje que esta escrito en el fochero de configuracion
     */
    private void LoadLenguaje(){
        Locale locale = new Locale( tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "lang"));
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        this.getApplicationContext().getResources().updateConfiguration(config, null);

    }

}
