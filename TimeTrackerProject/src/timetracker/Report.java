package timetracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;





/**
 * Clase abstracta Report, se usa como base para los informes que se realizan sobre el timetracker
 * contiene metodos globales y estructura de datos de los que hacen uso los hijos que heredan que 
 * son infromes con una estructura concreta. Ademas hace de cliente en el patron Visitor
 * @author Jose Luis Gomez, Eric Jaen, Jesus Serrano
 *
 */
public abstract class Report {
  /**
  * Logger para mostrar mensajes.
  */
  private static Logger logger = LoggerFactory.getLogger(Report.class);

  /**
   * @uml.property  name="element"
   * @uml.associationEnd  multiplicity="(0 -1)" aggregation="composite"
   *                      inverse="report:timetracker.Element"
   */
  private Collection element;
  protected Period interval;
  private Project projectReported;
  //SimpleDateFormat: ajusta el formato de hora para los objetos Calendar
  protected SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy', 'HH:mm'h'");
  final int noIntersecta = 0;
  
  /**
   * Inicializacion de las diferentes listas de elementos usadas en los informes.
   * Se declaran como protected para que las usen los hijos.
   */
  protected ArrayList<Activity> subActivities = new ArrayList<Activity>();
  protected ArrayList<Element> reportElements = new ArrayList<Element>();
  protected ArrayList<Project> projectList = new ArrayList<Project>();
  protected Table subProjectList;
  protected Table tasksList;
  protected Table intervalList;
  
  /**
   * Getter of the property <tt>element</tt>
   * @return  Returns the element.
   * @uml.property  name="element"
   */
  public Collection getElement() {
    return element;
  }

  /**
   * Setter of the property <tt>element</tt>
   * @param element  The element to set.
   * @uml.property  name="element"
   */
  public void setElement(Collection element) {
    assert ( element != null ) : "Report: elemento nulo";
    this.element = element;
  }

/**
 * Contructor abstracto de la clase. Todos los hijos llaman a este constructor base
 * que inicializa los calculos para los reports.
 * @param dateStart : Fecha de inicio de intervalo.
 * @param dateEnd : fecha fin del intervalo.
 * @param project : proyecto raiz desde el cual partir para analizar.
 */
  public Report(Calendar dateStart, Calendar dateEnd, Project project) {
    assert ( dateStart != null ) : "En un report la fecha inicial no puede ser nula";
    assert ( dateEnd != null ) : "En un report la fecha final no puede ser nula";
    assert ( project != null ) : "En un report el report no puede ser nula";
    assert (dateStart.getTimeInMillis() < dateEnd.getTimeInMillis()) :
          "Data final no puede ser menor a data inicial"; 

    this.interval = new Period();
    this.interval.setDateInit(dateStart);
    this.interval.setDateEnd(dateEnd);
    this.projectReported = project;
  }
    
    /**
     * Calcula el tiempo comprendido entre dos fechas.
     */
  public long calculateLapse(Calendar ini, Calendar end) {
    //precondiciones
    assert (ini != null);
    assert (end != null);
      
    long dateIni = ini.getTimeInMillis();
    long dateEnd = end.getTimeInMillis();
    long lapse = dateEnd - dateIni;
     
    //post condicion
    assert (lapse >= 0);      
    return lapse;
  }
    
