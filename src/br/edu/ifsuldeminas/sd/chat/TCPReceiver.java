package br.edu.ifsuldeminas.sd.chat;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPReceiver implements Receiver {
    private int portNumber;
    private MessageContainer container;
    private ServerSocket receiverSocket;
    private Socket socket;
    private DataInputStream inputFlow;

    public TCPReceiver(int portNumber, MessageContainer container) throws ChatException {
        this.portNumber = portNumber;
        this.container = container;
        try {
            prepare();
        } catch (IOException ioException) {
            throw new ChatException("Houve um erro ao iniciar o receiver.", ioException);
        }
        new Thread(this).start();
    }

    private void prepare() throws IOException {
        this.receiverSocket = new ServerSocket(this.portNumber);
    }

    @Override
    public void run() {
        try {
            this.socket = this.receiverSocket.accept();
            this.inputFlow = new DataInputStream(this.socket.getInputStream());
            while (true) {
                receive();
            }
        } catch (IOException ioException) {
            container.newMessage("Houve um erro ao receber as mensagens.");
        }
    }

    private void receive() throws IOException {
        String message = this.inputFlow.readUTF();
        this.container.newMessage(message);
    }
}