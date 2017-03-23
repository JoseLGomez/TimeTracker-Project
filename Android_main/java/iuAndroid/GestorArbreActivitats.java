package iuAndroid;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import timetracker.Activity;
import timetracker.BasicReport;
import timetracker.FullReport;
import timetracker.Html;
import timetracker.Interval;
import timetracker.Project;
import timetracker.Clock;
import timetracker.BasicTask;
import timetracker.Txt;
import utils.tools;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


/**
 * Llegeix, manté en memòria i desa l'arbre de projectes, tasques i intervals, i
 * en gestiona tant la navegació per ell com el cronometrat de tasques, quan
 * així es sol·licita des de les classes que capturen els events d'interacció
 * amb l'usuari. A més, envia a les Activity que ho demanen la llista de les
 * dades d'activitat o d'interval que s'han de mostrar.
 *
 * @author joans
 * @version 26 gener 2012
 */
public class GestorArbreActivitats extends Service implements Actualitzable {

    /**
     * Nom de la classe per fer aparèixer als missatges de logging del LogCat.
     *
     * @see Log
     */
    private final String tag = this.getClass().getSimpleName();

    /**
     * El node arrel de l'arbre de projectes, tasques i intervals. Aquest node
     * és doncs especial perquè no existeix per a l'usuari, només serveix per
     * contenir la llista d'activitats del primer nivell que si veu l'usuari.
     */
    private Project arrel;

    /**
     * Observable que actualitza les tasques que estan sent cronometrades en
     * invocar el seu mètode update().
     */
    private Clock rellotge;

    /**
     * Arxiu on es desa tot l'arbre de projectes, tasques i intervals.
     */
    private String nomArxiu = "timetracker.dat";

    /**
     * En navegar per l'arbre de projectes i tasques, és el projecte pare de la
     * llista d'activitats mostrada per últim cop gràcies a la classe
     * <code>LlistaActivitatsActivity</code>, o bé la tasca pare dels intervals
     * mostrats a la activitat <code>LlistaIntervalsActivity</code>. El seu
     * valor inicial és el projecte arrel de tot l'abre, {@link #arrel}
     */
    private Activity activitatPareActual;

    /**
     * Lista de actividades que almacena los nodos hijos ordenados segun opcion
     */
    private ArrayList<Activity> Ordered_activities = new ArrayList<Activity>();

    /**
     * Constantes de ordenacion por fecha o nombre
     */
    private int option = 1; //por defecto por nombre
    public static final int SORT_NAME = 1;
    public static final int SORT_DATE = 2;

    /**
     * El servei consisteix en processar, com en el cas d'engegar i parar
     * cronometre de tasca, o be processar i retornar unes dades, com en la
     * resta, ja que cal actualitzar el la activitat actual (tasca o projecte) i
     * enviar la llista d'activitats filles o intervals, segons el cas. Per tal
     * de retornar dades, dissenyem aquest intent.
     */
    public static final String TE_FILLS =
            "es.uab.es2.TimeTracker.iuAndroid.Te_fills";

    /**
     * Crida a la pantalla de editar item amb l'informacio corresponent
     */
    public static final String EDITAR_ITEM =
            "es.uab.es2.TimeTracker.iuAndroid.Editar_Item";

    /**
     * Envia la respuesta a LlistaActivitatsActivity sobre si hay o no tareas cronometrandose
     */
    public static final String RESPOSTA_TASCA_CRONOMETRANTSE =
            "es.uab.es2.TimeTracker.iuAndroid.Resposta_tasca_cronometrantse";

    /**
     * Usada a onCreate i carregaArbreActivitats per crear un o
     * altre tipus d'arbre de projectes, tasques i intervals.
     */
    private final int llegirArbreArxiu = 0;

    /**
     * Usada a onCreate i carregaArbreActivitats per crear un o
     * altre tipus d'arbre de projectes, tasques i intervals.
     */
    private final int ferArbrePetitBuit = 1;

    /**
     * Usada a onCreate i carregaArbreActivitats per crear un o
     * altre tipus d'arbre de projectes, tasques i intervals.
     */
    private final int ferArbreGran = 2;

