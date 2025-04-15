public class Rider {
    private static int idCounter = 0;
    private final int riderID;
    private final String name;

    public Rider(String name) {
        this.name = name;
        this.riderID = idCounter++;
        System.out.println("Rider created: " + name + " ID: " + riderID);
    }

    public int getID() { return riderID; }
    public String getName() { return name; }
}
