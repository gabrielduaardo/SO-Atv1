package Robos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Recurso compartilhado: o "mundo" onde os robôs se movem.
 * move() é a chamada que indica um movimento do robô; ela registra o movimento
 * e mede se dois robôs chegaram a se mover ao mesmo tempo (o que seria um erro).
 */
public class Mundo {
    private final List<String> historico = new ArrayList<>();
    private final AtomicInteger movendo = new AtomicInteger();
    private final AtomicInteger maxSimultaneos = new AtomicInteger();

    public void move(String robo) throws InterruptedException {
        int n = movendo.incrementAndGet();
        maxSimultaneos.accumulateAndGet(n, Math::max);
        synchronized (historico) { historico.add(robo); }   // apenas registro para validação
        System.out.println(robo + " se move");
        Thread.sleep((int) (Math.random() * 3));            // duração do movimento
        movendo.decrementAndGet();
    }

    public List<String> getHistorico() { synchronized (historico) { return new ArrayList<>(historico); } }
    public int getMaxSimultaneos()     { return maxSimultaneos.get(); }
}