    /**
     * Rep els "intents" que envien <code>LlistaActivitatsActivity</code> i
     * <code>LlistaIntervalsActivity</code>. El receptor els rep tots (no hi ha
     * cap filtre) i els diferència per la seva "action". Veure {@link Receptor}
     * per saber quins són.
     *
     */
    private Receptor receptor;

    /**
     * Handler que ens permet actualitzar la interfase d'usuari quan hi ha
     * alguna tasca que s'està cronometrant. Concretament, actualitza les
     * Activity que mostren activitats i intervals. S'engega i es para a
     * {@link Receptor#onReceive}.
     */
    private Actualitzador actualitzadorIU;

    /**
     * Llista de tasques que estan sent cronometrades en cada moment, que
     * mantenim per tal que en parar el servei les puguem deixar de cronometrar
     * i fer que no es desin com si ho fossin i llavors tenir dades
     * inconsistents en tornar a carregar l'arbre.
     */
    private ArrayList<BasicTask> tasquesCronometrantse = new ArrayList<BasicTask>();

    /**
     * Període de temps en segons dada quan s'actualitza la interfase d'usuari.
     * Es un paràmetre del constructor de {@link #actualitzadorIU}.
     */
    private int periodeRefrescIU = 1000 ;

    /**
     * Llegeix l'abre de projectes, tasques i intervals desat en l'arxiu. Aquest
     * arxiu és propi i privat de la aplicació.
     *
     * @param opcio
     *            permet escollir entre 3 tipus d'arbres :
     *            <p>
     *            <ol>
     *            <li>Llegir l'arbre d'arxiu, si existeix, que vol dir si prové
     *            de desar l'arbre existent en una crida anterior al mètode
     *            <code>GestorArbreActivitats#desaArbre</code>. Si no existeix,
     *            fa un arbre que només te el node arrel.</li>
     *            <li>Fer un arbre petit, amb 3 tasques i 2 projectes a més de
     *            l'arrel, i sense intervals.</li>
     *            <li>Fer un arbre gran amb projectes, tasques i intervals, amb
     *            dades aleatòries però consistents. Cada vegada es fa el mateix
     *            arbre, però.</li>
     *            </ol>
     *
     * veure desaArbreActivitats
     */
    public final void carregaArbreActivitats(final int opcio) {
        // lectura d'un arxiu molt gran pugui trigui massa i provoqui que la
        // aplicació perdi responsiveness o pitjor, que aparegui el diàleg
        // ANR = application is not responding, demanant si volem forçar el
        // tancament de la aplicació o esperar.
        // La solució deu ser fer servir una AsyncTask, tal com s'explica a
        // l'article "Painless Threading" de la documentació del Android SDK.
        // Veure'l a la versió local o a
        // developer.android.com/resources/articles/painless-threading.html

        switch (opcio) {
            /*Se lee el archivo de sesión que se almacena cada vez que se deja la aplicación, si no
              existe o hay algun problema al cargarlo, se crea un arbol vacío*/
            case llegirArbreArxiu:
                Log.d("TAG", "carrega arbre d'activitats");
                nomArxiu = tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "savepath");
                try {
                    FileInputStream fips = openFileInput(nomArxiu);
                    ObjectInputStream in = new ObjectInputStream(fips);
                    arrel = (Project) in.readObject();
                    in.close();
                    Log.d(tag, "Arbre llegit d'arxiu");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Log.d(tag, "La clase del arxiu no es troba, fem un arbre buit");
                    arrel = new Project("ARREL", "arrel de projectes", null);
                } catch (StreamCorruptedException e) {
                    e.printStackTrace();
                    Log.d(tag, "El format o fluxe esta corrupte, fem un arbre buit");
                    arrel = new Project("ARREL", "arrel de projectes", null);
                } catch (FileNotFoundException e) {
                    Log.d(tag, "L'arxiu no es troba, fem un arbre buit");
                    arrel = new Project("ARREL", "arrel de projectes", null);
                    // e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                // no hi ha més opcions possibles
                assert false : "opció de creació de l'arbre no existent";
        }
    }

    /**
     * Desa l'arbre de projectes, tasques i intervals en un arxiu propi de la
     * aplicació. El mecanisme emprat per desar és serialitzar l'arrel.
     */

