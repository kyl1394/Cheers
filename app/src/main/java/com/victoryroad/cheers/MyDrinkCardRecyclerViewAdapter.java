package com.victoryroad.cheers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.victoryroad.cheers.dataclasses.CheckIn;
import com.victoryroad.cheers.dummy.DummyContent.DummyItem;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Handler;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link MyFeedFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyDrinkCardRecyclerViewAdapter extends RecyclerView.Adapter<MyDrinkCardRecyclerViewAdapter.ViewHolder> {

    private final List<CheckIn> mValues;
    private final MyFeedFragment.OnListFragmentInteractionListener mListener;
    private final DrinkFeedFragment.OnListFragmentInteractionListener mListener2;

    public MyDrinkCardRecyclerViewAdapter(List<CheckIn> items, MyFeedFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mListener2 = null;
    }

    public MyDrinkCardRecyclerViewAdapter(List<CheckIn> items, DrinkFeedFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener2 = listener;
        mListener = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_drinkcard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Profile profile = Profile.getCurrentProfile();
        String id = mValues.get(position).id == null ? profile.getId() : mValues.get(position).id;
        String userName = mValues.get(position).userName == null ? profile.getName() : mValues.get(position).userName;

        holder.mItem = mValues.get(position);
        holder.mDrinkName.setText(mValues.get(position).DrinkKey);
        String categoriesString = mValues.get(position).Categories.toString();

        holder.mCategories.setText("Categories:\n" + categoriesString.substring(1, categoriesString.length() - 1));
        DateFormat sdf = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        holder.mDate.setText(sdf.format(mValues.get(position).Time));

        holder.mName.setText(userName);
        holder.mProfilePic.setProfileId(id);

        //holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                } else if (null != mListener2) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener2.onListFragmentInteraction(holder.mItem);
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
        public final TextView mName;
        public final TextView mDate;
        public final TextView mDrinkName;
        public final TextView mCategories;
        public final ProfilePictureView mProfilePic;
        public final ImageView mDrinkPic;
        public CheckIn mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.name_text);
            mDate = (TextView) view.findViewById(R.id.date_text);
            mDrinkName = (TextView) view.findViewById(R.id.drink_text);
            mCategories = (TextView) view.findViewById(R.id.category_text);
            mProfilePic = (ProfilePictureView) view.findViewById(R.id.profile_pic);
            mDrinkPic = (ImageView) view.findViewById(R.id.drink_pic);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "" + "'";
        }
    }
}
