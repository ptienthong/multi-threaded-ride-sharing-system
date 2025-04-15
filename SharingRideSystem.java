import java.util.*;
import java.util.concurrent.*;

public class SharingRideSystem {
    private final BlockingQueue<Rider> riderQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Driver> driverQueue = new LinkedBlockingQueue<>();
    private final List<Ride> completedRides = Collections.synchronizedList(new ArrayList<>());

    private final ExecutorService executor;
    // Constructor to initialize the system with a specified number of worker threads
    // This allows for concurrent processing of ride requests
    public SharingRideSystem(int workerThreads) {
        this.executor = Executors.newFixedThreadPool(workerThreads);
        startMatchingWorkers(workerThreads);
    }
    // Method to add a rider to the queue
    // This method is synchronized to ensure thread safety when adding riders
    public void addRider(Rider rider) {
        try {
            riderQueue.put(rider);
            System.out.println("Rider added to queue: " + rider.getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    // Method to add a driver to the queue
    // This method is synchronized to ensure thread safety when adding drivers
    public void addDriver(Driver driver) {
        try {
            driverQueue.put(driver);
            System.out.println("Driver added to queue: " + driver.getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    // Method to start matching riders with drivers
    // This method creates a specified number of worker threads to handle the matching process
    // Each worker thread will take a rider and a driver from the queues and process the ride
    // The method uses a Runnable to define the task for each worker thread
    // The task involves taking a rider and a driver from the queues, simulating ride processing,
    // and adding the completed ride to the list of completed rides
    // The method also handles interruptions gracefully
    // by checking if the thread is interrupted and breaking the loop if it is
    // The method uses a Random object to simulate ride duration
    // The method also includes a sleep statement to simulate processing time
    // The method uses a synchronized block to ensure thread safety when accessing the completed rides list
    // The method uses a try-catch block to handle InterruptedException
    // The method uses a for loop to create the specified number of worker threads
    // The method uses the ExecutorService to submit the task for execution
    private void startMatchingWorkers(int threadCount) {
        Runnable matchAndProcess = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Rider rider = riderQueue.take();
                    Driver driver = driverQueue.take();

                    System.out.println("Matched " + rider.getName() + " with " + driver.getName());

                    Ride ride = new StandardRide("Start", "End", 5 + new Random().nextInt(10));
                    Thread.sleep(500); // simulate processing time

                    completedRides.add(ride);
                    System.out.println("Ride completed for " + rider.getName() + " with " + driver.getName());

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        };

        for (int i = 0; i < threadCount; i++) {
            executor.submit(matchAndProcess);
        }
    }
    // Method to shut down the system
    // This method shuts down the executor service and waits for the tasks to complete
    // The method uses a try-catch block to handle InterruptedException
    public void shutdown() {
        executor.shutdownNow();
        try {
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                System.err.println("Force shutdown!");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
    // Method to print completed rides
    // This method iterates through the completed rides list and prints the details of each ride
    // The method uses a synchronized block to ensure thread safety when accessing the completed rides list
    // The method uses a for-each loop to iterate through the completed rides list
    public void printCompletedRides() {
        synchronized (completedRides) {
            for (Ride ride : completedRides) {
                ride.rideDetails();
            }
        }
    }
}

