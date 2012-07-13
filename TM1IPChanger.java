/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tm1ipchanger;

import java.awt.Frame;
import java.io.*;
import java.util.regex.*;

/**
 * tool to change the IP address in the TM1 Configuration files
 * @author tom.saxton-howes
 */
public class TM1IPChanger {

    /*The default configuration file location*/
    private static String ConfigLoc = "C:\\Program Files\\ibm\\cognos\\tm1\\webapps\\pmpsvc\\WEB-INF\\configuration\\";
    /*Contributor configuration file*/
    private static String pmpsvc = "pmpsvc_config.xml";
    /*tm1 servers config file*/
    private static String fmpsvc = "fpmsvc_config.xml";
    private static GUI gui;
    private static File pmpFile;
    private static File fmpFile;
   /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        gui = new GUI();
        gui.setVisible(true);
        findIP(ConfigLoc,pmpsvc,fmpsvc);
    }
    /**
     * find the IP Address in the configuration files
     * @param Config folder location of the configuration files
     * @param pmpsvc name of the pmpsvc configuration file
     * @param fmpsvc name of the fmpsvc configuration file
     */
    public static void findIP(String Config, String pmpsvc, String fmpsvc){
        if (Config.endsWith("\\")){
            String pmpString;
            String fmpString;
            pmpFile = new File(Config + pmpsvc);
            fmpFile = new File(Config + fmpsvc);
            if(pmpFile.exists()){
                pmpString = ReadWriteTextFile.getContents(pmpFile);
                fmpString = ReadWriteTextFile.getContents(fmpFile);

                if (!"".equals(pmpString)){
                    //locate ip in config files
                    try{
                    Pattern IPPattern = Pattern.compile("([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])");
                    Matcher pmpIPMatcher = IPPattern.matcher(pmpString);
                    pmpIPMatcher.find();
                    int pmpIPstart = pmpIPMatcher.start();
                    int pmpIPend = pmpIPMatcher.end();
                    
                    Matcher fmpIPMatcher = IPPattern.matcher(fmpString);
                    fmpIPMatcher.find();
                    int fmpIPstart = fmpIPMatcher.start();
                    int fmpIPend = fmpIPMatcher.end();
                    
                    if(pmpIPstart >=0){
                        String pmpIP = pmpString.substring(pmpIPstart, pmpIPend);
                        String fmpIP = fmpString.substring(fmpIPstart, fmpIPend);

                        if (pmpIP.equals(fmpIP) && pmpIPstart >=0){
                            gui.setConfigureIP(pmpIP);
                        }else{ //ip mismatch error
                            System.err.print("error ip addresses do not match");
                            Frame[] guiFrame = gui.getFrames();
                            MsgBox msgbox = new MsgBox(guiFrame[0], "Warning IP"
                                    + " addresses may not match in config files"
                                    ,false);
                            gui.setConfigureIP(pmpIP);
                            msgbox.dispose();
                        }
                    }else{
                        Frame[] guiFrame = gui.getFrames();
                        MsgBox msgbox = new MsgBox(guiFrame[0], "IP Not Found"
                                + " in configuration file continue with IBM"
                                + " Configuration or choose another "
                                + "configuration file",false);
                        msgbox.dispose();
                    }
                    }catch(IllegalStateException ISE){
                        Frame[] guiFrame = gui.getFrames();
                        MsgBox msgbox = new MsgBox(guiFrame[0], "IP Not Found"
                                + " in configuration file continue with IBM"
                                + " Configuration or choose another "
                                + "configuration file",false);
                        msgbox.dispose();
                    }
                }
            }
        }
    }
    /**
     * update the configuration files
     * @param IP IP address to change to in the configuration files
     * @param ConfIP IP address in the configuration file
     * @param Conf configuration file location
     * @param pmpsvc pmpsvc configuration file name
     * @param fmpsvc fmpsvc configuration file location
     */
    public static void updateconfig(String IP, String ConfIP, String Conf, String pmpsvc, String fmpsvc){
        if(pmpFile.exists()){
            if (!"".equals(ConfIP) | ConfIP != null){
                ConfIP = ConfIP.trim();
                IP = IP.trim();
                if (validateIPv4(IP)){
                    String pmpString = ReadWriteTextFile.getContents(pmpFile);
                    String fmpString = ReadWriteTextFile.getContents(fmpFile);
                    try{
                        String[] pmpSplit = pmpString.split(ConfIP);
                        int p = 0;
                        //if the counter is 0 or even add the ip address to the end of the string
                        while(p <pmpSplit.length){
                            if (isEven.isEvenOr0(p)){
                                pmpString = pmpSplit[p] + IP;
                            }else{
                                pmpString = pmpString + pmpSplit[p];
                            }
                            p ++;
                        }
                        String[] fmpSplit = fmpString.split(ConfIP);
                        int f = 0;
                        while(f <fmpSplit.length){
                            if (isEven.isEvenOr0(f)){
                                fmpString = fmpSplit[f] + IP;
                            }else{
                                fmpString = fmpString + fmpSplit[f];
                            }
                            f ++;
                        }
                        //ConfIPPattern.matcher(fmpString).replaceAll(IP);
                        ReadWriteTextFile.setContents(pmpFile, pmpString);
                        ReadWriteTextFile.setContents(fmpFile, fmpString);
                        Frame[] guiFrame = gui.getFrames();
                        MsgBox msgbox = new MsgBox(guiFrame[0], "change "
                                + "complete",false);
                        msgbox.dispose();
                    }catch(IOException IO){
                        System.err.print(IO);
                        Frame[] guiFrame = gui.getFrames();
                        MsgBox msgbox = new MsgBox(guiFrame[0], "Error writing to"
                                + " file",false);
                        msgbox.dispose();
                    }
                }else{
                    Frame[] guiFrame = gui.getFrames();
                    MsgBox msgbox = new MsgBox(guiFrame[0], "not a valid IPv4"
                            + " IP",false);
                    msgbox.dispose();
                }
            }else{
                Frame[] guiFrame = gui.getFrames();
                MsgBox msgbox = new MsgBox(guiFrame[0], "Error reading pmpsvc"
                        + " file",false);
                msgbox.dispose();
            }
        }else{
            Frame[] guiFrame = gui.getFrames();
            MsgBox msgbox = new MsgBox(guiFrame[0], "Error pmpsvc file no found"
                    + " no changes made"
                    ,false);
            msgbox.dispose();
        }
            
    }
    /**
     * returns the location of the configuration file
     * @return String location of the configuration file
     */
   public static String getConfigLocation(){
       return ConfigLoc;
   }
   
   /**
    * returns the pmpsvc file name default is pmpsvc_config.xml
    * @return String pmpsvc file name
    */
   public static String getPmpsvcLocation(){
       return pmpsvc;
   }
   
   /**
    * returns the fmpsvc file name default is fmpsvc_config.xml
    * @return String fmpsvc file name
    */
   public static String getFmpsvcLocation(){
       return fmpsvc;
   }
    
    public void setConfigLocation(String config){
        if (config != null){
            ConfigLoc = config;
        } //else do nothing
    }
    
    /**
     * checks the string passed into it to make sure it's a validates the 
     * IPv4 address 
     * @param IP IP address to validate
     * @return true if the IP is valid
     */
    public static boolean validateIPv4(String IP){
        Pattern IPPattern = Pattern.compile("([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])");
        Matcher pmpIPMatcher = IPPattern.matcher(IP);
        if (pmpIPMatcher.find()){
            return true;
        }else{
            return false;
        }
    }
}
