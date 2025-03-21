package zombicide;

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

public class WindowGame extends JPanel {

	private static final long serialVersionUID = 1L;
	private final Engine engine;
    private final JLabel message;
    private JButton saveButton, guideButton, exitButton, attackButton, shootButton, bandageButton, healthButton;

    public WindowGame(Zombicide frame, Engine engine, boolean debugMode, int perception) {
        this.engine = engine;
        setBackground(new Color(0x5E2129));
        setLayout(new BorderLayout(5, 5));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0x5E2129));
        
        message = new JLabel(debugMode ? "Modo DEBUG ativado!" : "Jogo iniciado! Percepção: " + perception);
        message.setForeground(Color.WHITE);
        message.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        topPanel.add(message, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        
        add(engine, BorderLayout.CENTER);
        
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBackground(new Color(0x5E2129));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Add flexible space at the top
        actionPanel.add(Box.createVerticalGlue());

        JPanel combatPanel = new JPanel(new GridLayout(2, 1, 3, 3));
        combatPanel.setBackground(new Color(0x5E2129));
        combatPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), "Combate", 
            javax.swing.border.TitledBorder.CENTER, 
            javax.swing.border.TitledBorder.TOP, null, Color.WHITE));

        attackButton = createButton("Bater", new Dimension(80, 30));
        shootButton = createButton("Atirar", new Dimension(80, 30));
        combatPanel.add(attackButton);
        combatPanel.add(shootButton);
        actionPanel.add(combatPanel);

        actionPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 3, 3));
        statusPanel.setBackground(new Color(0x5E2129));
        statusPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), "Status", 
            javax.swing.border.TitledBorder.CENTER, 
            javax.swing.border.TitledBorder.TOP, null, Color.WHITE));

        bandageButton = createButton("Cura", new Dimension(80, 30));
        healthButton = createButton("Vida", new Dimension(80, 30));
        statusPanel.add(bandageButton);
        statusPanel.add(healthButton);
        actionPanel.add(statusPanel);

        actionPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel systemPanel = new JPanel(new GridLayout(3, 1, 3, 3));
        systemPanel.setBackground(new Color(0x5E2129));
        systemPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), "Sistema", 
            javax.swing.border.TitledBorder.CENTER, 
            javax.swing.border.TitledBorder.TOP, null, Color.WHITE));

        guideButton = createButton("Guia", new Dimension(80, 30));
        saveButton = createButton("Salvar", new Dimension(80, 30));
        exitButton = createButton("Sair", new Dimension(80, 30));

        systemPanel.add(guideButton);
        systemPanel.add(saveButton);
        systemPanel.add(exitButton);
        actionPanel.add(systemPanel);

        // Add flexible space at the bottom
        actionPanel.add(Box.createVerticalGlue());

        add(actionPanel, BorderLayout.EAST);
        
        saveButton.addActionListener(e -> engine.saveGame(frame.getSavePath()));

        guideButton.addActionListener(e -> {
            String rules = "Clique em uma posição horizontal ou vertical para mexer seu Personagem.\n" +
                           "O personagem pode andar uma casa por turno.\n" +
                           "Elimine todos os Zumbis para concluir o mapa.\n" +
                           "Pegue baus para conseguir itens que te auxiliarão.\n\n" +
                           "Existem 3 classes de zumbis:\n" +
                           "Normais - 2 de vida; Andam uma casa por turno.\n" +
                           "Corredor - 2 de vida; Andam duas casas por turno; não podem ser atingidos por tiros.\n" +
                           "Rastejantes - 1 de vida; cuidado por onde anda!\n" +
                           "Gigantes -  4 de vida; Não se movem; não podem ser atingidos por golpes normais.\nBoa sorte sobrevivente!";
            JOptionPane.showMessageDialog(this, rules, "Regras do Jogo", JOptionPane.INFORMATION_MESSAGE);
        });

        exitButton.addActionListener(e -> frame.dispose());

        attackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (engine.isInCombat()) {
                    engine.playerTurn("bater");
                    updateMessage();
                } else {
                    message.setText("Não há zumbi para bater!");
                }
            }
        });

        shootButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (engine.isInCombat()) {
                    Player player = engine.getPlayer();
                    if (player.getBullets() > 0) {
                        engine.playerTurn("atirar");
                        updateMessage();
                    } else {
                        message.setText("Você não tem balas!");
                    }
                } else {
                    message.setText("Não há zumbi para atirar!");
                }
            }
        });

        bandageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (engine.isInCombat()) {
                    message.setText("Bandagem só pode ser usada fora de combate!");
                } else {
                    Player player = engine.getPlayer();
                    if (player.getBandages() > 0) {
                        engine.playerTurn("curar");
                        // Message is shown by engine via JOptionPane
                    } else {
                        // Use only the top message instead of a dialog
                        message.setText("Você não tem bandagens!");
                    }
                }
            }
        });

        healthButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Player player = engine.getPlayer();
                message.setText("Vida atual: " + player.getHealth());
            }
        });

        updateMessage();
    }

    private JButton createButton(String text, Dimension size) {
        JButton button = new JButton(text);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setPreferredSize(size);
        return button;
    }

    private void updateMessage() {
        String combatLog = engine.getCombatLog();
        Player player = engine.getPlayer();
        if (combatLog.contains("Zumbi derrotado!")) {
            String zombieClass = "";
            if (combatLog.contains("Zumbi comum")) zombieClass = "comum";
            else if (combatLog.contains("Zumbi corredor")) zombieClass = "corredor";
            else if (combatLog.contains("Zumbi rastejante")) zombieClass = "rastejante";
            else if (combatLog.contains("Zumbi gigante")) zombieClass = "gigante";
            message.setText("Zumbi " + zombieClass + " morto! Vida restante: " + player.getHealth());
        } else if (!combatLog.isEmpty() && engine.isInCombat()) {
            message.setText(combatLog);
        }
    }
}