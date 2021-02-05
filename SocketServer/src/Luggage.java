public class Luggage {
    private int id;
    private String flight;
    private String weight;
    private int bags;
    private Owner owner;

    Luggage() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setFlight(String flight) {
        this.flight = flight;
    }

    public String getFlight() {
        return flight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeight() {
        return weight;
    }

    public void setBags(int bagsNumber) {
        this.bags = bagsNumber;
    }

    public int getBags() {
        return bags;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Owner getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n----- Luggage -----\n");

        sb.append("ID: " + getId() + "\n");

        sb.append("Flight Code: " + getFlight() + "\n");

        sb.append("Weight: " + getWeight() + "\n");

        sb.append("Number of Bags: " + getBags() + "\n");

        sb.append("Owner: " + getOwner() + "\n");

        sb.append("--------------------------");

        return sb.toString();

    }
}
