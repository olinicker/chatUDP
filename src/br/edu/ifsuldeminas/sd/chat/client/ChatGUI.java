package br.edu.ifsuldeminas.sd.chat.client;

import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
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
    private JComboBox<String> cbProtocolo;
    private JButton btnConectar;

    private JTextArea areaMensagens;
    private JTextField txtMensagem;
    private JButton btnEnviar;

    private Sender sender;
    private String nomeUsuario;

    private final Color bgPrincipal = new Color(54, 57, 63);
    private final Color bgPaineis = new Color(47, 49, 54);
    private final Color bgInputs = new Color(64, 68, 75);
    private final Color textoClaro = new Color(220, 221, 222);
    private final Color corDestaque = new Color(88, 101, 242); // Azul estilo botão
    private final Font fontePadrao = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font fonteTitulos = new Font("Segoe UI", Font.BOLD, 12);

    public ChatGUI() {
        configurarJanela();
        inicializarComponentes();
        montarLayout();
        configurarEventos();
    }

    private void configurarJanela() {
        setTitle("Trabalho TCP | Linicker - Dyogo");
        setSize(850, 600);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgPrincipal);

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    private void estilizarCampo(JComponent componente) {
        componente.setBackground(bgInputs);
        componente.setForeground(textoClaro);
        componente.setFont(fontePadrao);
        componente.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(32, 34, 37), 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }

    private void inicializarComponentes() {
        txtNome = new JTextField(10);
        txtIpRemoto = new JTextField("localhost", 10);
        txtPortaLocal = new JTextField(5);
        txtPortaRemota = new JTextField(5);

        String[] protocolos = {"UDP", "TCP"};
        cbProtocolo = new JComboBox<>(protocolos);
        estilizarCampo(cbProtocolo);

        estilizarCampo(txtNome);
        estilizarCampo(txtIpRemoto);
        estilizarCampo(txtPortaLocal);
        estilizarCampo(txtPortaRemota);

        btnConectar = new JButton("CONECTAR");
        btnConectar.setBackground(corDestaque);
        btnConectar.setForeground(Color.WHITE);
        btnConectar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnConectar.setFocusPainted(false);
        btnConectar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        areaMensagens = new JTextArea();
        areaMensagens.setEditable(false);
        areaMensagens.setLineWrap(true);
        areaMensagens.setWrapStyleWord(true);
        areaMensagens.setBackground(bgPaineis);
        areaMensagens.setForeground(textoClaro);
        areaMensagens.setFont(fontePadrao);
        areaMensagens.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtMensagem = new JTextField();
        estilizarCampo(txtMensagem);
        txtMensagem.setEnabled(false);

        btnEnviar = new JButton("ENVIAR");
        btnEnviar.setBackground(corDestaque);
        btnEnviar.setForeground(Color.WHITE);
        btnEnviar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEnviar.setFocusPainted(false);
        btnEnviar.setEnabled(false);
        btnEnviar.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void montarLayout() {
        Container painelPrincipal = getContentPane();
        painelPrincipal.setLayout(new BorderLayout(15, 15));
        ((JPanel) painelPrincipal).setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel painelConfig = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        painelConfig.setBackground(bgPaineis);
        
        TitledBorder bordaConfig = BorderFactory.createTitledBorder(
                new LineBorder(bgInputs, 1, true), " CONFIGURAÇÕES DE REDE ", 
                TitledBorder.LEFT, TitledBorder.TOP, fonteTitulos, textoClaro);
        painelConfig.setBorder(BorderFactory.createCompoundBorder(bordaConfig, new EmptyBorder(5, 5, 5, 5)));

        adicionarLabelColorida("Nome:", painelConfig);
        painelConfig.add(txtNome);
        adicionarLabelColorida("Porta Local:", painelConfig);
        painelConfig.add(txtPortaLocal);
        adicionarLabelColorida("IP Remoto:", painelConfig);
        painelConfig.add(txtIpRemoto);
        adicionarLabelColorida("Porta Remota:", painelConfig);
        painelConfig.add(txtPortaRemota);
        adicionarLabelColorida("Protocolo:", painelConfig);
        painelConfig.add(cbProtocolo);
        painelConfig.add(btnConectar);

        JScrollPane scrollChat = new JScrollPane(areaMensagens);
        scrollChat.getViewport().setBackground(bgPaineis);
        scrollChat.setBorder(BorderFactory.createLineBorder(bgInputs, 1));
        
        scrollChat.getVerticalScrollBar().setBackground(bgPaineis);

        JPanel painelEnvio = new JPanel(new BorderLayout(10, 0));
        painelEnvio.setBackground(bgPrincipal);
        painelEnvio.add(txtMensagem, BorderLayout.CENTER);
        painelEnvio.add(btnEnviar, BorderLayout.EAST);

        painelPrincipal.add(painelConfig, BorderLayout.NORTH);
        painelPrincipal.add(scrollChat, BorderLayout.CENTER);
        painelPrincipal.add(painelEnvio, BorderLayout.SOUTH);
    }

    private void adicionarLabelColorida(String texto, JPanel painel) {
        JLabel label = new JLabel(texto);
        label.setForeground(textoClaro);
        label.setFont(fonteTitulos);
        painel.add(label);
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
            boolean isTCP = cbProtocolo.getSelectedItem().equals("TCP");

            if (nomeUsuario.isEmpty() || ipRemoto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos corretamente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            btnConectar.setEnabled(false);
            btnConectar.setText("AGUARDANDO O OUTRO PC...");

            new Thread(() -> {
                try {
                    sender = ChatFactory.build(isTCP, ipRemoto, portaRemota, portaLocal, this);

                    SwingUtilities.invokeLater(() -> {
                        txtNome.setEnabled(false);
                        txtIpRemoto.setEnabled(false);
                        txtPortaLocal.setEnabled(false);
                        txtPortaRemota.setEnabled(false);
                        cbProtocolo.setEnabled(false);
                        
                        btnConectar.setText("CONECTADO");
                        btnConectar.setBackground(new Color(59, 165, 93)); // Verde sucesso

                        txtMensagem.setEnabled(true);
                        btnEnviar.setEnabled(true);
                        txtMensagem.requestFocus();

                        String descProtocolo = isTCP ? "TCP" : "UDP";
                        areaMensagens.append("🤖 Sistema: Chat (" + descProtocolo + ") iniciado em " + ipRemoto + ":" + portaRemota + "\n\n");
                    });

                } catch (ChatException ex) {
                    SwingUtilities.invokeLater(() -> {
                        btnConectar.setEnabled(true);
                        btnConectar.setText("CONECTAR");
                        JOptionPane.showMessageDialog(this, "Erro ao iniciar o chat:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "As portas devem ser números inteiros válidos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
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
                areaMensagens.append("⚠️ Erro ao processar mensagem recebida.\n");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatGUI().setVisible(true);
        });
    }
}