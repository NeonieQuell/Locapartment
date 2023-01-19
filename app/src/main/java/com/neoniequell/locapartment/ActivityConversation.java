package com.neoniequell.locapartment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.FirebaseAuth;
import com.neoniequell.locapartment.databinding.ActivityConversationBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityConversation extends AppCompatActivity {

    private String mConvoKey;
    private boolean mConvoExist;

    private FirebaseAuth mAuth;
    private DbConversations mDbConvo;

    private ModelUser mUser;
    private ArrayList<String> mParticipantList;
    private List<ModelMessage> mMessageList;

    private ActivityConversationBinding mBinding;
    private Context mContext;

    private Toast mToast;

    private AdapterMessage mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));
        mBinding = ActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mUser = getIntent().getParcelableExtra("user");
        mAuth = FirebaseAuth.getInstance();
        mDbConvo = new DbConversations();
        mContext = mBinding.getRoot().getContext();

        mParticipantList = new ArrayList<>();
        mMessageList = new ArrayList<>();

        mParticipantList.add(mAuth.getUid());
        mParticipantList.add(mUser.getUid());

        mToast = Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT);
        mBinding.shimmerFl.startShimmer();

        setStatusAndActionBar();
        setRecyclerView();
        checkIfConvoExist();
        getMessages();

        mBinding.rlMessage.setBackgroundColor(SurfaceColors.SURFACE_2.getColor(this));
        mBinding.btnSend.setBackgroundColor(SurfaceColors.SURFACE_2.getColor(this));
        mBinding.btnSend.setOnClickListener(v -> sendMessage());
    }

    private void setStatusAndActionBar() {
        setSupportActionBar(mBinding.toolbar);

        //Set status bar color
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));

        mBinding.toolbar.setBackgroundColor(SurfaceColors.SURFACE_2.getColor(this));

        setUserDetails();
    }

    private void setUserDetails() {
        if (mUser.getPhotoUrl().equalsIgnoreCase("null")) {
            mBinding.accountImg.setImageResource(R.drawable.img_user_placeholder);
        } else {
            Glide.with(this).load(mUser.getPhotoUrl()).into(mBinding.accountImg);
        }

        mBinding.tvAccountName.setText(mUser.getDisplayName());
        mBinding.toolbar.setNavigationOnClickListener(v -> super.onBackPressed());
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);

        mAdapter = new AdapterMessage(this, mMessageList, mUser.getPhotoUrl());

        layoutManager.setStackFromEnd(true);
        mBinding.recyclerView.setHasFixedSize(false);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    private void checkIfConvoExist() {
        //Check if conversation between users exist
        mDbConvo.hasConversation(mParticipantList, exist -> {
            mConvoExist = exist;

            if (mConvoExist) {
                mDbConvo.getConversationKey(mParticipantList, convoKey -> mConvoKey = convoKey);
            }

            mBinding.shimmerFl.stopShimmer();
            mBinding.shimmerFl.setVisibility(View.GONE);
        });
    }

    private void getMessages() {
        mDbConvo.getMessages(mParticipantList, messageList -> {
            mMessageList.clear();
            mMessageList.addAll(messageList);
            mAdapter.notifyDataSetChanged();

            mBinding.recyclerView.scrollToPosition(mMessageList.size() - 1);
        });
    }

    private void sendMessage() {
        String msgContent = String.valueOf(mBinding.etMessage.getText()).trim();

        if (msgContent.equals("") || msgContent.length() == 0) mToast.show();
        else {
            if (!mConvoExist) {
                mConvoKey = mDbConvo.generateKey();

                ModelConversation conversation;
                conversation = new ModelConversation(mConvoKey, mParticipantList);

                mDbConvo.createConversation(conversation)
                        .addOnSuccessListener(s -> {
                            uploadMessage(msgContent);
                            mConvoExist = true;
                        })
                        .addOnFailureListener(Throwable::printStackTrace);
            } else {
                uploadMessage(msgContent);
            }
        }
    }

    private void uploadMessage(String msgContent) {
        ModelMessage message = new ModelMessage();
        message.setConvoKey(mConvoKey);
        message.setKey(mDbConvo.generateKey());
        message.setSender(mAuth.getUid());
        message.setReceiver(mUser.getUid());
        message.setMessage(msgContent);

        mDbConvo.uploadMessage(message)
                .addOnSuccessListener(s1 -> mBinding.etMessage.setText(""))
                .addOnFailureListener(Throwable::printStackTrace);
    }
}
