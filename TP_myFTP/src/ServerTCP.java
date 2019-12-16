import java.io.*;
import java.net.*;
import java.util.*;
import java.rmi.*;
/*import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;*/

public class ServerTCP {
	public static void main(String[] args) throws IOException {
		int port = Integer.valueOf(args[0]);

		ServerSocket socket = new ServerSocket(port);

		while (true) {
			Socket s = null;

			try {
				s = socket.accept();
				System.out.println("Novo cliente conectado!");
				ObjectInputStream input = new ObjectInputStream(s.getInputStream());
				ObjectOutputStream output = new ObjectOutputStream(s.getOutputStream());

				System.out.println("Atribuindo nova thread para o cliente.");

				Thread thread = new ClientHandler(s, input, output);
				thread.start();
			} catch (Exception e) {
				if (socket != null && socket.isClosed()) {
					try {
						socket.close();
					} catch (IOException io) {
						io.printStackTrace();
					}
				}
				s.close();
				System.out.println("Excceção: " + e.toString());
			}
		}
	}
}

class ClientHandler extends Thread {
	private ObjectInputStream input;
	private ObjectOutputStream output;
	final Socket socket;
	String root = "";
	String passRoot = "";
	String pass;
	String keyPass = "vkrkq9VVjxuRt5vZ";
	Boolean connection = true;
	String message;
	Pacote pacote = new Pacote();

	public ClientHandler(Socket socket, ObjectInputStream input, ObjectOutputStream output) {
		this.socket = socket;
		this.input = input;
		this.output = output;
	}

