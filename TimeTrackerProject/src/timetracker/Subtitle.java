package timetracker;



/**
 * Clase que crea un subtitulo para el informe.
 * @author Jose Luis Gomez, Eric jaen, Jesus Serrano
 *
 */
public class Subtitle extends Element {

  private String subtitle;
  /**
   * accept del SubTitle.
   */
  public void accept(Format visitor) {
    assert ( visitor != null ) : "Subtitle: visitor nulo";
    visitor.visitSubtitle(this);
  }

/**
 * contructror con el texto necesario.
 */
  public Subtitle(String subtitulo) {
    assert ( subtitulo != null ) : "Subtitle: subtitle nulo";
    assert ( !subtitulo.isEmpty()) : "Subtitle: subtitle vacio";
    this.subtitle = subtitulo;
    checkInvariante();
  }
  
  private void checkInvariante() {
    assert ( !this.subtitle.isEmpty()) : "Subtitle: subtitle vacio";
    assert ( this.subtitle != null) : "Subtitle: subtitle nulo";
  }


  public String getSubtitle() {
    return subtitle;
  }

/**
 * Set Subtitle.
 */
  public void setSubtitle(String subtitle) {
    assert ( subtitle != null ) : "Subtitle: subtitle nulo";
    assert ( !subtitle.isEmpty()) : "Subtitle: subtitle vacio";
    this.subtitle = subtitle;
    checkInvariante();
  }

}
