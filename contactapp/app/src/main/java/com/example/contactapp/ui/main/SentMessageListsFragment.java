package com.example.contactapp.ui.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.contactapp.R;
import com.example.contactapp.ui.main.data.ContactLists;
import com.example.contactapp.ui.main.data.SentMessageLists;
import com.example.contactapp.ui.main.dummy.DummyContent;
import com.example.contactapp.ui.main.dummy.DummyContent.DummyItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SentMessageListsFragment extends Fragment {

    private DatabaseReference mDatabaseRef;
    private  static String TAG = "SentMessageListFragments";

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    final List<SentMessageLists> tempSentMessageLists = new ArrayList<>();
    private MySentMessageListsRecyclerViewAdapter adapter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SentMessageListsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SentMessageListsFragment newInstance(int columnCount) {
        SentMessageListsFragment fragment = new SentMessageListsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sentmessagelists_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mDatabaseRef = FirebaseDatabase.getInstance().getReference();




         /*   SentMessageLists messageLists = new SentMessageLists("Sonal S","890890","47474");
          tempSentMessageLists.add(messageLists);*/

            readData(new MyCallback() {
                @Override
                public void onCallback(List<SentMessageLists> messageLists) {

                    //recyclerView.setAdapter(new MySentMessageListsRecyclerViewAdapter(messageLists, mListener));
                    adapter = new MySentMessageListsRecyclerViewAdapter(tempSentMessageLists,mListener);
                    recyclerView.setAdapter(adapter);
                }
            });


        }
        return view;
    }

    public interface MyCallback {


        void onCallback(List<SentMessageLists> messageLists);
    }

    public void readData(final MyCallback myCallback) {
      //  final List<SentMessageLists>messageLists = new ArrayList<>();
        //messageLists.clear();
       // tempSentMessageLists.clear();
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dschildData = dataSnapshot.getChildren();
                tempSentMessageLists.clear();
                for(DataSnapshot postSnapshot : dschildData){
                    SentMessageLists message = new SentMessageLists();
                    String fullName = postSnapshot.child("contactName").getValue(String.class);
                    String otp = postSnapshot.child("otp").getValue(String.class);
                    String otpTime = "11:40";
                    message.setContactFullName(fullName);
                    message.setSentOtp(otp);
                    message.setOtpTime(otpTime);
                    tempSentMessageLists.add(message);
                    myCallback.onCallback(tempSentMessageLists);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,databaseError.getMessage());
            }
        });

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
           /* throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");*/
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(SentMessageLists item);
    }
}
