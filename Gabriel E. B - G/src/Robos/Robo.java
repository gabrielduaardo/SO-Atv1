package Robos;

import java.util.concurrent.Semaphore;

/**
 * Thread de controle de um robô. A cada ciclo executa seus "passos"; cada passo é:
 *   esperar[i].acquire()  ->  move()  ->  liberar[i].release()
 * Bart e Maggie têm 1 passo por ciclo; Lisa tem 2 (move-se entre Bart/Maggie e entre Maggie/Bart).
 * Não há espera ocupada: a thread fica bloqueada no semáforo até chegar a sua vez.
 */
public class Robo extends Thread {
    private final Mundo mundo;
    private final String nome;
    private final Semaphore[] esperar;
    private final Semaphore[] liberar;
    private final int ciclos;

    public Robo(Mundo mundo, String nome, Semaphore[] esperar, Semaphore[] liberar, int ciclos) {
        super(nome);
        this.mundo = mundo;
        this.nome = nome;
        this.esperar = esperar;
        this.liberar = liberar;
        this.ciclos = ciclos;
    }

    @Override
    public void run() {
        try {
            for (int c = 0; c < ciclos; c++) {
                for (int i = 0; i < esperar.length; i++) {
                    esperar[i].acquire();     // espera a sua vez (bloqueia, sem busy wait)
                    mundo.move(nome);         // região crítica: só um robô se move por vez
                    liberar[i].release();     // passa a vez ao próximo robô da sequência
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
