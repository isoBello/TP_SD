import java.io.*;
import java.net.*;
import java.util.*;
import java.rmi.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ServerTCP {
	public static void main(String[] args) throws IOException {
		int port = Integer.valueOf(args[0]);

		ServerSocket socket = new ServerSocket(port);

		while (true) {
			Socket s = null;

			try {
				s = socket.accept();
				System.out.println("Novo cliente conectado!");
				DataInputStream input = new DataInputStream(s.getInputStream());
				DataOutputStream output = new DataOutputStream(s.getOutputStream());

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
	private DataInputStream input;
	private DataOutputStream output;
	final Socket socket;
	String root = "";
	String passRoot = "";
	String pass;
	String keyPass = "vkrkq9VVjxuRt5vZ";

	public ClientHandler(Socket socket, DataInputStream input, DataOutputStream output) {
		this.socket = socket;
		this.input = input;
		this.output = output;
	}

	@Override
	public void run() {
		String received;
		this.root = "isa";
		this.passRoot = "useroot";
		Map<String, String> comandos = new HashMap<String, String>();
		preencheComandos(comandos);

		try {
			output.writeUTF("O que você quer? [Login | root]:\n" + "Digite Exit para terminar a conexão.");
			received = input.readUTF();
			if (received.equals("Exit")) {
				encerraConexao();
			}else if (received.equals("Login")) {
				rotinaLogin();
			}else if(received.equals("root")){
				rotinaRoot();
			}else{
				output.writeUTF("Entrada inválida.");

			} 
		}catch (IOException e1) {
			e1.printStackTrace();
		}try{
			this.input.close();
			this.output.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}


	private void rotinaLogin() throws IOException {
		String opt;
		String user;
		Map<String, String> comandos = new HashMap<String, String>();

		preencheComandos(comandos);

		output.writeUTF("Digite: [Usuário] ou Exit para terminar a conexão");
		opt = input.readUTF();

		if(opt.equalsIgnoreCase("Exit")) {
			encerraConexao();output.writeUTF("Digite: [Usuário] ou Exit para terminar a conexão");
			opt = input.readUTF();

			if(opt.equalsIgnoreCase("Exit")) {
				encerraConexao();
			}

			user = opt;
			output.writeUTF("Bem vindo, " + user);
			output.writeUTF("Digite: [Senha] ou Exit para terminar a conexão");
			opt = input.readUTF();

			if(opt.equalsIgnoreCase("Exit")) {
				encerraConexao();
			}

			pass = criptografaString(opt);
			ClientSenha client = new ClientSenha();

			boolean result;
			try {
				result = client.verificaSenha(user, pass, root, passRoot);
				if(result == true) {
					gerenciaArquivos(comandos, user);
				}else {
					output.writeUTF("Não foi possível realizar o seu login. Verifique a senha e tente novamente!");
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private void rotinaRoot() throws IOException {
		String opt;
		String user;
		Map<String, String> comandos = new HashMap<String, String>();
		preencheComandos(comandos);

		output.writeUTF("Iniciando conexão segura...\nDigite [Senha] ou Exit para terminar a conexão");
		opt = input.readUTF();

		if(opt.equalsIgnoreCase("Exit")) {
			encerraConexao();
		}

		pass = criptografaString(opt);
		ClientSenha client = new ClientSenha();
		boolean result;
		try {
			result = client.verificaSenha(root, pass, root, passRoot);
			if(result == true) {
				output.writeUTF("Escolha [Adicionar Usuário | Excluir Usuário | Exit]");
				opt = input.readUTF();

				if(opt.equalsIgnoreCase("Adicionar")) {
					output.writeUTF("Escolha: [Usuário | Exit]");
					opt = input.readUTF();
					user = opt;
					output.writeUTF("O usuário escolhido foi: " + user +  "\nEscolha: [Senha | Exit]");
					String pass = input.readUTF();
					pass = criptografaString(pass);
					result = client.addUser(opt, pass, root, passRoot);
					if(result == true) {
						output.writeUTF("Usuário adicionado com sucesso!");
					}else {
						output.writeUTF("Usuário não pôde ser adicionado!");
					}
				}else if(opt.equalsIgnoreCase("Excluir")) {
					output.writeUTF("Escolha: [Usuário | Exit]");
					opt = input.readUTF();
					output.writeUTF("Você escolheu: " + opt +  "\nEscolha: [Senha | Exit]");
					pass = input.readUTF();
					pass = criptografaString(pass);
					result = client.addUser(opt, pass, root, passRoot);
					if(result == true) {
						output.writeUTF("Usuário adicionado com sucesso!");
					}else {
						output.writeUTF("Usuário não pôde ser adicionado!");
					}
				}else if(opt.equalsIgnoreCase("Exit")) {
					encerraConexao();
				}else {
					output.writeUTF("Comando inválido!");
				}
				gerenciaArquivos(comandos, root);
			}else {
				output.writeUTF("Não foi possível realizar o seu login. Verifique a senha e tente novamente!");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
	}

	private void gerenciaArquivos(Map<String, String> comandos, String user) throws IOException {
		ClienteArquivos clientArqs = new ClienteArquivos();
		boolean result;
		
		output.writeUTF("Escolha [help | Digite seu comando | Exit]\n");
		String received = input.readUTF();
		
		switch(received) {
		case "Exit":
			try {
				encerraConexao();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "help":
			comandos.forEach((k, v) -> System.out.println("Comando: " + k + "Realiza: " + v));
			break;
		case "mkdir":
			output.writeUTF("Escolha [Nome do diretório| Exit]\n");
			received = input.readUTF();
			
			result = clientArqs.criaPasta(received, user);
			if(result == true) {
				output.writeUTF("Pasta criada com sucesso!");
				break;
			}else {
				output.writeUTF("Não foi possível criar a pasta. Verifique as credenciais e tente novamente.");
				break;
			}
		case "rmdir":
			output.writeUTF("Escolha [Nome do diretório| Exit]\n");
			received = input.readUTF();
			result = clientArqs.removePasta(received, user);
			if(result == true) {
				output.writeUTF("Pasta emovida com sucesso!");
				break;
			}else {
				output.writeUTF("Não foi possível criar a pasta. Verifique as credenciais e tente novamente.");
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

	private String criptografaString(String pass){
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
	public boolean listaArquivos(String caminho) throws IOException{
		return msi.listaArquivos(caminho);
	}
	public boolean put(String caminho, String arquivo) throws IOException{
		return msi.put(caminho, arquivo);
	}
	public File get(String caminho, String arquivo) throws IOException{
		return msi.get(caminho, arquivo);
	}
	public boolean criaPasta(String user) throws IOException{
		return msi.criaPasta(user);
	}
	public boolean removePasta(String user) throws IOException{
		return msi.criaPasta(user);
	}
	private InterfaceArquivos msi;
}