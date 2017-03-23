package timetracker;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;



//import BasicTask;
//import Project;


/** 
 * Clase Principal de la aplicación donde se realizan las pruebas del TimeTracker.
 */
public class Client {

    /**
     * Logger para mostrar mensajes.
     */
  private static Logger logger = LoggerFactory.getLogger(Client.class);
  
  /**
   * Main.
   */
  public static void main(String[] args) throws InterruptedException {
    //testA();  //guarda lso ficheros en c:\
    //testLoad();
    //testChain();
    //testLimited();
    //testPre();
    //testA2();
    //testA3();

    // Realizan los infromes tanto breve como detallado en html y TXT
    //testBasicReportText();  //realiza lo mismo que testAvancedReportText
    testAvancedReportText();  //realiza lo mismo que testBasicReportText
    
  }
  
  private static void testLoad() {
    SerializeGenerator serializer = new SerializeGenerator();
    Clock clk ;
    Project p0;
    p0 = (Project)serializer.loadObjects("C:\\project.dat");
    clk = (Clock)serializer.loadObjects("C:\\clock.dat");
    System.out.print(p0.toString());    
  }
  
  /**
   * testeo por tiempo.
   */
  public static void testLimited() throws InterruptedException {
    logger.info("Start testLimited");
    //Variable declaration
    int time = 2000;

    Clock clk = Clock.getInstance(time);
    Project p0;
    Project p1;
    Project p2;


    BasicTask t1;
    //Project initialization
    p0 = new Project("root","rootDescription",null);
    p1 = new Project("Proj1","Project1 description",p0);

    //Task initialization
    t1 = new BasicTask("Task1", "Task1 description",p1);
    BasicTask t2;
    t2 = new BasicTask("Task2", "Task2 description",p1);
    int limitTime = 4000;
    LimitedTask t3;
    t3 = new LimitedTask("DecLim","Decorator limited",p1,t1,limitTime);
    t1.setParentActivity(t3);
    
    //Print the project tree
    System.out.println(p1.getActivities());
    System.out.println("--------"
        + "-----------------------------------------------------------" 
        + "---------------------------------------" 
        + "-------------------------" 
        + "-----------------------------------------");

    t3.start();
    
    Thread.sleep(10000);
    
    t3.stop();
    
    //8
    t2.start();
    Thread.sleep(4000);            
    t2.stop();
    

    //Print the project tree
    System.out.println("--------" 
        + "-----------------------------------------------------------" 
        + "---------------------------------------" 
        + "-------------------------" 
        + "-----------------------------------------");
    System.out.println(p0.toString());
    logger.info("Stop testLimited");
  }

