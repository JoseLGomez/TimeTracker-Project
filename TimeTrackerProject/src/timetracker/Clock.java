package timetracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.Observable;
import java.util.Timer;



/** 
 * Clase Reloj que implementa los patrones Patrones Observer y singleton.
 * Es el encargado de notificar a todos el tiempo transcurrido desde que se inicializo, además
 * es instanciado una unica vez (singleton).
 */
public class Clock extends Observable implements Serializable {

  /**
  * Logger to print messages.
  */
  private static final Logger logger = LoggerFactory.getLogger(Clock.class);

  /**
   * @uml.property  name="INSTANCE"
   */
  private static Clock instance;
  /**
   * @uml.property  name="messageNotify"
   */
  private static DataClock messageNotify;
  /** 
   * @uml.property name="timer"
   */
  private static Timer timer;
  /**
   * @uml.property  name="timerTask"
   */
  private static TicGenerator timerTask;
  
  /**
   * Contructor privado para poder realizar el patron singleton.
   */
  private Clock(int tick) {
    assert (tickClock >= 0 ) : "el valor del tick es demasiado bajo";
    logger.info("Instancia de nuevo Reloj");
    this.tickClock = tick;
    this.timerTask = new TicGenerator(tick);
    this.timerTask.start();

  }   
  
  /** 
   * Realiza la instancia del reloj y se sincroniza por si hay problema de thread.
   */
  private static synchronized void createInstance(int tick) {
    assert (tick >= 0 ) : "el valor del tick es demasiado bajo";  
    if (instance == null) { 
      System.out.println("entrooo");
      instance = new Clock(tick);
    //hacemos el run aqui para que no se haga un bucle infinito y crear instancia de 
    //instancias (antes el run se hacia en el constructor de clock)
      //timerTask.run();
      //timer = new Timer();
    }
  } 
  
  /**
   * Instancia del Sigleton valor por defecto.
   */
  public static Clock getInstance(int tick) {
    assert (tick >= 0 ) : "el valor del tick es demasiado bajo";
    if (instance == null) {
      instance = new Clock(tick);      
    }
    return instance;
  }
  
  /**
   * instanciacion del clock sin parametros de entrada. 
   */
  public static Clock getInstance() {
    if (instance == null) {
      instance = new Clock(2000);      
    }
    return instance;
  } 
  
  /**
   *  {@link Override Observable}.
   *  Notifica a cada elemento que esta asociado al observable que ha avido un cambio.
   *  
   */
  public void notifyObserver() {
    //logger.info("Notificacion a los observers");
    messageNotify = new DataClock(new Date(),this.tickClock);
    setChanged();    
    notifyObservers(new DataClock(new Date(),this.tickClock));
  }
  
  private final class TicGenerator extends Thread implements java.io.Serializable {  

    /**
     * Clase que implementa el Thread que ira contando el tiempo (ticks) e infromara a la clase.
     * reloj de cuando notificar a los observers
     */

    /**
     * Metode para notificar a los observers cada vez que pasa un tick.
     * */
    public void run() {
      encendido = true;
      while (encendido) {
        try {
          Thread.sleep(this.tick);
          
          Clock.getInstance().notifyObserver();
          
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      
    }            
    
    public TicGenerator(int tick) {
      assert (tick >= 0 ) : "el valor del tick es demasiado bajo";
      this.tick = tick;
    }

    /**
     * @uml.property  name="tick"
     */
    private int tick;
    /**
     * Getter of the property <tt>tick</tt>
     * @return  Returns the tick.
     * @uml.property  name="tick"
     */
    public int getTick() {
      return tick;
    }
    /**
     * Setter of the property <tt>tick</tt>
     * @param tick  The tick to set.
     * @uml.property  name="tick"
     */
    public void setTick(int tick) {
      assert (tick >= 0 ) : "el valor del tick es demasiado bajo";
      this.tick = tick;
    }

    /**
     * @uml.property  name="encendido"
     */
    private boolean encendido = true;
        
    /**
     * Parar el Thread.
     */
    public void stopThread() {
      encendido = false;
    }
  
  }
  /**
   * @uml.property  name="tickClock"
   */
  private int tickClock;
  /**
   * Getter of the property <tt>tickClock</tt>
   * @return  Returns the tickClock.
   * @uml.property  name="tickClock"
   */
  public int getTickClock() {
    return tickClock;
  }

  /**
   * Setter of the property <tt>tickClock</tt>
   * @param tickClock  The tickClock to set.
   * @uml.property  name="tickClock"
   */
  public void setTickClock(int tickClock) {
    assert (tickClock >= 0 ) : "el valor del tick es demasiado bajo";
    this.tickClock = tickClock;
  }
  
  @SuppressWarnings("deprecation")
  public void stop() {
    timerTask.stopThread();
    
  }
  

}
