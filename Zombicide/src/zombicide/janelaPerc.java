package zombicide;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class janelaPerc extends JPanel {
    private final JSlider barraPercep;
    private Image fundo;

    public janelaPerc(Zombicide frame, Engine engine, boolean debugMode) {
        setPreferredSize(new Dimension(900, 900));
        setLayout(new GridBagLayout());

        try {
            fundo = ImageIO.read(new File("D:\\Arquivos\\Java\\Poo\\Recursos\\Imagens\\Background.png"));
        } catch (IOException e) {
        }

        GridBagConstraints config = new GridBagConstraints();
        config.insets = new Insets(10, 10, 10, 10);
        config.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Escolha sua percepção" + (debugMode ? " (DEBUG)" : ""));
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 25));
        config.gridx = 0;
        config.gridy = 0;
        config.gridwidth = 1;
        add(titulo, config);

        barraPercep = new JSlider(JSlider.HORIZONTAL, 1, 3, 2);
        barraPercep.setMajorTickSpacing(1);
        barraPercep.setPaintTicks(true);
        barraPercep.setPaintLabels(true);
        barraPercep.setBackground(new Color(0x5E2129));
        barraPercep.setForeground(Color.WHITE);
        config.gridy = 1;
        add(barraPercep, config);

        JLabel texto = new JLabel("<html>A percepção muda a dificuldade.<br>Menor percepção, menos chance de sobreviver!</html>");
        texto.setForeground(Color.WHITE);
        texto.setFont(new Font("Arial", Font.PLAIN, 14));
        config.gridy = 2;
        add(texto, config);

        JButton btnConfirma = criaBotao("Confirmar");
        config.gridy = 3;
        add(btnConfirma, config);

        btnConfirma.addActionListener((ActionEvent e) -> {
            int percep = barraPercep.getValue();
            engine.setPercepcao(percep);
            frame.startGame(debugMode, percep);
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