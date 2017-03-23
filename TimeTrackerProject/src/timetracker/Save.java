package timetracker;


public class Save extends Element {
  
  private String name;
  
  @Override
  public void accept(Format visitor) {
    assert ( visitor != null ) : "Save: visitor nulo";
    visitor.visitGuardar(this);
  }

  /**
   * Nombre del fichero.
   */
  public Save(String fileName) {
    assert ( fileName != null ) : "Save: filename nulo";
    this.name = fileName;
  }


  public String getName() {
    return name;
  }

/**
 * Set name.
 */
  public void setName(String name) {
    assert ( name != null ) : "Save: name nulo";
    assert ( !name.isEmpty() ) : "Save: visitor nulo";
    this.name = name;
  }

}
