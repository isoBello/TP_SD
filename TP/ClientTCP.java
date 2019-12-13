import java.io.*;
import java.util.*;
import java.net.*;

public class ClientTCP{
    public static void main(String[] args) throws IOException{  
              
        try{
            Scanner scanner = new Scanner(System.in);
            InetAddress IP = InetAddress.getByName(args[0]);
            int port = Integer.valueOf(args[1]);

            Socket socket = new Socket(IP, port);

            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            while(true){
                System.out.println(input.readUTF());
                String command = scanner.nextLine();
                output.writeUTF(command);

                if(command.equalsIgnoreCase("Exit")){
                    System.out.println("Encerrando a conexão: " + socket);
                    socket.close();
                    System.out.println("Conexão encerrada!");
                    break;
                }
                String received = input.readUTF();
                System.out.println(received);  
            }
            scanner.close();
            input.close();
            output.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}