    /**
     * Calcula la interseccion entre el periodo del report y el periodo de una actividad.
     * Retornando si interescta el tiempo comprendido.
     */
  public long intersection( Period pedl ) {
      
      //precondiciones
      assert (pedl != null) : "Report: que el periodo entrante no sea null";
      
    Calendar intersectIni = null;
    Calendar intersectEnd = null;
      
    //Calculo de las diferentes posibilidades que se pueden dar en la interseccion
    if (pedl.getDateEnd().getTimeInMillis() < this.interval.getDateInit().getTimeInMillis() 
          || pedl.getDateInit().getTimeInMillis() > this.interval.getDateEnd().getTimeInMillis()) {
      //caso fuera sin interseccion tanto por un lado como el otro
      return 0;
    } else if (pedl.getDateEnd().getTimeInMillis() 
                == this.interval.getDateInit().getTimeInMillis()) {
        // Caso limite, coincide final con el principio
      intersectIni = pedl.getDateEnd();
      intersectEnd = pedl.getDateEnd();
      //post condicion
      assert (intersectIni.getTime() == intersectEnd.getTime());
      //miramos que efectivamente ambas fechas son las mismas
        
    } else if (pedl.getDateInit().getTimeInMillis() 
           == this.interval.getDateEnd().getTimeInMillis()) {
      // Caso limite, coincide principio con el final
      intersectIni = pedl.getDateInit();
      intersectEnd = pedl.getDateInit();
      //post condicion
      assert (intersectIni.getTime() == intersectEnd.getTime());
      //miramos que efectivamente ambas fechas son las mismas
        
    } else if (pedl.getDateInit().getTimeInMillis() 
        >= this.interval.getDateInit().getTimeInMillis() 
        && pedl.getDateEnd().getTimeInMillis() <= this.interval.getDateEnd().getTimeInMillis()) {
        //Caso interseccion total interna de Tl
      intersectIni = pedl.getDateInit();
      intersectEnd = pedl.getDateEnd();
      //post condicion
      assert (intersectIni.getTimeInMillis() < intersectEnd.getTimeInMillis()); 
      //comprovamos que se mantiene la coherencia entre fechas
              
    } else if (pedl.getDateInit().getTimeInMillis() < this.interval.getDateInit().getTimeInMillis() 
            && pedl.getDateEnd().getTimeInMillis() > this.interval.getDateEnd().getTimeInMillis()) {
        //Caso interseccion total interna del objeto actual con Tl
      intersectIni = this.interval.getDateInit();
      intersectEnd = this.interval.getDateEnd();
      //post condicion
      assert (intersectIni.getTimeInMillis() < intersectEnd.getTimeInMillis()); 
      //comprovamos que se mantiene la coherencia entre fechas
        
    } else if (pedl.getDateInit().getTimeInMillis() < this.interval.getDateInit().getTimeInMillis() 
           && pedl.getDateEnd().getTimeInMillis() < this.interval.getDateEnd().getTimeInMillis()) {
        //Caso interseccion parcial por la izquierda
      intersectIni = this.interval.getDateInit();
      intersectEnd = pedl.getDateEnd();
      //post condicion
      assert (intersectIni.getTimeInMillis() < intersectEnd.getTimeInMillis()); 
      //comprovamos que se mantiene la coherencia entre fechas
        
    } else if (pedl.getDateInit().getTimeInMillis() > this.interval.getDateInit().getTimeInMillis() 
           && pedl.getDateEnd().getTimeInMillis() > this.interval.getDateEnd().getTimeInMillis()) {
        //Caso interseccion parcial por la derecha
      intersectIni = pedl.getDateInit();
      intersectEnd = this.interval.getDateEnd();
      //post condicion
      assert (intersectIni.getTimeInMillis() < intersectEnd.getTimeInMillis()); 
      //comprovamos que se mantiene la coherencia entre fechas
    }

    long result = calculateLapse(intersectIni, intersectEnd);
    //post condicion
    assert (result >= 0); 
    //comprovamos que se mantiene la coherencia y duracion es mayor o igual a 0
    return result;
  }
 /**
   * Conversor de milisegundos a formato XXh XXm XXs.
  */
  public String tempsTotalConversor(long temps) {
    assert ( temps >= 0 ) : "Report: El tiempo no puede ser 0 o mas pequeño";
    temps = temps / 1000;
    long seconds = temps % 60;
    long minutes = (temps / 60) % 60;
    long hours = temps / 3600;
    String result = Long.toString(hours) + "h " + Long.toString(minutes) + "m "
                    + Long.toString(seconds) + "s";
    return result;
  }
  /**
  * Calculos para hacer el informe breve, calcula las intersecciones para el proyecto
  * a analizar, recorre las subactividades que contiene el proyecto facilitado y retorna
  * una lista de estos, excluyendo las actividades. Esta funcion se coloca aquí porque 
  * la utilizan tanto el informe breve como el completo.
  * @param rootProject projecto padre.
  */
  public ArrayList<Project> calculateProjects(Project rootProject) {
    logger.info("Calculando proyectos del root");
    assert ( rootProject != null ) : "Report: proyecto Raiz nulo";
    Activity actualActivity = null;
    ArrayList<Project> listaProyectos = new ArrayList<Project>();
    subActivities = rootProject.getActivities(); //cogemos las actividades del proyecto a analizar
    for (int i = 0; i < subActivities.size() ; i++ ) {
      actualActivity = subActivities.get(i);
      if (actualActivity.isIsAProject() == true) {
        //insertamos en la lista las actividades que sean proyectos
        listaProyectos.add((Project)actualActivity); 
      }
    }
    return listaProyectos;
  }
      
