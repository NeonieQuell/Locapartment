package com.neoniequell.locapartment;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DbUsers {

    private DatabaseReference mDbRef;

    public DbUsers() {
        mDbRef = FirebaseDatabase.getInstance().getReference("users");
    }

    public void hasUser(ModelUser user, OnUserExistListener listener) {
        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listener.onUserExist(snapshot.hasChild(user.getUid()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getAllUsers(GetAllUserListener listener) {
        List<ModelUser> userList = new ArrayList<>();
        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseAuth auth = FirebaseAuth.getInstance();

                for (DataSnapshot userDs : snapshot.getChildren()) {
                    if (!Objects.equals(auth.getUid(), userDs.getKey())) {
                        ModelUser user = userDs.getValue(ModelUser.class);
                        userList.add(user);
                    }
                }

                listener.getAllUser(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public Task<Void> createUser(ModelUser user) {
        return mDbRef.child(user.getUid()).setValue(user);
    }

    public void getSingleUserInfo(String uid, GetSingleUserInfoListener listener) {
        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.child(uid).getValue(ModelUser.class);
                listener.getSingleUserInfo(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface OnUserExistListener {
        void onUserExist(boolean exist);
    }

    public interface GetAllUserListener {
        void getAllUser(List<ModelUser> userList);
    }

    public interface GetSingleUserInfoListener {
        void getSingleUserInfo(ModelUser user);
    }
}
