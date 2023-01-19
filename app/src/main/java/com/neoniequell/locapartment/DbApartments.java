package com.neoniequell.locapartment;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DbApartments {

    private DatabaseReference mDbRef;

    public DbApartments() {
        mDbRef = FirebaseDatabase.getInstance().getReference("apartments");
    }

    public String getKey() {
        return mDbRef.push().getKey();
    }

    public Task<Void> uploadApartment(ModelApartment apartment) {
        return mDbRef.child(apartment.getKey()).setValue(apartment);
    }

    public void getApartments(GetApartmentsListener listener) {
        List<ModelApartment> apartmentList = new ArrayList<>();
        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                apartmentList.clear();

                for (DataSnapshot apartmentDs : snapshot.getChildren()) {
                    ModelApartment apartment = apartmentDs.getValue(ModelApartment.class);
                    apartmentList.add(apartment);
                }

                listener.getApartments(apartmentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public Task<Void> changeAvailability(ModelApartment apartment) {
        return mDbRef.child(apartment.getKey()).child("availability")
                .setValue(apartment.getAvailability());
    }

    public void getUserApartments(GetApartmentsListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        List<ModelApartment> apartmentList = new ArrayList<>();

        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                apartmentList.clear();

                for (DataSnapshot apartmentDs : snapshot.getChildren()) {
                    ModelApartment apartment = apartmentDs.getValue(ModelApartment.class);
                    assert apartment != null;

                    if (Objects.equals(user.getUid(), apartment.getLandlordUid())) {
                        apartmentList.add(apartment);
                    }
                }

                listener.getApartments(apartmentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public Task<Void> deleteApartment(ModelApartment apartment) {
        return mDbRef.child(apartment.getKey()).removeValue();
    }

    public interface GetApartmentsListener {
        void getApartments(List<ModelApartment> apartmentList);
    }
}