 /** 
   * Getter of the property <tt>subActivities</tt>
   * @return  Returns the subActivities.
   * @uml.property  name="subActivities"
   */
  public ArrayList<Activity> getSubActivities() {
    return subActivities;
  }

 /** 
  * Getter of the property <tt>ReportElements</tt>
  * @return  Returns the reportElements.
  * @uml.property  name="ReportElements"
 */
  public ArrayList<Element> getReportElements() {
    return reportElements;
  }
  
/**
 * añadir elemento a la lista reportElement.
 */
  public void add(Element element) {
   assert (element != null);
    this.reportElements.add(element);
  }
  
 /**
  * Añadir proyecto a la lista projectList.
  */
  public void add(Project project) {
    assert (project != null);
    this.projectList.add(project);
  }

    /** 
     * Getter of the property <tt>ProjectList</tt>
     * @return  Returns the projectList.
     * @uml.property  name="ProjectList"
     */
  public ArrayList<Project> getProjectList() {
    return projectList;
  }


  /** 
   * Setter of the property <tt>subActivities</tt>
   * @param subActivities  The subActivities to set.
   * @uml.property  name="subActivities"
   */
  public void setSubActivities(ArrayList<Activity> subActivities) {
    this.subActivities = subActivities;
  }
  /** 
   * Setter of the property <tt>ReportElements</tt>
   * @param ReportElements  The reportElements to set.
   * @uml.property  name="ReportElements"
   */
  public void setReportElements(ArrayList<Element> reportElements) {
    this.reportElements = reportElements;
  }
  /** 
   * Setter of the property <tt>ProjectList</tt>
   * @param ProjectList  The projectList to set.
   * @uml.property  name="ProjectList"
   */
  public void setProjectList(ArrayList<Project> projectList) {
    this.projectList = projectList;
  }
  
  /** 
   * Getter of the property <tt>ProjectList</tt>
   * @return  Returns the projectList.
   * @uml.property  name="ProjectList"
   */
  public ArrayList<Project> getSubProjectList() {
    return projectList;
  }
  /** 
   * Setter of the property <tt>subActivities</tt>
   * @param subActivities  The subActivities to set.
   * @uml.property  name="subActivities"
   */
  public void setSubProjectList(ArrayList<Project> subProjects) {
    this.projectList = subProjects;
  }
   
  
  
  /**
   * Funcion que recorre la lista de elementos y realiza el visitor haciendo interactuar
   * los elementos con su visitor facilitado que es el formato.
   */
  public void calculateReport(Format formato) {
    assert ( formato != null ) : "se esta pasando un valor nulo";
    for (Element element: reportElements) {
      element.accept(formato);
    }
    
  }
}
