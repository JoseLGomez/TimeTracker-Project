package timetracker;

import java.io.Serializable;

/**
 * Clase que crea una tarea. Corresponde al rol de Leaf del patron
 * @author
 * @uml.dependency   supplier="TimeTracker.Observer"
 */
public abstract class Task extends Activity implements Serializable {

  public Task(String vname, String vdescription,Project pad) {
    super(vname, vdescription,pad,false);
  }
}
