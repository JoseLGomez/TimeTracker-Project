package timetracker;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;



/**
 * Clase que añade en el patron decorator una funcionalidad a tarea basica.
 * poder programar el inicio de la tarea a una hora determinada
 * @author Jose Luis Gomez, Eric Jaen, Jesus Serrano
 */
public class PreprogramTask extends DecoratorTask implements Observer {

  /**
  * Logger para mostrar mensajes.
  */
  private static final Logger logger = LoggerFactory.getLogger(PreprogramTask.class);
      
  private Calendar iniTime;
  
  /**
  * Constructor.
  */      
  public PreprogramTask(final String name, final String description, Project pad,
                        BasicTask paramBasicTask,Calendar iniTimeParam) {
    super(name,description,pad);
    logger.debug(" Nueva tarea preprogramada ");      
    basicTask = paramBasicTask;
    iniTime = iniTimeParam;
    Clock.getInstance().addObserver(this); 
          
    logger.info(" Created task " + getId());
    checkInvariante();
  }

  private void checkInvariante() {
    assert (this.basicTask != null) : "no puede se nulo el parametro backTask";
    assert (this.iniTime != null) : "el parametro no puede ser nulo del iniciar nueva hora"; 
    assert (this.iniTime.compareTo(this.basicTask.getPeriodo().getDateInit()) >= 0) :
            "la fecha actual no puede ser nula";
  }
  
  public void start() {
    basicTask.start();
  }

    
  public void stop() {
    basicTask.stop();      
  }

  /**Metodo de actualizacion del tiempo de los padres
  * @param updateDate  The date of ending.
  */
  @Override
  public void update(DataClock updateDate) {
    assert (updateDate != null) : "El valor updateDate pasado no puede ser nulo";
    logger.debug("time updated for task " + getId());
    this.basicTask.getParentActivity().update(updateDate);
  }
      
  /**
   * Actualizacion del estado del reloj.
   */
  public void update(Observable org, Object arg) {
    assert (arg != null) : "el objecto DataClock no puede ser nulo";
    DataClock dateAux = (DataClock)arg;
    assert (dateAux != null) : "error al instnaciar el objecto update";
    Calendar currTime = Calendar.getInstance();
    assert (currTime != null) : "error a instanciar el calendario";
    currTime.setTime(dateAux.getActualDate());
    String newLine = System.getProperty("line.separator");
    String message = "";
    message += "iniTime:" + iniTime.getTime() + newLine;
    message += "currTime:" + currTime.getTime() + newLine;
    System.out.println(message);
    if (iniTime.before(currTime)) {
      start();
      Clock.getInstance().deleteObserver(this); 
    }
  }

}