  /**
   * testeo pro Chain.
   */
  public static void testChain() throws InterruptedException {

    logger.info("Test de cadena");
  //Variable declaration
    int time = 500;
    Clock clk = Clock.getInstance(time);
    Project p0;
    Project p1;
    Project p2;

    BasicTask t1;  
    //Project initialization
    p0 = new Project("root","rootDescription",null);
    p1 = new Project("Proj1","Project1 description",p0);

    // Task initialization
    t1 = new BasicTask("Task1", "Task1 description",p1);
    BasicTask t2;
    t2 = new BasicTask("Task2", "Task2 description",p1);
    EnchainTask t3;
    t3 = new EnchainTask("DecChain","Decorator chain",p1,t1,t2);
    t1.setParentActivity(t3);
  
    //Print the project tree
    System.out.println(p1.getActivities());
    System.out.println("--------" 
        + "-----------------------------------------------------------" 
        + "---------------------------------------" 
        + "-------------------------" 
        + "-----------------------------------------");
    t3.start();
  
    Thread.sleep(2000);
  
    t3.stop();
  
    //8
    Thread.sleep(4000);            
    t2.stop();
  

  //Print the project tree
    System.out.println("--------" 
         + "-----------------------------------------------------------" 
         + "---------------------------------------" 
         + "-------------------------" 
         + "-----------------------------------------");
    System.out.println(p0.toString());
    logger.info("-- Fin Test de cadena --");
  }
  
/**
 * test Pre.
 */
  public static void testPre() throws InterruptedException {
    logger.info("Inicio test tarea preprogramada");
   //Variable declaration
    int time = 500;
    Calendar iniTime = Calendar.getInstance();
    Calendar iniTimeAux = Calendar.getInstance();
    Clock clk = Clock.getInstance(time);

    Project p2;

    iniTime.setTime(new Date());
    iniTimeAux.setTime(new Date());
  
    iniTime.set(Calendar.HOUR_OF_DAY, iniTime.get(Calendar.HOUR_OF_DAY));
    iniTime.set(Calendar.MINUTE, iniTime.get(Calendar.MINUTE)); 
    iniTime.set(Calendar.SECOND, iniTime.get(Calendar.SECOND) + 10);
    iniTime.set(Calendar.MILLISECOND, iniTime.get(Calendar.MILLISECOND));
  
    //iniTime.setTimeInMillis((iniTimeAux.getTimeInMillis()));
    //Project initialization
    Project p0;
    p0 = new Project("root","rootDescription",null);
    Project p1;
    p1 = new Project("Proj1","Project1 description",p0);

    PreprogramTask t3;
    BasicTask t2;
    BasicTask t1;
  //Task initialization
    t1 = new BasicTask("Task1", "Task1 description",p1);
    t2 = new BasicTask("Task2", "Task2 description",p1);
    t3 = new PreprogramTask("DecProg","Decorator programmed",p1,t1,iniTime);
    t1.setParentActivity(t3);
      
    //Print the project tree
    System.out.println(p1.getActivities());
    System.out.println("--------" 
        + "-----------------------------------------------------------" 
        + "---------------------------------------" 
        + "-------------------------" 
        + "-----------------------------------------");
  
     /* t3.start();
      
      Thread.sleep(10000);
      
      t3.stop();*/
      
      //8
    t2.start();
    Thread.sleep(4000);            
    t2.stop();

    Thread.sleep(10000);        
    t3.stop();

  //Print the project tree
    System.out.println("--------" 
        + "-----------------------------------------------------------" 
        + "---------------------------------------" 
        + "-------------------------" 
        + "-----------------------------------------");
    System.out.println(p0.toString());
    logger.info("Fin del test");
  }

  /**
   * Teste A.
   */
  public static void testA() throws InterruptedException {
    logger.debug(" Inicio test A ");
    //Inicializacion de la clase para serializar y guardar o recuperar
    final SerializeGenerator serializer = new SerializeGenerator();
    String fileName = "Activity.dat"; //Ruta del archivo para la serializacion
      //Inicializacion de proyectos
    Project p0 = new Project("root","rootDescription",null);
    Project p1 = new Project("Proj1","Project1 description",p0);
    Project p2 = new Project("Proj2","Project2 description",p1);
   //Inicializacion de tareas
    BasicTask t1 = new BasicTask("Task1", "Task1 description",p2);

    BasicTask t3 = new BasicTask("Task3", "Task3 description",p1);

   //test de tiempos
    t3.start();
    Thread.sleep(3000);
    t3.stop();     
    Thread.sleep(7000);  
    BasicTask t2 = new BasicTask("Task2", "Task2 description",p1);
    t2.start();
    Thread.sleep(10000);
    t2.stop();          
    t3.start();
    Thread.sleep(2000);
    t3.stop();
    serializer.storeObject(p0,"project.dat");
    Clock clock = Clock.getInstance(2000);
    serializer.storeObject(clock,"clock.dat");
  
    //Print de la raiz
    System.out.println(p1.toString());
    logger.debug(" Final test A ");
  }

