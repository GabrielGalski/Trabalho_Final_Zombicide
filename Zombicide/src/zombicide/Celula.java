package zombicide;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import java.awt.Image;
import javax.swing.JButton;

public class Celula extends JPanel {
    private final char type;
    private transient ImageIcon icon;
    private static final String CAMINHO_IMAGENS = "D:\\Arquivos\\Java\\Poo\\Recursos\\Imagens\\";

    public Celula(char type) {
        this.type = type;
        setOpaque(true);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        celulaDesign();
    }

    public char getType() {
        return type;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    private void celulaDesign() {
        switch (type) {
            case 'P' -> {
                setBackground(Color.YELLOW);
                carregaImagem("personagem.png", 40, 40);
            }
            case 'Z' -> {
                setBackground(Color.BLACK);
                carregaImagem("zumbi.png", 40, 40);
            }
            case 'C' -> {
                setBackground(Color.BLACK);
                carregaImagem("corredor.png", 40, 40);
            }
            case 'R' -> {
                setBackground(Color.BLACK);
                carregaImagem("rastejante.png", 40, 40);
            }
            case 'G' -> {
                setBackground(Color.BLACK);
                carregaImagem("gigante.png", 40, 40);
            }
            case 'B' -> {
                setBackground(Color.BLACK);
                carregaImagem("bau.png", 40, 40);
            }
            case '1' -> {
                setBackground(Color.GRAY);
                carregaImagem("p.png", 80, 80);
            }
            case 'V' -> setBackground(Color.WHITE);
            default -> setBackground(Color.WHITE);
        }
    }

    private void carregaImagem(String nomeArquivo, int largura, int altura) {
        try {
            ImageIcon originalIcon = new ImageIcon(CAMINHO_IMAGENS + nomeArquivo);
            if (originalIcon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
                System.err.println("ERRO! Imagem " + nomeArquivo + " não carregada!");
                icon = null;
            } else {
                Image scaledImage = originalIcon.getImage().getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            System.err.println("ERRO! Imagem " + nomeArquivo + " não carregada! " + e.getMessage());
            icon = null;
        }
    }

    public void botaoDesign(JButton button, boolean isVisible, boolean isMemorized, boolean debugMode) {
        Color background = getBackground();
        if (isVisible || debugMode) { 
            if ((type == 'B' || type == '1' || type == 'Z' || type == 'C' || type == 'G' || type == 'R' || type == 'P') && icon != null) {
                button.setIcon(icon);
                button.setText("");
                button.setBackground(background);
            } else if (type == 'V') {
                button.setIcon(null);
                button.setText("");
                button.setBackground(background);
            } else {
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
            button.setIcon(null);
            button.setText("");
            button.setBackground(Color.WHITE);
        }
    }

    private void recarregarObjeto(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
        in.defaultReadObject();
        celulaDesign();
    }
}