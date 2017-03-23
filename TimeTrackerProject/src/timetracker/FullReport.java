package timetracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;


/**
 * Clase que realiza el informe completo, la estructura que tendra se monta a partir de
 * elementos de la clase Element en el constructor. Cada elemento se inserta en una lista
 * de manera ordenada que luego se recorrera para efectuar el visitor correspondiente
 * @author Jose Luis Gomez, Eric Jaen, Jesus Serrano
 *
 */
public class FullReport extends Report {  
  /**
  * Logger para mostrar mensajes.
  */
  private static Logger logger = LoggerFactory.getLogger(FullReport.class);

  /**
  * Realiza el informe completo de Proyectos, Tareas e intervales comprendidas
  * entre dos fechas.
  */
  public FullReport(Calendar dateEnd, Calendar dateStart, Project project) {
    super(dateEnd, dateStart, project);
    Element separatorOne = new Separator(); //separador
    reportElements.add(separatorOne);
  
    Element title = new Title("Informe Datallat"); //Titol
    reportElements.add(title);
  
    Element separatorTwo = new Separator();//separador
    reportElements.add(separatorTwo);
  
    Element subtitleOne = new Subtitle("Periode");  //subtitol
    reportElements.add(subtitleOne);
  
    //creacion valores de la tabla Periodo, Parte comun con Informe breve
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
   //Bucle para el contenido de cada una de las filas
    this.projectList = calculateProjects(project);
    for (int i = 1; i <= this.projectList.size() ; i++ ) {
      Project auxiliar = this.projectList.get(i - 1);
      long tempsTotal = 0;
      if (auxiliar.getPeriodo() != null) { //comprobamos que tenga periodo
        tempsTotal = intersection(auxiliar.getPeriodo()); //miramos si intersecta
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
  
  //seccion del fullReport
    //creamos las tablas para intervalos, tareas y subproyectos
    createTaulaSubprojectes();
    createTaulaTasques();
    createTaulaIntervals();
    calculateActivityLists("",project);
    Element subtitleThree = new Subtitle("Subprojectes");//Subtitul - Subprojectos
    reportElements.add(subtitleThree);
    Element descriptionOne = new Text("S'inclouen en la següent taula només els subprojectes" 
                        +  " que tinguin alguna tasca amb algun interval dins del període.");//text
    reportElements.add(descriptionOne);
    Element taulaSubprojectes = this.subProjectList;//Taula subproyectos
    reportElements.add(taulaSubprojectes);
    Element separatorFive = new Separator();//separador
    reportElements.add(separatorFive);
    Element subtitleFour = new Subtitle("Tasques");//Subtitul - Tareas
    reportElements.add(subtitleFour);
    Element descriptionTwo = new Text("S'inclouen en la següent taula la durada de totes" 
                                       + "les tasques i el projecte al qual pertanyen.");//text
    reportElements.add(descriptionTwo);
    Element taulaTasques = this.tasksList;//Taula tareas
    reportElements.add(taulaTasques);
    Element separatorSix = new Separator();//separador
    reportElements.add(separatorSix);
    Element subtitleFive = new Subtitle("Intervals");//Subtitul - Intervalos
    reportElements.add(subtitleFive);
    Element descriptionThree = new Text("S'inclouen en la seguent taula el temps d'inici, "
            + "final i durada de tots els intervals entre la data inicial i final especificades, "
            + "i la tasca i projecte al qual pertanyen.");//text
    reportElements.add(descriptionThree);
    Element taulaIntervals = this.intervalList;//Taula intervalos
    reportElements.add(taulaIntervals);
    Element separatorSeven = new Separator();//separador
    reportElements.add(separatorSeven);
    Element timetrackerinfo = new Text("Time Tracker v1.0");//text
    reportElements.add(timetrackerinfo);
    Element guardar = new Save("FullReport");
    reportElements.add(guardar);
    logger.info("Informe completo realizado");
    checkInvariante();
  }

  /**
   * Calcula las tablas infromativas que comprende el informe completo.
   * Esta son las tabla de subproyectos, tareas y actividades. Generando
   * su numeración correspondiente e indicativa de la relación entre
   * estas actividades.
   */
  public void calculateActivityLists(String counter,Project rootProject) {
    logger.info("Calculando subtablas para informe completo");
    assert ( counter != null ) : "FullReport: Valor Counter nulo";
    assert ( rootProject != null ) : "FullReport: proyectoRaiz nulo";
    ArrayList<Table> tableList = new ArrayList<Table>();
    //Array con la lista de actividades desconocida a recorrer y ver si hay tareas o projectos
    ArrayList<Activity> listActivities = new ArrayList<Activity>();
    Activity actualActivity = null;
    listActivities = rootProject.getActivities(); //recuperamos tareas del proyecto raiz
    for (int i = 1; i <= listActivities.size() ; i++ ) {
      counter += Integer.toString(i);
      actualActivity = listActivities.get(i - 1); 
        //cogemos la primera tarea
      if (actualActivity.getPeriodo() != null) { //miramos que el periodo no sea nulo
        long tempsTotal = intersection(actualActivity.getPeriodo()); 
        //comprobamos que intersecte la actividad
        if (tempsTotal != noIntersecta) {
          if (actualActivity.isIsAProject() == true) { //miramos si es un proyecto
            if (counter.length() > 1) { //para evitar los proyectos de la raiz
              //rellenamos valores de la tabla de subprojectos, lo hacemos dentro de este if
              //porque significara que no estamos en los projectos de la raiz.
              subProjectList.afegeixFila();
              int fila = subProjectList.getNfiles();
              subProjectList.setPosicio(fila, 1,counter);
              subProjectList.setPosicio(fila, 2,((Project)actualActivity).getName());
              subProjectList.setPosicio(fila, 3,dataFormat.format((
                   (Project)actualActivity).getPeriodo().getDateInit().getTime()));
              subProjectList.setPosicio(fila, 4,dataFormat.format((
                  (Project)actualActivity).getPeriodo().getDateEnd().getTime()));
              subProjectList.setPosicio(fila, 5,tempsTotalConversor(tempsTotal));
            }        
            calculateActivityLists(counter + ".",(Project)actualActivity);
              //lamamos recursivamente para recorrer el nuevo proyecto encontrado        
          } else if (((BasicTask)actualActivity).getIntervalList() != null) { 
          //miramos si es una tarea y tiene intervalos
            ArrayList<Interval> aux = new ArrayList<Interval>();
            aux = ((BasicTask)actualActivity).getIntervalList(); //lista de intervalos auxiliar
            for (int j = 1; j <= aux.size(); j++ ) {
              tempsTotal = intersection(aux.get(j - 1).getPeriod()); 
              //comprobamos que intersecte la actividad
              if (tempsTotal != noIntersecta) {
                //rellenamos valores para la tabla de intervalos
                intervalList.afegeixFila();
                int fila = intervalList.getNfiles();
                intervalList.setPosicio(fila, 1,counter.substring(0, counter.length() - 2));
                intervalList.setPosicio(fila, 2,((BasicTask)actualActivity).getName());
                intervalList.setPosicio(fila, 3,Integer.toString(j));
                intervalList.setPosicio(fila, 4,dataFormat.format(
                    aux.get(j - 1).getPeriod().getDateInit().getTime()));
                intervalList.setPosicio(fila, 5,dataFormat.format(
                    aux.get(j - 1).getPeriod().getDateEnd().getTime()));
                intervalList.setPosicio(fila, 6,tempsTotalConversor(tempsTotal));
              }
            }
            tempsTotal = intersection(((BasicTask)actualActivity).getPeriodo()); 
            //comprobamos que intersecte la actividad
            if (tempsTotal != noIntersecta) {
                //rellenamos tabla para las tareas
              tasksList.afegeixFila();
              int fila = tasksList.getNfiles();
              tasksList.setPosicio(fila, 1,counter.substring(0, counter.length() - 2));
              tasksList.setPosicio(fila, 2,((BasicTask)actualActivity).getName());
              tasksList.setPosicio(fila, 3,dataFormat.format((
                  (BasicTask)actualActivity).getPeriodo().getDateInit().getTime()));
              tasksList.setPosicio(fila, 4,dataFormat.format((
                   (BasicTask)actualActivity).getPeriodo().getDateEnd().getTime()));
              tasksList.setPosicio(fila, 5,tempsTotalConversor(tempsTotal));  
            }
          }
          //Si no entramos en los if es que es una tarea vacia y no hacemos nada
          counter = counter.substring(0, counter.length() - 1);
          //quitamos el ultimo indice añadido al counter
        }
      }
    }
    checkInvariante();
  }
  
  /**
   * Crea la tabla  de tareas de los subprojectos y la cabecera.
   */
  public void createTaulaSubprojectes() {
    this.subProjectList = new Table(1,5);
    //1a Fila
    this.subProjectList.setPosicio(1, 1,"No.");
    this.subProjectList.setPosicio(1, 2,"Projecte");
    this.subProjectList.setPosicio(1, 3,"Data inici");
    this.subProjectList.setPosicio(1, 4,"Data final");
    this.subProjectList.setPosicio(1, 5,"Temps Total");
  }
  
  /**
   * Crea la tabla de tareas pertinentes a los proyectos y la cabecera.
   */
  public void createTaulaTasques() {
    this.tasksList = new Table(1,5);
    //1a Fila
    this.tasksList.setPosicio(1, 1,"No.(sub) Projecte");
    this.tasksList.setPosicio(1, 2,"Tasca");
    this.tasksList.setPosicio(1, 3,"Data inici");
    this.tasksList.setPosicio(1, 4,"Data final");
    this.tasksList.setPosicio(1, 5,"Temps Total");
  }  
  
  /**
   * Crea la tabla de intervalos de las tareas y la cabecera.
   */
  public void createTaulaIntervals() {
    this.intervalList = new Table(1,6);
    //1a Fila
    this.intervalList.setPosicio(1, 1,"No.(sub) Projecte");
    this.intervalList.setPosicio(1, 2,"Tasca");
    this.intervalList.setPosicio(1, 3,"Interval");
    this.intervalList.setPosicio(1, 4,"Data inici");
    this.intervalList.setPosicio(1, 5,"Data final");
    this.intervalList.setPosicio(1, 6,"Durada");  
  }  
  /**
   * invariantes contructor.
   */
  private void checkInvariante() {
    assert (this.reportElements != null) : "lista de elementos no nula";
    assert (this.reportElements.size() >= 0) : "tamaño debe ser mayor o igual a 0 elementos";
    assert (this.interval != null) : "El intervalo no puede ser nulo";
  }
}