  /**
   * testeo 2A.
   */
  public static void testA2() throws InterruptedException {
    logger.debug("inicio test A2");
    //Variable declaratio
    int time = 2000; 
    Clock clk = Clock.getInstance(time);
    Project p0;
    Project p1;
    Project p2;

    //inicializacion de los proyectos
    p0 = new Project("root","rootDescription",null);
    p1 = new Project("Proj1","Project1 description",p0);
    p2 = new Project("Proj2","Project2 description",p1);

  //inicializacion de tareas
    BasicTask t3;
    BasicTask t2;
    BasicTask t1;
    t1 = new BasicTask("Task1", "Task1 description",p2);
    t2 = new BasicTask("Task2", "Task2 description",p2);
    t3 = new BasicTask("Task3", "Task3 description",p1);
  
  //test de tiempos
    System.out.println(p0.toString());
    System.out.println("--------" 
        + "-----------------------------------------------------------" 
        + "---------------------------------------" 
        + "-------------------------" 
        + "-----------------------------------------");
          

    t3.start();
  
    Thread.sleep(4000);
    t2.start();
  
    Thread.sleep(2000);
    t3.stop();
  
    Thread.sleep(2000);
    t1.start();
  
    Thread.sleep(4000);
    t1.stop();
  
    Thread.sleep(2000);            
    t2.stop();
  
    Thread.sleep(4000);            
    t3.start();
    Thread.sleep(2000);
    t3.stop();

  //Print the project tree
    System.out.println("--------" 
          + "-----------------------------------------------------------" 
          + "---------------------------------------" 
          +  "-------------------------" 
          +  "-----------------------------------------");
    System.out.println(p0.toString());
      
    logger.info("Stop A2");
  }

  /**
   * TESTEO NUMERO 3A.
   */
  public static void testA3() throws InterruptedException {
    logger.debug("inicio test A3");
    //Variable declaratio
    int time = 2000;
    Clock clk = Clock.getInstance(time);
    Project p0;
    Project p1;
    Project p2;

    //inicializacion de los proyectos
    p0 = new Project("root","rootDescription",null);
    p1 = new Project("Proj1","Project1 description",p0);
    p2 = new Project("Proj2","Project2 description",p1);

      //inicializacion de tareas
    
    BasicTask t5;
    BasicTask t2;
    BasicTask t1;
    t1 = new BasicTask("Task1", "Task1 description",p2);
    t2 = new BasicTask("Task2", "Task2 description",null);
    t5 = new BasicTask("Task3", "Task3 description",null);
    EnchainTask t4;
    LimitedTask t3;
    t3 = new LimitedTask("Decorador Limitado", "Prueba del decorador", p2, t2, 4000);
    t4 = new EnchainTask("Tarea encadenada", "Prueba del decorador", p2, t5, t1);
    t2.setParentActivity(t3);
    t5.setParentActivity(t4);
    //test de tiempos
    System.out.println(p0.toString());
    System.out.println("--------" 
        + "-----------------------------------------------------------" 
        + "---------------------------------------" 
        + "-------------------------" 
        + "-----------------------------------------");
          

     //t3.start();
     //Thread.sleep(10000);
          
    t4.start();          
    Thread.sleep(4000);
    t4.stop();

          
    Thread.sleep(10000);
    t1.stop();

          //Print the project tree
    System.out.println("--------" 
         + "-----------------------------------------------------------" 
         + "---------------------------------------" 
         + "-------------------------" 
         +  "-----------------------------------------");
    System.out.println(p0.toString());
    logger.info("Stop testA3");
  }
  
