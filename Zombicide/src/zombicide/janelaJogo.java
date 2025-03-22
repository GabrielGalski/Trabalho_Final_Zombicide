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


public class janelaJogo extends JPanel {
    private final Engine engine;
    private final JLabel mensagem;
    private JButton btnSalva, btnGuia, btnSair, btnBate, btnAtira, btnCura, btnVida;

    public janelaJogo(Zombicide frame, Engine engine, boolean debugMode, int percep) {
        this.engine = engine;
        setBackground(new Color(0x5E2129));
        setLayout(new BorderLayout(5, 5));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(new Color(0x5E2129));

        mensagem = new JLabel(debugMode ? "DEBUG ativo!" : "Jogo iniciado! Percepção: " + percep);
        mensagem.setForeground(Color.WHITE);
        mensagem.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        topo.add(mensagem, BorderLayout.CENTER);

        add(topo, BorderLayout.NORTH);
        add(engine, BorderLayout.CENTER);

        JPanel acoes = new JPanel();
        acoes.setLayout(new BoxLayout(acoes, BoxLayout.Y_AXIS));
        acoes.setBackground(new Color(0x5E2129));
        acoes.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        acoes.add(Box.createVerticalGlue());

        JPanel luta = new JPanel(new GridLayout(2, 1, 3, 3));
        luta.setBackground(new Color(0x5E2129));
        luta.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), "Combate",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP, null, Color.WHITE));

        btnBate = criaBotao("Bater", new Dimension(80, 30));
        btnAtira = criaBotao("Atirar", new Dimension(80, 30));
        luta.add(btnBate);
        luta.add(btnAtira);
        acoes.add(luta);

        acoes.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel estado = new JPanel(new GridLayout(2, 1, 3, 3));
        estado.setBackground(new Color(0x5E2129));
        estado.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), "Status",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP, null, Color.WHITE));

        btnCura = criaBotao("Cura", new Dimension(80, 30));
        btnVida = criaBotao("Vida", new Dimension(80, 30));
        estado.add(btnCura);
        estado.add(btnVida);
        acoes.add(estado);

        acoes.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel sistema = new JPanel(new GridLayout(3, 1, 3, 3));
        sistema.setBackground(new Color(0x5E2129));
        sistema.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), "Sistema",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP, null, Color.WHITE));

        btnGuia = criaBotao("Guia", new Dimension(80, 30));
        btnSalva = criaBotao("Salvar", new Dimension(80, 30));
        btnSair = criaBotao("Sair", new Dimension(80, 30));

        sistema.add(btnGuia);
        sistema.add(btnSalva);
        sistema.add(btnSair);
        acoes.add(sistema);

        acoes.add(Box.createVerticalGlue());

        add(acoes, BorderLayout.EAST);

        btnSalva.addActionListener(e -> engine.saveGame(frame.getSavePath()));

        btnGuia.addActionListener(e -> {
            String regras = "Clique para mover o personagem.\n" +
                           "Move uma casa por turno.\n" +
                           "Mate todos os zumbis para vencer.\n" +
                           "Pegue baús para itens.\n\n" +
                           "Tipos de zumbis:\n" +
                           "Normal - 2 vida; 1 casa/turno.\n" +
                           "Corredor - 2 vida; 2 casas/turno; imune a tiro.\n" +
                           "Rastejante - 1 vida; cuidado!\n" +
                           "Gigante - 4 vida; fixo; imune a golpe.\nBoa sorte!";
            JOptionPane.showMessageDialog(this, regras, "Regras", JOptionPane.INFORMATION_MESSAGE);
        });

        btnSair.addActionListener(e -> frame.dispose());

        btnBate.addActionListener((ActionEvent e) -> {
            if (engine.isCombate()) {
                engine.playerTurno("bater");
                atualizaMsg();
            } else {
                mensagem.setText("Sem zumbi para bater!");
            }
        });

        btnAtira.addActionListener((ActionEvent e) -> {
            if (engine.isCombate()) {
                Player player = engine.getPlayer();
                if (player.getTiros() > 0) {
                    engine.playerTurno("atirar");
                    atualizaMsg();
                } else {
                    mensagem.setText("Sem balas!");
                }
            } else {
                mensagem.setText("Sem zumbi para atirar!");
            }
        });

        btnCura.addActionListener((ActionEvent e) -> {
            Player player = engine.getPlayer();
            if (player.getTemCura() > 0) {
                engine.playerTurno("curar");
                atualizaMsg();
            } else {
                mensagem.setText("Sem bandagens!");
            }
        });

        btnVida.addActionListener((ActionEvent e) -> {
            Player player = engine.getPlayer();
            mensagem.setText("Vida: " + player.getVida());
        });

        atualizaMsg();
    }

    private JButton criaBotao(String texto, Dimension tam) {
        JButton botao = new JButton(texto);
        botao.setBackground(Color.BLACK);
        botao.setForeground(Color.WHITE);
        botao.setOpaque(true);
        botao.setBorderPainted(false);
        botao.setPreferredSize(tam);
        return botao;
    }

    private void atualizaMsg() {
        String logLuta = engine.getCombateTxt();
        Player player = engine.getPlayer();
        if (logLuta.contains("Zumbi derrotado!")) {
            String tipoZumbi = "";
            if (logLuta.contains("Zumbi comum")) tipoZumbi = "comum";
            else if (logLuta.contains("Zumbi corredor")) tipoZumbi = "corredor";
            else if (logLuta.contains("Zumbi rastejante")) tipoZumbi = "rastejante";
            else if (logLuta.contains("Zumbi gigante")) tipoZumbi = "gigante";
            mensagem.setText("Zumbi " + tipoZumbi + " morto! Vida: " + player.getVida());
        } else if (!logLuta.isEmpty() && engine.isCombate()) {
            mensagem.setText(logLuta);
        }
    }
}