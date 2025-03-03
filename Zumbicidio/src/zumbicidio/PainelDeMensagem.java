package zumbicidio;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane; // Importação adicionada
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PainelDeMensagem extends JPanel {
    private final JLabel mensagem;
    private final JButton botaoDebug;
    private final JButton botaoIniciar;
    private final JButton botaoSair;
    private final JButton botaoSalvar;
    private final JButton botaoCarregar;
    private final Tabuleiro tabuleiro;
    private final Zumbicidio frame;
    private boolean jogoIniciado = false;

    public PainelDeMensagem(String texto, Tabuleiro tabuleiro, Zumbicidio frame) {
        this.tabuleiro = tabuleiro;
        this.frame = frame;
        mensagem = new JLabel(texto);
        botaoDebug = new JButton("DEBUG");
        botaoIniciar = new JButton("Iniciar");
        botaoSair = new JButton("Sair");
        botaoSalvar = new JButton("Salvar");
        botaoCarregar = new JButton("Carregar");

        add(mensagem);
        add(botaoDebug);
        add(botaoIniciar);
        add(botaoSalvar);
        add(botaoCarregar);
        add(botaoSair);

        botaoDebug.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alterarTexto("Modo DEBUG ativado!");
                tabuleiro.setModoDebug(true);
                botaoDebug.setEnabled(false);
                botaoIniciar.setEnabled(false);
                botaoCarregar.setEnabled(false);
                jogoIniciado = true;
            }
        });

        botaoIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alterarTexto("Jogo iniciado!");
                tabuleiro.setModoDebug(false);
                botaoDebug.setEnabled(false);
                botaoIniciar.setEnabled(false);
                botaoCarregar.setEnabled(false);
                jogoIniciado = true;
            }
        });

        botaoSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        botaoSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jogoIniciado) {
                    tabuleiro.salvarJogo(frame.getSavePath());
                } else {
                    JOptionPane.showMessageDialog(frame, "Inicie o jogo antes de salvar!");
                }
            }
        });

        botaoCarregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!jogoIniciado) {
                    tabuleiro.carregarJogo(frame.getSavePath());
                    alterarTexto("Jogo carregado!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Não é possível carregar durante o jogo!");
                }
            }
        });
    }

    public void alterarTexto(String texto) {
        mensagem.setText(texto);
    }
}