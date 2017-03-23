package timetracker;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;



/** 
 * Clase abstracta que implementa el rol de decorator del patron y que permitirá seleccionar.
 * que opciones adicionales se le añadiran a la tarea
 * @author Jose Luis Gomez, Eric Jaen, Jesus Serrano
 */
public abstract class DecoratorTask extends Task implements Serializable{
    /**
     * Logger para mostrar mensajes.
     */
  private static final Logger logger = LoggerFactory.getLogger(DecoratorTask.class);
/**
* @uml.property   name="task"
*@uml.associationEnd multiplicity="(1 1)" aggregation="composite"
*                    inverse="decoratorTask:timetracker.Task"
*/
  
  protected BasicTask basicTask;
  private Project parent;
  /**
   *  contructora  
   * @param name nombre del decorator.
   * @param description descripcion del decorator.
   * @param pad proyecto al que esta unido.
   */
  public DecoratorTask(String name, String description, Project pad) {
    super(name,description,pad);
    assert (name != null) : "la fecha actual no puede ser nula";
    assert (description != null) : "la fecha actual no puede ser nula";
  }
  
  public Project getParent() {
    return this.parent;
  }
  
  public void setParent(Project par) {
    assert (par != null) : "no puedes modificar un projecto por uno nulo";
    this.parent = par;
  }

  /**
   * Getter of the property <tt>task</tt>
   * @return  Returns the task.
   * @uml.property  name="task"
   */
  public Task getTask() {
    return this.basicTask;
  }

  /**
   * Setter of the property <tt>task</tt>
   * @param task  The task to set.
   * @uml.property  name="task"
   */
  public void setTask(BasicTask task) {
    assert (task != null) : "no puede modificar una Task por una nula";
    this.basicTask = task;
  }

}
