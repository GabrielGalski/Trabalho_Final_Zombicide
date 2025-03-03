package zumbicidio;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Menu extends JPanel {
    private final Zumbicidio frame;
    private final Tabuleiro tabuleiro;
    private Image backgroundImage;

    public Menu(Zumbicidio frame, Tabuleiro tabuleiro) {
        this.frame = frame;
        this.tabuleiro = tabuleiro;
        setPreferredSize(new Dimension(900, 900)); // Define o tamanho do painel como 900x900
        setLayout(new GridBagLayout());

        // Carrega a imagem de fundo
        try {
            backgroundImage = ImageIO.read(new File("D:\\Arquivos\\Java\\Poo\\Recursos\\Imagens\\Background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JButton botaoIniciar = criarBotao("Iniciar");
        c.gridx = 0;
        c.gridy = 0;
        add(botaoIniciar, c);

        JButton botaoDebug = criarBotao("Modo Debug");
        c.gridy = 1;
        add(botaoDebug, c);

        JButton botaoCarregar = criarBotao("Carregar Jogo");
        c.gridy = 2;
        add(botaoCarregar, c);

        JButton botaoSair = criarBotao("Sair");
        c.gridy = 3;
        add(botaoSair, c);

        botaoIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.iniciarDificuldade(false); // Modo normal
            }
        });

        botaoDebug.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.iniciarDificuldade(true); // Modo DEBUG
            }
        });

        botaoCarregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabuleiro.carregarJogo(frame.getSavePath());
            }
        });

        botaoSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(Color.BLACK);
        botao.setForeground(Color.WHITE);
        botao.setOpaque(true);
        botao.setBorderPainted(false);
        botao.setPreferredSize(new Dimension(150, 50));
        return botao;
    }
}