package servidorSenha;
import java.rmi.*;
import java.rmi.server.*;
import java.sql.*;
import java.util.logging.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ServerSenha extends UnicastRemoteObject implements InterfaceSenha{
    private static final String url = "jdbc:mysql://localhost:3306/tpdb";
    private String root = "";
    private String passRoot = "";
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;
    String keyPass = "vkrkq9VVjxuRt5vZ";

    public ServerSenha() throws RemoteException{
        System.out.println("Servidor de senhas iniciando...");
    }

    public boolean verificaSenha(String pass, String root, String passRoot){
        try{
            con = DriverManager.getConnection(url, root, passRoot);

            if(con != null){
                System.out.println("Conectado com sucesso.");
            }else{
                System.out.println("Sem conexão. Verifique a senha.");
            }

            String args = "SELECT user_password from users";

            stmt = con.createStatement();
            rs = stmt.executeQuery(args);

            while(rs.next()){
                String password = rs.getString(2);
                password = descriptografaString(password);

                if(password != "false"){
                    if(password.equals(pass))
                        return true;
                }else{
                    System.out.println("Erro!");
                }
                
            }
        }catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
            try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
        }
        return false;
    }

	public boolean addUser(String user, String pass, String userr, String passR) throws ClassNotFoundException{
		try{
            this.root = userr;
            this.passRoot = passR;

            String query = "INSERT INTO users(user_id, user_password) VALUES(?,?)";
            Class.forName("com.mysql.jdbc.Driver");
            try(Connection con = DriverManager.getConnection(url, root, passRoot);
                PreparedStatement pst = con.prepareStatement(query)){
            		System.out.println("Conectado.");
                    pst.setString(1, user);
                    pst.setString(2, pass);
                    pst.executeUpdate();

                    System.out.println("Um novo usuário foi inserido!");
                    return true;
            }catch(SQLException ex){
                Logger lgr = Logger.getLogger(ServerSenha.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }finally{
            System.out.println("Não deu, se passou aqui.");
        }
		System.out.println("não deu");
        return false;
     }

    public boolean removeUser(String user, String pass, String root, String passRoot){
        try{
            String query = "DELETE FROM users \n" + "WHERE user_id = " + user;
            String teste = "select user_id from users";

            con = DriverManager.getConnection(url, root, passRoot);

            if(con != null){
                System.out.println("Conectado com sucesso.");
            }else{
                System.out.println("Sem conexão. Verifique a senha.");
            }

            stmt = con.createStatement();
            stmt.executeUpdate(query);

            rs = stmt.executeQuery(teste);
            
            while(rs.next()){
                String name = rs.getString(1);
                if(name.equals(user))
                    return false;
                else
                    return true;
            }            
        }catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
            try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
        }
        return false;
    }

    private String descriptografaString(String pass){
        SecretKey key = new SecretKeySpec(keyPass.getBytes(), "AES");
        String fs = new String("false");

        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] msg = cipher.doFinal(pass.getBytes());
            String senha = new String(msg);
            return senha;
        }catch(Exception e){
            e.printStackTrace();
        }
        return fs;
    }
}