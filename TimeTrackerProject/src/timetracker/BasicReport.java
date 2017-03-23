package timetracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;



/**
 * Clase que realiza el informe breve, la estructura que tendra se monta a partir de
 * elementos de la clase Element en el constructor. Cada elemento se inserta en una lista
 * de manera ordenada que luego se recorrera para efectuar el visitor correspondiente
 * @author Jose Luis Gomez, Eric Jaen, Jesus Serrano
 *
 */
public class BasicReport extends Report {

  /**
  * Logger para mostrar mensajes.
  */
  private static Logger logger = LoggerFactory.getLogger(BasicReport.class);
  
  /**
   * Contructor para un informe basico, crea los elementos necesarios para la estructura
   * del informe breve y los inserta en una lista de manera ordenada para su posterior 
   * recorrido y así efectuar el visitor.
   * @param dateEnd fecha final.
   * @param dateStart fecha inicial.
   */
  public BasicReport(Calendar dateEnd, Calendar dateStart, Project project) {
    super(dateEnd, dateStart, project);
    Element separatorOne = new Separator(); //separador
    reportElements.add(separatorOne);
  
    Element title = new Title("Informe Breu"); //Titol
    reportElements.add(title);
  
    Element separatorTwo = new Separator();//separador
    reportElements.add(separatorTwo);
  
    Element subtitleOne = new Subtitle("Periode");//subtitol
    reportElements.add(subtitleOne);
  
  //creacion valores de la tabla Periodo
    Table taulaPeriode = new Table(4,2);//Taula de Periode
  //fila 1
    taulaPeriode.setPosicio(1, 1,"  ");
    taulaPeriode.setPosicio(1, 2,"Data");
    //Fila 2
    taulaPeriode.setPosicio(2, 1,"Desde");
    taulaPeriode.setPosicio(2, 2, dataFormat.format(
                            project.getPeriodo().getDateInit().getTime().getTime()));
    //Fila 3
    taulaPeriode.setPosicio(3, 1,"Fins a");
    taulaPeriode.setPosicio(3, 2, dataFormat.format(project.getPeriodo().getDateEnd().getTime()));
    //Fila 4
    taulaPeriode.setPosicio(4, 1,"Data generació del informe");
    taulaPeriode.setPosicio(4, 2, dataFormat.format(Calendar.getInstance().getTime()));
    reportElements.add(taulaPeriode);
  
    Element separatorThree = new Separator();//separador
    reportElements.add(separatorThree);
  
    Element subtitleTwo = new Subtitle("Projectes Arrel");//subtitol
    reportElements.add(subtitleTwo);
  
  //creacion valores de la tabla Projectes Arrel
    Table taulaProjectes = new Table(1,5);//taula de projectes
  //1a Fila
    taulaProjectes.setPosicio(1, 1,"No.");
    taulaProjectes.setPosicio(1, 2,"Projecte");
    taulaProjectes.setPosicio(1, 3,"Data inici");
    taulaProjectes.setPosicio(1, 4,"Data final");
    taulaProjectes.setPosicio(1, 5,"Temps Total");
    //Bucle de filas
    this.projectList = calculateProjects(project);
    for (int i = 1; i <= this.projectList.size() ; i++ ) {
      Project auxiliar = this.projectList.get(i - 1);
      long tempsTotal = 0;
      if (auxiliar.getPeriodo() != null) {
        tempsTotal = intersection(auxiliar.getPeriodo());
      }
      if (tempsTotal != noIntersecta) {
        taulaProjectes.afegeixFila();
        taulaProjectes.setPosicio(i + 1, 1,Integer.toString(i));
        taulaProjectes.setPosicio(i + 1, 2,auxiliar.getName());
        taulaProjectes.setPosicio(i + 1, 3,dataFormat.format(
                                  auxiliar.getPeriodo().getDateInit().getTime()));
        taulaProjectes.setPosicio(i + 1, 4,dataFormat.format(
                                  auxiliar.getPeriodo().getDateEnd().getTime()));
        taulaProjectes.setPosicio(i + 1, 5,tempsTotalConversor(tempsTotal));
      }
    }   
    reportElements.add(taulaProjectes);
  
    Element separatorFour = new Separator(); //sepatador
    reportElements.add(separatorFour);
  
    Element timetrackerinfo = new Text("Time Tracker v1.0");//text
    reportElements.add(timetrackerinfo);
    
    Element guardar = new Save("BasicReport");
    reportElements.add(guardar);
    logger.info("Informe breve realizado");
    checkInvariante();
  }
  
  private void checkInvariante() {
    assert (this.reportElements != null) : "lista de elementos no nula";
    assert (this.reportElements.size() >= 0) : "tamaño debe ser mayor o igual a 0 elementos";
    assert (this.interval != null) : "El intervalo no puede ser nulo";
  }  
}
