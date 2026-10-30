package Barbeiro;

/**
 * Thread de um cliente: executa uma única vez (um corte de cabelo, sem laço).
 */
public class Cliente extends Thread {
    private final Barbearia barbearia;
    private final int id;

    public Cliente(Barbearia barbearia, int id) {
        super("Cliente-" + id);
        this.barbearia = barbearia;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            barbearia.clienteChega(id);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