	@Override
	public void run() {

		this.root = "isa";
		this.passRoot = "useroot";

		while(this.connection) { 
			String received;
			try {
				message = "Login Server FTP:\n> (root/login) : ";
				output.writeObject(new Pacote(message));

				received = ((Pacote)input.readObject()).getMessage().toLowerCase();

				switch(received) {
				case "exit":
					encerraConexao();
					this.connection = false;
					break;

				case "login":
					rotinaLogin();
					this.connection = false;
					break;

				case "root":
					rotinaRoot();
					this.connection = false;
					break;

				default:
					message = "Entrada " + received + " Inválida.";
					output.writeObject(new Pacote(message));
					break;
				} 
			}catch (IOException e1) {
				this.connection = false;
				//e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void rotinaLogin() throws IOException, ClassNotFoundException {
		String opt;
		String user;
		Map<String, String> comandos = new HashMap<String, String>();

		preencheComandos(comandos);

		message ="> user: ";
		output.writeObject(new Pacote(message));
		opt = ((Pacote)input.readObject()).getMessage();

		if(opt.equalsIgnoreCase("Exit")) {
			encerraConexao();
			this.connection = false;
			return;
		}


		user = opt;
		message = user + " > senha: ";
		output.writeObject(new Pacote(message));

		opt = ((Pacote)input.readObject()).getMessage();

		if(opt.equalsIgnoreCase("Exit")) {
			encerraConexao();
			this.connection = false;
			return;
		}

		//pass = criptografaString(opt);
		pass = opt;
		ClientSenha client = new ClientSenha();

		boolean result;
		try {
			result = client.verificaSenha(user, pass, root, passRoot);
			if(result == true) {
				gerenciaArquivos(comandos, user);
			}else {
				message = "Não foi possível realizar o seu login. Verifique a senha e tente novamente!\n>";
				output.writeObject(new Pacote(message));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void rotinaRoot() throws IOException, ClassNotFoundException{
		String opt;

		Map<String, String> comandos = new HashMap<String, String>();
		preencheComandos(comandos);

		message = "Iniciando conexão segura...\nroot> senha: ";
		output.writeObject(new Pacote(message));
		opt = ((Pacote)input.readObject()).getMessage().toLowerCase();


		if(opt.equals("exit")) {
			encerraConexao();
			this.connection = false;
			return;
		}

		//pass = criptografaString(opt);
		pass = opt;
		
		ClientSenha client = new ClientSenha();
		boolean result;
		try {
			result = client.verificaSenha(root, pass, root, passRoot);
			if(result) {
				message = "root> (useradd | userrm | exit): ";
				output.writeObject(new Pacote(message));
				while (result) {
					opt = ((Pacote)input.readObject()).getMessage();

					if(opt.equalsIgnoreCase("useradd")) {
						message = "root> (usuario e senha): ";
						output.writeObject(new Pacote(message));
						opt = ((Pacote)input.readObject()).getMessage();
						String[] entries = opt.split(" ");
						//entries[1] = criptografaString(entries[1]);
						result = client.addUser(entries[0], entries[1], root, passRoot);
						if(result) {
							message = "Usuário adicionado com sucesso!\nroot> (useradd | userrm | exit):  ";
							output.writeObject(new Pacote(message));
						}else {
							message = "Usuário não pôde ser adicionado!\nroot> (useradd | userrm | exit): ";
							output.writeObject(new Pacote(message));
						}

					}else if(opt.equalsIgnoreCase("userrm")) {
						message = "root> (usuario e senha): ";
						output.writeObject(new Pacote(message));
						opt = ((Pacote)input.readObject()).getMessage();
						String[] entries = opt.split(" ");
						//entries[1] = criptografaString(entries[1]);
						result = client.removeUser(entries[0], entries[1], root, passRoot);
						if(result) {
							message = "Usuário adicionado com sucesso!\nroot> (useradd | userrm | exit): ";
							output.writeObject(new Pacote(message));
						}else {
							message = "Usuário não pôde ser adicionado!\nroot> (useradd | userrm | exit): ";
							output.writeObject(new Pacote(message));
						}
					}else if(opt.equalsIgnoreCase("exit")) {
						encerraConexao();
						result = false;
						this.connection = false;
					}else {
						message = "Comando " +opt+ " inválido!\nroot> (useradd | userrm | exit): ";
						output.writeObject(new Pacote(message));
					}
				}
			}else {
				message = "Não foi possível realizar o seu login. Verifique a senha e tente novamente!\n>";
				output.writeObject(new Pacote(message));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return;
	}

	private void gerenciaArquivos(Map<String, String> comandos, String user) throws IOException, ClassNotFoundException {
		ClienteArquivos clientArqs = new ClienteArquivos();
		boolean result;
		String opt; 
		clientArqs.entraPasta(user, "");
		message = user + " > (help|comando|Exit): ";
		output.writeObject(new Pacote(message));
		Boolean usando = true;
		while (usando) {
			pacote = (Pacote)input.readObject();
			opt = (pacote).getMessage().toLowerCase();
			String[] entries = opt.split(" ");

			switch(entries[0]) { 
			case "Exit":
				try {
					encerraConexao();
					this.connection = false;
					usando = false;
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "help":
				message = "";
				comandos.forEach((k, v) -> message += "Comando: " + k + "\t Realiza: " + v + "\n");
				message += user + " > (help|comando|Exit): ";
				output.writeObject(new Pacote(message));
				break;
			case "mkdir":
				result = clientArqs.criaPasta(entries[1]);
				if(result == true) {
					message = "Pasta criada com sucesso!\n" + user + " > (help|comando|Exit): ";
					output.writeObject(new Pacote(message));
					break;
				}else{
					message = "Não foi possível criar a pasta.\n" +user + " > (help|comando|Exit): ";
					output.writeObject(new Pacote(message));
					break;
				}
			case "rmdir":
				result = clientArqs.removePasta(entries[1]);
				if(result == true) {
					message = "Pasta emovida com sucesso!\n"+user + " > (help|comando|Exit): ";
					output.writeObject(new Pacote(message));
					break;
				}else{
					message = "Não foi possível remover a pasta.\n"+user + " > (help|comando|Exit): ";
					output.writeObject(new Pacote(message));
					break;
				}
			case "ls":
				message = clientArqs.listaArquivos();
				if(message != "") {
					message += user + " > (help|comando|Exit): ";
					output.writeObject(new Pacote(message));
					break;
				}else{
					message = "Não foi possível visualizar a pasta.\n"+user + " > (help|comando|Exit): ";
					output.writeObject(new Pacote(message));
					break;
				}
			case "put":			
				System.out.println(pacote.getArquivo());
				result = clientArqs.put(entries[1], pacote.getArquivo());
				if(result == true) {
					message = "Inserido com sucesso!\n"+user + " > (help|comando|Exit): ";
					output.writeObject(new Pacote(message));
					break;
				}else{
					message = "Não foi possível inserir na pasta. Verifique as credenciais e tente novamente.\n"+user + " > (help|comando|Exit): ";
					output.writeObject(new Pacote(message));
					break;
				}
			case "get":
				File arq = clientArqs.get(entries[1], user);
				if(arq.isFile()) {
					message = "Retornado com sucesso!\n"+user + " > (help|comando|Exit): ";
					output.writeObject(new Pacote(message));
					break;
				}else{
					message = "Não foi possível encontrar na pasta. Verifique as credenciais e tente novamente.\n"+user + " > (help|comando|Exit): ";
					output.writeObject(new Pacote(message));
					break;
				}
			case "cd":
				if(entries[1].contains("home")) {
					if(clientArqs.entraPasta("home", user)) {
						message = "Retornado com sucesso!\n"+user + " > (help|comando|Exit): ";
						output.writeObject(new Pacote(message));
						break;
					}else{
						message = "Não foi possível encontrar na pasta. Verifique as credenciais e tente novamente.\n"+user + " > (help|comando|Exit): ";
						output.writeObject(new Pacote(message));
						break;
					}
				} else {
					if(clientArqs.entraPasta(entries[1], user)) {
						message = "Retornado com sucesso!\n"+user + " > (help|comando|Exit): ";
						output.writeObject(new Pacote(message));
						break;
					}else{
						message = "Não foi possível encontrar na pasta. Verifique as credenciais e tente novamente.\n"+user + " > (help|comando|Exit): ";
						output.writeObject(new Pacote(message));
						break;
					}
				}
			default:
				message = "Comando inválido!\n"+user + " > (help|comando|Exit): ";
				output.writeObject(new Pacote(message));
				break;
			}
		}
	}

	private void preencheComandos(Map<String, String> comandos) {
		comandos.put("mkdir", "Cria um novo diretório.");
		comandos.put("rmdir", "Remove um diretório.");
		comandos.put("ls", "Mostra o conteúdo do diretório.");
		comandos.put("put @", "Adiciona arquivo ao diretório.");
		comandos.put("get @","Seleciona arquivo do diretório.");
		comandos.put("cd @", "Caminha até um diretório.");
		comandos.put("cd", "Volta ao diretório inicial.");
	}

	private void encerraConexao() throws IOException{
		System.out.println("Cliente " + this.socket + " enviou exit...");
		System.out.println("Encerrando a conexão.");
		this.socket.close();
		System.out.println("Conexão encerrada!");
	}

	/*private String criptografaString(String pass){
		SecretKey key = new SecretKeySpec(keyPass.getBytes(), "AES");
		String fs = new String("false");

		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);	 	 	 	 
			byte[] msg = cipher.doFinal(pass.getBytes());	 	 	 
			return msg.toString();
		}catch (Exception e) {
			e.printStackTrace();
		}

		return fs;
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
	}*/
}

class ClientSenha{
	public ClientSenha() {
		System.out.println("Referenciando cliente...");
		try{
			msi = (InterfaceSenha) Naming.lookup("rmi://127.0.0.1/Servidor01");
		}catch(Exception e){
			System.out.println("A referência falhou.\n" + e);
			System.out.println("Certifique-se que o Servidor de Registros e a Aplicação Servidora estão funcionando.");
			System.exit(0);
		}
	}
	public boolean verificaSenha(String user, String pass, String root, String passRoot) throws RemoteException, ClassNotFoundException{
		return msi.verificaSenha(user, pass, root, passRoot);
	}
	public boolean addUser(String user, String pass, String root, String passRoot) throws ClassNotFoundException, IOException{
		return msi.addUser(user, pass, root, passRoot);
	}
	public boolean removeUser(String user, String pass, String root, String passRoot) throws ClassNotFoundException, IOException{
		return msi.removeUser(user, pass, root, passRoot);
	}
	private InterfaceSenha msi;
}

class ClienteArquivos {
	public ClienteArquivos(){
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
	public boolean removePasta(String nome, String user) throws IOException{
		return msi.criaPasta(nome, user);
	}
	public boolean entraPasta(String caminho, String user) throws IOException{
		return msi.entraPasta(caminho, user);
	}
	public String listaArquivos() throws IOException{
		return msi.listaArquivos();
	}
	public boolean put(String nome, byte[] arquivo) throws IOException{
		return msi.put(nome, arquivo);
	}
	public File get(String caminho, String arquivo) throws IOException{
		return msi.get(caminho, arquivo);
	}
	public boolean criaPasta(String user) throws IOException{
		return msi.criaPasta(user);
	}
	public boolean removePasta(String user) throws IOException{
		return msi.removePasta(user);
	}
	private InterfaceArquivos msi;
}