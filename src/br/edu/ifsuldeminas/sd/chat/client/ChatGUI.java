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

    private final Color bgPrincipal = new Color(139, 139, 139);
    private final Color bgPaineis = new Color(198, 198, 198); 
    private final Color bgInputs = new Color(0, 0, 0, 150);
    private final Color textoClaro = new Color(255, 255, 255);
    private final Color textoAmarelo = new Color(255, 255, 85);
    private final Color corDestaque = new Color(85, 255, 85);

    private final Font fontePadrao = new Font("Monospaced", Font.PLAIN, 14);
    private final Font fonteTitulos = new Font("Monospaced", Font.BOLD, 12);
    
    public ChatGUI() {
        configurarJanela();
        inicializarComponentes();
        montarLayout();
        configurarEventos();
    }

    private void configurarJanela() {
        setTitle("Minecraft Chat TCP/UDP | Linicker - Dyogo");
        setSize(1050, 600);
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

    private void tocarSom(String caminhoArquivo) {
        try {
            java.io.File arquivoSom = new java.io.File(caminhoArquivo);
            
            if(arquivoSom.exists()) {
                javax.sound.sampled.AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(arquivoSom);
                javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } else {
            }
            
        } catch (javax.sound.sampled.UnsupportedAudioFileException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void estilizarCampo(JComponent componente) {
        componente.setBackground(bgInputs);
        componente.setForeground(textoClaro);
        componente.setFont(fontePadrao);
        componente.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.BLACK, 2, false),
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

        btnConectar = new JButton("CONECTAR") {
            @Override
            protected void paintComponent(Graphics g) {
                try {
                    Image fundo = new ImageIcon("grass.png").getImage();
                    g.drawImage(fundo, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {}
                super.paintComponent(g);
            }
        };
        btnConectar.setContentAreaFilled(false);
        btnConectar.setForeground(Color.BLACK);
        btnConectar.setFont(new Font("Monospaced", Font.BOLD, 14));
        btnConectar.setFocusPainted(false);
        btnConectar.setBorder(new LineBorder(Color.BLACK, 2, false));
        btnConectar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        areaMensagens = new JTextArea();
        areaMensagens.setEditable(false);
        areaMensagens.setLineWrap(true);
        areaMensagens.setWrapStyleWord(true);
        areaMensagens.setBackground(bgInputs);
        areaMensagens.setForeground(textoClaro);
        areaMensagens.setFont(fontePadrao);
        areaMensagens.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtMensagem = new JTextField();
        estilizarCampo(txtMensagem);
        txtMensagem.setEnabled(false);

        btnEnviar = new JButton("ENVIAR") {
            @Override
            protected void paintComponent(Graphics g) {
                try {
                    Image fundo = new ImageIcon("grass.png").getImage();
                    g.drawImage(fundo, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {}
                super.paintComponent(g);
            }
        };
        btnEnviar.setContentAreaFilled(false);
        btnEnviar.setForeground(Color.BLACK);
        btnEnviar.setFont(new Font("Monospaced", Font.BOLD, 14));
        btnEnviar.setFocusPainted(false);
        btnEnviar.setBorder(new LineBorder(Color.BLACK, 2, false));
        btnEnviar.setEnabled(false);
        btnEnviar.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void montarLayout() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image fundo = new ImageIcon("dirt.png").getImage();
                    g.drawImage(fundo, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {}
            }
        };
        painelPrincipal.setBackground(bgPrincipal);
        painelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(painelPrincipal);

        JPanel painelConfig = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        painelConfig.setBackground(bgPaineis);
        
        TitledBorder bordaConfig = BorderFactory.createTitledBorder(
                new LineBorder(Color.BLACK, 2, false), " INVENTÁRIO DE REDE ", 
                TitledBorder.LEFT, TitledBorder.TOP, fonteTitulos, Color.BLACK);
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
        scrollChat.setBorder(new LineBorder(Color.BLACK, 2, false));
        
        scrollChat.getVerticalScrollBar().setBackground(bgPaineis);

        JPanel painelEnvio = new JPanel(new BorderLayout(10, 0));
        painelEnvio.setOpaque(false);
        painelEnvio.add(txtMensagem, BorderLayout.CENTER);
        painelEnvio.add(btnEnviar, BorderLayout.EAST);

        painelPrincipal.add(painelConfig, BorderLayout.NORTH);
        painelPrincipal.add(scrollChat, BorderLayout.CENTER);
        painelPrincipal.add(painelEnvio, BorderLayout.SOUTH);
    }

    private void adicionarLabelColorida(String texto, JPanel painel) {
        JLabel label = new JLabel(texto);
        label.setForeground(Color.BLACK);
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
            btnConectar.setText("CONECTANDO...");

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

                        txtMensagem.setEnabled(true);
                        btnEnviar.setEnabled(true);
                        txtMensagem.requestFocus();

                        String descProtocolo = isTCP ? "TCP" : "UDP";
                        areaMensagens.append("🤖 Server: Chat (" + descProtocolo + ") iniciado em " + ipRemoto + ":" + portaRemota + "\n\n");
                        
                        tocarSom("xp.wav");
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
            areaMensagens.append("<" + nomeUsuario + "> " + texto + "\n");
            txtMensagem.setText("");
            
            tocarSom("pop.wav");
            
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
                    areaMensagens.append("<" + remetente + "> " + conteudo + "\n");
                    
                    tocarSom("villager.wav");
                    
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