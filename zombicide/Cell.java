package zombicide;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.BorderFactory;
import java.io.Serializable;
import javax.swing.ImageIcon;
import java.awt.Image;
import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;

public class Cell extends JPanel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final char type;
    private transient ImageIcon icon; // Para o baú como ícone
    private transient BufferedImage texture; // Para a parede como textura
    private static final Color GOLD = new Color(255, 215, 0); // Cor ouro para o baú

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

    public BufferedImage getTexture() {
        return texture;
    }

    private void setupAppearance() {
        switch (type) {
            case 'P': setBackground(Color.YELLOW); break;
            case 'Z': setBackground(Color.GREEN); break;
            case 'C': setBackground(Color.ORANGE); break;
            case 'R': setBackground(Color.CYAN); break;
            case 'G': setBackground(Color.RED); break;
            case 'B':
                setBackground(GOLD);
                try {
                    ImageIcon originalIcon = new ImageIcon("D:\\Arquivos\\Java\\Poo\\Recursos\\Imagens\\bau.png");
                    if (originalIcon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
                        System.err.println("Imagem do baú não carregada corretamente.");
                        icon = null;
                    } else {
                        Image scaledImage = originalIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
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
                        texture = null;
                    } else {
                        // Redimensiona para o tamanho do botão (40x40)
                        Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                        BufferedImage bufferedImage = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
                        Graphics g = bufferedImage.getGraphics();
                        g.drawImage(scaledImage, 0, 0, null);
                        g.dispose();
                        texture = bufferedImage;
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao carregar a imagem da parede: " + e.getMessage());
                    texture = null;
                }
                break;
            case 'V': setBackground(Color.WHITE); break;
            default: setBackground(Color.WHITE); break;
        }
    }

    public void configureButton(JButton button, boolean isVisible, boolean isMemorized, boolean debugMode) {
        Color background = getBackground();
        if (isVisible) {
            if (type == 'B' && icon != null) {
                button.setIcon(icon);
                button.setText("");
                button.setBackground(background);
                button.setContentAreaFilled(true);
            } else if (type == '1' && texture != null) {
                button.setIcon(null);
                button.setText("");
                button.setContentAreaFilled(false); // Remove o preenchimento padrão
                button.setOpaque(true);
                // Define a pintura personalizada para a textura
                button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
                    @Override
                    protected void paintButtonPressed(Graphics g, javax.swing.AbstractButton b) {
                        if (texture != null) {
                            TexturePaint tp = new TexturePaint(texture, new Rectangle(0, 0, 40, 40));
                            g.setPaint(tp);
                            g.fillRect(0, 0, b.getWidth(), b.getHeight());
                        }
                        super.paintButtonPressed(g, b);
                    }

                    @Override
                    public void paint(Graphics g, javax.swing.JComponent c) {
                        if (texture != null) {
                            TexturePaint tp = new TexturePaint(texture, new Rectangle(0, 0, 40, 40));
                            g.setPaint(tp);
                            g.fillRect(0, 0, c.getWidth(), c.getHeight());
                        }
                        super.paint(g, c);
                    }
                });
                button.setBackground(background); // Fallback
            } else if (type == 'P' || type == 'Z' || type == 'C' || type == 'G') {
                button.setIcon(null);
                button.setText(String.valueOf(type));
                button.setBackground(background);
                button.setContentAreaFilled(true);
            } else {
                button.setIcon(null);
                button.setText("");
                button.setBackground(background);
                button.setContentAreaFilled(true);
            }
        } else if (isMemorized) {
            Color fadedColor = new Color(
                    Math.min(255, background.getRed() + 50),
                    Math.min(255, background.getGreen() + 50),
                    Math.min(255, background.getBlue() + 50)
            );
            if (type == 'B' && icon != null) {
                button.setIcon(icon);
                button.setText("");
                button.setBackground(fadedColor);
                button.setContentAreaFilled(true);
            } else if (type == '1' && texture != null) {
                button.setIcon(null);
                button.setText("");
                button.setContentAreaFilled(false);
                button.setOpaque(true);
                button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
                    @Override
                    protected void paintButtonPressed(Graphics g, javax.swing.AbstractButton b) {
                        if (texture != null) {
                            TexturePaint tp = new TexturePaint(texture, new Rectangle(0, 0, 40, 40));
                            g.setPaint(tp);
                            g.fillRect(0, 0, b.getWidth(), b.getHeight());
                        }
                        super.paintButtonPressed(g, b);
                    }

                    @Override
                    public void paint(Graphics g, javax.swing.JComponent c) {
                        if (texture != null) {
                            TexturePaint tp = new TexturePaint(texture, new Rectangle(0, 0, 40, 40));
                            g.setPaint(tp);
                            g.fillRect(0, 0, c.getWidth(), c.getHeight());
                        }
                        super.paint(g, c);
                    }
                });
                button.setBackground(fadedColor);
            } else if (type == 'V') {
                button.setIcon(null);
                button.setText("");
                button.setBackground(fadedColor);
                button.setContentAreaFilled(true);
            } else {
                button.setIcon(null);
                button.setText(String.valueOf(type));
                button.setBackground(fadedColor);
                button.setContentAreaFilled(true);
            }
        } else {
            button.setIcon(null);
            button.setText("");
            button.setBackground(Color.WHITE);
            button.setContentAreaFilled(true);
            button.setUI(null); // Restaura a UI padrão
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
        in.defaultReadObject();
        setupAppearance(); // Recarrega as imagens após desserialização
    }
}