import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LuggageSystem {
    private static ArrayList<Luggage> luggages = new ArrayList<Luggage>();
    private static LuggageSystem instance;

    private LuggageSystem() {

    };

    public static LuggageSystem getLuggageSystem() {
        if (instance == null) {
            instance = new LuggageSystem();

            return instance;
        }

        return instance;
    }

    public void loadData() {

        try {
            ObjectMapper mapper = new ObjectMapper();

            File dataStore = new File("data.json");
            if (dataStore.exists()) {

                System.out.println("File exists! successfully!");

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
            // } else {
            // System.out.println("File already exists.");

            // System.out.println("File name: " + dataStore.getName());
            // System.out.println("Absolute path: " + dataStore.getAbsolutePath());
            // System.out.println("Writeable: " + dataStore.canWrite());
            // System.out.println("Readable " + dataStore.canRead());
            // System.out.println("File size in bytes " + dataStore.length());

            // ArrayList<Owner> lg = mapper.readValue(dataStore, new
            // TypeReference<ArrayList<Owner>>() {
            // });

            // System.out.println(lg.size());

            // lg.forEach(x -> System.out.println(x.getFirstname() + x.getLastname()));
            // }
        } catch (

        IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void saveJsonData() {

        try {

            ObjectMapper mapper = new ObjectMapper();

            File dataStore = new File("data.json");
            FileWriter dataStoreWriter = new FileWriter("data.json");

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
            // TODO: handle exception
            System.out.println("Error saving JSON = >");
            e.printStackTrace();
        }

    }

    public String getLuggageJSON(int index) {
        ObjectMapper mapper = new ObjectMapper();
        if (LuggageSystem.luggages == null || LuggageSystem.luggages.size() == 0) {
            System.out.println("Luggages List is empty!");

            throw new NullPointerException("There are no checked-in Luggages!");
        }

        if (index > LuggageSystem.luggages.size()) {
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

    public Boolean addLuggage(String luggageJSON) {
        ObjectMapper mapper = new ObjectMapper();
        Boolean added = false;
        try {
            Luggage l = mapper.readValue(luggageJSON, Luggage.class);
            int index = LuggageSystem.luggages.size() + 1;

            l.setId(index);

            System.out.println("Luggage added successfully! => ");

            added = LuggageSystem.luggages.add(l);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
        return added;
    }

    public Boolean checkoutLuggage(int id) {

        try {

            if (LuggageSystem.luggages == null || LuggageSystem.luggages.size() == 0) {
                System.out.println("Luggages List is empty!");

                return false;
            }

            if (id > LuggageSystem.luggages.size()) {
                System.out.println("Luggage does not exist!");

                return false;
            }

            id = id - 1;

            Luggage l = LuggageSystem.luggages.remove(id);

            System.out.println("Luggage checked out successfully! => " + l.getId());

        } catch (NullPointerException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

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

    public int searchLuggages(String ownerlastname) {

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

        return index;
    }

    public int luggagesCount() {

        if (LuggageSystem.luggages == null) {
            return 0;
        }

        return LuggageSystem.luggages.size();
    }

    public String printReport() {

        if (LuggageSystem.luggages == null || LuggageSystem.luggages.size() == 0) {
            return "\n <----- No Luggages Yet ----->\n";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("\n |----- Lost Luggage System -----|\n");
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
