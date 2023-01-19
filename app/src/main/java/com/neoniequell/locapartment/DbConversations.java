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

public class DbConversations {

    private DatabaseReference mDbRef;

    public DbConversations() {
        mDbRef = FirebaseDatabase.getInstance().getReference("conversations");
    }

    public String generateKey() {
        return mDbRef.push().getKey();
    }

    private boolean checkParticipants(List<String> list1, List<String> list2) {
        int ctr = 0;

        for (String i : list1) {
            for (String j : list2) {
                if (i.equals(j)) {
                    ctr += 1;
                    break;
                }
            }
        }

        return ctr == 2;
    }

    public void hasConversation(List<String> participantList,
                                OnConversationExistListener listener) {
        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    boolean exist = false;

                    for (DataSnapshot convoDs : snapshot.getChildren()) {
                        ModelConversation convo = convoDs.getValue(ModelConversation.class);
                        assert convo != null;

                        boolean equal;
                        equal = checkParticipants(participantList, convo.getParticipantList());

                        if (equal) {
                            listener.onConversationExist(true);
                            exist = true;
                            break;
                        }
                    }

                    if (!exist) listener.onConversationExist(false);
                } else listener.onConversationExist(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getConversationKey(List<String> participantList,
                                   GetConversationKeyListener listener) {
        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot convoDs : snapshot.getChildren()) {
                    ModelConversation convo = convoDs.getValue(ModelConversation.class);
                    assert convo != null;

                    boolean equal;
                    equal = checkParticipants(participantList, convo.getParticipantList());

                    if (equal) {
                        listener.getConversationKey(convo.getKey());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public Task<Void> createConversation(ModelConversation conversation) {
        return mDbRef.child(conversation.getKey()).setValue(conversation);
    }

    public Task<Void> uploadMessage(ModelMessage message) {
        return mDbRef.child(message.getConvoKey()).child("messages")
                .child(message.getKey()).setValue(message);
    }

    public void getMessages(List<String> participantList, GetMessagesListener listener) {
        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot convoDs : snapshot.getChildren()) {
                    ModelConversation convo = convoDs.getValue(ModelConversation.class);
                    assert convo != null;

                    boolean equal = checkParticipants(participantList, convo.getParticipantList());
                    if (equal) getMessagesHelper(convo.getKey(), listener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMessagesHelper(String convoKey, GetMessagesListener listener) {
        List<ModelMessage> messageList = new ArrayList<>();
        mDbRef.child(convoKey).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot messageDs : snapshot.getChildren()) {
                    ModelMessage message = messageDs.getValue(ModelMessage.class);
                    messageList.add(message);
                }

                listener.getMessages(messageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Fetches the UID of the users
    public void getUserWithConvo(GetUserWithConversationListener listener) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        List<String> userWithConvoList = new ArrayList<>();

        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userWithConvoList.clear();

                for (DataSnapshot convoDs : snapshot.getChildren()) {
                    ModelConversation convo = convoDs.getValue(ModelConversation.class);
                    assert convo != null;

                    if (convo.getParticipantList().contains(auth.getUid())) {
                        for (String participant : convo.getParticipantList()) {
                            if (!participant.equals(auth.getUid())) {
                                userWithConvoList.add(participant);
                                break;
                            }
                        }
                    }
                }

                listener.getUserWithConvo(userWithConvoList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Fetches the information of the users
    public void getUserWithConversationInfo(List<String> userList,
                                            OnGetUserWithConversationInfoListener listener) {
        List<ModelUser> tempUserList = new ArrayList<>();

        DatabaseReference usersDbRef = FirebaseDatabase.getInstance().getReference("users");
        usersDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tempUserList.clear();

                for (String strUser : userList) {
                    for (DataSnapshot userDs : snapshot.getChildren()) {
                        ModelUser modelUser = userDs.getValue(ModelUser.class);
                        assert modelUser != null;

                        if (strUser.equals(modelUser.getUid())) {
                            tempUserList.add(modelUser);
                            break;
                        }
                    }
                }

                listener.onGetUserWithConvoInfo(tempUserList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface OnConversationExistListener {
        void onConversationExist(boolean exist);
    }

    public interface GetConversationKeyListener {
        void getConversationKey(String convoKey);
    }

    public interface GetMessagesListener {
        void getMessages(List<ModelMessage> messageList);
    }

    public interface GetUserWithConversationListener {
        void getUserWithConvo(List<String> userWithConvoList);
    }

    public interface OnGetUserWithConversationInfoListener {
        void onGetUserWithConvoInfo(List<ModelUser> userList);
    }
}