    public final void desaArbreActivitats() {
        Log.d("TAG", "desa arbre activitats");
        nomArxiu = tools.readFileXML(getFileStreamPath("config.xml").getAbsolutePath(), "savepath");
        try {
            FileOutputStream fops = openFileOutput(nomArxiu,
                    Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fops);
            out.writeObject(arrel);
            out.close();
        } catch (FileNotFoundException e) {
            Log.d(tag, "L'arxiu no es troba");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * El nostre servei no està lligat a cap activitat per tal de compartir
     * dades. No és una bona opció per que el servei és destruït quan deixa
     * d'estar lligat a cap Activity. Tot i així cal sobrecarregar aquest mètode
     * per que retorni null.
     *
     * @param arg0
     *            argument no utilitzat
     * @return null
     */
    @Override
    public final IBinder onBind(final Intent arg0) {
        return null;
    }

    /**
     * Invocada quan es crea el servei per primer cop, fa una sèrie
     * d'inicialitzacions. Són les següents inicialitzacions
     * <ul>
     * <li>estableix els tipus d'intents als quals dona resposta (veure
     * {@link Receptor})</li>
     * <li>crea el "handler" actualitzadorIU que actualitzarà la
     * interfase d'usuari a mesura que vagi passant el temps</li>
     * <li>crea i posa en marxa el rellotge per cronometrar tasques</li>
     * </ul>
     */
    public final void onCreate() {
        Log.d(tag, "onCreate");

        //Filtros necesarios para responder peticiones(intents) de otras activities
        IntentFilter filter;
        filter = new IntentFilter();
        filter.addAction(LlistaActivitatsActivity.DONAM_FILLS);
        filter.addAction(LlistaActivitatsActivity.PUJA_NIVELL);
        filter.addAction(LlistaActivitatsActivity.BAIXA_NIVELL);
        filter.addAction(LlistaActivitatsActivity.ENGEGA_CRONOMETRE);
        filter.addAction(LlistaActivitatsActivity.ENGEGA_TOTS_CRONOMETRES);
        filter.addAction(LlistaActivitatsActivity.PARA_TOTS_CRONOMETRES);
        filter.addAction(LlistaActivitatsActivity.PARA_CRONOMETRE);
        filter.addAction(LlistaActivitatsActivity.DESA_ARBRE);
        filter.addAction(LlistaActivitatsActivity.PARA_SERVEI);
        filter.addAction(LlistaActivitatsActivity.ELIMINAR_ITEM);
        filter.addAction(LlistaActivitatsActivity.EDITAR_ITEM);
        filter.addAction(LlistaActivitatsActivity.TASCA_CRONOMETRANTSE);
        filter.addAction(LlistaIntervalsActivity.PUJA_NIVELL);
        filter.addAction(LlistaActivitatsActivity.CANVI_ORDENACIO);
        filter.addAction(NewProjectAdd.AFEGIR_PROJECTE);
        filter.addAction(NewTaskAdd.AFEGIR_TASCA);
        filter.addAction(EditActivity.ACTUALITZAR_ITEM);
        filter.addAction(InformeActivity.REPORT);

        receptor = new Receptor();
        registerReceiver(receptor, filter);
        //carga de los valores de configuracion del archivo para el reloj y refresco de pantalla
        periodeRefrescIU = Integer.parseInt(tools.readFileXML(
                getFileStreamPath("config.xml").getAbsolutePath(), "cooliu")) *1000;
        rellotge = Clock.getInstance(Integer.parseInt(tools.readFileXML(
                getFileStreamPath("config.xml").getAbsolutePath(), "coolclock")) *1000);
        actualitzadorIU = new Actualitzador(this, periodeRefrescIU,
                "gestor_arbre_activitats");
        carregaArbreActivitats(llegirArbreArxiu);
        activitatPareActual = arrel;
        if (arrel != null) {
            Log.d(tag, "l'arrel te " + arrel.getActivities().size() + " fills");
        }
    }

    /**
     * En engegar el servei per primer cop, o després de ser parat i tornat a
     * engegar, enviem les dades de la llista de fills de la activitat actual.
     *
     * @param intent
     *               veure la documentació online
     * @param flags
     *               veure la documentació online
     * @param startId
     *               veure la documentació online
     * @return
     *               veure la documentació online
     */
    @Override
    public final int onStartCommand(final Intent intent, final int flags,
                                    final int startId) {
        if ((flags & Service.START_FLAG_RETRY) == 0) {
            // es un restart, després d'acabar de manera anormal
            Log.d(tag, "onStartCommand repetit");
        } else {
            Log.d(tag, "onStartCommand per primer cop");
        }
        enviaFills();
        return Service.START_STICKY;
    }

    /**
     * Conté el mètode <code>onReceive</code> on es dona servei, o sigui, es
     * gestionen, les peticions provinents de les classes Activity que capturen
     * la interacció de l'usuari.
     * <p>
     * Concretament, de {@link LlistaActivitatsActivity} rebem les peticions de
     * <ul>
     * <li><code>ENGEGA_CRONOMETRE</code> i <code>PARA_CRONOMETRE</code> d'una
     * tasca clicada, la qual s'inidica com el número d'ordre en la llista
     * d'activitats mostrades. Si la tasca no està sent ja cronometrada i es
     * demana, se li engega el cronòmetre. Si ho està sent i es demana, se li
     * para.</li>
     * <li><code>PUJA_NIVELL</code> i <code>BAIXA_NIVELL</code>, que fan que
     * s'actualitzi la activitat pare actual. Es responsabilitat del
     * sol·licitant llavors demanar que se li enviïn les noves dades a mostrar.
     * </li>
     * <li><code>DONAM_FILLS</code> demana que es construeixi una llista de les
     * dades dels fills de la activitat pare actual, ja sigui projecte o tasca.
     * Ho farà el mètode {@link GestorArbreActivitats#enviaFills}, el qual
     * construeix aquesta llista i la posa com un "extra" a un Intent que té una
     * acció igual a TE_FILLS.</li>
     * <li><code>DESA_ARBRE</code> demana que s'escrigui l'arbre actual a
     * l'arxiu per defecte, privat de la aplicació, el nom del qual és a
     * {@link GestorArbreActivitats#nomArxiu}.</li>
     * <li>PARA_SERVEI</li> demana el que faríem si en Android es pogués
     * "sortir" de la aplicació: parar els handlers actualitzadors de la
     * interfase i el rellotge, parar el receptor d'intents, parar el cronòmetre
     * de les tasques que ho estan essent, i desar l'arbre a arxiu. Tot això ho
     * fa <code>paraServei</code>, que a més a més, fa un <code>stopSelf</code>
     * d'aquest servei.
     * </ul>
     * I de LlistaintervalsActivity rebem <code>PUJA_NIVELL</code> i
     * <code>DONAM_FILLS</code> que tenen el mateix tractament.
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

        /**
         * Per comptes de fer una classe receptora per a cada tipus d'accio o
         * intent, els tracto tots aquí mateix, distingit-los per la seva
         * "action".
         *
         * @param context
         *            sol·licitant
         * @param intent
         *            petició consistent en una acció (string) per identificar
         *            el tipus de petició, i paràmetres que l'acompanyen en
         *            forma d'extres de l'intent.
         */
        @Override
        public final void onReceive(final Context context,
                                    final Intent intent) {
            Log.d(tag, "onReceive");
            String accio = intent.getAction();
            Log.d(tag, "accio = " + accio);
            if ((accio.equals(LlistaActivitatsActivity.ENGEGA_CRONOMETRE))
                    || (accio.equals(LlistaActivitatsActivity.PARA_CRONOMETRE))) {
                int posicio = intent.getIntExtra("posicio", -1);
                BasicTask tascaClicada = (BasicTask) Ordered_activities.toArray()[posicio];
                if (accio.equals(LlistaActivitatsActivity.ENGEGA_CRONOMETRE)) {
                    if (!tascaClicada.isTaskStarted()){
                        // rellotge.engega();
                        tascaClicada.start();
                        Log.d(tag, "engego cronometre de "
                                + tascaClicada.getName());
                        tasquesCronometrantse.add(tascaClicada);
                        actualitzadorIU.engega(); // si ja ho esta no fa res
                    } else {
                        Log.w(tag, "intentat cronometrar la tasca "
                                + tascaClicada.getName()
                                + " que ja ho esta sent");
                    }
                }
                if (accio.equals(LlistaActivitatsActivity.PARA_CRONOMETRE)) {
                    if (tascaClicada.isTaskStarted()) {
                        tascaClicada.stop();
                        tasquesCronometrantse.remove(tascaClicada);
                        if (tasquesCronometrantse.size() == 0) {
                            enviaFills();
                            actualitzadorIU.para();
                        }
                    } else {
                        Log.w(tag, "intentat parar cronometre de la tasca "
                                + tascaClicada.getName()
                                + " que ja el te parat");
                    }
                }
                Log.d(tag, "Hi ha " + tasquesCronometrantse.size()
                        + " tasques cronometrant-se");
            //acción que enciende todos los cronometros activos en el nivel actual del arbol
            } else if (accio.equals(LlistaActivitatsActivity.ENGEGA_TOTS_CRONOMETRES)) {
                for (Activity act : ((Project) activitatPareActual).getActivities()) {
                    if (!act.isIsAProject()) {
                        ((BasicTask) act).start();
                        tasquesCronometrantse.add((BasicTask) act);
                    }
                }
                actualitzadorIU.engega();
            //acción que para todos los cronometros activos en el nivel actual del arbol
            } else if (accio.equals(LlistaActivitatsActivity.PARA_TOTS_CRONOMETRES)) {
                for (Activity act : ((Project) activitatPareActual).getActivities()) {
                    if (!act.isIsAProject()) {
                        ((BasicTask) act).stop();
                        tasquesCronometrantse.remove((BasicTask) act);
                    }
                }
                actualitzadorIU.para();
            } else if (accio.equals(LlistaActivitatsActivity.DESA_ARBRE)) {
                desaArbreActivitats();
            //Envia los hijos del nodo actual a las activities de listas para mostrar por pantalla
            } else if (accio.equals(LlistaActivitatsActivity.DONAM_FILLS)
                    || (accio.equals(LlistaIntervalsActivity.DONAM_FILLS))) {
                enviaFills();
            //control para determinar si se ordena por nombre o fecha
            }else if(accio.equals(LlistaActivitatsActivity.CANVI_ORDENACIO)){
                if(option==SORT_NAME){
                    option = SORT_DATE;
                }else{
                    option = SORT_NAME;
                }
                enviaFills();
            } else if (accio.equals(LlistaActivitatsActivity.PUJA_NIVELL)
                    || (accio.equals(LlistaIntervalsActivity.PUJA_NIVELL))) {
                activitatPareActual = activitatPareActual.getParentActivity();
            } else if (accio.equals(LlistaActivitatsActivity.BAIXA_NIVELL)) {
                // Anem a una les activitats filles => actualitzem
                // l'activitat actual i enviem la llista d'activitats filles si
                // és un projecte, o els intervals si és una tasca.
                int posicio = intent.getIntExtra("posicio", 0);
                // El pare d'una activitat clicada nomes pot ser un projecte
                // per que els intervals no son clicables (no gestionem aquest
                // event). Ara, la activitat clicada tant pot ser un projecte
                // com una tasca.
                Activity activitatClicada =
                        (Activity) Ordered_activities.toArray()[posicio];
                activitatPareActual = activitatClicada;
            } else if (accio.equals(LlistaActivitatsActivity.PARA_SERVEI)) {
                paraServei();
            } else if (accio.equals(LlistaActivitatsActivity.ELIMINAR_ITEM)) {
                int posicio = intent.getIntExtra("posicio", -1);
                if (activitatPareActual.isIsAProject()){
                    Activity activitatClicada = (Activity) ((Project) activitatPareActual)
                            .getActivities().toArray()[posicio];
                    ((Project)activitatPareActual).getActivities().remove(activitatClicada);
                } else {
                    Interval intervalClicat = (Interval) ((BasicTask) activitatPareActual)
                            .getIntervalList().toArray()[posicio];
                    ((BasicTask)activitatPareActual).getIntervalList().remove(intervalClicat);
                }
                enviaFills();
                // acciones adicionales añadidas a as predefinidas
                //Añadir proyecto
            } else if (accio.equals(NewProjectAdd.AFEGIR_PROJECTE)) {
                String nombre = intent.getStringExtra("Nombre");
                String descripcion = intent.getStringExtra("Descripcion");
                Project nuevo_projecto = new Project(nombre,descripcion,(Project)activitatPareActual);
                enviaFills();
                //Añadir tarea
            } else if (accio.equals(NewTaskAdd.AFEGIR_TASCA)) {
                String nombre = intent.getStringExtra("Nombre");
                String descripcion = intent.getStringExtra("Descripcion");
                BasicTask nuevo_projecto = new BasicTask(nombre,descripcion,(Project)activitatPareActual);
                enviaFills();
                //Falta implementar control del decorator
            } else if (accio.equals(LlistaActivitatsActivity.EDITAR_ITEM)) {
                int posicio = intent.getIntExtra("posicio", -1);
                Activity activitatClicada = (Activity) ((Project) activitatPareActual)
                        .getActivities().toArray()[posicio];
                Intent resposta_edit = new Intent(GestorArbreActivitats.EDITAR_ITEM);
                resposta_edit.putExtra("Activitat",activitatClicada);
                sendBroadcast(resposta_edit);
            } else if (accio.equals(EditActivity.ACTUALITZAR_ITEM)) {
                int posicio = intent.getIntExtra("posicion", -1);
                String nombre_nuevo = intent.getStringExtra("Nombre");
                String descripcion_nueva = intent.getStringExtra("Descripcion");
                Activity activitatModificada = (Activity) ((Project) activitatPareActual)
                        .getActivities().toArray()[posicio];
                activitatModificada.setName(nombre_nuevo);
                activitatModificada.setDescription(descripcion_nueva);
                enviaFills();
                //Falta implementar control del decorator
            } else if (accio.equals(LlistaActivitatsActivity.TASCA_CRONOMETRANTSE)) {
                Intent resposta_tasca = new Intent(GestorArbreActivitats.RESPOSTA_TASCA_CRONOMETRANTSE);
                resposta_tasca.putExtra("Resposta",!tasquesCronometrantse.isEmpty());
                sendBroadcast(resposta_tasca);
                Log.d(tag, "enviat intent TASCA_CRONOMETRANTSE"
                        + activitatPareActual.getClass().getName());
            } else if (accio.equals(InformeActivity.REPORT)) {
                Bundle bundle = intent.getExtras();
                Calendar dataInici = (Calendar) bundle.get("dataInici");
                Calendar dataFi = (Calendar) bundle.get("dataFinal");
                String format = intent.getStringExtra("formato");
                String tipus  = intent.getStringExtra("tipo");
                try {
                    if (tipus.equals("breve")) {
                        BasicReport informe = new BasicReport(dataInici, dataFi, (Project) activitatPareActual);
                        if (format.equals("txt")) {
                            Txt textPla = new Txt();
                            informe.calculateReport(textPla);
                        } else if (format.equals("html")) {
                            Html textHtml = new Html();
                            informe.calculateReport(textHtml);
                        }
                    } else if (tipus.equals("detallado")) {
                        FullReport informe = new FullReport(dataInici, dataFi, (Project) activitatPareActual);
                        if (format.equals("txt")) {
                            Txt textPla = new Txt();
                            informe.calculateReport(textPla);
                        } else if (format.equals("html")) {
                            Html textHtml = new Html();
                            informe.calculateReport(textHtml);
                        }
                    } else {
                        Log.d(tag, "informa sense definir el tipus detallado/breve");
                    }
                }catch (Exception ex){
                    Log.d(tag, "error a crear informe");
                }
            } else {
                Log.d(tag, "accio desconeguda!");
            }
            Log.d(tag, "final de onReceive");
        }
    }

    /**
     * Construeixi una llista de les dades dels fills de la activitat pare
     * actual, ja sigui projecte o tasca, per tal de ser mostrades (totes o
     * algunes d'aquestes dades) a la interfase d'usuari. Aquesta llista es posa
     * com a "extra" serialitzable d'un intent de nom TE_FILLS, del qual se'n fa
     * "broadcast".
     */
    private void enviaFills() {
        Intent resposta = new Intent(GestorArbreActivitats.TE_FILLS);
        resposta.putExtra("activitat_pare_actual_es_arrel",
                (activitatPareActual == arrel));
        if (activitatPareActual.isIsAProject()) {
            Ordered_activities = sort_activities();
            ArrayList<DadesActivitat> llistaDadesProjectes = new ArrayList<DadesActivitat>();
            for (Activity act : Ordered_activities) {
                llistaDadesProjectes.add(new DadesActivitat(act));
            }
            resposta.putExtra("llista_dades_activitats", llistaDadesProjectes);
        } else { // es tasca
            ArrayList<DadesInterval> llistaDadesInter =
                    new ArrayList<DadesInterval>();
            for (Interval inter : ((BasicTask) activitatPareActual)
                    .getIntervalList()) {
                llistaDadesInter.add(new DadesInterval(inter));
            }
            resposta.putExtra("llista_dades_intervals", llistaDadesInter);
        }
        resposta.putExtra("Nom_pare", activitatPareActual.getName());
        sendBroadcast(resposta);
        Log.d(tag, "enviat intent TE_FILLS d'activitat "
                + activitatPareActual.getClass().getName());
    }

    /**
     * Parar els handlers actualitzadors de la interfase i el rellotge, parar el
     * receptor d'intents, parar el cronòmetre de les tasques que ho estan
     * essent, desar l'arbre a arxiu i per últim fa un <code>stopSelf</code>
     * d'aquest servei, amb la qual cosa és semblant a tancar la aplicació.
     */
    private void paraServei() {
        actualitzadorIU.para();
        rellotge.stop();
        // el garbage collector ja els el·liminarà quan sigui, després de veure
        // que no es "reachable", com a conseqüència de que el servei és
        // eliminat també (espero).
        unregisterReceiver(receptor);
        // Això cal fer-ho per evitar un error en fer el darrer 'back'
        paraCronometreDeTasques();
        // Cal parar totes les tasques que s'estiguin cronometrant, per tal que
        // no es desin a l'arxiu com que si que ho estan sent i després, en
        // llegir
        // apareguin com cronometrant-se i generin problemes.
        desaArbreActivitats();
        stopSelf();
        Log.d(tag, "servei desinstalat");
    }

    /**
     * Mètode de "forwarding" de <code>enviaFills</code> (per actualitzar
     * la interfase d'usuari) però només quan aquesta pot haver canviat,
     * és a dir, quan hi ha alguna tasca que s'està cronometrant.
     */
    public final void actualitza() {
        Log.d(tag, "entro a actualitza de GestorArbreActivitats");
        if (tasquesCronometrantse.size() > 0) {
            enviaFills();
        }
    }

    /**
     * Para el cronòmetre de totes les tasques que ho estiguin sent,
     * al nivell que sigui de l'arbre.
     */
    private void paraCronometreDeTasques() {
        for (BasicTask t : tasquesCronometrantse) {
            t.stop();
        }
    }

    /**
     * Funcion que ordena la lista de actividades a criterio del usuario,
     * ya sea por nombre o fecha, se realiza para asegurar que los datos manejados
     * esten ordenados y en correspondencia. La ordenación siempre es por tipo inicalmente y
     * luego ya por fecha o nombre a seleccion del ussuario.
     * @return
     */
    public ArrayList<Activity> sort_activities() {
        ArrayList<Activity> llistaDadesProjectes = new ArrayList<Activity>();
        ArrayList<Activity> llistaDadesTasques = new ArrayList<Activity>();
        //Creacion de listas separadas para realizar una ordenacion
        for (Activity act : ((Project) activitatPareActual).getActivities()) {
            if (act.isIsAProject()) {
                llistaDadesProjectes.add(act);
            }else{
                llistaDadesTasques.add(act);
            }
        }
        switch (option) {
            case SORT_NAME:
                Collections.sort(llistaDadesProjectes, new NameComparator());
                Collections.sort(llistaDadesTasques, new NameComparator());
                break;
            case SORT_DATE:
                Collections.sort(llistaDadesProjectes, new DateComparator());
                Collections.sort(llistaDadesTasques, new DateComparator());
                break;
        }
        llistaDadesProjectes.addAll(llistaDadesTasques);
        return llistaDadesProjectes;
    }
    /**
     * Comparador para ordenar las listas de tareas o proyectos
     */
    public class NameComparator implements Comparator<Activity> {
        @Override
        public int compare(Activity o1, Activity o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
    public class DateComparator implements Comparator<Activity> {
        @Override
        public int compare(Activity o1, Activity o2) {
            try {
                return o1.getPeriodo().getDateInit().compareTo(o2.getPeriodo().getDateInit());
            }catch (Exception e){
                return 0;
            }
        }
    }

}
