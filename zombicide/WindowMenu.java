package zombicide;

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

public class WindowMenu extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image backgroundImage;

    public WindowMenu(Zombicide frame, Engine engine) {
        setPreferredSize(new Dimension(900, 900)); // Define o tamanho do painel como 900x900
        setLayout(new GridBagLayout());

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("D:\\Arquivos\\Java\\Poo\\Recursos\\Imagens\\Background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JButton startButton = createButton("Iniciar");
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(startButton, constraints);

        JButton debugButton = createButton("Modo Debug");
        constraints.gridy = 1;
        add(debugButton, constraints);

        JButton loadButton = createButton("Carregar Jogo");
        constraints.gridy = 2;
        add(loadButton, constraints);

        JButton exitButton = createButton("Sair");
        constraints.gridy = 3;
        add(exitButton, constraints);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.startDifficulty(false); // Normal mode
            }
        });

        debugButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.startDifficulty(true); // Debug mode
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.loadGame(); // Load game
            }
        });

        exitButton.addActionListener(new ActionListener() {
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

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 50));
        return button;
    }
}