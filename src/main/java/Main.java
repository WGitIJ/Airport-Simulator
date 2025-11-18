import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static List<Thread> threadList = new ArrayList<>();
    static Cronometre cronometre = new Cronometre();

    public static void main(String[] args) {
        cronometre.inicia();
        System.out.println("Bienvenido al simulador de torre de control aéreo");
        System.out.println("-----------------------------------------------");

        // Crear la torre de control
        System.out.println("Introduce el número de pistas disponibles (Min: 2):");
        int numRunways = 0;
        while (true){
            numRunways = scanner.nextInt();
            if (numRunways >= 2){
                break;
            }
            System.out.println("El número de pistas debe ser al menos 2. Inténtalo de nuevo:");
        }
        ControlTower controlTower = new ControlTower(numRunways);

        // Crear aviones
        System.out.println("Introduce el número de aviones a simular (Min: 10):");
        int numPlanes = 0;
        while (true){
            numPlanes = scanner.nextInt();
            if (numPlanes >= 10){
                break;
            }
            System.out.println("El número de aviones debe ser al menos 10. Inténtalo de nuevo:");
        }

        List<Plane> planes = new ArrayList<>();
        for (int i = 0; i < numPlanes; i++) {
            Plane plane = new Plane("AV" + (i + 1), getRandomCondition(), controlTower);
            planes.add(plane);
            Thread planeThread = new Thread(plane);
            threadList.add(planeThread);
            planeThread.start();
        }

        // Monitor que imprime el estado cada segundo
        Thread monitor = new Thread(() -> {
            try {
                while (true) {
                    System.out.println("\n----- ESTADO DE LOS AVIONES -----");
                    for (Plane p : planes) {
                        System.out.println("[" + p.getCode() + "] "
                                + p.getCondition()
                                + ", Combustible: " + p.getFuelLevel() + "L]");
                    }
                    System.out.println("----- ESTADO DE LAS PISTAS -----");
                    System.out.println("Pistas disponibles: " + controlTower.availableRunwaysCount());
                    System.out.println("--------------------------------\n");

                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        monitor.setDaemon(true);
        monitor.start();

        // Esperar a que terminen TODOS los aviones
        for (Thread t : threadList) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        cronometre.atura();

        System.out.println("Resumen final: ");
        System.out.println("Tiempo total transcurrido: " + cronometre.mostra() / 1000.0 + " s");
        System.out.println("Simulación finalizada.");
    }

    private static AircraftCondition getRandomCondition() {
        Random random = new Random();
        return random.nextBoolean() ? AircraftCondition.EN_VUELO : AircraftCondition.EN_TERMINAL;
    }
}
