public class Driver {
    private static int idCounter = 0;
    private final int driverID;
    private final String name;
    // Constructor to initialize driver details
    public Driver(String name) {
        this.name = name;
        this.driverID = idCounter++;
        System.out.println("Driver created: " + name + " ID: " + driverID);
    }
    // Getters for driver details
    public int getID() { return driverID; }
    public String getName() { return name; }
}