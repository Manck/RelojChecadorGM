package lectorentrada;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Francisco
 */
public class ClaseQuery {
    
    Connection Objeto_ConexionSQL;
    ArrayList<byte[]> byteHuellas = new ArrayList<>();
    ArrayList<String> listaClave = new ArrayList<>();
    ArrayList<ArrayList> resultadoBusqueda = new ArrayList<>();
    
    ClaseQuery(){
        //El constructor intenta conectar a la base de datos local
        try {
            Objeto_ConexionSQL = ConexionSQL_Local.alternativeConnection();
        } catch (Exception ex) {
            //Si no puede, intenta con la remota
            try {
                Objeto_ConexionSQL = Conexion_SQL.createConnection();
            } catch (Exception ex1) {
                //Si no puede conectarse con la remota, envía mensaje de error
                JOptionPane.showMessageDialog(new JFrame(), "Error, no fue posible conectar a ninguna base de datos; Reportar Problema");
            }
        }
    }
    
    public int CantidadRegistrosHuella(){
        //JOptionPane.showMessageDialog(new JFrame(), "Query");
        int cantidadDeRegistros = 0;
        try {
            String cadenaSQL = "SELECT COUNT(*) FROM Huella";
            PreparedStatement psCantidadRegistros = Objeto_ConexionSQL.prepareStatement(cadenaSQL);
            ResultSet rsCantidadRegistros =psCantidadRegistros.executeQuery();
            
            if(rsCantidadRegistros.next()){
                    cantidadDeRegistros = rsCantidadRegistros.getInt(1);
            }
            
        } catch (Exception ex) {
            Logger.getLogger(LectorHuella.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cantidadDeRegistros;
    }
    
    public ArrayList obtenerTemplateSQL(String huellaBuscada){
        byte[] bytes = new byte[ 2^31-1];
        String claveNomina;
        try {
            String cadenaSQL = new StringBuilder().append("SELECT ClaveNomina, ").append(huellaBuscada).append(" FROM Huella WHERE ").
                    append(huellaBuscada).append(" IS NOT NULL").toString();
            PreparedStatement buscarHuella = Objeto_ConexionSQL.prepareStatement(cadenaSQL);
            //buscarHuella.setBinaryStream(1, new ByteArrayInputStream(template.serialize()), template.serialize().length);
            ResultSet resultadoDeHuella = buscarHuella.executeQuery();
            //JOptionPane.showMessageDialog(new JFrame(), huellaBuscada);
            while(resultadoDeHuella.next()){
                bytes = IOUtils.toByteArray(resultadoDeHuella.getBinaryStream(2));
                claveNomina = resultadoDeHuella.getString("ClaveNomina");
                //JOptionPane.showMessageDialog(new JFrame(), claveNomina + String.valueOf(bytes).substring(0, 4));
                byteHuellas.add(bytes);
                listaClave.add(claveNomina);
            }
            
        } catch (Exception ex) {
            
            JOptionPane.showMessageDialog(new JFrame(), "Error en la busqueda de huellas; Huella Buscada: " + huellaBuscada);
        }
            resultadoBusqueda.add(byteHuellas);
            resultadoBusqueda.add(listaClave);
            return resultadoBusqueda;            
    }
    
    public void limpiarListaHuellas(){
        byteHuellas.clear();
        listaClave.clear();
        resultadoBusqueda.clear();
    }
}
