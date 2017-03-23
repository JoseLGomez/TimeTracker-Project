package iuAndroid;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import timetracker.iuandroid.R;

public class NewProjectAdd extends Activity {

    public static final String AFEGIR_PROJECTE =
            "es.uab.es2.TimeTracker.iuAndroid.afegir_projecte";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project_add);

        setTitle(getIntent().getExtras().getString("nombre"));
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //accion de darle al boton de añadir proyecto
        Button btnAdd = (Button) findViewById(R.id.btnNewProject_Save);
        btnAdd.setOnClickListener(saveNewProject);



    }
    //funcion llamada al darle al boton añadir proyecto
    private View.OnClickListener saveNewProject = new View.OnClickListener(){
        public void onClick(View v){
            EditText editTextName = (EditText)findViewById(R.id.txtNewProject_Name);
            EditText editTextDescription = (EditText) findViewById(R.id.txtNewProject_Description);
            Intent intent = new Intent(NewProjectAdd.this, LlistaActivitatsActivity.class);
            Intent broadcast = new Intent(NewProjectAdd.AFEGIR_PROJECTE);
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


}
