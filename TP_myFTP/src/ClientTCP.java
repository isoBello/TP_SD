import java.io.*;
import java.util.*;
import java.net.*;

public class ClientTCP{
    public static void main(String[] args) throws IOException{  
        Pacote pacote = new Pacote();
        try{
            Scanner scanner = new Scanner(System.in);
            InetAddress IP = InetAddress.getByName(args[0]);
            int port = Integer.valueOf(args[1]);

            Socket socket = new Socket(IP, port); 
            
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            
            System.out.println(((Pacote)input.readObject()).getMessage());

            while(true){
            	 
                
                String command = scanner.nextLine();
                pacote.setMessage(command);
                //System.out.println(command); 
                if (command.split(" ")[0].contentEquals("put")) {
                	File f = new File(command.split(" ")[1]);
                	FileInputStream a = new FileInputStream(f);
                	BufferedInputStream br = new BufferedInputStream(a);
                	pacote.setArquivo(br.readAllBytes());
                	br.close();
                }
                
                output.writeObject(new Pacote(command));
                
                if(command.equals("Exit")){
                    System.out.println("Encerrando a conexão: " + socket);
                    socket.close();
                    System.out.println("Conexão encerrada!");
                    break;
                }
                
                pacote = (Pacote)input.readObject();
                System.out.print((pacote).getMessage());

                //String received = input.readUTF();
                //System.out.println(received);  
            }
            
            scanner.close();
            input.close();
            output.close();
        }catch(Exception e){ 
            //e.printStackTrace();
        }
    } 
}