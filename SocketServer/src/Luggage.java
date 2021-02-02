public class Luggage {
    private int id;
    private String color;
    private String weight;
    private int bags;
    private Owner owner;
    // private String ownerfirstname;
    // private String ownerlastname;

    Luggage() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
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

    // public void setOwnerfirstname(String ownerfirstname) {
    // this.ownerfirstname = ownerfirstname;
    // }

    // public String getOwnerfirstname() {
    // return ownerfirstname;
    // }

    // public void setOwnerlastname(String ownerlastname) {
    // this.ownerlastname = ownerlastname;
    // }

    // public String getOwnerlastname() {
    // return ownerlastname;
    // }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n----- Luggage -----\n");

        sb.append("ID: " + getId() + "\n");

        sb.append("Color: " + getColor() + "\n");

        sb.append("Weight: " + getWeight() + "\n");

        sb.append("Number of Bags: " + getBags() + "\n");

        sb.append("Owner: " + getOwner() + "\n");

        sb.append("--------------------------");

        return sb.toString();

    }
}
