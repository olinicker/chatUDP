package br.edu.ifsuldeminas.sd.chat.client;

import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatGUI extends JFrame implements MessageContainer {

    private JTextField txtNome;
    private JTextField txtIpRemoto;
    private JTextField txtPortaLocal;
    private JTextField txtPortaRemota;
    private JButton btnConectar;

    private JTextArea areaMensagens;
    private JTextField txtMensagem;
    private JButton btnEnviar;

    private Sender sender;
    private String nomeUsuario;

    public ChatGUI() {
        configurarJanela();
        inicializarComponentes();
        montarLayout();
        configurarEventos();
    }

    private void configurarJanela() {
        setTitle("UDP Chat");
        setSize(600, 500);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}
    }

    private void inicializarComponentes() {
        txtNome = new JTextField(10);
        txtIpRemoto = new JTextField("localhost", 10);
        txtPortaLocal = new JTextField(5);
        txtPortaRemota = new JTextField(5);
        btnConectar = new JButton("Conectar");

        areaMensagens = new JTextArea();
        areaMensagens.setEditable(false);
        areaMensagens.setLineWrap(true);
        areaMensagens.setWrapStyleWord(true);
        areaMensagens.setFont(new Font("SansSerif", Font.PLAIN, 14));
        areaMensagens.setMargin(new Insets(5, 5, 5, 5));

        txtMensagem = new JTextField();
        txtMensagem.setEnabled(false); 
        btnEnviar = new JButton("Enviar");
        btnEnviar.setEnabled(false);
    }

    private void montarLayout() {
        Container painelPrincipal = getContentPane();
        painelPrincipal.setLayout(new BorderLayout(10, 10));
        ((JPanel)painelPrincipal).setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel painelConfig = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        painelConfig.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Configuração de Rede", TitledBorder.LEFT, TitledBorder.TOP));

        painelConfig.add(new JLabel("Seu Nome:"));
        painelConfig.add(txtNome);
        painelConfig.add(new JLabel("Porta Local:"));
        painelConfig.add(txtPortaLocal);
        painelConfig.add(new JLabel("IP Remoto:"));
        painelConfig.add(txtIpRemoto);
        painelConfig.add(new JLabel("Porta Remota:"));
        painelConfig.add(txtPortaRemota);
        painelConfig.add(btnConectar);

        JScrollPane scrollChat = new JScrollPane(areaMensagens);
        scrollChat.setBorder(BorderFactory.createTitledBorder("Histórico de Mensagens"));

        JPanel painelEnvio = new JPanel(new BorderLayout(10, 0));
        painelEnvio.add(txtMensagem, BorderLayout.CENTER);
        painelEnvio.add(btnEnviar, BorderLayout.EAST);

        painelPrincipal.add(painelConfig, BorderLayout.NORTH);
        painelPrincipal.add(scrollChat, BorderLayout.CENTER);
        painelPrincipal.add(painelEnvio, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnConectar.addActionListener(this::iniciarConexao);

        btnEnviar.addActionListener(e -> enviarMensagem());
        txtMensagem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enviarMensagem();
                }
            }
        });
    }

    private void iniciarConexao(ActionEvent e) {
        try {
            nomeUsuario = txtNome.getText().trim();
            String ipRemoto = txtIpRemoto.getText().trim();
            int portaLocal = Integer.parseInt(txtPortaLocal.getText().trim());
            int portaRemota = Integer.parseInt(txtPortaRemota.getText().trim());

            if (nomeUsuario.isEmpty() || ipRemoto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos corretamente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            sender = ChatFactory.build(ipRemoto, portaRemota, portaLocal, this);

            txtNome.setEnabled(false);
            txtIpRemoto.setEnabled(false);
            txtPortaLocal.setEnabled(false);
            txtPortaRemota.setEnabled(false);
            btnConectar.setEnabled(false);
            btnConectar.setText("Conectado");

            txtMensagem.setEnabled(true);
            btnEnviar.setEnabled(true);
            txtMensagem.requestFocus();

            areaMensagens.append("--- Sistema: Chat iniciado em " + ipRemoto + ":" + portaRemota + " ---\n");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "As portas devem ser números inteiros válidos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        } catch (ChatException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao iniciar o chat:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enviarMensagem() {
        String texto = txtMensagem.getText().trim();
        if (texto.isEmpty() || sender == null) return;

        try {
            String mensagemFormatada = String.format("%s%s%s", texto, MessageContainer.FROM, nomeUsuario);
            sender.send(mensagemFormatada);
            areaMensagens.append("Você: " + texto + "\n");
            txtMensagem.setText("");
            
        } catch (ChatException ex) {
            JOptionPane.showMessageDialog(this, "Falha ao enviar: " + ex.getMessage(), "Erro de Envio", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void newMessage(String message) {
        if (message == null || message.isEmpty()) return;

        SwingUtilities.invokeLater(() -> {
            try {
                String[] partes = message.split(MessageContainer.FROM);
                if (partes.length >= 2) {
                    String conteudo = partes[0];
                    String remetente = partes[1];
                    areaMensagens.append(remetente + ": " + conteudo + "\n");
                } else {
                    areaMensagens.append("Desconhecido: " + message + "\n");
                }
                areaMensagens.setCaretPosition(areaMensagens.getDocument().getLength());
            } catch (Exception e) {
                areaMensagens.append("--- Erro ao processar mensagem recebida ---\n");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatGUI().setVisible(true);
        });
    }
}