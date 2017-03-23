package iuAndroid;

import java.io.Serializable;
import java.util.Calendar;

import timetracker.Activity;
import timetracker.BasicTask;



/**
 * Conté les dades d'una activitat (projecte o tasca) que poden ser mostrades
 * per la interfase d'usuari. <code>GestorArbreActivitats</code> en fa una
 * llista amb les dades de les activitats filles del projecte actual, i l'envia
 * a la Activity <code>LlistaActivitatsActivity</code> per que la mostri.
 * <p>
 * Com que és una classe sense funcionalitat, només és una estructura de dades,
 * així que faig els seus atributs públics per simplificar el codi.
 * <p>
 * Aquesta classe simplifica el passar les dades de projectes i tasques a la
 * Activity corresponent que les visualitza. Si passéssim directament la llista
 * d'activitats filles, com que es fa per serialització, s'acaba enviat tot
 * l'arbre, ja que els fills referencien als pares. El problema es tal, que amb
 * un arbre mitjà es perd tota la "responsiveness".
 *
 * @author joans
 * @version 6 febrer 2012
 */
public class DadesActivitat implements Serializable {
    /**
     * Ho demana el Checkstyle, però no he mirat per a què deu servir.
     */
    private static final long serialVersionUID = 1L;

    /**
     * @see Activity
     */
    private Calendar dataInicial;

    /**
     * @see Activity
     */
    private Calendar dataFinal;

    /**
     * @see Activity
     */
    private double durada; // en segons

    /**
     * @see Activity
     */
    private String nom;

    /**
     * @see Activity
     */
    private String descripcio;

    /**
     * Hores de durada.
     */
    private long hores;

    /**
     * Minuts de durada.
     */
    private long minuts;

    /**
     * Segons de durada.
     */
    private long segons;

    /**
     * Per tal d'identificar el tipus d'activitat en la interfase d'usuari.
     */
    private boolean isProjecte;

    /**
     * Per tal d'identificar el tipus d'activitat en la interfase d'usuari.
     */
    private boolean isTasca;

    /**
     * La interfase d'usuari ho necessita saber per denotar-ho i també per
     * adequar la interacció (per exemple, no hauria de deixar cronometrar una
     * tasca que ja ho està sent).
     */
    private boolean isCronometreEngegat = false; // nomes te sentit per tasques

    /**
     * Extreu les dades de la activitat passada per paràmetre i les copia als
     * atributs propis.
     *
     * @param act
     *            Tasca o projecte.
     */
    public DadesActivitat(final Activity act) {
        /**
         * Factor de conversió
         */
        final long segonsPerHora = 3600;

        /**
         * Factor de conversió
         */
        final long segonsPerMinut = 60;
        if (act.getPeriodo() != null) {
            dataInicial = act.getPeriodo().getDateInit();
            dataFinal = act.getPeriodo().getDateEnd();
            durada = act.getPeriodo().getTimeTotal()/1000;
        }
        nom = act.getName();
        descripcio = act.getDescription();
        hores = (long) (durada / segonsPerHora);
        minuts = (long) ((durada - hores * segonsPerHora) / segonsPerMinut);
        segons = (long) (durada - segonsPerHora * hores
                - segonsPerMinut * minuts);

        if (act.isIsAProject()) {
            isProjecte = true;
            isTasca = false;
        } else {
            isProjecte = false;
            isTasca = true;
            isCronometreEngegat = ((BasicTask) act).isTaskStarted();
        }
    }

    /**
     * Converteix una part de les dades d'un objecte DadesActivitat a un String,
     * que serà el que es mostrarà a la interfase d'usuari, ja que els
     * <code>ListView</code> mostren el que retorna aquest mètode per a cada un
     * dels seus elements. Veure
     * {@link LlistaActivitatsActivity.Receptor#onReceive}.
     *
     * @return nom i durada de la activitat, en format hores, minuts i segons.
     */
    @Override
    public final String toString() {
        String str = nom;
        String strdurada;
        String horesF="",minutsF="",segonsF="";
        if (durada > 0) {
            horesF=Long.toString(hores);
            minutsF=Long.toString(minuts);
            segonsF=Long.toString(segons);
            if(hores <=9){
                horesF = "0"+Long.toString(hores);
            }
            if(minuts <=9){
                minutsF = "0"+Long.toString(minuts);
            }
            if(segons <=9){
                segonsF = "0"+Long.toString(segons);
            }
            strdurada = "\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t\t" + horesF +":"+minutsF+":"+segonsF;

        } else {
            strdurada = "\n"+"\t\t\t\t\t\t\t\t\t\t\t\t00:00:00";
        }
        str += " " + strdurada;
        return str;
    }

    // Getters

    /**
     * Getter de <code>dataInicial</code>.
     * @return {@link #dataInicial}.
     */
    public final Calendar getDataInicial() {
        return dataInicial;
    }

    /**
     * Getter de <code>dataFinal</code>.
     * @return {@link #dataFinal}.
     */
    public final Calendar getDataFinal() {
        return dataFinal;
    }

    /**
     * Getter de <code>durada</code>.
     * @return {@link #durada}.
     */
    public final double getDurada() {
        return durada;
    }

    /**
     * Getter de <code>hores</code>.
     * @return {@link #hores}.
     */
    public final long getHores() {
        return hores;
    }

    /**
     * Getter de <code>minuts</code>.
     * @return {@link #minuts}.
     */
    public final long getMinuts() {
        return minuts;
    }

    /**
     * Getter de <code>segons</code>.
     * @return {@link #segons}.
     */
    public final long getSegons() {
        return segons;
    }

    /**
     * Getter de <code>nom</code>.
     * @return {@link #nom}.
     */
    public final String getNom() {
        return nom;
    }

    /**
     * Getter de <code>descripcio</code>.
     * @return {@link #descripcio}.
     */
    public final String getDescripcio() {
        return descripcio;
    }

    /**
     * Getter de <code>isProjecte</code>.
     * @return {@link #isProjecte}.
     */
    public final boolean isProjecte() {
        return isProjecte;
    }

    /**
     * Getter de <code>isTasca</code>.
     * @return {@link #isTasca}.
     */
    public final boolean isTasca() {
        return isTasca;
    }

    /**
     * Getter de <code>isCronometreEngegat</code>.
     * @return {@link #isCronometreEngegat}.
     */
    public final boolean isCronometreEngegat() {
        return isCronometreEngegat;
    }
}
