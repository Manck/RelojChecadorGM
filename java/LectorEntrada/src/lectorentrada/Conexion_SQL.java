package lectorentrada;

/**
 *
 * @autor Francisco Javier Lamas Green
 * Clase de conexión SQL
 */

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.codehaus.jackson.map.ObjectMapper;

public class Conexion_SQL {
  
    Configuracion config;
    String ip;
    String BaseDatos;
    String Usuario; 
    String Password;
    
    /*Conexión a DB*/
    @SuppressWarnings("finally")
    public static Connection createConnection() throws Exception {
    	        Connection conexion=null;
                ObjectMapper mapperLectura = new ObjectMapper();
                Configuracion configJSON = mapperLectura.readValue(new File("C:\\Program Files\\LectorHuella\\ConfiguracionRemota.json"), Configuracion.class);
               try{
    	            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    	            String url = "jdbc:sqlserver:// "+ configJSON.getIp()+" ;databaseName= "+configJSON.getBasedatos()+" ; "
                    + "user= "+configJSON.getUsuario()+" ;password = " +configJSON.getPassword()+ ";";
    	            conexion= DriverManager.getConnection(url);
    	            //System.out.println("Conexión con SQL Server Exitosa");
    	        }
    	        catch(ClassNotFoundException ex){
    	            JOptionPane.showMessageDialog(new JFrame(),"Error en la Conexión con la BD 1 : "+ex.getMessage());
    	            conexion=null;
    	        }
    	        catch(SQLException ex){
                    //Colocar la conexión como local
    	            conexion= ConexionSQL_Local.alternativeConnection();
    	        }
    	        catch(Exception ex){
    	            JOptionPane.showMessageDialog(new JFrame(),"Error en la Conexión con la BD 3 : "+ex.getMessage());
    	            conexion=null;
    	        }
    	        finally{   
    	            return conexion;
    	        }
    	 }
    
}