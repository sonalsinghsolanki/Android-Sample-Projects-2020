package com.example.contactapp.ui.main;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.contactapp.R;
import com.example.contactapp.ui.main.data.ContactLists;
import com.example.contactapp.ui.main.dummy.DummyContent;
import com.example.contactapp.ui.main.dummy.DummyContent.DummyItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ContactListsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private  MyContactListsRecyclerViewAdapter myContactListsRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactListsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ContactListsFragment newInstance(int columnCount) {
        ContactListsFragment fragment = new ContactListsFragment();
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
        View view = inflater.inflate(R.layout.fragment_contactlists_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            //recyclerView.setAdapter(new MyContactListsRecyclerViewAdapter(DummyContent.ITEMS, mListener));
            List<ContactLists> tempContactLists = new ArrayList<>();
            try {
                tempContactLists = writeJsonIntoAdapter();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            recyclerView.setAdapter(new MyContactListsRecyclerViewAdapter(tempContactLists, mListener));


        }
        return view;
    }

    private List<ContactLists> writeJsonIntoAdapter() throws JSONException {


        List<ContactLists> contactLists = new ArrayList<>();

        JSONObject getJsonFromfile = new JSONObject(loadJSONFromAsset());
        JSONArray contactListArray = getJsonFromfile.getJSONArray("contactlists");
        final int numberOfItemsInResp = contactListArray.length();
        for (int i = 0; i < numberOfItemsInResp; i++) {
            JSONObject allContacts = contactListArray.getJSONObject(i);
            ContactLists tempContact = new ContactLists();
            tempContact.setmFirstName(allContacts.getString("firstname"));
            tempContact.setmLastName(allContacts.getString("lastname"));
            tempContact.setmPhoneNumber(allContacts.getString("phoneno"));
            tempContact.setmEmail(allContacts.getString("email"));

            contactLists.add(tempContact);
        }
        return contactLists;

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
       // void onListFragmentInteraction(DummyItem item);
        void onListFragmentInteraction(ContactLists contactLists);
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getActivity().getApplicationContext().getAssets().open("contactslists.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;


    }
}
