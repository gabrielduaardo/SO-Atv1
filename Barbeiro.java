package Barbeiro;

/**
 * Thread do barbeiro: laço infinito (dorme quando não há clientes).
 * É encerrada pelo main via interrupt() quando todos os clientes foram tratados.
 */
public class Barbeiro extends Thread {
    private final Barbearia barbearia;

    public Barbeiro(Barbearia barbearia) {
        super("Barbeiro");
        this.barbearia = barbearia;
    }

    @Override
    public void run() {
        try {
            while (true) {
                barbearia.proximoCliente();
                barbearia.cortarCabelo();
            }
        } catch (InterruptedException e) {
            System.out.println("Barbeiro: fim do expediente.");
        }
    }
}
