/*
This file is part of Open CSTA.

    Open CSTA is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Open CSTA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Open CSTA.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.opencsta.cstatesttool;

import org.opencsta.client.CSTAMulti;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.opencsta.apps.objects.CSTAApplication;
import org.opencsta.servicedescription.common.AgentEvent;
import org.opencsta.servicedescription.common.CallEvent;

/**
 * The main class of the application.
 */
public class CSTATestToolApp extends SingleFrameApplication  implements CSTAApplication{
    private Logger log = Logger.getLogger(CSTATestToolApp.class) ;
    private static CSTAMulti csta;
    private Properties theProps ;
    private Thread cstaThread ;

    public static CSTAMulti getCSTA() {
        return csta;
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        setTheProps(loadPropertiesFromFile()) ;
        try {
            startCSTA(getTheProps());
        } catch (Exception ex) {
            ex.printStackTrace() ;
            log.fatal(this.getClass().getName() + " ---> " + " tried to start CSTA but there is a fatal exception - probably no config file");
            exit() ;
        }
        show(new CSTATestToolView(this));
    }

    private void startCSTA(Properties props) throws Exception{
        csta = new CSTAMulti(this,props) ;
        csta.RegisterParentApplication(this) ;
        cstaThread = new Thread(csta,"CSTA Thread") ;
        csta.run() ;
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of HandyCSTATestToolApp
     */
    public static CSTATestToolApp getApplication() {
        return Application.getInstance(CSTATestToolApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(CSTATestToolApp.class, args);

    }
    
    public void CSTACallEventReceived(CallEvent event){
        
    }
    
    public void CSTAAgentEventReceived(AgentEvent event){
        
    }
    
    public void TDSDataReceived(String dev, String code, String data){
        log.info( this.getClass().getName() + " CSTA APP - TDS Data Received ") ;
    }

    
    public String convertcallid(String human){
        log.debug(this.getClass().getName() + " ---> " + " convert " + human + " to machine");
        char[] charray = human.toCharArray() ;
        int digleft = 0;
        int digright = 0 ;
        System.out.println("The length of the char array is: " + Integer.toString(charray.length) ) ;
        if( charray.length == 4 ){
                for( int i = 0 ; i < charray.length ; i++ ){
                    if( i == 0 ){
                        if( (int)charray[i] >= 48 && (int)charray[i] <=57 ){
                            digleft = (int)(charray[i]-48)*16 ;
                        }
                        else if( (int)charray[i] >= 65 && (int)charray[i] <=70 ){
                            digleft = (int)(charray[i]-65+10)*16 ;
                        }
                        else if( (int)charray[i] >= 97 && (int)charray[i] <=102){
                            digleft = (int)(charray[i]-97+10)*16 ;
                        }
                    }
                    if( i == 1 ){
                        if( (int)charray[i] >= 48 && (int)charray[i] <=57 ){
                            digleft += (int)(charray[i]-48) ;
                        }
                        else if( (int)charray[i] >= 65 && (int)charray[i] <=70 ){
                            digleft += (int)(charray[i]-65+10) ;
                        }
                        else if( (int)charray[i] >= 97 && (int)charray[i] <=102){
                            digleft += (int)(charray[i]-97+10) ;
                        }
                        System.out.println("digleft = " + digleft ) ;
                    }
                    if( i == 2 ){
                        if( (int)charray[i] >= 48 && (int)charray[i] <=57 ){
                            digright = (int)(charray[i]-48)*16 ;
                        }
                        else if( (int)charray[i] >= 65 && (int)charray[i] <=70 ){
                            digright = (int)(charray[i]-65+10)*16 ;
                        }
                        else if( (int)charray[i] >= 97 && (int)charray[i] <=102){
                            digright = (int)(charray[i]-97+10)*16 ;
                        }
                    }
                    if( i == 3 ){
                        if( (int)charray[i] >= 48 && (int)charray[i] <=57 ){
                            digright += (int)(charray[i]-48) ;
                        }
                        else if( (int)charray[i] >= 65 && (int)charray[i] <=70 ){
                            digright += (int)(charray[i]-65+10) ;
                        }
                        else if( (int)charray[i] >= 97 && (int)charray[i] <=102){
                            digright += (int)(charray[i]-97+10) ;
                        }
                        System.out.println("digright = " + digright ) ;
                    }
                }
            char[] chtmp = {(char)digleft,(char)digright} ;
            String machinereadable = new String(chtmp) ;
            log.debug(this.getClass().getName() + " ---> " + " machine readable has length: " + machinereadable.length() ) ;
            return machinereadable ;
        }
        return null ;
    }

    public void cstaFail() {
        System.exit(0) ;
    }

    private static Properties loadPropertiesFromFile(String filename){
        FileInputStream is ;
        try {
            System.out.println("Trying to load properties from:  " + System.getProperty("user.dir") + "/" + filename) ;
            is = new FileInputStream( (System.getProperty("user.dir") + "/"+filename) );
            Properties props = new Properties() ;
            props.load(is) ;
            return props ;
        }catch (FileNotFoundException ex) {
            ex.printStackTrace() ;
        } catch (IOException ex) {
            ex.printStackTrace() ;
        }
        return null ;
    }

    private Properties loadProperties(InputStream propstream){
        Properties props ;
        try {
            props = new Properties();
            props.load(propstream);
            return props ;
        } catch (IOException ex) {
            ex.printStackTrace();
            props = null ;
        }
        return null ;
    }

    private static Properties loadPropertiesFromFile(){
        return loadPropertiesFromFile("handycsta.conf") ;
    }

    /**
     * @return the theProps
     */
    public Properties getTheProps() {
        return theProps;
    }

    /**
     * @param theProps the theProps to set
     */
    public void setTheProps(Properties theProps) {
        this.theProps = theProps;
    }

    
}
