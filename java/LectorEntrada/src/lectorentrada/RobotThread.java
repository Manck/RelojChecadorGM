package lectorentrada;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 *
 * @author Francisco
 */
public class RobotThread implements Runnable{
    
    String ClaveNomina;
    Robot robot;
    
    public RobotThread(String ClaveNomina) throws AWTException{
        this.robot = new Robot();        
        this.ClaveNomina = ClaveNomina;
    }
    
    @Override
    public void run() {
        for(int i =0; i < ClaveNomina.length(); i++){
            boolean EsCaracterValido = Character.getNumericValue(ClaveNomina.charAt(i)) != -1;
            if(EsCaracterValido){
                robot.keyPress(ClaveNomina.charAt(i));
                System.out.println("Tecla presionada: " + ClaveNomina.charAt(i));
                robot.delay(100);
            }
        }
        robot.delay(100);
        robot.keyPress(KeyEvent.VK_ENTER);
    }
}
