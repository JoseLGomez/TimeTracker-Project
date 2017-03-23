package timetracker;

import java.util.Date;



public class DataClock {

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
   * @uml.property name="ActualDate"
   */
  private Date actualDate;

  /** 
   * Getter of the property <tt>ActualDate</tt>
   * @return  Returns the ActualDate.
   * @uml.property  name="ActualDate"
   */
  public Date getActualDate() {
    return actualDate;
  }

  /** 
   * Setter of the property <tt>ActualDate</tt>
   * @param dateActualy  The ActualDate to set.
   * @uml.property  name="ActualDate"
   */
  public void setActualDate(Date actualDate) {
    assert (actualDate != null) : "la fecha actual no puede ser nula";
    this.actualDate = actualDate;
  }
  
    /**
     * guardar el tick y la fecha.
     */
  public DataClock(Date date, int tick) {
    assert (date != null) : "la fecha no puede ser nula";
    assert (tick >= 0) : "el tick valor no puede ser negativo o nulo";
    this.tick = tick;
    this.actualDate = date;
  }
}
