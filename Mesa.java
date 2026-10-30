package Filosofos;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Recurso compartilhado: a mesa com 5 palitos.
 * Cada palito é um semáforo binário (inicia em 1 = livre).
 *
 * Prevenção de deadlock: todo filósofo pega primeiro o palito de MENOR índice.
 * Assim o filósofo 4 pega p0 antes de p4, quebrando a espera circular.
 */
public class Mesa {
    public static final int QUANTIDADE_DE_FILOSOFOS = 5;
    private final Semaphore[] palitos = new Semaphore[QUANTIDADE_DE_FILOSOFOS];

    // Métricas de validação
    private final boolean[] filosofoComendo = new boolean[QUANTIDADE_DE_FILOSOFOS];
    private final AtomicInteger totalRefeicoes = new AtomicInteger();
    private volatile boolean vizinhosComeramJuntos = false;

    public Mesa() {
        for (int indicePalito = 0; indicePalito < QUANTIDADE_DE_FILOSOFOS; indicePalito++) {
            palitos[indicePalito] = new Semaphore(1);
        }
    }

    public void pegarPalitos(int indiceFilosofo) throws InterruptedException {
        int palitoDireito = indiceFilosofo;
        int palitoEsquerdo = (indiceFilosofo + 1) % QUANTIDADE_DE_FILOSOFOS;
        int palitoMenorIndice = Math.min(palitoDireito, palitoEsquerdo);
        int palitoMaiorIndice = Math.max(palitoDireito, palitoEsquerdo);

        palitos[palitoMenorIndice].acquire();
        palitos[palitoMaiorIndice].acquire();
    }

    public void comer(int indiceFilosofo) throws InterruptedException {
        filosofoComendo[indiceFilosofo] = true;
        int vizinhoDireito = (indiceFilosofo + 1) % QUANTIDADE_DE_FILOSOFOS;
        int vizinhoEsquerdo = (indiceFilosofo + QUANTIDADE_DE_FILOSOFOS - 1) % QUANTIDADE_DE_FILOSOFOS;

        if (filosofoComendo[vizinhoDireito] || filosofoComendo[vizinhoEsquerdo]) {
            vizinhosComeramJuntos = true;
        }

        System.out.println("Filosofo " + indiceFilosofo + ": COMENDO");
        Thread.sleep((int) (Math.random() * 50));
        filosofoComendo[indiceFilosofo] = false;
        totalRefeicoes.incrementAndGet();
    }

    public void soltarPalitos(int indiceFilosofo) {
        int palitoDireito = indiceFilosofo;
        int palitoEsquerdo = (indiceFilosofo + 1) % QUANTIDADE_DE_FILOSOFOS;
        int palitoMenorIndice = Math.min(palitoDireito, palitoEsquerdo);
        int palitoMaiorIndice = Math.max(palitoDireito, palitoEsquerdo);

        palitos[palitoMenorIndice].release();
        palitos[palitoMaiorIndice].release();
        System.out.println("Filosofo " + indiceFilosofo + ": soltou os palitos");
    }

    public int getRefeicoes()         { return totalRefeicoes.get(); }
    public boolean isVizinhosJuntos() { return vizinhosComeramJuntos; }
}
