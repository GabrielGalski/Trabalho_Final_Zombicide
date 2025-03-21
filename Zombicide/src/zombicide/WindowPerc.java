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
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class WindowPerc extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JSlider perceptionSlider;
    private Image backgroundImage;

    public WindowPerc(Zombicide frame, Engine engine, boolean debugMode) {
        setPreferredSize(new Dimension(900, 900));
        setLayout(new GridBagLayout());

        try {
            backgroundImage = ImageIO.read(new File("D:\\Arquivos\\Java\\Poo\\Recursos\\Imagens\\Background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Escolha seu nivel de percepção" + (debugMode ? " (DEBUG)" : ""));
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 25));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        add(title, constraints);

        perceptionSlider = new JSlider(JSlider.HORIZONTAL, 1, 3, 2);
        perceptionSlider.setMajorTickSpacing(1);
        perceptionSlider.setPaintTicks(true);
        perceptionSlider.setPaintLabels(true);
        perceptionSlider.setBackground(new Color(0x5E2129));
        perceptionSlider.setForeground(Color.WHITE);
        constraints.gridy = 1;
        add(perceptionSlider, constraints);

        JLabel text = new JLabel("<html>A percepção altera a dificuldade do jogo.<br>Quanto menor a percepção, menores são suas chances de sobrevivência!</html>");
        text.setForeground(Color.WHITE);
        text.setFont(new Font("Arial", Font.PLAIN, 14));
        constraints.gridy = 2;
        add(text, constraints);

        JButton confirmButton = createButton("Confirmar");
        constraints.gridy = 3;
        add(confirmButton, constraints);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int perception = perceptionSlider.getValue();
                engine.setPerception(perception); // Save perception in engine
                frame.startGame(debugMode, perception); // Use received debugMode
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