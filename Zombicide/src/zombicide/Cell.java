package zombicide;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.BorderFactory;
import java.io.Serializable;
import javax.swing.ImageIcon;
import java.awt.Image;
import javax.swing.JButton;

public class Cell extends JPanel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final char type;
    private transient ImageIcon icon;

    public Cell(char type) {
        this.type = type;
        setOpaque(true);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setupAppearance();
    }

    public char getType() {
        return type;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    private void setupAppearance() {
        switch (type) {
            case 'P': 
                setBackground(Color.YELLOW); // Fundo amarelo para o personagem
                try {
                    ImageIcon originalIcon = new ImageIcon("D:\\Arquivos\\Java\\Poo\\Recursos\\Imagens\\personagem.png");
                    if (originalIcon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
                        System.err.println("Imagem do personagem não carregada corretamente.");
                        icon = null;
                    } else {
                        Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(scaledImage);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao carregar a imagem do personagem: " + e.getMessage());
                    icon = null;
                }
                break;
            case 'Z':
                setBackground(Color.BLACK);
                try {
                    ImageIcon originalIcon = new ImageIcon("D:\\Arquivos\\Java\\Poo\\Recursos\\Imagens\\zumbi.png");
                    if (originalIcon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
                        System.err.println("Imagem do zumbi não carregada corretamente.");
                        icon = null;
                    } else {
                        Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(scaledImage);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao carregar a imagem do zumbi: " + e.getMessage());
                    icon = null;
                }
                break;
            case 'C':
                setBackground(Color.BLACK);
                try {
                    ImageIcon originalIcon = new ImageIcon("D:\\Arquivos\\Java\\Poo\\Recursos\\Imagens\\corredor.png");
                    if (originalIcon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
                        System.err.println("Imagem do corredor não carregada corretamente.");
                        icon = null;
                    } else {
                        Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(scaledImage);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao carregar a imagem do corredor: " + e.getMessage());
                    icon = null;
                }
                break;
            case 'R':
                setBackground(Color.BLACK); // Fundo preto para o rastejante
                try {
                    ImageIcon originalIcon = new ImageIcon("D:\\Arquivos\\Java\\Poo\\Recursos\\Imagens\\rastejante.png");
                    if (originalIcon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
                        System.err.println("Imagem do rastejante não carregada corretamente.");
                        icon = null;
                    } else {
                        Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(scaledImage);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao carregar a imagem do rastejante: " + e.getMessage());
                    icon = null;
                }
                break;
            case 'G':
                setBackground(Color.BLACK);
                try {
                    ImageIcon originalIcon = new ImageIcon("D:\\Arquivos\\Java\\Poo\\Recursos\\Imagens\\gigante.png");
                    if (originalIcon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
                        System.err.println("Imagem do gigante não carregada corretamente.");
                        icon = null;
                    } else {
                        Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(scaledImage);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao carregar a imagem do gigante: " + e.getMessage());
                    icon = null;
                }
                break;
            case 'B':
                setBackground(Color.BLACK);
                try {
                    ImageIcon originalIcon = new ImageIcon("D:\\Arquivos\\Java\\Poo\\Recursos\\Imagens\\bau.png");
                    if (originalIcon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
                        System.err.println("Imagem do baú não carregada corretamente.");
                        icon = null;
                    } else {
                        Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(scaledImage);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao carregar a imagem do baú: " + e.getMessage());
                    icon = null;
                }
                break;
            case '1':
                setBackground(Color.GRAY);
                try {
                    ImageIcon originalIcon = new ImageIcon("D:\\Arquivos\\Java\\Poo\\Recursos\\Imagens\\p.png");
                    if (originalIcon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
                        System.err.println("Imagem da parede não carregada corretamente.");
                        icon = null;
                    } else {
                        int buttonWidth = 80;
                        int buttonHeight = 80;
                        Image scaledImage = originalIcon.getImage().getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(scaledImage);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao carregar a imagem da parede: " + e.getMessage());
                    icon = null;
                }
                break;
            case 'V': 
                setBackground(Color.WHITE); 
                break;
            default: 
                setBackground(Color.WHITE); 
                break;
        }
    }
    public void configureButton(JButton button, boolean isVisible, boolean isMemorized, boolean debugMode) {
        Color background = getBackground();
        if (isVisible || debugMode) { // Garante visibilidade no modo Debug
            if ((type == 'B' || type == '1' || type == 'Z' || type == 'C' || type == 'G' || type == 'R' || type == 'P') && icon != null) {
                // Caso tenha uma imagem associada, exibe a imagem
                button.setIcon(icon);
                button.setText("");
                button.setBackground(background);
            } else if (type == 'V') {
                // Caso seja vazio, não exibe texto ou ícone
                button.setIcon(null);
                button.setText("");
                button.setBackground(background);
            } else {
                // Caso não tenha imagem e não seja vazio, exibe o texto do tipo
                button.setIcon(null);
                button.setText(String.valueOf(type));
                button.setBackground(background);
            }
        } else if (isMemorized) {
            Color fadedColor = new Color(
                    Math.min(255, background.getRed() + 50),
                    Math.min(255, background.getGreen() + 50),
                    Math.min(255, background.getBlue() + 50)
            );
            if ((type == 'B' || type == '1' || type == 'Z' || type == 'C' || type == 'G' || type == 'R' || type == 'P') && icon != null) {
                button.setIcon(icon);
                button.setText("");
                button.setBackground(fadedColor);
            } else if (type == 'V') {
                button.setIcon(null);
                button.setText("");
                button.setBackground(fadedColor);
            } else {
                button.setIcon(null);
                button.setText(String.valueOf(type));
                button.setBackground(fadedColor);
            }
        } else {
            // Caso não esteja visível nem memorizado
            button.setIcon(null);
            button.setText("");
            button.setBackground(Color.WHITE);
        }
    }
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
        in.defaultReadObject();
        setupAppearance(); // Recarrega as imagens após desserialização
    }
}