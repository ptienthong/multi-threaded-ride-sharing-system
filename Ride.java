public abstract class Ride {
    protected final int rideID;
    protected final String pickup;
    protected final String dropoff;
    protected final double distance;

    private static int idCounter = 0;

    // Constructor to initialize ride details
    public Ride(String pickup, String dropoff, double distance) {
        this.rideID = idCounter++;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.distance = distance;
    }
    // Abstract method to calculate fare required to be implemented by subclasses
    public abstract double calculateFare();
    // Print ride details
    public void rideDetails() {
        System.out.println("Ride ID: " + rideID);
        System.out.println("Pickup: " + pickup);
        System.out.println("Dropoff: " + dropoff);
        System.out.println("Distance: " + distance + " miles");
        System.out.println("Fare: $" + calculateFare());
        System.out.println("--------------------------------");
    }
}
// Subclass of Ride for Standard rides
class StandardRide extends Ride {
    public StandardRide(String pickup, String dropoff, double distance) {
        super(pickup, dropoff, distance);
        System.out.println("StandardRide created with ID: " + rideID);
    }

    @Override
    public double calculateFare() {
        return distance * 1.5;
    }
}
// Subclass of Ride for Premium rides
class PremiumRide extends Ride {
    public PremiumRide(String pickup, String dropoff, double distance) {
        super(pickup, dropoff, distance);
        System.out.println("PremiumRide created with ID: " + rideID);
    }

    @Override
    public double calculateFare() {
        return distance * 3.0;
    }
}
