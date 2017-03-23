/**
 * Aquest package conté la interfase gràfica d'usuari Android pel projecte
 * de TimeTracker. La part que no és de la interfase d'usuari, o sigui, les
 * classes <code>Tasca</code>, <code>Projecte</code>, <code>Rellotge</code>
 * etc. es troba en forma de llibreria .jar al directori <code>lib/</code>.
 * Està provada per plataforma 2.1 i API level 7.
 * <p>
 * La interfase consisteix en dues activitats:
 * <ol>
 * <li>{@link LlistaActivitatsActivity}, que mostra tasques i projectes filles
 * d'algun projecte pare,</li>
 * <li>{@link LlistaIntervalsActivity}, que mostra els intervals
 * d'una tasca.</li>
 * </ol>
 * <p>
 * A banda, hi ha la classe {@link GestorArbreActivitats}, derivada de
 * la classe <code>Service</code> d'Android, que conte les dades que
 * comparteixen aquestes activitats i que són bàsicament l'arbre de tasques,
 * projectes i intervals.
 * <p>
 * La classe {@link Actualitzador} es un "handler" que serveix per actualitzar
 * aquestes vistes ja que es mostren simultàniament al recompte de temps. Evita
 * l'us de Timer i TimerTask, que causa conflictes en Android.
 * <p>
 * La interfase {@link Actualitzable} es simplement per forçar les classes que
 * s'han d'actualitzar a tenir el mètode <code>actualitza</code> que fa
 * l'actualització. D'aquesta manera, també podem substituir el Timer del
 * Rellotge per aquest handler.
 * <p>
 * La classe {@link ArbreAleatori} genera un arbre de tasques, projectes i
 * intervals dins d'un període de temps donat. Altres paràmetres són la durada
 * mínima i màxima d'interval i el nombre màxim d'activitats i intervals fills.
 * El nom, descripció, durada, dates inicial i final (dins del període) són
 * aleatoris.
 * <p>
 * Les classes {@link DadesActivitat} i {@link DadesInterval} contenen,
 * respectivament, les dades de projecte i tasca filles del projecte
 * actual, i dels intervals de la tasca actual, per tal de ser mostrades a les
 * Activity ("pantalles" Android) {@link LlistaActivitatsActivity} i
 * {@link LlistaActivitatsActivity}. Són creades pel servei
 * {@link GestorArbreActivitats} i enviades com a "intents" de "broadcast".
 * Aquestes pantalles poden mostrar totes o una part d'aquestes dades, i
 * eventualment fer-ho fer en altres Activity.
 */
package iuAndroid;

