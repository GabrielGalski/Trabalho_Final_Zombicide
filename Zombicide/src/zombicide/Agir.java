package zombicide;

import java.util.Random;

public class Agir {
    private static final Random random = new Random();

    public static class danoTotal {
        public final int dano;
        public final int critico;

        public danoTotal(int dano, int critico) {
            this.dano = dano;
            this.critico = critico;
        }
    }

    public static danoTotal ataque(Player attacker, Entidade target) {
        int dano = attacker.getAttack(); // Atualizado de getAtaque
        int critico = random.nextInt(6) + 1;
        if (critico == 6) {
            dano = 3;
        }
        return new danoTotal(dano, critico);
    }

    public static danoTotal ataque(Entidade attacker, Player target) {
        int dano = 1;
        int perceptionRoll = random.nextInt(5);
        if (perceptionRoll <= target.getPercep()) { 
            dano = 0;
        } else {
            int critico = random.nextInt(6) + 1;
            if (critico == 6) {
                dano = 3;
            }
        }
        return new danoTotal(dano, perceptionRoll);
    }

    public static danoTotal atira(Player player, Entidade target) {
        if (player.getTiros() > 0) { 
            player.useTiro(); 
            int critico = random.nextInt(6) + 1;
            int dano = (critico == 6) ? 3 : 2;
            return new danoTotal(dano, critico);
        }
        return new danoTotal(0, -1);
    }

    public static void cura(Player player, Engine engine) {
        if (player.getTemCura() > 0) { 
            int newVida = Math.min(player.getVida() + 2, 5); 
            player.setVida(newVida); 
            player.useCura(); 
        }
    }

    public static String pegou(Player player, Engine engine, int x, int y) {
        if (engine.getCelula(x, y).getTipo() != 'B') {
            return "Nenhum baú aqui!";
        }

        player.incrementBaus();

        if (player.getBaus() == 3 && !player.temTaco()) {
            player.setTemTaco(true);
            engine.setCelula(x, y, Imovel.createImovel('V')); // Remove o baú
            return "Encontrou um taco de basebol! Seu ataque agora causa 2 de dano.";
        }

        int chance = random.nextInt(100); 

        if (chance < 5) {
            ZumbiR zumbiR = new ZumbiR(x, y);
            engine.setCelula(x, y, zumbiR);
            engine.combateStart(x, y);
            return "Você abriu o baú, mas era uma armadilha! Um zumbi rastejante apareceu!";
        } else if (chance < 45) { // 40% de chance (5-44)
            player.addCura();
            return "Você encontrou uma bandagem! Bandagens: " + player.getTemCura();
        } else if (chance < 85) { // 40% de chance (45-84)
            player.addTiro();
            return "Você encontrou uma pistola com 1 bala! Balas: " + player.getTiros();
        } else if (chance < 100) { // 15% de chance (85-99)
            if (!player.temTaco()) {
                player.setTemTaco(true);
                return "Você encontrou um taco de basebol! Seu ataque agora causa 2 de dano.";
            } else {
                player.addCura();
                return "Encontrou uma bandagem. Bandagens: " + player.getTemCura();
            }
        }

        engine.setCelula(x, y, Imovel.createImovel('V')); 
        return "Você abriu o baú, mas não encontrou nada útil.";
    }

    public static boolean esquiva(Player player) {
        return random.nextInt(5) < player.getPercep(); 
    }
}