package iuAndroid;

import java.util.ArrayList;
import java.util.List;

import timetracker.iuandroid.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Mostra la llista de projectes i tasques filles del projecte pare actual.
 * Inicialment, en engegar la aplicació per primer cop (o quan s'ha engegat el
 * telèfon) mostra doncs les activitats del primer "nivell", considerant que hi
 * ha un projecte arrel invisible de cara als usuaris que és el seu projecte
 * pare.
 * <p>
 * Juntament amb el nom de projecte o tasca se'n mostra el temps total
 * cronometrat. I mentre que s'està cronometrant alguna tasca d'aquestes, o bé
 * descendent d'un dels projectes mostrats, el seu temps es veu que es va
 * actualitzant. Per tal de mostrar nom i durada mitjançant les
 * <code>ListView</code> d'Android, hem hagut de dotar la classe
 * <code>DadesActivitat</code> d'un mètode <code>toString</code> que és invocat
 * per un objecte de classe <code>Adapter</code>, que fa la connexió entre la
 * interfase i les dades que mostra.
 * <p>
 * També gestiona els events que permeten navegar per l'arbre de projectes i
 * tasques :
 * <ul>
 * <li>un click sobre un element de la llista baixa de nivell: passa a mostrar
 * els seus "fills", la siguin subprojectes i tasques (si era un projecte) o
 * intervals (si era tasca)</li>
 * <li>tecla o botó "back" puja de nivell: anem al projecte para del les
 * activitats de les quals mostrem les dades, o si ja són del primer nivell i no
 * podem pujar més, anem a la pantalla "Home"</li>
 * </ul>
 * I també events per tal de cronometrar una tasca p parar-ne el cronòmetre,
 * mitjançant un click llarg.
 * <p>
 * Totes dues funcions no són dutes a terme efectivament aquí sinó a
 * <code>GestorArbreActivitat</code>, que manté l'arbre de tasques, projectes i
 * intervals en memòria. Cal fer-ho així per que Android pot eliminar (
 * <code>destroy</code>) la instància d'aquesta classe quan no és visible per
 * que estem interactuant amb alguna altra aplicació, si necessita memòria. En
 * canvi, un servei com és <code>GestorArbreActivitats</code> només serà
 * destruït en circumstàncies extremes. La comunicació amb el servei es fa
 * mitjançant "intents", "broadcast" i una classe privada "receiver".
 *
 * @author joans
 * @version 6 febrer 2012
 */
public class LlistaActivitatsActivity extends Activity implements ActionMode.Callback,TaskActivated_Dialog_Fragment.NoticeDialogListener {

    /**
     * Nom de la classe per fer aparèixer als missatges de logging del LogCat.
     *
     * @see Log
     */
    private final String tag = this.getClass().getSimpleName();

    /**
     * Grup de vistes (controls de la interfase gràfica) que consisteix en un
     * <code>TextView</code> per a cada activitat a mostrar.
     */
    private ListView arrelListView;

    /**
     * Adaptador necessari per connectar les dades de la llista de projectes i
     * tasques filles del projecte pare actual, amb la interfase, segons el
     * mecanisme estàndard d'Android.
     * <p>
     * Per tal de fer-lo servir, cal que la classe <code>DadesActivitat</code>
     * tingui un mètode <code>toString</code> que retornarà l'string a mostrar
     * en els TextView (controls de text) de la llista ListView.
     */
    private ArrayAdapter<DadesActivitat> aaAct;

    /**
     * Llista de dades de les activitats (projectes i tasques) mostrades
     * actualment, filles del (sub)projecte on estem posicionats actualment.
     */
    private List<DadesActivitat> llistaDadesActivitats;

    /**
     * Identificador del View les propietats del qual (establertes amb l'editor
     * XML de la interfase gràfica) estableixen com es mostra cada un els items
     * o elements de la llista d'activitats (tasques i projectes) referenciada
     * per l'adaptador {@link #aaAct}. Si per comptes haguéssim posat
     * <code>android.R.layout.simple_list_item_1</code> llavors fora la
     * visualització per defecte d'un text. Ara la diferència es la mida de la
     * tipografia.
     */
    private int layoutID = R.layout.llista_activitats;

    /**
     * Flag que ens servirà per decidir fer que si premem el botó/tecla "back"
     * quan estem a l'arrel de l'arbre de projectes, tasques i intervals : si és
     * que si, desem l'arbre i tornem a la pantalla "Home", sinó hem d'anar al
     * projecte pare del pare actual (pujar de nivell).
     */
    private boolean activitatPareActualEsArrel;


