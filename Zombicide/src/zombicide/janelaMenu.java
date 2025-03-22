package zombicide;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class janelaMenu extends JPanel {
    private Image fundo;

    public janelaMenu(Zombicide frame, Engine engine) {
        setPreferredSize(new Dimension(900, 900));
        setLayout(new GridBagLayout());

        try {
            fundo = ImageIO.read(new File("D:\\Arquivos\\Java\\Poo\\Recursos\\Imagens\\Background.png"));
        } catch (IOException e) {
        }

        GridBagConstraints config = new GridBagConstraints();
        config.insets = new Insets(10, 10, 10, 10);
        config.fill = GridBagConstraints.HORIZONTAL;

        JButton btnInicio = criaBotao("Iniciar");
        config.gridx = 0;
        config.gridy = 0;
        add(btnInicio, config);

        JButton btnDebug = criaBotao("Debug");
        config.gridy = 1;
        add(btnDebug, config);

        JButton btnCarrega = criaBotao("Carregar");
        config.gridy = 2;
        add(btnCarrega, config);

        JButton btnSair = criaBotao("Sair");
        config.gridy = 3;
        add(btnSair, config);

        btnInicio.addActionListener((ActionEvent e) -> {
            frame.startDifficulty(false); 
        });

        btnDebug.addActionListener((ActionEvent e) -> {
            frame.startDifficulty(true); 
        });

        btnCarrega.addActionListener((ActionEvent e) -> {
            frame.loadGame(); 
        });

        btnSair.addActionListener((ActionEvent e) -> {
            frame.dispose();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fundo != null) {
            g.drawImage(fundo, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private JButton criaBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(Color.BLACK);
        botao.setForeground(Color.WHITE);
        botao.setOpaque(true);
        botao.setBorderPainted(false);
        botao.setPreferredSize(new Dimension(150, 50));
        return botao;
    }
}