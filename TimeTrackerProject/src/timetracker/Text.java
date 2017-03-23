package timetracker;



/**
 * Clase para crear lineas/parrafos que contengan texto en el informe.
 * @author Jose Luis Gomez, Eric jaen, Jesus Serrano
 *
 */
public class Text extends Element {
  private String text;
  /**
   * accept del Text.
   */
  public void accept(Format visitor) {
    assert ( visitor != null ) : "Text: visitor nulo";
    visitor.visitText(this);
  }

/**
 * Contructor.
 */
  public Text(String text) {
    assert ( text != null ) : "Text: text nulo";
    assert ( !text.isEmpty() ) : "Text: visitor vacio";
    this.text = text;
    checkInvariante();
  }

/**
 * Revision de las propiedades del la clases.
 */
  private void checkInvariante() {
    assert ( this.text != null ) : "Text: text nulo";
    assert ( !this.text.isEmpty() ) : "Text: visitor vacio";
  }
  

  public String getText() {
    return text;
  }

/**
 * set test.
 */
  public void setText(String text) {
    assert ( text != null ) : "Text: text nulo";
    assert ( !text.isEmpty() ) : "Text: visitor vacio";
    this.text = text;
    checkInvariante();
  }

}
