package solarsitingucsc.smartsolarsiting.Model;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class SolarSiting {
    private String id, name, userId, date;
    private List results;

    public SolarSiting(String id, String name, String[] results, String userId, String date) {
        this.id = id + name;
        this.name = name;
        this.results = Arrays.asList(results);
        this.userId = userId;
        this.date = date;
    }

    public SolarSiting() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }

    public List getResults() {
        return results;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void store() {
        FirebaseDatabase.getInstance().getReference("pictures").child(id).setValue(this);
    }
}
