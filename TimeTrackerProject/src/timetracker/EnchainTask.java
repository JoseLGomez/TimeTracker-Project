package timetracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que añade en el patron decorator una funcionalidad a tarea basica.
 * poder iniciar una tarea cuando esta finalice y asi encadenar
 * @author Jose Luis Gomez, Eric Jaen, Jesus Serrano
 */
public class EnchainTask extends DecoratorTask {

/**
 * Logger to print messages.
 */
  private static final Logger logger = LoggerFactory.getLogger(EnchainTask.class);

  private BasicTask nextTask;
  
/**
 * Contructor de la clase.
 */
  public EnchainTask(final String name, final String description, Project pad,
                     BasicTask paramBasicTask,BasicTask paramNextTask) {
    super(name,description,pad);
    logger.debug(" Nueva tarea encadenada ");
    basicTask = paramBasicTask; //tarea basica declarada en el decorator
    this.nextTask = paramNextTask;
    logger.info("Tarea creada " + getId());
    super.setParent(pad);
    checkInvariante();
  }
  
  /**
   * Revisa que las propiedades del la clase este estalbecidas.
   */
  private void checkInvariante() {
    assert (this.basicTask != null) : "la tarea bascia no peude ser nula";
  }
  
  /**
   *Metodo que inicia la tarea.
   */
  public void start() {
    logger.info(" Iniciar tarea " + this.getId());
    basicTask.start();
  }
  
  /**
   *Metodo que finaliza una tarea.
   */
  public void stop() {
    logger.debug("Task Stopped " + getId() );
    basicTask.stop();
    nextTask.start();
  }
  
  /**Actualizacion de datos a los padres
   * @param dateFin  The date of ending.
   */
  @Override
  public void update(DataClock updateDate) {
    assert (updateDate != null) : "no puedes actualizar una fecha nula";
    logger.debug("Tiempo actualizado para la tarea " + getId());
    super.getParent().update(updateDate);
  }
}
