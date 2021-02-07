import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LuggageSystem {
    private static ArrayList<Luggage> luggages = new ArrayList<Luggage>();
    private static LuggageSystem instance;
    private static String dir; // current directory...

    private LuggageSystem() {
        Path currentDir = Paths.get("");
        LuggageSystem.dir = currentDir.toAbsolutePath().toString();
    };

    public static LuggageSystem getLuggageSystem() {
        if (instance == null) {
            instance = new LuggageSystem();

            return instance;
        }

        return instance;
    }

    public void loadData() {
        // the JSON data in the data file and put it in the Luggage

        try {
            ObjectMapper mapper = new ObjectMapper();

            File dataStore = new File(LuggageSystem.dir + "/data/luggages.json");

            if (dataStore.exists()) {

                System.out.println("File exists!");

                System.out.println("File name: " + dataStore.getName());
                System.out.println("Absolute path: " + dataStore.getAbsolutePath());
                System.out.println("Writeable: " + dataStore.canWrite());
                System.out.println("Readable " + dataStore.canRead());
                System.out.println("File size in bytes " + dataStore.length());

                ArrayList<Luggage> lg = mapper.readValue(dataStore, new TypeReference<ArrayList<Luggage>>() {
                });

                LuggageSystem.luggages = lg;

                System.out.println(lg.size());

            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Save ArrayList of Luggages as JSON into data JSON file.
     */
    public void saveJsonData() {

        try {

            ObjectMapper mapper = new ObjectMapper();

            File dataStore = new File(LuggageSystem.dir + "/data/luggages.json");
            FileWriter dataStoreWriter = new FileWriter(LuggageSystem.dir + "/data/luggages.json");

            if (dataStore.createNewFile()) {
                System.out.println("File created successfully!");
            } else {
                System.out.println("File already exists!");
            }

            if (LuggageSystem.luggages == null || LuggageSystem.luggages.size() == 0) {
                System.out.println("Luggages List is empty!");

                try {
                    dataStoreWriter.write("");
                    dataStoreWriter.close();
                    mapper.writeValue(dataStore, new ArrayList<>());
                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    mapper.writeValue(dataStore, LuggageSystem.luggages);
                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ;
        } catch (IOException e) {
            System.out.println("Error saving JSON = >");
            e.printStackTrace();
        }

    }

    /**
     * Get Luggage JSON by Index
     * 
     * @param index
     * @return JSON of found Luggage
     */
    public String getLuggageJSONbyIndex(int index) {
        ObjectMapper mapper = new ObjectMapper();
        if (LuggageSystem.luggages == null || LuggageSystem.luggages.size() == 0) {
            System.out.println("Luggages List is empty!");

            throw new NullPointerException("There are no checked-in Luggages!");
        }

        if (index > LuggageSystem.luggages.size()) {
            System.out.println("Luggage does not exist!");

            throw new ArrayIndexOutOfBoundsException("Luggage does not exist!");
        }

        if (index < 0) {
            System.out.println("Luggage does not exist!");

            throw new ArrayIndexOutOfBoundsException("Luggage does not exist!");
        }

        Luggage foundLuggage = LuggageSystem.luggages.get(index);

        try {
            String json = mapper.writeValueAsString(foundLuggage);

            return json;

        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    /**
     * Create a new Luggage and add it to Luggages List
     * 
     * @param luggageJSON
     * @return true or false if the Check-in was successful
     */
    public Boolean checkinLuggage(String luggageJSON) {
        ObjectMapper mapper = new ObjectMapper();
        Boolean added = false;
        int min_index = 1;
        int max_index = 1000;

        try {
            Luggage l = mapper.readValue(luggageJSON, Luggage.class);
            int index = (int) (Math.random() * (max_index - min_index + 1) + min_index);

            l.setId(index);

            l.setDateAdded(LocalDateTime.now().toString());

            System.out.println("Luggage added successfully! => ");

            added = LuggageSystem.luggages.add(l);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
        return added;
    }

    /**
     * Finds a Luggage by Luggage.id and removes it from ArrayList of Luggages
     * 
     * @param id
     * @return true or false if it was checked out
     */
    public Boolean checkoutLuggage(int id) {

        try {

            if (LuggageSystem.luggages == null || LuggageSystem.luggages.size() == 0) {
                System.out.println("Luggages List is empty!");

                return false;
            }

            int index = this.findLuggageIndexById(id);

            if (index == -1 || index > LuggageSystem.luggages.size()) {
                System.out.println("Luggage does not exist!");

                return false;
            }

            Luggage l = LuggageSystem.luggages.remove(index);

            System.out.println("Luggage checked out successfully! => " + l.getId());

        } catch (NullPointerException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    /**
     * Returns all the Luggages in the system as JSON
     * 
     * @return JSON of Luggaes ArrayList[]
     */
    public String getLuggages() {
        ObjectMapper mapper = new ObjectMapper();
        try {

            if (LuggageSystem.luggages == null || LuggageSystem.luggages.size() == 0) {
                return "[]";
            }

            // this is a package that helps us work with JSON
            String json = mapper.writeValueAsString(LuggageSystem.luggages);

            System.out.println("Luggages gotten successfully!");
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]";
        }
    }

    /**
     * Finds the Luggage in JSON format by the ownerlastname
     * 
     * @param ownerlastname - the lastname of the Owner
     * @return JSON of found Luggage
     */
    public String findLuggageByOwnerlastname(String ownerlastname) {

        int index = -1;
        ownerlastname = ownerlastname.toLowerCase();
        // using classical for loop
        for (int i = 0; i < LuggageSystem.luggages.size(); i++) {
            Boolean found = LuggageSystem.luggages.get(i).getOwner().getLastname().toLowerCase().equals(ownerlastname);

            if (found) {
                index = i;
                break;
            }
        }

        return this.getLuggageJSONbyIndex(index);
    }

    /**
     * Find Luggage Index by Luggage Id. Luggage Id is automatically generated on
     * Check-in.
     * 
     * @param id
     * @return index of Luggage in Array
     */
    private int findLuggageIndexById(int id) {

        int index = -1;
        // using classical for loop
        for (int i = 0; i < LuggageSystem.luggages.size(); i++) {
            Boolean found = LuggageSystem.luggages.get(i).getId() == id;

            if (found) {
                index = i;
                break;
            }
        }

        return index;
    }

    /**
     * Count the number of Luggages in the System.
     * 
     * @return Int
     */
    public int luggagesCount() {

        if (LuggageSystem.luggages == null) {
            return 0;
        }

        return LuggageSystem.luggages.size();
    }

    /**
     * Print the system state report
     * 
     * @return String
     */
    public String printReport() {

        if (LuggageSystem.luggages == null || LuggageSystem.luggages.size() == 0) {
            return "\n <----- No Luggages Yet ----->\n";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("\n |----- Left Luggage System -----|\n");
        sb.append("|----- Luggages  -----|\n");

        for (int i = 0; i < LuggageSystem.luggages.size(); i++) {
            sb.append(LuggageSystem.luggages.get(i).toString());
        }

        sb.append("\n ***************************** \n");
        sb.append("|-- END OF SYSTEM STATE REPORT --|");
        sb.append("\n ***************************** \n");

        return sb.toString();
    }
}