    /**
     * Nombre del Projecto o tarea que se esta visualizando en este momento
     */
    private String nomPareActual;
    /**
     * Rep els "intents" que envia <code>GestorArbreActivitats</code> amb les
     * dades de les activitats a mostrar. El receptor els rep tots (no hi ha cap
     * filtre) per que només se'n n'hi envia un, el "TE_FILLS".
     *
     * @author joans
     * @version 6 febrer 2012
     */
    private class Receptor extends BroadcastReceiver {
        /**
         * Nom de la classe per fer aparèixer als missatges de logging del
         * LogCat.
         *
         * @see Log
         */
        private final String tag = this.getClass().getCanonicalName();

        @Override
        /**
         * Gestiona tots els intents enviats, de moment només el de la
         * acció TE_FILLS. La gestió consisteix en actualitzar la llista
         * de dades que s'està mostrant mitjançant el seu adaptador.
         *
         * @param context
         * @param intent
         * objecte Intent que arriba per "broadcast" i del qual en fem
         * servir l'atribut "action" per saber quina mena de intent és
         * i els extres per obtenir les dades a mostrar i si el projecte
         * actual és l'arrel de tot l'arbre o no
         *
         */
        public void onReceive(final Context context, final Intent intent) {
            Log.i(tag, "onReceive");
            if (intent.getAction().equals(GestorArbreActivitats.TE_FILLS)) {
                activitatPareActualEsArrel = intent.getBooleanExtra(
                        "activitat_pare_actual_es_arrel", false);
                // obtenim la nova llista de dades d'activitat que ve amb
                // l'intent
                @SuppressWarnings("unchecked")
                ArrayList<DadesActivitat> llistaDadesAct =
                        (ArrayList<DadesActivitat>) intent
                                .getSerializableExtra("llista_dades_activitats");
                aaAct.clear();
                if (llistaDadesAct != null) {
                    for (DadesActivitat dadesAct : llistaDadesAct) {
                        aaAct.add(dadesAct);
                    }
                }
                if (!activitatPareActualEsArrel) {
                    String Titulo = intent.getStringExtra("Nom_pare");
                    setTitle(Titulo);

                } else {
                    setTitle(R.string.app_name);
                }
                // això farà redibuixar el ListView
                aaAct.notifyDataSetChanged();
                Log.d(tag, "mostro els fills actualitzats");
            } else if (intent.getAction().equals(GestorArbreActivitats.RESPOSTA_TASCA_CRONOMETRANTSE)){
                //Si hay tareas cronometrandose mostramos el dialog, sino salimos de la aplicacion
                if (intent.getBooleanExtra("Resposta", false)) {
                    showNoticeDialog();
                } else {
                    sendBroadcast(new Intent(LlistaActivitatsActivity.PARA_SERVEI));
                    Log.d(tag, "parem servei");
                    CerrarAplicacion();
                }
            } else {
                // no pot ser
                assert false : "intent d'acció no prevista";
            }
        }
    }

    /**
     * Objecte únic de la classe {@link Receptor}.
     */
    private Receptor receptor;

    // Aquests són els "serveis", identificats per un string, que demana
    // aquesta classe a la classe Service GestorArbreActivitats, en funció
    // de la interacció de l'usuari:

    /**
     * String que defineix l'acció de demanar a <code>GestorActivitats</code> la
     * llista de les dades dels fills de l'activitat actual, que és un projecte.
     * Aquesta llista arribarà com a dades extres d'un Intent amb la "acció"
     * TE_FILLS.
     *
     * veure GestorActivitats.Receptor
     */
    public static final String DONAM_FILLS =
            "es.uab.es2.TimeTracker.iuAndroid.Donam_fills";

    /**
     * String que defineix l'acció de demanar a <code>GestorActivitats</code>
     * que engegui el cronòmetre de la tasca clicada.
     */
    public static final String ENGEGA_CRONOMETRE =
            "es.uab.es2.TimeTracker.iuAndroid.Engega_cronometre";

