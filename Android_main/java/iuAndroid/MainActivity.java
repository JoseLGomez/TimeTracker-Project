package iuAndroid;

import timetracker.iuandroid.R;
import utils.tools;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Pantalla principal, de carga
 */
public class MainActivity extends Activity {
    //Defino el tiempo que queiro que este el logo de la aplicacion
    private static int SPLASH_TIEMPO = 1000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        //Creo la confiugracion si no existe
        loadConfig();
        //Cargo el idioma a la aplicacion
        loadLenguaje();
        new Handler().postDelayed(new Runnable() {

			/*
			* Mostramos la pantalla de bienvenida con un temporizador.
			* De esta forma se puede mostrar el logo de la app o
			* compañia durante unos segundos.
			*/

            @Override
            public void run() {
                // Este método se ejecuta cuando se consume el tiempo del temporizador.
                // Se pasa a la activity principal
                Intent i = new Intent(MainActivity.this, LlistaActivitatsActivity.class);
                startActivity(i);
                // Cerramos esta activity
                finish();
            }
        }, SPLASH_TIEMPO);
	}

    /**
     * Cargo el fichero de configracion si no existe, en formato xml con unos valores prodefecto
     */
    private void loadConfig(){
        FileOutputStream fop = null;
        File file;
        String content = "<?xml version=\"1.0\" encoding=\"utf-8\"?><config><savepath>timetracker.dat</savepath><savepath1>aut</savepath1><lang>es</lang><formatdate>dd/MM/yyyy</formatdate><cooliu>1</cooliu><coolclock>1</coolclock></config>";
        try {
            file = new File(getFileStreamPath("config.xml").getPath());
            fop = new FileOutputStream(file);
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] contentInBytes = content.getBytes();
            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cargo el idioma por defecto segun el fichero de configuaracion creado anteriormente
     */
    private void loadLenguaje(){
        Locale locale = new Locale( tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "lang"));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        this.getApplicationContext().getResources().updateConfiguration(config, null);

    }

}
