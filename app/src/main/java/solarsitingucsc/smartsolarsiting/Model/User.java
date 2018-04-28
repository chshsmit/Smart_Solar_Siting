package solarsitingucsc.smartsolarsiting.Model;

import com.google.firebase.database.FirebaseDatabase;


public class User {

    private String email, name, id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User(String email, String name, String id) {
        this.email = email;
        this.name = name;
        this.id = id;
//        FirebaseDatabase.getInstance().getReference("users").child(id).setValue(this);
    }
}