    /**
     * String que defineix l'acció de demanar a <code>GestorActivitats</code>
     * que engegui tots els cronòmetre del projecte actual.
     */
    public static final String ENGEGA_TOTS_CRONOMETRES =
            "es.uab.es2.TimeTracker.iuAndroid.Engega_tots_cronometres";
    /**
     * String que defineix l'acció de demanar a <code>GestorActivitats</code>
     * que engegui tots els cronòmetre del projecte actual.
     */
    public static final String CANVI_ORDENACIO =
            "es.uab.es2.TimeTracker.iuAndroid.canvi_ordenacio";
    /**
     * String que defineix l'acció de demanar a <code>GestorActivitats</code>
     * que pari el cronòmetre de la tasca clicada.
     */
    public static final String PARA_CRONOMETRE =
            "es.uab.es2.TimeTracker.iuAndroid.Para_cronometre";

    /**
     * String que defineix l'acció de demanar a <code>GestorActivitats</code>
     * que pari tots els cronòmetres del projecte actual.
     */
    public static final String PARA_TOTS_CRONOMETRES =
            "es.uab.es2.TimeTracker.iuAndroid.Para_tots_cronometres";

    /**
     * String que defineix l'acció de demanar a <code>GestorActivitats</code>
     * que escrigui al disc l'arbre actual.
     */
    public static final String DESA_ARBRE =
            "es.uab.es2.TimeTracker.iuAndroid.Desa_arbre";

    /**
     * String que defineix l'acció de demanar a <code>GestorActivitats</code>
     * que el projecte pare de les activitats actuals sigui el projecte que
     * l'usuari ha clicat.
     */
    public static final String BAIXA_NIVELL =
            "es.uab.es2.TimeTracker.iuAndroid.Baixa_nivell";

    /**
     * String que defineix l'acció de demanar a <code>GestorActivitats</code>
     * que el projecte pare passi a ser el seu pare, o sigui, pujar de nivell.
     */
    public static final String PUJA_NIVELL =
            "es.uab.es2.TimeTracker.iuAndroid.Puja_nivell";

    /**
     * En voler pujar de nivell quan ja som a dalt de tot vol dir que l'usuari
     * desitja "deixar de treballar del tot" amb la aplicació, així que "parem"
     * el servei <code>GestorActivitats</code>, que vol dir parar el cronòmetre
     * de les tasques engegades, si n'hi ha alguna, desar l'arbre i parar
     * (invocant <code>stopSelf</code>) el servei. Tot això es fa a
     * GestorActivitats#paraServei.
     */
    public static final String PARA_SERVEI =
            "es.uab.es2.TimeTracker.iuAndroid.Para_servei";
    /**
     * Para eliminar un item de la lista
     */
    public static final String ELIMINAR_ITEM =
            "es.uab.es2.TimeTracker.iuAndroid.Eliminar_item";

    /**
     * Para editar un item de la lista
     */
    public static final String EDITAR_ITEM =
            "es.uab.es2.TimeTracker.iuAndroid.Editar_item";
    /**
     * Comprobacion de si hay alguna tarea cronometrandose
     */
    public static final String TASCA_CRONOMETRANTSE =
            "es.uab.es2.TimeTracker.iuAndroid.Tasca_cronometrantse";
    /**
     * Objeto para realizar accion en el menu
     */
    protected Object mActionMode;
    public int selectedItem = -1;

    /**
     * Quan aquesta Activity es mostra després d'haver estat ocultada per alguna
     * altra Activity cal tornar a fer receptor i el seu filtre per que atengui
     * als intents que es redifonen (broadcast). I també engegar el servei
     * <code>GestorArbreActivitats</code>, si és la primera vegada que es mostra
     * aquesta Activity. En fer-ho, el servei enviarà la llista de dades de les
     * activitats filles del projecte arrel actual.
     */
    @Override
    public final void onResume() {
        Log.i(tag, "onResume");

        IntentFilter filter;
        filter = new IntentFilter();
        filter.addAction(GestorArbreActivitats.TE_FILLS);
        filter.addAction(GestorArbreActivitats.RESPOSTA_TASCA_CRONOMETRANTSE);
        receptor = new Receptor();
        registerReceiver(receptor, filter);

        // Crea el servei GestorArbreActivitats, si no existia ja. A més,
        // executa el mètode onStartCommand del servei, de manera que
        // *un cop creat el servei* = havent llegit ja l'arbre si es el
        // primer cop, ens enviarà un Intent amb acció TE_FILLS amb les
        // dades de les activitats de primer nivell per que les mostrem.
        // El que no funcionava era crear el servei (aquí mateix o
        // a onCreate) i després demanar la llista d'activiats a mostrar
        // per que startService s'executa asíncronament = retorna de seguida,
        // i la petició no trobava el servei creat encara.
        startService(new Intent(this, GestorArbreActivitats.class));

        super.onResume();
        Log.i(tag, "final de onResume");
    }

