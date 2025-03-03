package zumbicidio;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Tabuleiro extends JPanel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Entidade[][] celulas;
    private transient JButton[][] botoes; // Botões não são serializados
    private boolean[][] visivel;
    private boolean[][] memorizado; // Novo array para registrar células já vistas
    public final int TAMANHO = 10;
    private int jogadorX, jogadorY;
    private Personagem personagem;
    private boolean modoDebug = false;
    private boolean emCombate = false;
    private Entidade zumbiEmCombate = null;
    private int combateX, combateY;
    private static final Random random = new Random();
    private StringBuilder relatorioCombate;
    private final transient Zumbicidio frame; // Frame não é serializado

    public Tabuleiro(Zumbicidio frame) {
        this.frame = frame;
        celulas = new Entidade[TAMANHO][TAMANHO];
        botoes = new JButton[TAMANHO][TAMANHO];
        visivel = new boolean[TAMANHO][TAMANHO];
        memorizado = new boolean[TAMANHO][TAMANHO]; // Inicializa matriz de memória
        personagem = new Personagem(0, 0, 5, 3);
        setLayout(new GridLayout(TAMANHO, TAMANHO));
        inicializarTabuleiro();
    }

    public boolean isEmCombate() {
        return emCombate;
    }

    public int getJogadorX() {
        return jogadorX;
    }

    public int getJogadorY() {
        return jogadorY;
    }

    public Personagem getPersonagem() {
        return personagem;
    }

    private void inicializarTabuleiro() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                celulas[i][j] = Imovel.criarImovel('V');
                visivel[i][j] = false;
                memorizado[i][j] = false; // Nada foi memorizado inicialmente
                botoes[i][j] = criarBotao(i, j);
                add(botoes[i][j]);
            }
        }
        jogadorX = 0;
        jogadorY = 0;
        celulas[jogadorX][jogadorY] = personagem;
    }

    public void carregarMapa(Scanner scanner) {
        removeAll();
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                char tipo = scanner.next().charAt(0);
                if (tipo == '0') tipo = 'V'; // Converte '0' dos mapas antigos para 'V'
                celulas[i][j] = criarEntidade(tipo, i, j);
                visivel[i][j] = false;
                memorizado[i][j] = false; // Reseta memória ao carregar novo mapa
                botoes[i][j] = criarBotao(i, j);
                add(botoes[i][j]);
                if (tipo == 'P') {
                    jogadorX = i;
                    jogadorY = j;
                    celulas[i][j] = personagem;
                }
            }
        }
        atualizarVisibilidade();
        revalidate();
        repaint();
    }

    private JButton criarBotao(int i, int j) {
        Entidade entidade = celulas[i][j];
        Celula celula = (Celula) entidade.getVisual();

        JButton botao = new JButton(String.valueOf(entidade.getTipo()));
        botao.setBackground(celula.getBackground());
        botao.setOpaque(true);
        botao.setBorderPainted(true);
        botao.addActionListener(e -> moverJogador(i, j));

        return botao;
    }

    private void moverJogador(int destinoX, int destinoY) {
        if (emCombate) return;

        int dx = Math.abs(destinoX - jogadorX);
        int dy = Math.abs(destinoY - jogadorY);
        if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
            if (destinoX >= 0 && destinoX < TAMANHO && destinoY >= 0 && destinoY < TAMANHO) {
                Entidade destino = celulas[destinoX][destinoY];
                char tipoDestino = destino.getTipo();

                if (destino instanceof ZumbiRastejante) {
                    personagem.setVida(personagem.getVida() - 1);
                    JOptionPane.showMessageDialog(this, "Você foi emboscado por um Zumbi Rastejante! Perdeu 1 de vida.", 
                                                  "Emboscada", JOptionPane.WARNING_MESSAGE);
                    iniciarCombate(destinoX, destinoY);
                } else if (ehZumbi(tipoDestino)) {
                    iniciarCombate(destinoX, destinoY);
                } else if (!ehSolido(destinoX, destinoY)) {
                    if (tipoDestino == 'B') {
                        String resultado = Agir.coletar(personagem, this, destinoX, destinoY);
                        JOptionPane.showMessageDialog(this, resultado, "Item Coletado", JOptionPane.INFORMATION_MESSAGE);
                        celulas[destinoX][destinoY] = Imovel.criarImovel('V');
                    }
                    celulas[jogadorX][jogadorY] = Imovel.criarImovel('V');
                    jogadorX = destinoX;
                    jogadorY = destinoY;
                    personagem.mover(dx, dy);
                    celulas[jogadorX][jogadorY] = personagem;
                    atualizarVisibilidade();
                    moverZumbis();
                    verificarFimDeJogo();
                }
            }
        }
    }

    private void iniciarCombate(int x, int y) {
        emCombate = true;
        zumbiEmCombate = celulas[x][y];
        combateX = x;
        combateY = y;
        relatorioCombate = new StringBuilder("Combate iniciado!\n");

        // Zumbi ataca primeiro
        Agir.ResultadoAtaque resultadoZumbi = Agir.bater(zumbiEmCombate, personagem);
        int danoZumbi = resultadoZumbi.dano;
        
        if (danoZumbi > 0) {
            personagem.setVida(personagem.getVida() - danoZumbi);
            relatorioCombate.append("Zumbi ataca e causa ").append(danoZumbi).append(" de dano!");
            if (resultadoZumbi.dadoCritico == 6) {
                relatorioCombate.append(" (Crítico!)");
            }
            relatorioCombate.append("\n");
        } else {
            relatorioCombate.append("Zumbi ataca, mas você esquiva!").append("\n");
        }

        atualizarVisibilidade();
        JOptionPane.showMessageDialog(this, relatorioCombate.toString(), "Combate", JOptionPane.INFORMATION_MESSAGE);
    }

    public void turnoPersonagem(String acao) {
        if (!emCombate || zumbiEmCombate == null) return;

        int vidaZumbi = 0;

        if (zumbiEmCombate instanceof ZumbiComum) {
            vidaZumbi = ((ZumbiComum) zumbiEmCombate).getVida();
        } else if (zumbiEmCombate instanceof ZumbiCorredor) {
            vidaZumbi = ((ZumbiCorredor) zumbiEmCombate).getVida();
        } else if (zumbiEmCombate instanceof ZumbiRastejante) {
            vidaZumbi = ((ZumbiRastejante) zumbiEmCombate).getVida();
        } else if (zumbiEmCombate instanceof ZumbiGigante) {
            vidaZumbi = ((ZumbiGigante) zumbiEmCombate).getVida();
        }

        int dano = 0;
        switch (acao) {
            case "bater":
                Agir.ResultadoAtaque resultadoAtaque = Agir.bater(personagem, zumbiEmCombate);
                dano = resultadoAtaque.dano;
                vidaZumbi -= dano;
                relatorioCombate.append("Você bateu e causou ").append(dano).append(" de dano!");
                if (resultadoAtaque.dadoCritico == 6) {
                    relatorioCombate.append(" (Crítico!)");
                }
                relatorioCombate.append("\n");
                break;
            case "atirar":
                Agir.ResultadoAtaque resultadoTiro = Agir.atirar(personagem, zumbiEmCombate);
                dano = resultadoTiro.dano;
                if (dano > 0) {
                    vidaZumbi -= dano;
                    relatorioCombate.append("Você atirou e causou ").append(dano).append(" de dano!");
                    if (resultadoTiro.dadoCritico == 6) {
                        relatorioCombate.append(" (Crítico!)");
                    }
                    relatorioCombate.append("\n");
                } else {
                    relatorioCombate.append("Sem balas para atirar!\n");
                }
                break;
            case "curar":
                int vidaAntes = personagem.getVida();
                Agir.heal(personagem);
                int vidaDepois = personagem.getVida();
                if (vidaDepois > vidaAntes) {
                    relatorioCombate.append("Você usou uma bandagem e recuperou ")
                                  .append(vidaDepois - vidaAntes)
                                  .append(" pontos de vida!\n");
                } else {
                    relatorioCombate.append("Você não tem bandagens para usar!\n");
                }
                break;
        }

        if (zumbiEmCombate instanceof ZumbiComum) {
            ((ZumbiComum) zumbiEmCombate).setVida(vidaZumbi);
        } else if (zumbiEmCombate instanceof ZumbiCorredor) {
            ((ZumbiCorredor) zumbiEmCombate).setVida(vidaZumbi);
        } else if (zumbiEmCombate instanceof ZumbiRastejante) {
            ((ZumbiRastejante) zumbiEmCombate).setVida(vidaZumbi);
        } else if (zumbiEmCombate instanceof ZumbiGigante) {
            ((ZumbiGigante) zumbiEmCombate).setVida(vidaZumbi);
        }

        if (vidaZumbi > 0) {
            // Zumbi contra-ataca
            Agir.ResultadoAtaque resultadoZumbi = Agir.bater(zumbiEmCombate, personagem);
            int danoZumbi = resultadoZumbi.dano;
            
            if (danoZumbi > 0) {
                personagem.setVida(personagem.getVida() - danoZumbi);
                relatorioCombate.append("Zumbi ataca e causa ").append(danoZumbi).append(" de dano!");
                if (resultadoZumbi.dadoCritico == 6) {
                    relatorioCombate.append(" (Crítico!)");
                }
                relatorioCombate.append("\n");
            } else {
                relatorioCombate.append("Zumbi ataca, mas você esquiva!").append("\n");
            }
        } else {
            String classeZumbi = "";
            if (zumbiEmCombate instanceof ZumbiComum) classeZumbi = "comum";
            else if (zumbiEmCombate instanceof ZumbiCorredor) classeZumbi = "corredor";
            else if (zumbiEmCombate instanceof ZumbiRastejante) classeZumbi = "rastejante";
            else if (zumbiEmCombate instanceof ZumbiGigante) classeZumbi = "gigante";
            relatorioCombate.append("Zumbi ").append(classeZumbi).append(" derrotado!\n");
            celulas[combateX][combateY] = Imovel.criarImovel('V');
            emCombate = false;
            zumbiEmCombate = null;
        }

        if (personagem.getVida() <= 0) {
            relatorioCombate.append("Você foi derrotado!\n");
            emCombate = false;
            JOptionPane.showMessageDialog(this, relatorioCombate.toString(), "Fim de Jogo", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
            return;
        }

        atualizarVisibilidade();
        JOptionPane.showMessageDialog(this, relatorioCombate.toString(), "Combate", JOptionPane.INFORMATION_MESSAGE);
        verificarFimDeJogo();
    }

    private void moverZumbis() {
        if (emCombate) return;

        // Criar cópia do tabuleiro para evitar problemas de movimento simultâneo de zumbis
        Entidade[][] celulasCopia = new Entidade[TAMANHO][TAMANHO];
        for (int i = 0; i < TAMANHO; i++) {
            System.arraycopy(celulas[i], 0, celulasCopia[i], 0, TAMANHO);
        }

        // Movimento dos Zumbis
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                Entidade entidade = celulasCopia[i][j];
                if (entidade instanceof Movel && ehZumbi(entidade.getTipo())) {
                    // Verificar qual tipo de zumbi está sendo movido
                    boolean isZumbiCorredor = entidade.getTipo() == 'C';
                    
                    // Determinar prioridade de movimentação (horizontal ou vertical)
                    int distX = jogadorX - i;
                    int distY = jogadorY - j;
                    
                    // Determinar se o movimento principal é horizontal ou vertical
                    boolean moveHorizontalFirst = Math.abs(distX) >= Math.abs(distY);
                    
                    // Calcular o sinal da direção do movimento
                    int deltaX = Integer.signum(distX);
                    int deltaY = Integer.signum(distY);
                    
                    // Para ZumbiCorredor, tentar mover duas casas
                    int maxStep = isZumbiCorredor ? 2 : 1;
                    
                    // Tentar mover na direção prioritária primeiro
                    boolean movido = false;
                    
                    if (moveHorizontalFirst) {
                        // Tentar mover horizontalmente
                        movido = tentarMoverZumbi(i, j, deltaX * maxStep, 0, entidade, isZumbiCorredor);
                        
                        // Se não conseguiu mover horizontalmente, tentar verticalmente
                        if (!movido) {
                            movido = tentarMoverZumbi(i, j, 0, deltaY * maxStep, entidade, isZumbiCorredor);
                        }
                    } else {
                        // Tentar mover verticalmente
                        movido = tentarMoverZumbi(i, j, 0, deltaY * maxStep, entidade, isZumbiCorredor);
                        
                        // Se não conseguiu mover verticalmente, tentar horizontalmente
                        if (!movido) {
                            movido = tentarMoverZumbi(i, j, deltaX * maxStep, 0, entidade, isZumbiCorredor);
                        }
                    }
                }
            }
        }
        atualizarVisibilidade();
        verificarFimDeJogo();
    }
    
    private boolean tentarMoverZumbi(int x, int y, int deltaX, int deltaY, Entidade zumbi, boolean isZumbiCorredor) {
        if (deltaX == 0 && deltaY == 0) return false;
        
        // Determinar o passo máximo
        int maxStep = Math.max(Math.abs(deltaX), Math.abs(deltaY));
        
        // Tentar movimento com passo máximo, e então reduzir caso não seja possível
        for (int step = maxStep; step > 0; step--) {
            // Calcular o passo atual
            int passoX = Integer.signum(deltaX) * step;
            int passoY = Integer.signum(deltaY) * step;
            
            // Nova posição calculada
            int novoX = x + passoX;
            int novoY = y + passoY;
            
            // Verificar se a nova posição é válida
            if (isDentroDoTabuleiro(novoX, novoY)) {
                // Se for o jogador, iniciar combate e não mover o zumbi
                if (novoX == jogadorX && novoY == jogadorY) {
                    iniciarCombate(x, y);
                    return true;
                }
                
                // Caso o zumbi corredor precise se mover duas casas, verificar se o caminho está livre
                if (step == 2 && isZumbiCorredor) {
                    int interX = x + Integer.signum(deltaX);
                    int interY = y + Integer.signum(deltaY);
                    
                    if (ehSolido(interX, interY)) {
                        // Caminho intermediário bloqueado, tentar com passo menor
                        continue;
                    }
                }
                
                // Verificar se destino está livre
                if (!ehSolido(novoX, novoY)) {
                    // Mover o zumbi
                    celulas[x][y] = Imovel.criarImovel('V');
                    ((Movel) zumbi).mover(passoX, passoY);
                    celulas[novoX][novoY] = zumbi;
                    
                    // Verificar se o zumbi está próximo ao jogador após o movimento
                    if (Math.abs(novoX - jogadorX) + Math.abs(novoY - jogadorY) == 1) {
                        iniciarCombate(novoX, novoY);
                    }
                    return true;
                }
            }
        }
        
        // Se o zumbi não conseguiu se mover em direção ao jogador, tentar movimento aleatório
        // Array com as quatro direções possíveis: cima, baixo, esquerda, direita
        int[][] direcoes = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        // Embaralhar as direções para tentar movimentos aleatórios
        for (int i = 0; i < direcoes.length; i++) {
            int j = random.nextInt(direcoes.length);
            int[] temp = direcoes[i];
            direcoes[i] = direcoes[j];
            direcoes[j] = temp;
        }
        
        // Tentar cada direção aleatória
        for (int[] dir : direcoes) {
            int novoX = x + dir[0];
            int novoY = y + dir[1];
            
            if (isDentroDoTabuleiro(novoX, novoY) && !ehSolido(novoX, novoY)) {
                // Mover o zumbi aleatoriamente
                celulas[x][y] = Imovel.criarImovel('V');
                ((Movel) zumbi).mover(dir[0], dir[1]);
                celulas[novoX][novoY] = zumbi;
                
                // Verificar se o zumbi está próximo ao jogador após o movimento
                if (Math.abs(novoX - jogadorX) + Math.abs(novoY - jogadorY) == 1) {
                    iniciarCombate(novoX, novoY);
                }
                return true;
            }
        }
        
        // Não foi possível mover o zumbi em nenhuma direção
        return false;
    }

    private boolean isDentroDoTabuleiro(int x, int y) {
        return x >= 0 && x < TAMANHO && y >= 0 && y < TAMANHO;
    }

    private boolean ehSolido(int x, int y) {
        char tipo = celulas[x][y].getTipo();
        return tipo == '1' || tipo == 'Z' || tipo == 'C' || tipo == 'G' || tipo == 'P' || tipo == 'R';
    }

    private boolean ehZumbi(char tipo) {
        return tipo == 'Z' || tipo == 'C' || tipo == 'G' || tipo == 'R';
    }

    public void setModoDebug(boolean debug) {
        modoDebug = debug;
        atualizarVisibilidade();
    }

    private void atualizarVisibilidade() {
        // No modo debug, tudo é visível
        if (modoDebug) {
            for (int i = 0; i < TAMANHO; i++) {
                for (int j = 0; j < TAMANHO; j++) {
                    visivel[i][j] = true;
                    memorizado[i][j] = true; // Tudo é memorizado no modo debug
                }
            }
        } else {
            // Resetar apenas visibilidade atual, não a memória
            for (int i = 0; i < TAMANHO; i++) {
                for (int j = 0; j < TAMANHO; j++) {
                    visivel[i][j] = false;
                }
            }
            
            // Atualizar visibilidade atual com base na posição do jogador
            for (int i = 0; i < TAMANHO; i++) {
                for (int j = 0; j < TAMANHO; j++) {
                    if (podeVerPosicao(i, j)) {
                        visivel[i][j] = true;
                        memorizado[i][j] = true; // Memoriza o que está sendo visto agora
                    }
                }
            }
        }
        
        // Atualizar a interface com base na visibilidade e memória
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                char tipo = celulas[i][j].getTipo();
                String texto = "";
                
                if (visivel[i][j]) {
                    // Células atualmente visíveis mostram seu conteúdo atual
                    if (tipo == 'P' || tipo == 'Z' || tipo == 'C' || tipo == 'G' || tipo == 'B' || tipo == 'R') {
                        texto = String.valueOf(tipo);
                    }
                    botoes[i][j].setText(texto);
                    botoes[i][j].setBackground(((Celula) celulas[i][j].getVisual()).getBackground());
                } else if (memorizado[i][j]) {
                    // Células memorizadas, mas não visíveis atualmente, mostram última aparência conhecida
                    // com uma tonalidade mais clara para indicar que não é visível no momento
                    Color corOriginal = ((Celula) celulas[i][j].getVisual()).getBackground();
                    Color corEsmaecida = new Color(
                        Math.min(255, corOriginal.getRed() + 50),
                        Math.min(255, corOriginal.getGreen() + 50),
                        Math.min(255, corOriginal.getBlue() + 50)
                    );
                    
                    // Mostra entidades memorizadas, mas não zumbis que podem ter se movido
                    if (tipo == '1' || tipo == 'V') {
                        texto = "";
                    } else if (ehZumbi(tipo)) {
                        // Os zumbis não devem aparecer na memória, pois se movem
                        texto = "";
                    } else if (tipo == 'B') {
                        // Baús permanecem visíveis na memória
                        texto = String.valueOf(tipo);
                    }
                    
                    botoes[i][j].setText(texto);
                    botoes[i][j].setBackground(corEsmaecida);
                } else {
                    // Células nunca vistas são brancas e sem texto
                    botoes[i][j].setText("");
                    botoes[i][j].setBackground(Color.WHITE);
                }
            }
        }
        revalidate();
        repaint();
    }

    private boolean podeVerPosicao(int x, int y) {
        // Células adjacentes são sempre visíveis
        if (Math.abs(x - jogadorX) + Math.abs(y - jogadorY) <= 1) {
            return true;
        }
        
        // Verificar linha de visão
        if (x == jogadorX) {
            // Célula está na mesma linha do jogador
            int inicio = Math.min(jogadorY, y);
            int fim = Math.max(jogadorY, y);
            
            for (int j = inicio + 1; j < fim; j++) {
                if (ehObstaculo(jogadorX, j)) {
                    return false;
                }
            }
            return true;
            
        } else if (y == jogadorY) {
            // Célula está na mesma coluna do jogador
            int inicio = Math.min(jogadorX, x);
            int fim = Math.max(jogadorX, x);
            
            for (int i = inicio + 1; i < fim; i++) {
                if (ehObstaculo(i, jogadorY)) {
                    return false;
                }
            }
            return true;
        }
        
        // Se não está na mesma linha ou coluna, não é visível
        return false;
    }

    private boolean ehObstaculo(int x, int y) {
        char tipo = celulas[x][y].getTipo();
        return tipo == '1' || tipo == 'Z' || tipo == 'C' || tipo == 'G' || tipo == 'R';
    }

    private Entidade criarEntidade(char tipo, int x, int y) {
        switch (tipo) {
            case 'P': return personagem;
            case 'Z': return new ZumbiComum(x, y);
            case 'C': return new ZumbiCorredor(x, y);
            case 'R': return new ZumbiRastejante(x, y);
            case 'G': return new ZumbiGigante(x, y);
            case 'B': return new Bau();
            case '1': return Imovel.criarImovel('1');
            case 'V': return Imovel.criarImovel('V');
            default: return Imovel.criarImovel('V');
        }
    }

    public void salvarJogo(String caminho) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminho))) {
            oos.writeObject(this);
            JOptionPane.showMessageDialog(this, "Jogo salvo em " + caminho);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar o jogo: " + e.getMessage());
        }
    }

    public void carregarJogo(String caminho) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminho))) {
            Tabuleiro loaded = (Tabuleiro) ois.readObject();
            for (int i = 0; i < TAMANHO; i++) {
                System.arraycopy(loaded.celulas[i], 0, this.celulas[i], 0, TAMANHO);
                System.arraycopy(loaded.visivel[i], 0, this.visivel[i], 0, TAMANHO);
                System.arraycopy(loaded.memorizado[i], 0, this.memorizado[i], 0, TAMANHO); // Carregar memória salva
            }
            this.jogadorX = loaded.jogadorX;
            this.jogadorY = loaded.jogadorY;
            this.modoDebug = loaded.modoDebug;
            this.personagem = (Personagem) loaded.celulas[jogadorX][jogadorY];
            removeAll();
            botoes = new JButton[TAMANHO][TAMANHO];
            for (int i = 0; i < TAMANHO; i++) {
                for (int j = 0; j < TAMANHO; j++) {
                    botoes[i][j] = criarBotao(i, j);
                    add(botoes[i][j]);
                }
            }
            atualizarVisibilidade();
            revalidate();
            repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar o jogo: " + e.getMessage());
        }
    }

    public String getRelatorioCombate() {
        return relatorioCombate != null ? relatorioCombate.toString() : "";
    }

    public Entidade getCelula(int x, int y) {
        return celulas[x][y];
    }

    public void setCelula(int x, int y, Entidade entidade) {
        celulas[x][y] = entidade;
    }

    private void verificarFimDeJogo() {
        boolean zumbisRestantes = false;
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                Entidade entidade = celulas[i][j];
                if (entidade instanceof ZumbiComum || entidade instanceof ZumbiCorredor || 
                    entidade instanceof ZumbiRastejante || entidade instanceof ZumbiGigante) {
                    zumbisRestantes = true;
                    break;
                }
            }
            if (zumbisRestantes) break;
        }

        if (!zumbisRestantes) {
            JOptionPane.showMessageDialog(this, "Mapa Finalizado! Você exterminou todos os zumbis.", 
                                          "Vitória", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        }
    }
    
}