package com.example.a21753725a.geogallery;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by 21753725a on 06/03/17.
 */

public class firebaseUtils {

    public static void addPhoto(Photo photo){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference photoRef = database.getReference("photo");
        photoRef.child("basic").setValue(photo);
    }
}
