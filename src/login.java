import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import jdbc.db;

public class login {
    public static boolean stop=false;
    public static Scanner sc=new Scanner(System.in);
    static RandomAccessFile onlineUsers;
    static List<String> registeredUsers=new ArrayList<String>();
    static List<String> onlineUsersList=new ArrayList<String>();


    public static boolean renameFiles(String oldName, String newName)
    {
        String sCurrentLine = "";
    
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(oldName));
            BufferedWriter bw = new BufferedWriter(new FileWriter(newName));
    
            while ((sCurrentLine = br.readLine()) != null)
            {
                bw.write(sCurrentLine);
                bw.newLine();
            }
    
            br.close();
            bw.close();
    
            File org = new File(oldName);
            org.delete();
            return true;
    
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    
    }
    public static void removeOnlineUser(String user) throws IOException{
        File file = new File("C:/Users/HP/Documents/Zoho/messenger/messenger_jdbc with group chat/src/online.txt");
        File temp = new File("C:/Users/HP/Documents/Zoho/messenger/messenger_jdbc with group chat/src/_temp_.txt");
        PrintWriter out = new PrintWriter(new FileWriter(temp));
        Files.lines(file.toPath())
            .filter(line -> !line.contains(user))
            .forEach(out::println);
        out.flush();
        out.close();
        // Path source = Paths.get("C:/Users/HP/Documents/Zoho/messenger/messenger/src/_temp_.txt");
        renameFiles("_temp_.txt", "online.txt");
        
    }

    public static boolean isUserOnline(String recipient) throws IOException{
        String buffer="";

            onlineUsers=new RandomAccessFile("C:/Users/HP/Documents/Zoho/messenger/messenger_jdbc with group chat/src/online.txt", "rw");         
            // System.out.println("\nUsers Online:");
            onlineUsers.seek(1);

            while(onlineUsers.getFilePointer() < onlineUsers.length()) {
                buffer=onlineUsers.readLine();
                String[] names=buffer.split("\t");
                for(String m:names){
                    onlineUsersList.add(m.toLowerCase());
                    
                }
            }

            if(onlineUsersList.contains(recipient)){
                return true;
            }
            else{
                return false;
            }
    }

    public static boolean isRegistered(String user) throws IOException, SQLException{
        // RandomAccessFile rd = new RandomAccessFile(new File("C:/Users/HP/Documents/Zoho/messenger/messenger_jdbc/src/users.txt"), "r");
        // String buffer="";

        // rd.seek(0);

        // while(rd.getFilePointer() < rd.length()) {
        //     buffer=(rd.readLine());
        //     registeredUsers.add(buffer.toLowerCase());
        //  }
        //  System.out.println();

        ResultSet getUsers=db.query.executeQuery("select user from chatusers where user=\""+user+"\"");
        if(getUsers.next()!=true){
            System.out.println("Username unavailable!!");
            return false;
        }
        else{
            return true;
        }

    }

    public static void send(String user) throws Exception{
        System.out.print("\nEnter recipient: ");
        String recipient=sc.nextLine();
        
        if(recipient.equalsIgnoreCase("stop")){
            stop=true;
            //remove a user who is online
            removeOnlineUser(user);
            // System.out.println(isUserOnline("madz"));//  isUserOnline("madz"),isUserOnline("jeevi"));


            return;
        }

        if(isRegistered(recipient)==false){
            System.out.println("User not available!!");
            return;
        }

        System.out.print("Enter message: ");
        String msg=sc.nextLine();
        messageLogger.sendMessage(user, msg, recipient);
        System.out.println("Message sent successfully!\n");
    }

    public static void checkMessage(String user,String recipient) throws Exception{
        messageLogger.readMessage(user,recipient);
    }


    public static void main(String[] args) throws Exception {
        db.getInstance();
        String choice="3";
        do{
            stop=false;
            System.out.print("\n1.Login\n2.Signup\n3.Exit\nEnter Choice: ");
            choice=sc.nextLine();
            switch (choice) {
                case "1":
                    loginUser();                    
                    break;
                case "2":
                    signup();
                    break;
                case "3":
                    //delete content from online
                    FileChannel.open(Path.of("online.txt"), StandardOpenOption.WRITE).truncate(0).close();
                    db.close();
                    System.exit(0);
            
                default:
                    break;
            }

        }while(choice!="3");
    }

    private static void signup() throws Exception {
        System.out.println("\n---- Sign Up Menu ----\n");
        System.out.println("Enter Username: ");
        String user=sc.nextLine();
        ResultSet getUsers=db.query.executeQuery("select id from chatusers where user=\""+user+"\"");
        if(getUsers.next()!=true){

            System.out.println("Username available!");
            db.insert("insert into chatusers(user) values(\"" + user + "\")");
            System.out.println("Signed Up Successfully!!\n");

        }
        else{
            System.out.println("Username already taken!!\n");

        }
    }

    private static void loginUser() throws Exception {
        String user=sc.nextLine();
        if(isRegistered(user)){

            StringBuffer buffer=new StringBuffer();

            onlineUsers=new RandomAccessFile("C:/Users/HP/Documents/Zoho/messenger/messenger_jdbc with group chat/src/online.txt", "rw");
            onlineUsers.setLength(onlineUsers.length()+1);
            onlineUsers.seek(onlineUsers.length());
            onlineUsers.write(user.getBytes());
            
            // System.out.println("\nUsers Online:");
            onlineUsers.seek(1);

            while(onlineUsers.getFilePointer() < onlineUsers.length()) {
                buffer.append(onlineUsers.readLine()+"\t");
            }

            // String contents = buffer.toString();
            // if(contents!=null){
            //     // System.out.println(contents);
            // }

            //check msg from selected recipients
            String choice="3";
            do{
                System.out.print("USER MENU\n1.Chat History\n2.Chat\n3.Logout\n4.Group Chat (with online users)\nEnter Choice: ");
                choice=sc.nextLine();
                if(choice.equals("1")){
                    String recipient=sc.nextLine();
                    checkMessage(user, recipient);
                }
                else if(choice.equals("2")){
                    System.out.println("Enter STOP to exit chat..");
                    while(!stop){
                        send(user);
                        // checkMessage(user);
                    }

                }
                else if(choice.equals("4")){
                    
                    Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd.exe","/K","cd \"C:\\Users\\HP\\Documents\\Zoho\\messenger\\messenger_jdbc with group chat\\src\" && java group_chat 239.0.0.0 1234",}); //powershell start cmd.exe /K \"java group_chat 239.0.0.0 1234\"");
                }
                else{
                    System.out.println("Logging out..");
                    stop=true;
                    //remove a user who is online
                    removeOnlineUser(user);
                    return;
                }
            }while(choice!="3");

        }

        else{
            System.out.println("User not registered!");
            return;
        }

       
    }
}
