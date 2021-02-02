import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App {
    private static Person person;
    private static ArrayList<Luggage> luggages = new ArrayList<Luggage>();

    public App() {
    };

    public Person getPerson() {
        return App.person;
    }

    public String getLuggageJSON(int index) {
        ObjectMapper mapper = new ObjectMapper();
        if (App.luggages == null || App.luggages.size() == 0) {
            System.out.println("Luggages List is empty!");

            throw new NullPointerException("There are no checked-in Luggages!");
        }

        if (index > App.luggages.size()) {
            System.out.println("Luggage does not exist!");

            throw new ArrayIndexOutOfBoundsException("Luggage does not exist!");
        }

        Luggage foundLuggage = App.luggages.get(index);

        try {
            String json = mapper.writeValueAsString(foundLuggage);

            return json;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return "{}";
        }
    }

    public void createPerson(String personJSON) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Person p = mapper.readValue(personJSON, Person.class);

            App.person = p;

            System.out.printf("Person %s created successfully!", p.getFirstname());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public Boolean addLuggage(String luggageJSON) {
        ObjectMapper mapper = new ObjectMapper();
        Boolean added = false;
        try {
            Luggage l = mapper.readValue(luggageJSON, Luggage.class);
            int index = App.luggages.size() + 1;

            l.setId(index);

            System.out.println("Luggage added successfully! => ");

            added = App.luggages.add(l);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
        return added;
    }

    public Boolean checkoutLuggage(int id) {

        try {

            if (App.luggages == null || App.luggages.size() == 0) {
                System.out.println("Luggages List is empty!");

                return false;
            }

            if (id > App.luggages.size()) {
                System.out.println("Luggage does not exist!");

                return false;
            }

            id = id - 1;

            Luggage l = App.luggages.remove(id);

            System.out.println("Luggage checked out successfully! => " + l.getId());

        } catch (NullPointerException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public String getPersonJSON() {
        ObjectMapper mapper = new ObjectMapper();
        try {

            if (App.person == null) {
                return "";
            }

            // this is a package that helps us work with JSON
            String json = mapper.writeValueAsString(App.person);

            System.out.printf("Person %s gotten successfully!", App.person.getFirstname());
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getLuggages() {
        ObjectMapper mapper = new ObjectMapper();
        try {

            if (App.luggages == null || App.luggages.size() == 0) {
                return "[]";
            }

            // this is a package that helps us work with JSON
            String json = mapper.writeValueAsString(App.luggages);

            System.out.println("Luggages gotten successfully!");
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]";
        }
    }

    public int searchLuggages(String ownerlastname) {

        int index = -1;
        ownerlastname = ownerlastname.toLowerCase();
        // using classical for loop
        for (int i = 0; i < App.luggages.size(); i++) {
            Boolean found = App.luggages.get(i).getOwner().getLastname().toLowerCase().equals(ownerlastname);

            if (found) {
                index = i;
                break;
            }
        }

        return index;
    }

    public int luggagesCount() {

        if (App.luggages == null) {
            return 0;
        }

        return App.luggages.size();
    }

    public String printReport() {

        if (App.luggages == null || App.luggages.size() == 0) {
            return "\n <----- No Luggages Yet ----->\n";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("\n |----- Lost Luggage System -----|\n");
        sb.append("|----- Luggages  -----|\n");

        for (int i = 0; i < App.luggages.size(); i++) {
            sb.append(App.luggages.get(i).toString());
        }

        sb.append("\n ***************************** \n");
        sb.append("|-- END OF SYSTEM STATE REPORT --|");
        sb.append("\n ***************************** \n");

        return sb.toString();
    }
}
