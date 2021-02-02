public class Owner {
    private String firstname;
    private String lastname;

    Owner() {
    };

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLastname() {
        return lastname;
    }

    @Override
    public String toString() {
        return getFirstname() + " " + getLastname();
    }
}
