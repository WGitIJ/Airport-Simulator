import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class ControlTower {
    final private Lock lock = new ReentrantLock();
    private Condition landingCondition = lock.newCondition();
    private Condition takeoffCondition = lock.newCondition();
    private Queue<Plane> landingQueue = new LinkedList<>();
    private Queue<Plane> takeoffQueue = new LinkedList<>();
    private int numberOfRunways;
    private boolean[] runwaysInUse;
    private int numOperaciones = 0;



    public ControlTower(int numberOfRunways) {
        this.runwaysInUse = new boolean[numberOfRunways];
        for (int i = 0; i < runwaysInUse.length; i++) {
            runwaysInUse[i] = true;
        }
    }

    //? Mostrar pistas disponibles
    public boolean availableRunway() {
        for (int i = 0; i < runwaysInUse.length; i++) {
            if (runwaysInUse[i]) { // true = libre
                return true;       // hay al menos una pista libre
            }
        }
        return false; // ninguna pista libre
    }

    public int availableRunwaysCount() {
        int count = 0;
        for (boolean runway : runwaysInUse) {
            if (runway) {
                count++;
            }
        }
        return count;
    }

    //? Asignar una pista a un avión
    public int assignRunway(){
        for (int i = 0; i < runwaysInUse.length; i++) {
            if (runwaysInUse[i] == true){
                runwaysInUse[i] = false;
                return i;
            }
        }
        return -1;
    }

    public void ocupyRunway(int runwayIndex){
        runwaysInUse[runwayIndex] = false;
    }


    public void requestLanding(Plane plane){
        lock.lock();
        try{
            landingQueue.add(plane);
            while (availableRunway() == false || getHighestPriorityPlane() != plane){
                System.out.println("No hay pistas disponibles para el avión " + plane.getCode() + ". Esperando para aterrizar...");
                plane.setCondition(AircraftCondition.ESPERANDO);
                landingCondition.await();
            }
            landingQueue.remove(plane);
            int assignedRunway = assignRunway();
            System.out.println("El avión " + plane.getCode() + " está aterrizando.");
            plane.setCondition(AircraftCondition.ATERRIZAR);
            sleep(2000);
            System.out.println("El avión " + plane.getCode() + " ha aterrizado en la pista " + assignedRunway + ".");
            numOperaciones++;
            plane.setCondition(AircraftCondition.EN_TERMINAL);
            runwaysInUse[assignedRunway] = true;
            landingCondition.signalAll(); // Avisar a un avión que espera para aterrizar
            takeoffCondition.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void requestTakeoff(Plane plane){
        takeoffQueue.add(plane);
        lock.lock();
        try{
            while (availableRunway() == false || landingQueue.isEmpty() || takeoffQueue.peek() != plane){
                System.out.println("No hay pistas disponibles para el avión " + plane.getCode() + ". Esperando para despegar...");
                takeoffCondition.await();
                plane.setCondition(AircraftCondition.ESPERANDO);
            }
            takeoffQueue.remove();
            int assignedRunway = assignRunway();
            System.out.println("El avión " + plane.getCode() + " está despegando.");
            plane.setCondition(AircraftCondition.DESPEGAR);
            sleep(2000);
            System.out.println("El avión " + plane.getCode() + " ha despegado de la pista " + assignedRunway + ".");
            plane.setCondition(AircraftCondition.EN_VUELO);
            runwaysInUse[assignedRunway] = true;
            takeoffCondition.signalAll();
            landingCondition.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void printRunwaysStatus() {
        for (int i = 0; i < runwaysInUse.length; i++) {
            String status = runwaysInUse[i] ? "libre" : "ocupada";
            System.out.println("Pista " + i + ": " + status);
        }
    }

    private Plane getHighestPriorityPlane() {
        Plane priorityPlane = null;
        for (Plane p : landingQueue) {
            if (priorityPlane == null || p.getFuelLevel() < priorityPlane.getFuelLevel()) {
                priorityPlane = p;
            }
        }
        return priorityPlane;
    }

    public float getFullTime(){
        return Main.cronometre.mostra();
    }
}
