package timetracker;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;




/** 
 *  Clase abstracta que implementan Project y Task. Forma parte del patron de diseño
 *  composite,haciendo el rol de Component.
 *  Una actividad puede ser un projecto o una tarea
 *  @author
 */
public abstract class Activity implements java.io.Serializable {

  // Logger para mostrar mensajes, creacion del logger para utilizarlo en la clase.
  private static Logger logger = LoggerFactory.getLogger(Activity.class); 
  /** 
   * nombre de la tarea
   * @uml.property name="name"
   */
  private String name;
  private Period periodo;
  
  

  /** 
   * Getter of the property <tt>name</tt>
   * @return  Returns the name.
   * @uml.property  name="name"
   */
  public String getName()  {
    return name;
  }

  /** 
   * Setter of the property <tt>name</tt>
   * @param name  The name to set.
   * @uml.property  name="name"
   */
  public void setName(String name)  {
    assert ( name != null ) : "no puede cambiar el name por null";
    assert ( !name.isEmpty()) : "no puede cambiar el name por null";
    logger.debug("Nombre asignado " + name);
    this.name = name;
  
  }

  /** 
   * Descripcion de la tarea
   * @uml.property name="description"
   */
  private String description;

  /** 
   * Getter of the property <tt>description</tt>
   * @return  Returns the description.
   * @uml.property  name="description"
   */
  public String getDescription()  {
    return description;
  }

  /** 
   * Setter of the property <tt>description</tt>
   * @param description  The description to set.
   * @uml.property  name="description"
   */
  public void setDescription(String description)  {
    assert ( description != null ) : "no puede cambiar la descripcion por null";
    assert ( !description.isEmpty()) : "no puede cambiar la descripcion por null";
    this.description = description;
  }

  /** 
   * identificador unico de la tarea
   * @uml.property name="id" readOnly="true"
   */
  private int id;

  /** 
   * Getter of the property <tt>id</tt>
   * @return  Returns the id.
   * @uml.property  name="id"
   */
  public int getId()  {
    return id;
  }

    
  public Period getPeriodo()  {
    return this.periodo;
  }
  /**
   * Cambiar el periodo
   * @param ped peridodo
   * @throws IOException excepcion si es nulo.
   */
  public void setPeriodo(Period ped) {
    assert (ped != null) : "El periodo no puede ser nulo";
    this.periodo = ped;
  }
  /**
   * @uml.property  name="IsAProject"
   */
  private boolean isAProject;



  /**
   * Getter of the property <tt>IsAProject</tt>
   * @return  Returns the isAProject.
   * @uml.property  name="IsAProject"
   */
  public boolean isIsAProject() {
    return isAProject;
  }

  /**
   * Setter of the property <tt>IsAProject</tt>
   * @param IsAProject  The isAProject to set.
   * @uml.property  name="IsAProject"
   */
  public void setIsAProject(boolean isAProject) {
    this.isAProject = isAProject;
  }
  
  /** 
   * Metodo de visualizacion de los datos DataClock.
   * @param DataClock="data"
   * @throws IOException excepcion del valor. 
   */  
  public void update(DataClock data) { 
    assert (data != null) : "El parametro pasado al update es incorrecto";  
    if (parentActivity != null) { 
      this.parentActivity.update(data);
    }
    Calendar auxDate = Calendar.getInstance();
    auxDate.setTime(data.getActualDate());
  }
  
  
  /** 
   * constructora que le pasas el nombre, descripcion y el porjecto que depende.
   */
  public Activity(String vname, String vdescripcion, Project fatherProject, boolean project) {
    assert (vname != null) : "la fecha actual no puede ser nula";
    assert (!vname.isEmpty()) : "la fecha actual no puede ser nula";
    assert (vdescripcion != null) : "la fecha actual no puede ser nula";
    assert (!vdescripcion.isEmpty()) : "la fecha actual no puede ser nula";
    this.name = vname;
    this.description = vdescripcion;
    IdGenerator identifyUnique = IdGenerator.getInstance();
    this.id = identifyUnique.newId();
    this.isAProject = project;
    if (fatherProject != null) {
      logger.debug(" Actividad añadida " + this.name);
      fatherProject.add(this); 
      this.parentActivity = fatherProject; 
    }
    checkInvariante();
    
  }  
 /**
  * invariantes contructor.
  */
  private void checkInvariante() {
    assert (this.name != null) : "El nombre no puede ser nulo";
    assert (this.description != null) : "La descripcion no puede ser nula";
    assert (!this.name.isEmpty()) : "El nombre no puede ser vacia";
    assert (!this.description.isEmpty()) : "La descripcion no puede ser vacia";
    assert (this.id !=  0 ) : "Se ha generao un error interno el identificador es nulo";

  }
  
  
  
  /** 
   * Contructora le pasas el nombre y la descripcion para crear (ROOT) de projectos.
   */
  /*public Activity(String vName, String vDescription) {
    this.name = vName;
    this.description = vDescription;
    IDGenerator identifyUnique = IDGenerator.getInstance();
    this.id = identifyUnique.NewID();
    this.dateStart = new Date();
  }*/

      
  /**
   * agrega otro nivel a la actividad.
   */
  /*public void add(Activity vproject) {
    
  }*/

  /** 
   * @uml.property name="parentActivity"
   */
  private Activity parentActivity;
  
  public Activity getParentActivity() {
    return this.parentActivity;
  }
  
  public void setParentActivity(Activity par) {
    assert (par != null) : "La actividad no puede ser nulla";
    this.parentActivity = par;
  }
  
  /**
   * Funcion para printar el contenido de la actividad.
   */
  public String toString()  {
    String newLine = System.getProperty("line.separator");
    String message = "";

    message +=  name + "\t" + "\t";
    message +=  id + "\t" + "\t";
    if (this.periodo != null) {
      if (this.periodo.getDateEnd() != (null) && (this.periodo.getDateInit() != null)) {
        message += this.periodo.getDateInit().getTime() + "\t" + "\t";
        message += this.periodo.getDateEnd().getTime() + "\t" + "\t";
        message += this.periodo.getTimeTotal() + "\t" + "\t" + newLine;
      } else {
        message += "\t" + "\t" + "\t";
        message += "\t" + "\t" + "\t";
        message += "\t" + "\t" + "\t" + "\t" + this.periodo.getTimeTotal() + newLine;    
      }
    }
    return message;
  }
}
