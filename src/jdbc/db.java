package jdbc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class db {
    public static Statement query;
    public static Connection connection=null;

    private static db instance=null;
    private db(){
        openDB();
    }

    synchronized public static db getInstance(){
        if(instance==null){
            instance=new db();
        }
        return instance;
    }

    public static void close(){
        
        try {

            connection.close();
            System.out.println("Closing db..");
            // System.exit(0);
            
        } catch (Exception e) {
            
            // e.printStackTrace();

        }

    }

    public static void insert(String sql) throws Exception{
        query.executeUpdate(sql);

    }

    static void openDB(){

        // jdbc connection to ping mysql db
        try{

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/ping", "root", "1234");
            query=connection.createStatement();
            // System.out.println("opened");
        }

        catch(Exception e){

            // e.printStackTrace();        

        }

    }
    
}
