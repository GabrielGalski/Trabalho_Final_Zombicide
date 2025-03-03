package zumbicidio;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(900, 900));

        mensagem = new JLabel(modoDebug ? "Modo DEBUG ativado!" : "Jogo iniciado! Percepção: " + percepcao);
        mensagem.setForeground(Color.WHITE);

        botaoSalvar = criarBotao("Salvar");
        botaoGuia = criarBotao("Guia");
        botaoSair = criarBotao("Sair");
        botaoBater = criarBotao("Bater");
        botaoAtirar = criarBotao("Atirar");
        botaoBandagem = criarBotao("Bandagem");
        botaoVida = criarBotao("Vida");

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 10, 10);

        // Adiciona a mensagem no topo
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3; // Ocupa 3 colunas
        add(mensagem, c);

        // Adiciona os botões Guia, Salvar e Sair acima dos botões de ação
        c.gridwidth = 1; // Reseta para 1 coluna
        c.gridy = 1;
        c.gridx = 0;
        add(botaoGuia, c);

        c.gridx = 1;
        add(botaoSalvar, c);

        c.gridx = 2;
        add(botaoSair, c);

        // Adiciona os botões de ação em um quadrado centralizado
        c.gridy = 2;
        c.gridx = 0;
        add(botaoBater, c);

        c.gridx = 1;
        add(botaoAtirar, c);

        c.gridy = 3;
        c.gridx = 0;
        add(botaoBandagem, c);

        c.gridx = 1;
        add(botaoVida, c);

        // Adiciona o tabuleiro abaixo dos botões de ação
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 3;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        add(tabuleiro, c);

        // Configura os listeners dos botões
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
                        Agir.heal(personagem);
                        mensagem.setText("Bandagem usada! Vida atual: " + personagem.getVida() + ", Bandagens: " + personagem.getBandagens());
                    } else {
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

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(Color.BLACK);
        botao.setForeground(Color.WHITE);
        botao.setOpaque(true);
        botao.setBorderPainted(false);
        botao.setPreferredSize(new Dimension(100, 30));
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
        } else if (!relatorio.isEmpty()) {
            mensagem.setText(relatorio);
        }
    }
}