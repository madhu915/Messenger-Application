import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import jdbc.db;

public class messageLogger {

    public static void sendMessage(String sender,String string,String user) throws Exception{
        db.getInstance();
        RandomAccessFile rd = new RandomAccessFile(new File("C:/Users/HP/Documents/Zoho/messenger/messenger_jdbc with group chat/"+user+".txt"), "rw");

        // rd.skipBytes((int)rd.length());
        rd.setLength(rd.length()+1);
        rd.seek(rd.length());


        // System.out.println(rd.getFilePointer());
        String msg=sender+": "+string+"\n";
        rd.write(msg.getBytes());
        // mem.put("X: bye\n".getBytes());
        db.insert("insert into messages(user,recipient,message) values(\""+user+"\",\""+sender+"\",\""+msg+"\")");
        rd.close();

    }

    public static void readMessage(String user,String recipient) throws Exception{
        // File file=new File("C:/Users/HP/Documents/Zoho/messenger/messenger_jdbc/"+user+".txt");
        // if(!file.exists()){
        //     // System.out.println("No new messages!!");
        //     return;
        // }

        // RandomAccessFile rd = new RandomAccessFile(file, "r");
        // StringBuffer buffer=new StringBuffer();
        // rd.seek(0);
        String contents="";

        // while(rd.getFilePointer() < rd.length()) {
        //     buffer.append(rd.readLine()+System.lineSeparator());
            
        // }

        //     String lines[]=buffer.toString().split("\n");
        //     for(String m:lines){
        //         String[] names=m.split(":");
        //         if(names[0].contains(recipient)){
        //             contents+=m;           
        //     }

        //     }

        //chat history full impl
        // ResultSet chats=db.query.executeQuery("select message,seen_status from messages where (recipient=\""+recipient+"\" and user=\""+user+"\") or (recipient=\""+user+"\" and user=\""+recipient+"\")");
        ResultSet unseenchats=db.query.executeQuery("select chatid from messages where seen_status is null and ((recipient=\""+recipient+"\" and user=\""+user+"\") or (recipient=\""+user+"\" and user=\""+recipient+"\"))");
        ArrayList<String> chatSet=new ArrayList<String>();
        while(unseenchats.next()){
            chatSet.add(unseenchats.getString(1));
        }

        for(String chat:chatSet){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String date=dtf.format(LocalDateTime.now());
            db.insert("update messages set seen_status=\""+date+"\" where chatid="+chat);
        }
        unseenchats=db.query.executeQuery("select message,seen_status from messages where (recipient=\""+recipient+"\" and user=\""+user+"\") or (recipient=\""+user+"\" and user=\""+recipient+"\")");
        while (unseenchats.next()) {

            if(unseenchats.getString(2)==null){
                System.out.println("null");
            }
            else{

            contents+=unseenchats.getString(1);
            contents+="\t";
            contents+=unseenchats.getString(2);
            contents+=System.lineSeparator();
            }
        }

        
         if(contents!=""){
            System.out.println("Chat History: \n"+contents);
         }
        //  else{
        //      System.out.println("No new messages!!");
        //  }
         
     
        // rd.close();
        // clear(user);
    }

    public static void clear(String user) throws IOException{
        // File file=new File("C:/Users/HP/Documents/Zoho/messenger/messenger/"+user+".txt");
        // file.delete();    
    }

    public static void main(String[] args) throws IOException, InterruptedException {
     
        }
    }
    