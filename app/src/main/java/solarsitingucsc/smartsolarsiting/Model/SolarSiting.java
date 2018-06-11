package solarsitingucsc.smartsolarsiting.Model;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SolarSiting {
    private String id, name, userId, date;
    private HashMap<String, HashMap<String, Double>> results;

    public SolarSiting(String userId, String name, HashMap<String, HashMap<String, Double>> results, String date) {
        this.id = userId + name;
        this.name = name;
        this.results = results;
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

    public HashMap<String, HashMap<String, Double>> getResults() {
        return results;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void store() {
        FirebaseDatabase.getInstance().getReference("pictures").child(id).setValue(this);
    }

    public void delete() {
        FirebaseDatabase.getInstance().getReference("pictures").child(id).removeValue();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(userId + name + ".jpg");
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Error occurred
            }
        });
    }
}
