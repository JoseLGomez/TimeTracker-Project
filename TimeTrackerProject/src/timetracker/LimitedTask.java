package timetracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Clase que añade otra opcion de decorador, en este caso la opcion de lmitar la duracion de.
 * una tarea automaticamente.
 * @author Jose Luis Gomez, Eric Jaen Marco, Jesus Serrano
 */
public class LimitedTask extends DecoratorTask {

  /**
  * Logger para mostrar mensajes.
  */
  private static final Logger logger = LoggerFactory.getLogger(PreprogramTask.class);
  
  /**
  * Tiempo en el que parara la tarea.
  */
  private int maxTimeTask;

  /**
   * indica si se ha excedido el tiempo de duracion maximo definido.
   */
  private boolean reachedMaxTime;
    
    
  /**
  * @uml.property  name="period"
  */
  private Period period = new Period();
  
  /**
   * Contructora de la clase LimitedTask, genera una tarea de tipo limitada.
   */
  public LimitedTask(final String name, final String description, Project pad,
                     BasicTask paramBasicTask,int maxTimeParam) {
    super(name,description,pad);
    logger.debug(" Nueva tarea limitada ");
    basicTask = paramBasicTask;
    this.maxTimeTask = maxTimeParam;
    this.reachedMaxTime = false;
    logger.info(" Tarea creada " + getId());
    super.setParent(pad);
    checkInvariant();
  }  
  
  private void checkInvariant() {
    assert (this.maxTimeTask > 0) : "el tiempo maximo no puede ser 0 o inferior";
    assert (this.basicTask != null) : "no se ha introducido una basic Task";
  }

  public void start() {
    basicTask.start();    
  }

    
  public void stop() {
    basicTask.stop();
  }

  /**
   * @param updateDate  The date of ending.
   */
  @Override
  public final void update(final DataClock updateDate) {
    assert (updateDate != null) : "el vlaor del update no puede ser nulo";
    logger.debug("Tiempo actualizado de la tarea " + getId());
    period.setTimeTotal(
        period.getTimeTotal() + updateDate.getTick()
    );
    if ((period.getTimeTotal() >= maxTimeTask) && !(reachedMaxTime)) {
      reachedMaxTime = true;
      basicTask.stop();            
    }
    super.getParent().update(updateDate);
  }
}
