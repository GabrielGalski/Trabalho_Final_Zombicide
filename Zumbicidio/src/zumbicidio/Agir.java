package zumbicidio;

import java.util.Random;

public class Agir {
    private static final Random random = new Random();

    public static class ResultadoAtaque {
        public final int dano;
        public final int dadoCritico; // Alterado para dadoCritico, já que é só para crítico

        public ResultadoAtaque(int dano, int dadoCritico) {
            this.dano = dano;
            this.dadoCritico = dadoCritico;
        }
    }

    // Ataque do personagem contra zumbi (sempre acerta, rola apenas crítico)
    public static ResultadoAtaque bater(Personagem atacante, Entidade alvo) {
        int dano = atacante.getAtaque(); // 1 ou 2 dependendo do taco
        int dadoCritico = random.nextInt(6) + 1; // Dado de 1 a 6 para crítico
        if (dadoCritico == 6) {
            dano = 3; // Crítico
        }
        return new ResultadoAtaque(dano, dadoCritico);
    }

    // Ataque do zumbi contra personagem (mantém percepção para esquiva)
    public static ResultadoAtaque bater(Entidade atacante, Personagem alvo) {
        int dano = 1; // Dano base dos zumbis
        int dadoPercepcao = random.nextInt(5); // Dado de 0 a 4
        if (dadoPercepcao <= alvo.getPercepcao()) {
            dano = 0; // Personagem esquivou
        } else {
            int dadoCritico = random.nextInt(6) + 1; // Dado de 1 a 6
            if (dadoCritico == 6) {
                dano = 3; // Crítico
            }
        }
        return new ResultadoAtaque(dano, dadoPercepcao);
    }

    public static ResultadoAtaque atirar(Personagem personagem, Entidade alvo) {
        if (personagem.getBalas() > 0) {
            personagem.usarBala();
            int dadoCritico = random.nextInt(6) + 1; // Rola crítico mesmo para atirar
            int dano = (dadoCritico == 6) ? 3 : 2; // 3 se crítico, 2 padrão
            return new ResultadoAtaque(dano, dadoCritico);
        }
        return new ResultadoAtaque(0, -1); // Sem balas, dado irrelevante
    }

    public static void heal(Personagem personagem) {
        if (personagem.getBandagens() > 0) {
            int novaVida = Math.min(personagem.getVida() + 2, 5);
            personagem.setVida(novaVida);
            personagem.usarBandagem();
        }
    }

    public static String coletar(Personagem personagem, Tabuleiro tabuleiro, int x, int y) {
        if (tabuleiro.getCelula(x, y).getTipo() != 'B') {
            return "Nenhum baú aqui!";
        }

        for (int i = 0; i < tabuleiro.TAMANHO; i++) {
            for (int j = 0; j < tabuleiro.TAMANHO; j++) {
                Entidade entidade = tabuleiro.getCelula(i, j);
                if (entidade.getTipo() == 'Z' || entidade.getTipo() == 'C' || 
                    entidade.getTipo() == 'R' || entidade.getTipo() == 'G') {
                    if (i == x && j == y) {
                        return "Um baú foi destruído, nenhum item ganho!";
                    }
                }
            }
        }

        int chance = random.nextInt(100);
        if (chance < 40) {
            personagem.adicionarBala();
            return "Você encontrou uma pistola com 1 bala! Balas: " + personagem.getBalas();
        } else if (chance < 80) {
            personagem.adicionarBandagem();
            return "Você encontrou uma bandagem! Bandagens: " + personagem.getBandagens();
        } else {
            if (!personagem.temTaco()) {
                personagem.setTemTaco(true);
                return "Você encontrou um taco de basebol! Seu ataque agora causa 2 de dano.";
            } else {
                personagem.adicionarBandagem();
                return "Você já tem um taco! Encontrou uma bandagem extra. Bandagens: " + personagem.getBandagens();
            }
        }
    }

    public static boolean esquivar(Personagem personagem) {
        return random.nextInt(5) < personagem.getPercepcao();
    }
}