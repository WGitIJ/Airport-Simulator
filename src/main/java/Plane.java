import java.util.Random;

public class Plane implements Runnable{
    private String code;
    private AircraftCondition condition;
    private boolean hasLanded = false;
    private boolean hasTakenOff = false;

    private ControlTower controlTower;
    private float fuelLevel;

    public Plane(String code, AircraftCondition actualCondition, ControlTower controlTower) {
        this.code = code;
        this.condition = actualCondition;
        this.controlTower = controlTower;
        this.fuelLevel = getRandomFuelLevel();
    }



    @Override
    public void run() {
        try {
            while (!hasLanded || !hasTakenOff){
                if (condition == AircraftCondition.EN_VUELO){
                    controlTower.requestLanding(this);
                    this.condition = AircraftCondition.ESPERANDO;
                    Thread.sleep(1000); // Simula el tiempo en tierra
                    this.condition = AircraftCondition.EN_TERMINAL;
                    this.hasLanded = true;
                } else if (condition == AircraftCondition.EN_TERMINAL) {
                    controlTower.requestTakeoff(this);
                    this.condition = AircraftCondition.DESPEGAR;
                    Thread.sleep(1000); // Simula el tiempo de vuelo
                    this.condition = AircraftCondition.EN_VUELO;
                    this.hasTakenOff = true;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //region Getters and Setters

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AircraftCondition getCondition() {
        return condition;
    }

    public void setCondition(AircraftCondition condition) {
        this.condition = condition;
    }

    public boolean isHasLanded() {
        return hasLanded;
    }

    public void setHasLanded(boolean hasLanded) {
        this.hasLanded = hasLanded;
    }

    public boolean isHasTakenOff() {
        return hasTakenOff;
    }

    public void setHasTakenOff(boolean hasTakenOff) {
        this.hasTakenOff = hasTakenOff;
    }

    public float getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(float fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    //endregion

    private static float getRandomFuelLevel() {
        Random random = new Random();
        return 150000 + random.nextFloat() * 300000;
    }
}
