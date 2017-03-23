package timetracker;

import java.util.Calendar;


public class Period implements java.io.Serializable{

  /**
   * @uml.property  name="DateInit"
   */
  private Calendar dateInit;

  /**
   * Getter of the property <tt>DateInit</tt>
   * @return  Returns the dateInit.
   * @uml.property  name="DateInit"
   */
  public Calendar getDateInit() {
    return dateInit;
  }

  /**
   * Setter of the property <tt>DateInit</tt>
   * @param DateInit  The dateInit to set.
   * @uml.property  name="DateInit"
   */
  public void setDateInit(Calendar dateInit) {
    assert (dateInit != null) : "la fecha inicial no puede ser nula";
    this.dateInit = dateInit;
  }

  /**
   * @uml.property  name="DateEnd"
   */
  private Calendar dateEnd;

  /**
   * Getter of the property <tt>DateEnd</tt>
   * @return  Returns the dateEnd.
   * @uml.property  name="DateEnd"
   */
  public Calendar getDateEnd() {
    return dateEnd;
  }

  /**
   * Setter of the property <tt>DateEnd</tt>
   * @param DateEnd  The dateEnd to set.
   * @uml.property  name="DateEnd"
   */
  public void setDateEnd(Calendar dateEnd) {
    assert (dateEnd != null) : "la fecha final no puede valer nulo";
    this.dateEnd = dateEnd;
  }

  /** 
   * @uml.property name="TimeTotal"
   */
  private double timeTotal;

  /** 
   * Getter of the property <tt>TimeTotal</tt>
   * @return  Returns the timeTotal.
   * @uml.property  name="TimeTotal"
   */
  public double getTimeTotal() {
    return timeTotal;
  }


  public Period(){ }

      
  public void update(DataClock infoClock) {
    assert (infoClock != null) : "la informacion del clock no puede ser nula";
    this.getDateEnd().setTime(infoClock.getActualDate());
  }

      /** 
       * Setter of the property <tt>TimeTotal</tt>
       * @param TimeTotal  The timeTotal to set.
       * @uml.property  name="TimeTotal"
       */
  public void setTimeTotal(double timeTotal) {
    assert (timeTotal > 0) : "el tiempo total no puedo ser negativo";
    this.timeTotal = timeTotal;
  }

}
