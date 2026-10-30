package Robos;

import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Problema dos três robôs: sequência Bart -> Lisa -> Maggie -> Lisa -> Bart -> Lisa -> Maggie -> ...
 *
 * A sequência se repete em ciclos de 4 movimentos: B, L, M, L.
 * Um semáforo por "vez" (cada um é um "token" que passa de robô para robô):
 *   vezBart   (inicial 1) : Bart pode se mover  -> só Bart começa liberado
 *   vezLisa1  (inicial 0) : Lisa, depois de Bart
 *   vezMaggie (inicial 0) : Maggie, depois da 1a Lisa
 *   vezLisa2  (inicial 0) : Lisa, depois de Maggie
 * Fluxo:  Bart -> vezLisa1 -> Lisa -> vezMaggie -> Maggie -> vezLisa2 -> Lisa -> vezBart -> Bart ...
 *
 * Como só existe um "token" circulando, apenas um robô se move por vez (exclusão mútua)
 * e a ordem é garantida. Deadlock não ocorre: o grafo de espera é um anel com exatamente
 * um token, então sempre há um robô liberado.
 */
public class TresRobos {

    static boolean cenario(int ciclos) throws InterruptedException {
        System.out.println("\n==================== " + ciclos + " ciclos ====================");
        Mundo mundo = new Mundo();
        Semaphore vezBart   = new Semaphore(1);
        Semaphore vezLisa1  = new Semaphore(0);
        Semaphore vezMaggie = new Semaphore(0);
        Semaphore vezLisa2  = new Semaphore(0);

        Robo bart   = new Robo(mundo, "Bart",   new Semaphore[]{vezBart},               new Semaphore[]{vezLisa1},  ciclos);
        Robo lisa   = new Robo(mundo, "Lisa",   new Semaphore[]{vezLisa1, vezLisa2},    new Semaphore[]{vezMaggie, vezBart}, ciclos);
        Robo maggie = new Robo(mundo, "Maggie", new Semaphore[]{vezMaggie},             new Semaphore[]{vezLisa2},  ciclos);

        // ordem de start propositalmente "invertida": a sequência não depende dela
        maggie.start(); lisa.start(); bart.start();

        boolean semDeadlock = true;
        for (Robo r : new Robo[]{bart, lisa, maggie}) {
            r.join(60000);
            if (r.isAlive()) { semDeadlock = false; r.interrupt(); }
        }

        List<String> h = mundo.getHistorico();
        String[] padrao = {"Bart", "Lisa", "Maggie", "Lisa"};
        boolean ordem = h.size() == 4 * ciclos;
        for (int i = 0; ordem && i < h.size(); i++) ordem = h.get(i).equals(padrao[i % 4]);
        boolean um = mundo.getMaxSimultaneos() <= 1;

        System.out.println("\n--- Validação (" + ciclos + " ciclos) ---");
        System.out.println("Total de movimentos         : " + h.size() + " (esperado " + 4 * ciclos + ")");
        System.out.println("Sequência B,L,M,L,B,L,M,L...: " + ok(ordem));
        System.out.println("Máx. robôs movendo ao mesmo tempo: " + mundo.getMaxSimultaneos() + "  -> " + ok(um));
        System.out.println("Sem deadlock (threads fim.) : " + ok(semDeadlock));
        return ordem && um && semDeadlock;
    }

    static String ok(boolean b) { return b ? "OK" : "FALHOU"; }

    public static void main(String[] args) throws InterruptedException {
        boolean tudo = true;
        tudo &= cenario(2);     // curto: dá para conferir a sequência a olho
        tudo &= cenario(50);    // longo
        tudo &= cenario(500);   // estresse
        // repetições para tentar expor condições de corrida (a ordem de escalonamento varia)
        for (int i = 0; i < 20; i++) tudo &= cenarioSilencioso(100);

        System.out.println("\n==================== RESULTADO FINAL ====================");
        System.out.println(tudo ? "TODOS OS TESTES PASSARAM" : "HOUVE FALHA EM ALGUM TESTE");
    }

    static boolean cenarioSilencioso(int ciclos) throws InterruptedException {
        java.io.PrintStream out = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        boolean r;
        try { r = cenario(ciclos); } finally { System.setOut(out); }
        if (!r) System.out.println("FALHA numa repetição de " + ciclos + " ciclos");
        return r;
    }
}