    /**
     * Just abans de quedar "oculta" aquesta Activity per una altra, anul·lem el
     * receptor de intents.
     */
    @Override
    public final void onPause() {
        Log.i(tag, "onPause");

        unregisterReceiver(receptor);

        super.onPause();
    }

    /**
     * Estableix com a activitats a visualitzar les filles del projecte
     * , així com els dos listeners que gestionen els
     * events de un click normal i un click llarg. El primer serveix per navegar
     * "cap avall" per l'arbre, o sigui, veure els fills d'un projecte o els
     * intervals d'una tasca. El segon per cronometrar, en cas que haguem clicat
     * sobre una tasca.
     *
     * @param savedInstanceState
     *            de tipus Bundle, però no el fem servir ja que el pas de
     *            paràmetres es fa via l'objecte aplicació
     *            <code>TimeTrackerApplication</code>.
     */
    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        LlistaActivitatsActivity llista = this;
        super.onCreate(savedInstanceState);
        Log.i(tag, "onCreate");

        setContentView(R.layout.main);
        arrelListView = (ListView) this.findViewById(R.id.listView1);

        viewStateActionMenu();

        llistaDadesActivitats = new ArrayList<DadesActivitat>();
        aaAct = new ActivityAdapter(this, this.layoutID, llistaDadesActivitats, new CallBacks() {
            @Override
            public void onClickTextView(int position) {
                Log.i(tag, "onItemClick111");
                Log.d(tag, "pos = " + position);
                    viewStateActionMenu();
                if (mActionMode == null) {
                    Intent inte = new Intent(LlistaActivitatsActivity.BAIXA_NIVELL);
                    inte.putExtra("posicio", position);
                    sendBroadcast(inte);
                    nomPareActual = llistaDadesActivitats.get(position).getNom();
                    if (llistaDadesActivitats.get(position).isProjecte()) {
                        sendBroadcast(new Intent(
                                LlistaActivitatsActivity.DONAM_FILLS));
                        Log.d(tag, "enviat intent DONAM_FILLS");
                    } else if (llistaDadesActivitats.get(position).isTasca()) {
                        startActivity(new Intent(LlistaActivitatsActivity.this,
                                LlistaIntervalsActivity.class));
                        // en aquesta classe ja es demanara la llista de fills
                    } else {
                        // no pot ser!
                        assert false : "activitat que no es projecte ni tasca";
                    }
                }
                }

            @Override
            public boolean onClickPlayPause(int position) {
                Log.i(tag, "SET ON CLICK EN POSICION" + position);
                if (mActionMode == null) {
                    if (llistaDadesActivitats.get(position).isTasca()) {
                        Intent inte;
                        if (!llistaDadesActivitats.get(position).isCronometreEngegat()) {
                            inte = new Intent(
                                    LlistaActivitatsActivity.ENGEGA_CRONOMETRE);
                            Log.d(tag, "enviat intent ENGEGA_CRONOMETRE de "
                                    + llistaDadesActivitats.get(position).getNom());
                        } else {
                            inte = new Intent(
                                    LlistaActivitatsActivity.PARA_CRONOMETRE);
                            Log.d(tag, "enviat intent PARA_CRONOMETRE de "
                                    + llistaDadesActivitats.get(position).getNom());
                        }
                        inte.putExtra("posicio", position);
                        sendBroadcast(inte);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean onItemLongClickPropio(int position) {
                Log.i(tag, "onItemLongClick");
                Log.d(tag, "pos = " + position);
                if (mActionMode != null) {
                    return false;
                }
                selectedItem = position;


                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = LlistaActivitatsActivity.this.startActionMode(LlistaActivitatsActivity.this);
                return true;
            }
        });
        arrelListView.setAdapter(aaAct);
    }

    /**
     * Gestor de l'event de prémer la tecla 'enrera' del D-pad. El que fem es
     * anar "cap amunt" en l'arbre de tasques i projectes. Si el projecte pare
     * de les activitats que es mostren ara no és nul (n'hi ha), 'pugem' per
     * mostrar-lo a ell i les seves activitats germanes. Si no n'hi ha, paro el
     * servei, deso l'arbre (equivalent a parar totes les tasques que s'estiguin
     * cronometrant) i pleguem de la aplicació.
     */
    @Override
    public final void onBackPressed() {
        Log.i(tag, "onBackPressed");
        if (activitatPareActualEsArrel) {
            sendBroadcast(new Intent(LlistaActivitatsActivity.TASCA_CRONOMETRANTSE));
        } else {
            sendBroadcast(new Intent(LlistaActivitatsActivity.PUJA_NIVELL));
            Log.d(tag, "enviat intent PUJA_NIVELL");
            sendBroadcast(new Intent(LlistaActivitatsActivity.DONAM_FILLS));
            Log.d(tag, "enviat intent DONAM_FILLS");
        }
    }

    // D'aqui en avall els mètodes que apareixen són simplement sobrecàrregues
    // de mètodes de Activity per tal que es mostri un missatge de logging i
    // d'aquesta manera puguem entendre el cicle de vida d'un objecte d'aquesta
    // classe i depurar errors de funcionament de la interfase (on posar què).

    /**
     * Mostra un missatge de log per entendre millor el cicle de vida d'una
     * Activity.
     *
     * @param savedInstanceState
     *            objecte de classe Bundle, que no fem servir.
     */
    @Override
    public final void onSaveInstanceState(final Bundle savedInstanceState) {
        Log.i(tag, "onSaveInstanceState");
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Aquesta funció es crida després de <code>onCreate</code> quan hi ha un
     * canvi de configuració = rotar el mòbil 90 graus, passant de "portrait" a
     * apaisat o al revés.
     *
     * @param savedInstanceState
     *            Bundle que de fet no es fa servir.
     *
     */
    @Override
    public final void onRestoreInstanceState(final Bundle savedInstanceState) {
        Log.i(tag, "onRestoreInstanceState");
            super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Mostra un missatge de log per entendre millor el cicle de vida d'una
     * Activity.
     */
    @Override
    public final void onStop () {
        Log.i(tag, "onStop");
        super.onStop();
    }

    /**
     * Mostra un missatge de log per entendre millor el cicle de vida d'una
     * Activity.
     */
    @Override
    public final void onDestroy() {
            Log.i(tag, "onDestroy");
        super.onDestroy();
    }

    /**
     * Mostra un missatge de log per entendre millor el cicle de vida d'una
     * Activity.
     */
    @Override
    public final void onStart() {
        Log.i(tag, "onStart");
        super.onStart();
    }

    /**
     * Mostra un missatge de log per entendre millor el cicle de vida d'una
     * Activity.
     */
    @Override
    public final void onRestart() {
            Log.i(tag, "onRestart");
        super.onRestart();
    }

    /**
     * Mostra un missatge de logging en rotar 90 graus el dispositiu (o
     * simular-ho en l'emulador). L'event <code>configChanged</code> passa quan
     * girem el dispositiu 90 graus i passem de portrait a landscape (apaisat) o
     * al revés. Això fa que les activitats siguin destruïdes (
     * <code>onDestroy</code>) i tornades a crear (<code>onCreate</code>). En
     * l'emulador del dispositiu, això es simula fent Ctrl-F11.
     *
     * @param newConfig
     *            nova configuració {@link Configuration}
     */
    @Override
    public final void onConfigurationChanged ( final Configuration newConfig) {
        Log.i(tag, "onConfigurationChanged");
        if (Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, newConfig.toString());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Funcionalidad de la action Bar, con todas las opciones de menu
     * @param item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                if (activitatPareActualEsArrel) {
                    Log.d(tag, "arrel");
                    //sendBroadcast(new Intent(LlistaActivitatsActivity.PARA_SERVEI));
                } else {
                    sendBroadcast(new Intent(LlistaActivitatsActivity.PUJA_NIVELL));
                    Log.d(tag, "enviat intent PUJA_NIVELL");
                    sendBroadcast(new Intent(LlistaActivitatsActivity.DONAM_FILLS));
                    Log.d(tag, "enviat intent DONAM_FILLS");
                }
                return true;
            case R.id.action_settings:
                onClickItemSelected(SettingActivity.class,null,null);
                return true;
            case R.id.action_NewProject:
                onClickItemSelected(NewProjectAdd.class,"nombre",nomPareActual);
                return true;
            case R.id.action_NewTask:
                onClickItemSelected(NewTaskAdd.class,"nombre",nomPareActual);
                return true;
            case R.id.action_stopTasks:
                sendBroadcast(new Intent(LlistaActivitatsActivity.PARA_TOTS_CRONOMETRES));
                Log.d(tag, "aturar totes les tasques");
                return true;
            case R.id.action_runTasks:
                sendBroadcast(new Intent(LlistaActivitatsActivity.ENGEGA_TOTS_CRONOMETRES));
                Log.d(tag, "engegar totes les tasques");
                return true;
            case R.id.report_item:
                Intent inte = new Intent(LlistaActivitatsActivity.this, InformeActivity.class);
                startActivity(inte);
                return true;
            case R.id.action_Search:
                if(item.getTitle() == getResources().getString(R.string.action_Search)){
                    item.setTitle(R.string.action_Search1);
                }else{
                    item.setTitle(R.string.action_Search);
                }
                sendBroadcast(new Intent(LlistaActivitatsActivity.CANVI_ORDENACIO));
                Log.d(tag, "ordenar");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Funcion para inicializar una activity, con o sin parametros
     * @param site actividad al que queire ir
     * @param key key del valor que quieres pasar
     * @param cadena cadena que quieres pasarle
     */
    public void onClickItemSelected(Class site, String key, String cadena ){

        Intent intent = new Intent(this,site);
        if(key != null && !key.isEmpty()) {
            intent.putExtra(key, cadena);
        }
        startActivity(intent);
    }
    //Boton retroceso del action menu, si es arrel entonces no muestra el boton
    public void viewStateActionMenu(){
        Boolean returnMenu =true;
        if (activitatPareActualEsArrel) {
            Log.d(tag, "arrel tres");
            returnMenu =false;
        } else {
            returnMenu = true;
            Log.d(tag, "no es arrel");
        }
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(returnMenu);
    }

    // Called when the action mode is created; startActionMode() was called
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        // Assumes that you have "contexual.xml" menu resources
        inflater.inflate(R.menu.action_bar_selection, menu);
        MenuItem mEditMenuItemOption = menu.findItem(R.id.edit_item);
        //MenuItem mReportMenuItemOption = menu.findItem(R.id.report_item);
        if (llistaDadesActivitats.get(selectedItem).isProjecte()){
            mEditMenuItemOption.setVisible(true);
            //mReportMenuItemOption.setVisible(true);
        }else if(llistaDadesActivitats.get(selectedItem).isTasca()){
            mEditMenuItemOption.setVisible(true);
            //mReportMenuItemOption.setVisible(false);
        }else{
            mEditMenuItemOption.setVisible(false);
        }
        return true;
    }

    // Called each time the action mode is shown. Always called after
    // onCreateActionMode, but
    // may be called multiple times if the mode is invalidated.
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false; // Return false if nothing is done
    }

    // Called when the user selects a contextual menu item
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        Intent inte;
        switch (item.getItemId()) {
            case R.id.delete_item:
                inte = new Intent(LlistaActivitatsActivity.ELIMINAR_ITEM);
                inte.putExtra("posicio", selectedItem);
                sendBroadcast(inte);
                // Action picked, so close the CAB
                mode.finish();
                return true;
            case R.id.edit_item:
                inte = new Intent(LlistaActivitatsActivity.EDITAR_ITEM);
                inte.putExtra("posicio", selectedItem);
                sendBroadcast(inte);
                Intent i = new Intent(LlistaActivitatsActivity.this, EditActivity.class);
                i.putExtra("nombre",llistaDadesActivitats.get(selectedItem).getNom());
                i.putExtra("descripcion",llistaDadesActivitats.get(selectedItem).getDescripcio());
                i.putExtra("posicio",selectedItem);
                startActivity(i);
                // Action picked, so close the CAB
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    /**
     * Funcion para realizar el cierre de la aplicacion
     */
    public void CerrarAplicacion() {
        sendBroadcast(new Intent(LlistaActivitatsActivity.PARA_SERVEI));
        Log.d(tag, "parem servei");
        super.onBackPressed();
    }
    // Called when the user exits the action mode
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        Intent inte;
        inte = new Intent(LlistaActivitatsActivity.DONAM_FILLS);
        sendBroadcast(inte);
        mActionMode = null;
        selectedItem = -1;
    }
    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new TaskActivated_Dialog_Fragment();
        dialog.show(getFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public boolean onPrepareActionMode(Menu menu) {
        return false;
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        CerrarAplicacion();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button

    }


}
