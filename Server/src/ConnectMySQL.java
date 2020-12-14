import java.io.IOException;
import java.sql.*;

public class ConnectMySQL {

    private static final String url = "jdbc:mysql://localhost:3306/database?serverTimezone=Europe/Minsk&useSSL=false";
    private static final String user = "root";
    private static final String password = "123123";

    private static Connection con;
    private static Statement stmnt;
    private static ResultSet rs;
    private static PreparedStatement preparedStmnt;

    public static boolean send(String query){
        try{
            con = DriverManager.getConnection(url, user, password);
            //String query = "SELECT * FROM users";
            //rs = stmnt.executeQuery(query);
            preparedStmnt = con.prepareStatement(query);
            preparedStmnt.execute();

            return true;

            /*while(rs.next()) {
                String email = rs.getString(2);
                String password = rs.getString(3);
                System.out.println("MySQL: email : " + email + " | pass: " + password);
            }*/
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            return false;
        }
        finally {
            try{ con.close(); } catch (SQLException throwables) {  }
            try{ preparedStmnt.close(); } catch (SQLException throwables) {  }
        }
    }

    public static String get(String query){
        try{
            con = DriverManager.getConnection(url, user, password);
            stmnt = con.createStatement();
            rs = stmnt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int column_count = rsmd.getColumnCount();


            String requery = new String();

            while(rs.next()){
                if(requery.length() != 0) requery += "\n";

                for(int i=1; i <= column_count; i++)
                    requery += rs.getString(i) + ";";
            }

            return requery;

            /*while(rs.next()) {
                String email = rs.getString(2);
                String password = rs.getString(3);
                System.out.println("MySQL: email : " + email + " | pass: " + password);
            }*/
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            return "error";
        }
        finally {
            try{ con.close(); } catch (SQLException throwables) {  }
            try{ stmnt.close(); } catch (SQLException throwables) { }
            try{ if(rs != null) rs.close(); } catch (SQLException throwables) { }
        }
    }

}
