package zumbicidio;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PainelDeJogo extends JPanel {
    private final Zumbicidio frame;
    private final Tabuleiro tabuleiro;
    private final JLabel mensagem;
    private final int percepcao;
    private JButton botaoSalvar, botaoGuia, botaoSair, botaoBater, botaoAtirar, botaoBandagem, botaoVida;

    public PainelDeJogo(Zumbicidio frame, Tabuleiro tabuleiro, boolean modoDebug, int percepcao) {
        this.frame = frame;
        this.tabuleiro = tabuleiro;
        this.percepcao = percepcao;
        setBackground(new Color(0x5E2129));
        setLayout(new BorderLayout(5, 5));
        
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(new Color(0x5E2129));
        
        mensagem = new JLabel(modoDebug ? "Modo DEBUG ativado!" : "Jogo iniciado! Percepção: " + percepcao);
        mensagem.setForeground(Color.WHITE);
        mensagem.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        painelTopo.add(mensagem, BorderLayout.CENTER);
        
        add(painelTopo, BorderLayout.NORTH);
        
        add(tabuleiro, BorderLayout.CENTER);
        
JPanel painelAcoes = new JPanel();
painelAcoes.setLayout(new BoxLayout(painelAcoes, BoxLayout.Y_AXIS));
painelAcoes.setBackground(new Color(0x5E2129));
painelAcoes.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

// Adiciona espaço flexível no topo
painelAcoes.add(Box.createVerticalGlue());

JPanel painelCombate = new JPanel(new GridLayout(2, 1, 3, 3));
painelCombate.setBackground(new Color(0x5E2129));
painelCombate.setBorder(BorderFactory.createTitledBorder(
    BorderFactory.createLineBorder(Color.WHITE), "Combate", 
    javax.swing.border.TitledBorder.CENTER, 
    javax.swing.border.TitledBorder.TOP, null, Color.WHITE));

botaoBater = criarBotao("Bater", new Dimension(80, 30));
botaoAtirar = criarBotao("Atirar", new Dimension(80, 30));
painelCombate.add(botaoBater);
painelCombate.add(botaoAtirar);
painelAcoes.add(painelCombate);

painelAcoes.add(Box.createRigidArea(new Dimension(0, 10)));

JPanel painelStatus = new JPanel(new GridLayout(2, 1, 3, 3));
painelStatus.setBackground(new Color(0x5E2129));
painelStatus.setBorder(BorderFactory.createTitledBorder(
    BorderFactory.createLineBorder(Color.WHITE), "Status", 
    javax.swing.border.TitledBorder.CENTER, 
    javax.swing.border.TitledBorder.TOP, null, Color.WHITE));

botaoBandagem = criarBotao("Cura", new Dimension(80, 30));
botaoVida = criarBotao("Vida", new Dimension(80, 30));
painelStatus.add(botaoBandagem);
painelStatus.add(botaoVida);
painelAcoes.add(painelStatus);

painelAcoes.add(Box.createRigidArea(new Dimension(0, 10)));

JPanel painelSistema = new JPanel(new GridLayout(3, 1, 3, 3));
painelSistema.setBackground(new Color(0x5E2129));
painelSistema.setBorder(BorderFactory.createTitledBorder(
    BorderFactory.createLineBorder(Color.WHITE), "Sistema", 
    javax.swing.border.TitledBorder.CENTER, 
    javax.swing.border.TitledBorder.TOP, null, Color.WHITE));

botaoGuia = criarBotao("Guia", new Dimension(80, 30));
botaoSalvar = criarBotao("Salvar", new Dimension(80, 30));
botaoSair = criarBotao("Sair", new Dimension(80, 30));

painelSistema.add(botaoGuia);
painelSistema.add(botaoSalvar);
painelSistema.add(botaoSair);
painelAcoes.add(painelSistema);

// Adiciona espaço flexível no final
painelAcoes.add(Box.createVerticalGlue());

add(painelAcoes, BorderLayout.EAST);
        
        botaoSalvar.addActionListener(e -> tabuleiro.salvarJogo(frame.getSavePath()));

        botaoGuia.addActionListener(e -> {
            String regras = "Clique em uma posição horizontal ou vertical para mexer seu Personagem\n" +
                            "Personagem pode andar uma casa por turno\n" +
                            "Elimine todos os Zumbis para concluir o mapa\n" +
                            "Pegue baus para conseguir itens que te auxiliarão\n" +
                            "Existem 3 classes de zumbis:\n" +
                            "Normais - 2 de vida; Andam uma casa por turno.\n" +
                            "Corredor - 2 de vida; Andam duas casas por turno.\n" +
                            "Rastejantes - 1 de vida; boa sorte!\n" +
                            "Gigantes -  4 de vida; Não se movem; Implacáveis! Boa sorte sobrevivente!";
            JOptionPane.showMessageDialog(this, regras, "Regras do Jogo", JOptionPane.INFORMATION_MESSAGE);
        });

        botaoSair.addActionListener(e -> frame.dispose());

        botaoBater.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tabuleiro.isEmCombate()) {
                    tabuleiro.turnoPersonagem("bater");
                    atualizarMensagem();
                } else {
                    mensagem.setText("Não há zumbi para bater!");
                }
            }
        });

        botaoAtirar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tabuleiro.isEmCombate()) {
                    Personagem personagem = tabuleiro.getPersonagem();
                    if (personagem.getBalas() > 0) {
                        tabuleiro.turnoPersonagem("atirar");
                        atualizarMensagem();
                    } else {
                        mensagem.setText("Você não tem balas!");
                    }
                } else {
                    mensagem.setText("Não há zumbi para atirar!");
                }
            }
        });

botaoBandagem.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        if (tabuleiro.isEmCombate()) {
            mensagem.setText("Bandagem só pode ser usada fora de combate!");
        } else {
            Personagem personagem = tabuleiro.getPersonagem();
            if (personagem.getBandagens() > 0) {
                tabuleiro.turnoPersonagem("curar");
                // A mensagem é exibida pelo próprio tabuleiro via JOptionPane
            } else {
                // Usar apenas a mensagem no topo do jogo ao invés de um diálogo
                mensagem.setText("Você não tem bandagens!");
            }
        }
    }
});

        botaoVida.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Personagem personagem = tabuleiro.getPersonagem();
                mensagem.setText("Vida atual: " + personagem.getVida());
            }
        });

        atualizarMensagem();
    }

    private JButton criarBotao(String texto, Dimension tamanho) {
        JButton botao = new JButton(texto);
        botao.setBackground(Color.BLACK);
        botao.setForeground(Color.WHITE);
        botao.setOpaque(true);
        botao.setBorderPainted(false);
        botao.setPreferredSize(tamanho);
        return botao;
    }

    private void atualizarMensagem() {
        String relatorio = tabuleiro.getRelatorioCombate();
        Personagem personagem = tabuleiro.getPersonagem();
        if (relatorio.contains("Zumbi derrotado!")) {
            String classeZumbi = "";
            if (relatorio.contains("Zumbi comum")) classeZumbi = "comum";
            else if (relatorio.contains("Zumbi corredor")) classeZumbi = "corredor";
            else if (relatorio.contains("Zumbi rastejante")) classeZumbi = "rastejante";
            else if (relatorio.contains("Zumbi gigante")) classeZumbi = "gigante";
            mensagem.setText("Zumbi " + classeZumbi + " morto! Vida restante: " + personagem.getVida());
        } else if (!relatorio.isEmpty() && tabuleiro.isEmCombate()) {
            mensagem.setText(relatorio);
        }
    }
}