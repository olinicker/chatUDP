package br.edu.ifsuldeminas.sd.chat.client;

import java.util.Scanner;
import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;

public class Chat {
    public static String KEY_TO_EXIT = "q";
    public static int RECEIVER_BUFFER_SIZE = 1000;

    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        
        System.out.print("Porta local: ");
        int localPort = reader.nextInt();
        
        System.out.print("Porta remota: ");
        int serverPort = reader.nextInt();
        
        reader.nextLine();

        System.out.print("Deseja usar TCP? (S/N): ");
        String tcpInput = reader.nextLine().trim().toUpperCase();
        boolean isTCP = tcpInput.equals("S");
        
        System.out.print("Nome: ");
        String from = reader.nextLine();
        
        try {
            Sender sender = ChatFactory.build(isTCP, "localhost", serverPort, localPort, new SysOutContainer());
            String message = "";
            
            System.out.println("--- Chat " + (isTCP ? "TCP" : "UDP") + " Iniciado ---");

            while (!message.equals(KEY_TO_EXIT)) {
                message = reader.nextLine();
                
                if (!message.equals("")) {
                    if (message.equals("q")) {
                        System.exit(0);
                    } else {
                        String formattedMessage = String.format("%s%s%s", message, MessageContainer.FROM, from);
                        sender.send(formattedMessage);
                    }
                }
            }
        } catch (ChatException chatException) {
            System.err.println("Houve algum erro no chat. Mensagem do erro: " + chatException.getCause().getMessage());
        } finally {
            reader.close();
            System.exit(0);
        }
    }
}