package timetracker;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;



public class SerializeGenerator {
    /**
     * Logger para mostrar mensajes.
     */
  private static Logger logger = LoggerFactory.getLogger(Client.class);
  private ObjectInputStream objectLoader;

  /**
   * Guarda el objeto en un fichero.
   */
  public void storeObject(Object objeto, String nameFile) {
    assert ( nameFile != null ) : "SerializeGenerator: nameFile nulo";
    assert ( !nameFile.isEmpty()) : "SerializeGenerator: nameFile vacio";
    try {
      FileOutputStream fileOutput = new FileOutputStream("C:\\" + nameFile);
      ObjectOutputStream outtosaveproject = new ObjectOutputStream(fileOutput);
      outtosaveproject.writeObject(objeto);
      outtosaveproject.close();
      fileOutput.close();
      logger.info("Fichero Guardado: " + nameFile);
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Carga el fichero.
   */
  public Object loadObjects(String fileName) {
    assert ( fileName != null ) : "SerializeGenerator: FileName nulo";
    assert ( !fileName.isEmpty()) : "SerializeGenerator: FileName vacio";
    Object object = null;
    try {
      objectLoader = new ObjectInputStream(new FileInputStream(
          fileName));
      object = objectLoader.readObject();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    logger.info("Fichero Cargado: " + fileName);
    return object;
  }
}
