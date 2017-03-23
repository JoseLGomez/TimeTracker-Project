package iuAndroid;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import timetracker.iuandroid.R;

/**
 * Created by Jose Master on 10/01/2016.
 */

/**
 * Seccion donde se gestiona tanto la visualizacion como la ediciond e datos, projectos y tareas
 */
public class EditActivity extends Activity {

    public static final String ACTUALITZAR_ITEM =
            "es.uab.es2.TimeTracker.iuAndroid.actualitzar_item";

    private String nombre;
    private String descripcion;
    private int posicion;
    private EditText editTextName;
    private EditText editTextDescription;

    TextView textViewName;
    TextView textViewDescription;

    Button btnEdit;
    Button btnAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_item);

        //ponemos el nombre
        setTitle(getIntent().getExtras().getString("nombre"));
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //recogemos todos los valores que nos pasan por el Intent y rellenamos los inputs y text
        btnAdd = (Button) findViewById(R.id.btnNewProject_Save);
        btnAdd.setOnClickListener(saveEditItem);
        nombre = getIntent().getExtras().getString("nombre");
        descripcion = getIntent().getExtras().getString("descripcion");
        posicion = getIntent().getExtras().getInt("posicio");
        editTextName = (EditText)findViewById(R.id.txtNewProject_Name);
        editTextDescription = (EditText)findViewById(R.id.txtNewProject_Description);
        editTextName.setText(nombre);
        editTextDescription.setText(descripcion);




        textViewName = (TextView)findViewById(R.id.txtEditItem_Name);
        textViewDescription = (TextView)findViewById(R.id.txtEditItem_Description);
        textViewName.setText(nombre);
        textViewDescription.setText(descripcion);

        btnEdit = (Button) findViewById(R.id.btnItemEdit_Edit);
        btnEdit.setOnClickListener(editEditItem);


    }
    //al presionar el boton de editar hacemos visible los input y desabilitamos los text y el boton
    //de editar lo cambiamos pro el de guardar
    private View.OnClickListener editEditItem = new View.OnClickListener() {
        public void onClick(View v) {
            editTextName.setVisibility(View.VISIBLE);
            editTextDescription.setVisibility(View.VISIBLE);

            textViewName.setVisibility(View.GONE);
            textViewDescription.setVisibility(View.GONE);

            btnAdd.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
        }
    };

    //funcion llamada al darle al boton save de lso elementos a presentar
    private View.OnClickListener saveEditItem = new View.OnClickListener(){
        public void onClick(View v){
            editTextName = (EditText)findViewById(R.id.txtNewProject_Name);
            editTextDescription = (EditText) findViewById(R.id.txtNewProject_Description);
            Intent intent = new Intent(EditActivity.this, LlistaActivitatsActivity.class);
            Intent broadcast = new Intent(EditActivity.ACTUALITZAR_ITEM);
            broadcast.putExtra("Nombre", editTextName.getText().toString());
            broadcast.putExtra("Descripcion", editTextDescription.getText().toString());
            broadcast.putExtra("posicion",posicion);
            sendBroadcast(broadcast);
            finish();
        }
    };


    /**
     * opciones de la action bar
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
}
