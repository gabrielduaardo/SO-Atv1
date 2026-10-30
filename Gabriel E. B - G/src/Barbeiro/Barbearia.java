package Barbeiro;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Recurso compartilhado: a barbearia (solução clássica de Tanenbaum com 3 semáforos).
 *
 * Semáforos:
 *  - customers : conta os clientes à espera de atendimento (inicia em 0). O barbeiro bloqueia aqui (dorme).
 *  - barbers   : número de barbeiros ociosos prontos para cortar (0 ou 1, inicia em 0).
 *  - mutex     : exclusão mútua sobre a variável "waiting" (região crítica).
 *
 * Variável compartilhada:
 *  - waiting : cópia de customers (não se pode ler o valor de um semáforo); só é lida/escrita sob mutex.
 *
 * Prevenção de deadlock: o mutex nunca é mantido enquanto se bloqueia em customers/barbers
 * (o cliente libera o mutex ANTES de fazer barbers.acquire(); o barbeiro libera o mutex antes de cortar).
 */
public class Barbearia {
    private final int cadeiras;

    private final Semaphore customers = new Semaphore(0);
    private final Semaphore barbers = new Semaphore(0);
    private final Semaphore mutex = new Semaphore(1);
    private int waiting = 0;

    // Métricas de validação
    private int maxEspera = 0;                              // alterado sob mutex
    private final AtomicInteger atendidos = new AtomicInteger();
    private final AtomicInteger recusados = new AtomicInteger();
    private final AtomicInteger cortando = new AtomicInteger();
    private final AtomicInteger maxCortando = new AtomicInteger();

    private final CountDownLatch fim;                       // libera quando todo cliente foi atendido ou recusado

    public Barbearia(int cadeiras, int totalClientes) {
        this.cadeiras = cadeiras;
        this.fim = new CountDownLatch(totalClientes);
    }

    /** Código do barbeiro: espera um cliente (dorme se não houver) e o chama. */
    public void proximoCliente() throws InterruptedException {
        if (customers.availablePermits() == 0) {
            System.out.println("Barbeiro: sem clientes, vai dormir...");
        }
        customers.acquire();      // dorme até chegar um cliente
        mutex.acquire();          // região crítica
        waiting--;                // um cliente deixa a sala de espera e vai para a cadeira
        barbers.release();        // barbeiro está pronto para cortar
        mutex.release();          // fim da região crítica
    }

    /** Corte de cabelo (fora da região crítica). */
    public void cortarCabelo() throws InterruptedException {
        int n = cortando.incrementAndGet();
        maxCortando.accumulateAndGet(n, Math::max);
        System.out.println("Barbeiro: cortando cabelo de um cliente...");
        Thread.sleep(100 + (int) (Math.random() * 100));
        cortando.decrementAndGet();
        int total = atendidos.incrementAndGet();
        System.out.println("Barbeiro: corte finalizado (atendidos=" + total + ")");
        fim.countDown();
    }

    /** Código do cliente: entra na barbearia, senta se houver cadeira ou vai embora. */
    public void clienteChega(int id) throws InterruptedException {
        System.out.println("Cliente " + id + ": chegou à barbearia");
        mutex.acquire();                          // região crítica
        if (waiting < cadeiras) {
            waiting++;                            // ocupa uma cadeira de espera
            if (waiting > maxEspera) maxEspera = waiting;
            System.out.println("Cliente " + id + ": sentou para esperar (esperando=" + waiting + "/" + cadeiras + ")");
            customers.release();                  // acorda o barbeiro (se estiver dormindo)
            mutex.release();                      // fim da região crítica
            barbers.acquire();                    // espera o barbeiro ficar livre
            System.out.println("Cliente " + id + ": está sendo atendido");
        } else {
            recusados.incrementAndGet();
            System.out.println("Cliente " + id + ": barbearia cheia, foi embora sem cortar");
            mutex.release();                      // libera antes de sair
            fim.countDown();
        }
    }

    /** Espera todos os clientes serem atendidos/recusados; retorna false se estourar o tempo (deadlock). */
    public boolean aguardarTodos(long timeoutMs) throws InterruptedException {
        return fim.await(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public int getAtendidos()   { return atendidos.get(); }
    public int getRecusados()   { return recusados.get(); }
    public int getMaxEspera()   { return maxEspera; }
    public int getMaxCortando() { return maxCortando.get(); }
    public int getWaiting()     { return waiting; }
    public int getCadeiras()    { return cadeiras; }
}
