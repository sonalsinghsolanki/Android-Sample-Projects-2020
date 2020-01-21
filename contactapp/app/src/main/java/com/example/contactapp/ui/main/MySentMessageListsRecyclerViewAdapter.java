package com.example.contactapp.ui.main;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.contactapp.R;
import com.example.contactapp.ui.main.SentMessageListsFragment.OnListFragmentInteractionListener;
import com.example.contactapp.ui.main.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySentMessageListsRecyclerViewAdapter extends RecyclerView.Adapter<MySentMessageListsRecyclerViewAdapter.ViewHolder> {

    private final List<DummyItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MySentMessageListsRecyclerViewAdapter(List<DummyItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sentmessagelists, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTxtContactName.setText(mValues.get(position).id);
        holder.mSentOTP.setText(mValues.get(position).content);
        holder.mSmsTime.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTxtContactName,mSmsTime,mSentOTP;
       // public final TextView mContentView;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTxtContactName = (TextView) view.findViewById(R.id.txt_contact_name);
            mSmsTime = (TextView) view.findViewById(R.id.txt_sms_time);
            mSentOTP = (TextView) view.findViewById(R.id.txt_otp);
        }

       /* @Override
        public String toString() {
          //  return super.toString() + " '" + mContentView.getText() + "'";
        }*/
    }
}
