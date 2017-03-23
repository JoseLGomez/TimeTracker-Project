
package timetracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;



/** 
 * Clase que crea los intervalos de tiempo que se asociaran al Clock.
 * Encargado de controlar inicio y final del intervalo de tiempo y crearlo como observer.
 * @author Jose Luis Gomez, Eric Jaen, Jesus Serrano.
 */
public class Interval implements Observer,Serializable{
  /**
  * Logger para mostrar mensajes por pantalla.
  */
  private static final Logger logger = LoggerFactory.getLogger(Interval.class);
  
  /**
   * @uml.property  name="clock"
   */
  private Clock clock;
  /** 
   * Contiene la tasca que se ha instanciado, para poder retornarlo
   * @uml.property name="task" readOnly="true"
   */
  private BasicTask task;

  private Period period;
  
  private Task parent = null;
  
  /**
   * Contructor Intervalo.
   */
  public Interval(BasicTask elementTask) {
    assert (elementTask != null) : "la fecha actual no puede ser nula";
    logger.debug(" Nuevo Intervalo ");
    this.task = elementTask; //tarea padre
    this.clock = Clock.getInstance();
    this.period = new Period(); //creacion del periodo asociado al intervalo
    checkInvariant();
  }
  
  private void checkInvariant() {
    assert (this.task != null) : "se ha enviado una BasicTask incorrecta";
    assert (this.clock != null) : "no se ha podido intanciar el clock";
    assert (this.period != null) : "se ha intentado crear un periodo nulo";  
  }
  
  /**
   * Metodo llamado cada vez que se realiza un tick en el reloj.
   * añade el tiempo transcurrido y actualiza la fecha final en el periodo asociado a este intervalo
   * Tambien actualiza la fecha de la tarea padre a la que pertenece
   */
  public void update(Observable arg0, Object arg1) {  
    logger.debug("Actualización del intervalo");
    DataClock date = (DataClock)arg1;
    this.period.getDateEnd().setTime(date.getActualDate());
    this.period.setTimeTotal(this.period.getTimeTotal() + date.getTick());
    if (this.task != null) {
      this.task.update(date);
    }
    //this.task.update();
  }
  
  /** 
   * Getter of the property <tt>task</tt>
   * @return  Returns the task.
   * @uml.property  name="task"
   */
  public Task getTask() {
    return task;
  }
  /**
   * Agrega el intervalo como Observer del reloj.
   * Inicializa la fecha de inicio y final en el periodo.
   */
  public void start() {
    logger.debug("Inicio del intervalo");
    this.active = true;
    this.clock.addObserver(this); //añade el itnervalo como observer de reloj
    this.period.setDateInit(Calendar.getInstance()); //Ponemos la fecha de inicio en el periodo
    this.period.getDateInit().setTime(new Date());
    this.period.setDateEnd(Calendar.getInstance());
    this.period.getDateEnd().setTime(new Date());
    this.task.updateIniTime(this.period.getDateInit());
  }
  
  /**
   * Eliminacion del intervalo como observer.
   * actualizacion de fecha final en el periodo y se envia la infomación a la tarea padre.
   */
  public void stop() {
    logger.debug("Final del intervalo");
    this.active = false;
    this.clock = Clock.getInstance();
    this.period.setDateEnd(Calendar.getInstance());
    this.period.getDateEnd().setTime(new Date());
    this.clock.deleteObserver(this);
    if (this.parent != null) {
      this.parent.update(new DataClock(new Date(), this.clock.getTickClock()));
    }
    // Para parar el el Thread y no siga mostrando informacion una vez finalizado todo
    /*if (this.clock.countObservers() == 0){
      this.clock.stop();
    }*/
  }

  /** 
   * @uml.property name="basicTask"
   * @uml.associationEnd multiplicity="(1 1)" inverse="interval:TimeTracker.BasicTask"
   */
  //private BasicTask basicTask = new TimeTracker.BasicTask();
  /** 
   * Getter of the property <tt>basicTask</tt>
   * @return  Returns the basicTask.
   * @uml.property  name="basicTask"
   */
  /*public BasicTask getBasicTask() {
    return basicTask;
  }*/

  /** 
   * Setter of the property <tt>basicTask</tt>
   * @param basicTask  The basicTask to set.
   * @uml.property  name="basicTask"
   */
  /*public void setBasicTask(BasicTask basicTask) {
    this.basicTask = basicTask;
  }*/

  /**
   * @uml.property  name="active"
   */
  private boolean active = false;

  /**
   * Getter of the property <tt>active</tt>
   * @return  Returns the active.
   * @uml.property  name="active"
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Setter of the property <tt>active</tt>
   * @param active  The active to set.
   * @uml.property  name="active"
   */
  public void setActive(boolean active) {
    this.active = active;
  }
  
  public Period getPeriod() {
    return this.period;
  }
}
