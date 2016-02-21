package com.zeyad.cleanarchetecturet.presentation.presenters;

import android.support.annotation.NonNull;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.zeyad.cleanarchetecturet.domain.interactor.UseCase;
import com.zeyad.cleanarchetecturet.presentation.view.UserDetailsView;
import com.zeyad.cleanarchetecturet.utilities.Constants;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

public class FirebasePresenter implements BasePresenter {

    Firebase ref;

    @Inject
    public FirebasePresenter(@Named("userList") UseCase getUserListUserCase, Firebase ref) {
        this.ref = ref;
    }

    public void setView(@NonNull UserDetailsView view) {
        // to write with confirmation callback
        ref.child("").setValue(1, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {

            }
        });
//        ref.child("").updateChildren();
//        ref.child("").push();
//        ref.child("").runTransaction();
        ref.child("").removeValue();
        ref.child("").removeValue();
        // for listening to changes on specific data but just once!
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        // for listening to changes on specific child nodes
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        // create multiple instance of the same object
        Firebase listsRef = new Firebase(Constants.FIREBASE_URL).child("");
        listsRef.push().setValue(new ShoppingList("userEnteredName", "",
                new HashMap<>().put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP)));
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }
}