package com.example.contactapp.ui.main;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contactapp.ContactDetailsActivity;
import com.example.contactapp.R;
import com.example.contactapp.ui.main.ContactListsFragment.OnListFragmentInteractionListener;
import com.example.contactapp.ui.main.data.ContactLists;
import com.example.contactapp.ui.main.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyContactListsRecyclerViewAdapter extends RecyclerView.Adapter<MyContactListsRecyclerViewAdapter.ViewHolder> {

   // private final List<DummyItem> mValues;
   private final List<ContactLists> mContactLists;
    private final OnListFragmentInteractionListener mListener;

    //public MyContactListsRecyclerViewAdapter(List<DummyItem> items, OnListFragmentInteractionListener listener)
    public MyContactListsRecyclerViewAdapter(List<ContactLists> contactLists, OnListFragmentInteractionListener listener) {
        mContactLists = contactLists;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contactlists, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mContactLists.get(position);
       // holder.mIdView.setText(mContactLists.get(position).id);
        final String fullName  = mContactLists.get(position).getmFirstName()+" "+mContactLists.get(position).getmLastName();
        holder.mTxtContactNameValue.setText(fullName);
       // holder.mTxtContactNameValue.setText(mContactLists.get(position).getmFirstName());
        final String phoneNo = mContactLists.get(position).getmPhoneNumber();
        final String email = mContactLists.get(position).getmEmail();


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                    //Log.v("MyContactListAdapter","List clicked");

                }
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Intent i = new Intent(view.getContext(), ContactDetailsActivity.class);
                     i.putExtra("FULL_NAME", fullName);
                     i.putExtra("PHONE_NO", phoneNo);
                    view.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContactLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
      //  public final TextView mLblContactName;
        public final TextView mTxtContactNameValue;
       // public DummyItem mItem;
       public ContactLists mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTxtContactNameValue = (TextView) view.findViewById(R.id.txt_contact_name);
           // mContentView = (TextView) view.findViewById(R.id.content);
        }

       /* @Override
        public String toString() {
            return super.toString() + " '" + mTxtContactNameValue.getText() + "'";
        }*/
    }
}
