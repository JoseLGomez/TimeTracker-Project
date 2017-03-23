package timetracker;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;






/**
 * Clase que realiza la tarea básica, que tiene asociado un conjunto de intervalos .
 * Elemento basico del decorator.
 * @author Jose Luis Gomez, Eric Jaen, Jesus Serrano
 */
public class BasicTask extends Task implements Serializable{

  //Logger para mostrar mensajes.
  private static Logger logger = LoggerFactory.getLogger(BasicTask.class);  
  private ArrayList<Interval> intervalList = new ArrayList<Interval>();
  private Project pad = null;
  
  /**
   * Constructor.
   * @param name.
   * @param description.
   * @param pad.
   */
  public BasicTask(String name, String description, Project pad) {
    super(name,description,pad);
    assert (name != null) : "el name no puede ser nulo";
    assert (description != null) : "el descripcion no puede ser nulo";
    logger.debug(" Nueva tarea basica ");
    this.pad = pad;
    
  }  
 
  
  public Period getPeriodo() {
    return super.getPeriodo();
  }
    /**
     * Metodo que crea un intervalo y lo isnerta en la lista de intervalos asociada a la tarea.
     */
  public void start() {
    logger.info("Inicio tarea basica" + this.getId());
    //hay que comprovar que no haya ya un intervalo encendido
    this.currentInterval = new Interval(this);
    this.intervalList.add(currentInterval);
    this.currentInterval.start(); 
  }  
  
  /**
   * Actualiza el periodo dado un calendario.
   * @param dateIni.
   */
  public void updateIniTime(Calendar dateIni) {
    assert (dateIni != null) : "La fecha inicial no puede ser nula.";
    if (super.getPeriodo() == null) {
      logger.debug("Tarea que aun no tiene intervalo");
      super.setPeriodo(new Period());
      super.getPeriodo().setDateInit(Calendar.getInstance());
      super.getPeriodo().getDateInit().setTimeInMillis(dateIni.getTimeInMillis());
      super.getPeriodo().setDateEnd(Calendar.getInstance());
      super.getPeriodo().getDateEnd().setTime(new Date());
      if (this.pad != null) {
        this.pad.updateIniTime(dateIni);
      }
    }
  }

  /**
   * Metodo que para el intervalo.
   */
  public void stop() {
    logger.info("Final tarea basica");
    //this.interval.stop(); Comento esto porque ahora interval es un array list y no un objeto
    Clock aux = Clock.getInstance();
    if (this.currentInterval != null) {
      this.currentInterval.stop();
    }
    this.currentInterval = null;
  }        
  /**
   * Actualiza el periodo dado un DataClock.
   */
  public void update(DataClock date) {
    assert (date != null) : "parametro pasado al update es incorrecto";
    //LOG.debug("time updated for task "+this.id);
    int totalTime = 0;
    //recorrer lista de intervalos y obtener el tiempo total
    for (Interval i : intervalList) {
      totalTime += i.getPeriod().getTimeTotal();
    }
    super.getPeriodo().update(date);
    super.getPeriodo().setTimeTotal(totalTime);
    if (this.pad != null) {
      this.pad.update(date);
    }
  }   
  
  /**
   * Añade nuevo interval.
  */
  public void addInterval(Interval interval) {
    assert (interval != null) : "el intervalo no puede ser nulo";
    this.intervalList.add(interval);
    this.currentInterval = interval;
  }

  /**
   * @uml.property  name="currentInterval"
   */
  private Interval currentInterval;

/**
 * Getter of the property <tt>currentInterval</tt>
 * @return  Returns the currentInterval.
 * @uml.property  name="currentInterval"
 */
  public Interval getCurrentInterval() {
    return currentInterval;
  }

  /**
   * Setter of the property <tt>currentInterval</tt>
  * @param currentInterval  The currentInterval to set.
  * @uml.property  name="currentInterval"
  */
  public void setIntervalListl(Interval currentInterval) {
    this.currentInterval = currentInterval;
  }
  
  public ArrayList<Interval> getIntervalList() {
    return intervalList;
  }

  public void setIntervalList(ArrayList<Interval> intervalList) {
    this.intervalList = intervalList;
  }
}
