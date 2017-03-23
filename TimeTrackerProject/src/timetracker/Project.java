
package timetracker;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;



/** 
 * Clase que crea un proyecto. Realiza el rol de Composite en el patron.
 * @author Jose Master
 */
public class Project extends Activity implements Serializable {

  /**
  * Logger para mostrar mensajes.
  */
  private static Logger logger = LoggerFactory.getLogger(Project.class);
  private Project parent;
  /**
   * creacion de un projecto nuevo indico sus caracteristicas.
   */
  public Project(String vname, String vdescripcion, Project pad) {
    super(vname,vdescripcion,pad,true);
    this.activities = new ArrayList<Activity>();
    this.parent = pad;
  }
  /**
   * inicializa la fecha inicial.
   */
  public void updateIniTime(Calendar dateIni) {
    assert (dateIni != null) : "la fecha inicial no puede ser nula";
    if (super.getPeriodo() == null) {
      super.setPeriodo(new Period());
      super.getPeriodo().setDateInit(Calendar.getInstance());
      super.getPeriodo().getDateInit().setTimeInMillis(dateIni.getTimeInMillis());
      super.getPeriodo().setDateEnd(Calendar.getInstance());
      super.getPeriodo().getDateEnd().setTime(new Date());
      if (this.parent != null) {
        this.parent.updateIniTime(dateIni);
      }
    }
  }
  /**
 * @uml.property   name="activity"
 * @uml.associationEnd   multiplicity="(0 -1)" aggregation="composite"
 *                                             inverse="project:timetracker.Activity"
 */
  private ArrayList<Activity> activities;

  /** 
   * Getter of the property <tt>activity</tt>
   * @return  Returns the activity.
   * @uml.property  name="activity"
   */
  public ArrayList<Activity> getActivities() {
    return activities;
  }

  /** 
   * Setter of the property <tt>activity</tt>
   * @param activities  The activity to set.
   * @uml.property  name="activity"
   */
//  public void setActivities(ArrayList<Activity> activities) {
//    assert (elementTask != null) : "la fecha actual no puede ser nula";
//    this.activities = activities;
//  }

    
  /**
  * metodo para pasar la informacion a los proyectos padres.
  */
  public void update(DataClock dc) {
    assert (dc != null) : "no puedes actualizar un valor nulo";
    //logger.debug(" Actualizando proyectos ");
    double total = 0;
    for (int i = 0; i < this.activities.size(); i++) {
      if (this.activities.get(i).getPeriodo() != null) {
        total += this.activities.get(i).getPeriodo().getTimeTotal();
        //System.out.println(this.activities.get(i).toString());
      }
    }
    super.getPeriodo().setTimeTotal(total);
    super.getPeriodo().update(dc);
    if (super.getParentActivity() != null) {
      super.getParentActivity().update(dc);
    } else {
      //System.out.println(this.toString());
    }
  }

  public Period getPeriodo() {
    return super.getPeriodo();
  }
    

  public void add(Activity activity) {
    assert (activity != null);
    this.activities.add(activity);
  }
  /**
   * @uml.property   name="informe"
   * @uml.associationEnd   multiplicity="(0 -1)" aggregation="composite"
   *                                             inverse="project:timetracker.Report"
   */
  private Collection informe;
  
  /** 
   * Getter of the property <tt>informe</tt>
   * @return  Returns the informe.
   * @uml.property  name="informe"
  */
  public Collection getInforme() {
    return informe;
  }
  
  /** 
   * Setter of the property <tt>informe</tt>
   * @param informe  The informe to set.
   * @uml.property  name="informe"
   */
  public void setInforme(Collection informe) {
    assert (informe != null) : "no puedes crear un informe nulo";
    this.informe = informe;
  }
}
