public class Main {
    public static void main(String[] args) throws InterruptedException {
        SharingRideSystem system = new SharingRideSystem(3); // 3 matching worker threads

        // Simulate adding riders and drivers
        for (int i = 0; i < 5; i++) {
            system.addRider(new Rider("Rider" + i));
        }

        for (int i = 0; i < 5; i++) {
            system.addDriver(new Driver("Driver" + i));
        }
        // Start matching workers
        Thread.sleep(4000); // let workers match rides
        // Shutdown the system after some time
        system.shutdown();

        System.out.println("\n--- Completed Rides ---");
        // Print completed rides
        system.printCompletedRides();
    }
}
