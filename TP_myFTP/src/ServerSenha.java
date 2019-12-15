
import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.sql.*;
import java.util.logging.*;

@SuppressWarnings("serial")
public class ServerSenha extends UnicastRemoteObject implements InterfaceSenha {
	private static final String url = "jdbc:mysql://localhost:3306/tpdb";
	private String root = "";
	private String passRoot = "";
	String keyPass = "vkrkq9VVjxuRt5vZ";

	public ServerSenha() throws RemoteException {
		System.out.println("Servidor de senhas iniciando...");
	}

	public boolean verificaSenha(String user, String pass, String userr, String passR) throws ClassNotFoundException {
		try {
			this.root = userr;
			this.passRoot = passR;

			String query = "SELECT * FROM users";
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection con = DriverManager.getConnection(url, root, passRoot);
					PreparedStatement pst = con.prepareStatement(query)) {
				System.out.println("Conectado.");
				
				if(user.equals(userr)) {
					return true;
				}
				
				ResultSet rs = pst.executeQuery();

				while(rs.next()) {
					String data = rs.getString(1);
					if(data.equals(user)) {
						String pas = rs.getString(2);
						if(pas.equals(pass)) {
							return true;
						}
					}
				}

				//return true;
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(ServerSenha.class.getName());
				lgr.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} finally {
			System.out.println("Finalizando a conexão...");
		}
		return false;
	}

	public boolean addUser(String user, String pass, String userr, String passR) throws ClassNotFoundException, IOException {
		try {
			this.root = userr;
			this.passRoot = passR;

			String query = "INSERT INTO users(user_id, user_password) VALUES(?,?)";
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection con = DriverManager.getConnection(url, root, passRoot);
					PreparedStatement pst = con.prepareStatement(query)) {
				System.out.println("Conectado.");
				pst.setString(1, user);
				pst.setString(2, pass);
				pst.executeUpdate();

				System.out.println("Um novo usuário foi inserido!");
				
				ClientArqs client = new ClientArqs();
				client.criaPasta(user);
				return true;
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(ServerSenha.class.getName());
				lgr.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} finally {
			System.out.println("Finalizando a conexão...");
		}
		return false;
	}

	public boolean removeUser(String user, String pass, String userr, String passR) throws ClassNotFoundException, IOException{
		try{
			this.root = userr;
			this.passRoot = passR;

			String query = "DELETE FROM users WHERE user_id = ?";
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection con = DriverManager.getConnection(url, root, passRoot);
					PreparedStatement pst = con.prepareStatement(query)) {
				System.out.println("Conectado.");
				pst.setString(1, user);
				pst.executeUpdate();

				System.out.println("O usuário foi removido!");
				ClientArqs client = new ClientArqs();
				client.removePasta(user);
				return true;
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(ServerSenha.class.getName());
				lgr.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} finally {
			System.out.println("Finalizando a conexão...");
		}
		System.out.println("O usuário não foi encontrado. Verifique as credenciais e tente novamente!");
		return false;
	}

/*	private String descriptografaString(String pass){
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
	}*/
}


class ClientArqs {
	public ClientArqs(){
        System.out.println("Referenciando cliente...");
        try{
            msi = (InterfaceArquivos) Naming.lookup("rmi://127.0.0.1/Servidor02");
        }catch(Exception e){
            System.out.println("A referência falhou.\n" + e);
            System.out.println("Certifique-se que o Servidor de Registros e a Aplicação Servidora estão funcionando.");
            System.exit(0);
        }
    }
	public boolean criaPasta(String nome, String user) throws IOException{
        return msi.criaPasta(nome, user);
    }
	public boolean criaPasta(String user) throws IOException{
        return msi.criaPasta(user);
    }
	public boolean removePasta(String user) throws IOException{
        return msi.criaPasta(user);
    }
	private InterfaceArquivos msi;
}