  /**
   * Testeo Basico del report formato text basico.
   */
  public static void testBasicReportText() throws InterruptedException {
    logger.debug(" Inicio test A con Informe Basico ");
    //Inicializacion de la clase para serializar y guardar o recuperar
    //Creacion de intervalos de tiempo, Inicializacion de tareas, Inicializacion de proyectos
    Calendar inicio = Calendar.getInstance();
    inicio.setTime(new Date());
    Calendar fin = Calendar.getInstance();
    fin.setTime(new Date());
    
    fin.set(Calendar.HOUR_OF_DAY, inicio.get(Calendar.HOUR_OF_DAY));
    fin.set(Calendar.MINUTE, inicio.get(Calendar.MINUTE)); 
    fin.set(Calendar.SECOND, inicio.get(Calendar.SECOND) + 10);
    fin.set(Calendar.MILLISECOND, inicio.get(Calendar.MILLISECOND));
    int time = 500;
    Clock clk = Clock.getInstance(time);
    //test de tiempos
    Project p0 = new Project("root","rootDescription",null);
    Project p1 = new Project("Proj1","Project1 description",p0);
    BasicTask t3 = new BasicTask("Task3", "Task3 description",p1);
    t3.start();
    Thread.sleep(1000);
    t3.stop();     
    Thread.sleep(2000);  
    Project p2 = new Project("Proj2","Project2 description",p0);
    Project p3 = new Project("Proj3","Project3 description",p2);
    BasicTask t1 = new BasicTask("Task1", "Task1 description",p2);
    BasicTask t2 = new BasicTask("Task2", "Task2 description",p3);
    t1.start();
    t2.start();
    Thread.sleep(1000);
    t1.stop();  
    t3.start();
    Thread.sleep(1000);
    t3.stop();
    t2.stop();
      //creamos report
    Format textPla = new Txt();
    Format textPla1 = new Html();
    BasicReport testA = new BasicReport(inicio,fin,p0);
    testA.calculateReport(textPla1);
    FullReport testB = new FullReport(inicio,fin,p0);
    testB.calculateReport(textPla1);
    testA.calculateReport(textPla);
    testB.calculateReport(textPla);
  }
  
  /**
   * Testeo Basico del report formato text basico.
   */
  public static void testAvancedReportText() throws InterruptedException {
    logger.debug(" Inicio test A con Informe Basico ");
    //Inicializacion de la clase para serializar y guardar o recuperar
    //Creacion de intervalos de tiempo, Inicializacion de tareas, Inicializacion de proyectos
    Calendar inicio = Calendar.getInstance();
    inicio.setTime(new Date());
    Calendar fin = Calendar.getInstance();
    fin.setTime(new Date());
    
    fin.set(Calendar.HOUR_OF_DAY, inicio.get(Calendar.HOUR_OF_DAY));
    fin.set(Calendar.MINUTE, inicio.get(Calendar.MINUTE)); 
    fin.set(Calendar.SECOND, inicio.get(Calendar.SECOND) + 9);
    fin.set(Calendar.MILLISECOND, inicio.get(Calendar.MILLISECOND));
    int time = 500;
    Clock clk = Clock.getInstance(time);
    //test de tiempos
    Project p0 = new Project("root","rootDescription",null);
    Project p2 = new Project("Proj2","Project2 description",p0);
    Project p3 = new Project("Proj3","Project3 description",p2);
    Project p4 = new Project("Proj4","Project3 description",p3);
    
    BasicTask t1 = new BasicTask("Task1", "Task1 description",p2);
    BasicTask t2 = new BasicTask("Task2", "Task2 description",p3);
    BasicTask t5 = new BasicTask("Task5", "Task5 description",p4);
    
    t1.start();
    t2.start();
    t5.start();
    Thread.sleep(2000);
    Project p1 = new Project("Proj1","Project1 description",p0);
    BasicTask t3 = new BasicTask("Task3", "Task3 description",p1);
    t3.start();
    Thread.sleep(1000);
    t3.stop(); //1r inervalo de 1s    
    Thread.sleep(1000);  
    t2.stop(); //1r inervalo de 4s   
    t3.start(); //fuera de rango
    t1.stop(); //1r inervalo 4s
    t2.start(); //fuera de rango
    Thread.sleep(2000);
    t3.stop(); //2o inervalo de 2s -1s de estar fuera de rango
    t1.stop();  //1r inervalo de 2s-1s  
    BasicTask t4 = new BasicTask("Task4", "Task4 description",p1);
    t4.start(); //no saldra fuera de rango junto a p3
    t3.start(); //no saldra
    Thread.sleep(2000);
    t3.stop(); //3r inervalo que no saldra
    t2.stop(); //2o inervalo que no saldra
    t5.stop(); //1r intervalo de 10s -5s
      //creamos report
    BasicReport testA = new BasicReport(inicio,fin,p0);
    testA.calculateReport(new Html());
    FullReport testB = new FullReport(inicio,fin,p0);
    testB.calculateReport(new Html());
    testA.calculateReport(new Txt());
    testB.calculateReport(new Txt());
  }
}
