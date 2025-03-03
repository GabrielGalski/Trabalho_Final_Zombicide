package zumbicidio;

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
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Dificuldade extends JPanel {
    private final Zumbicidio frame;
    private final Tabuleiro tabuleiro;
    private final JSlider sliderPercepcao;
    private final boolean modoDebug; // Novo atributo
    private Image backgroundImage;

    public Dificuldade(Zumbicidio frame, Tabuleiro tabuleiro, boolean modoDebug) {
        this.frame = frame;
        this.tabuleiro = tabuleiro;
        this.modoDebug = modoDebug;
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

        JLabel titulo = new JLabel("Escolha seu nivel de percepção" + (modoDebug ? " (DEBUG)" : ""));
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 25));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        add(titulo, c);

        sliderPercepcao = new JSlider(JSlider.HORIZONTAL, 1, 3, 2);
        sliderPercepcao.setMajorTickSpacing(1);
        sliderPercepcao.setPaintTicks(true);
        sliderPercepcao.setPaintLabels(true);
        sliderPercepcao.setBackground(new Color(0x5E2129));
        sliderPercepcao.setForeground(Color.WHITE);
        c.gridy = 1;
        add(sliderPercepcao, c);

        JLabel texto = new JLabel("<html>A percepção altera a dificuldade do jogo.<br>Quanto menor a percepção, menores são suas chances de sobrevivência!</html>");
        texto.setForeground(Color.WHITE);
        texto.setFont(new Font("Arial", Font.PLAIN, 14));
        c.gridy = 2;
        add(texto, c);

        JButton botaoConfirmar = criarBotao("Confirmar");
        c.gridy = 3;
        add(botaoConfirmar, c);

        botaoConfirmar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int percepcao = sliderPercepcao.getValue();
                frame.iniciarJogo(modoDebug, percepcao); // Usa o modoDebug recebido
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