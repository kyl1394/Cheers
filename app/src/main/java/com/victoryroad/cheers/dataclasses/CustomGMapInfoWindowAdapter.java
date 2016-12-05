package com.victoryroad.cheers.dataclasses;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.victoryroad.cheers.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Squiggs on 12/2/2016.
 */

public class CustomGMapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter, GoogleMap.OnMarkerClickListener {
    public static final String DRINKCARD_LAYOUT = "TZSZXIa4VqWJoq4";

    private View cardLayout;
    private Context viewContext = null;
    private CheckIn cardData;

    public CustomGMapInfoWindowAdapter(Context context, CheckIn data) {
        viewContext = context;
        cardData = data;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        if(!marker.getTitle().equals(DRINKCARD_LAYOUT))
            return null;
        cardLayout = LayoutInflater.from(viewContext).inflate(R.layout.fragment_drinkcard, null);

        if(marker.getTag() != null && marker.getTitle().equals(DRINKCARD_LAYOUT)) {
            ProfilePictureView profileView = (ProfilePictureView) cardLayout.findViewById(R.id.profile_pic);
            TextView nameView = (TextView) cardLayout.findViewById(R.id.name_text);
            TextView dateView = (TextView) cardLayout.findViewById(R.id.date_text);
            TextView drinkTitleView = (TextView) cardLayout.findViewById(R.id.drink_text);
            TextView categoryView = (TextView) cardLayout.findViewById(R.id.category_text);

            profileView.setDefaultProfilePicture(cardData.getProfilePic());

            nameView.setText(cardData.userName);
            DateFormat sdf = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            dateView.setText(sdf.format(cardData.Time));
            drinkTitleView.setText(cardData.DrinkKey);

            String categories = cardData.Categories.toString().substring(1, cardData.Categories.toString().length()-1);

            categoryView.setText(categories);

        }
        return cardLayout;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.getTitle().equals(DRINKCARD_LAYOUT)) {
            try {
                cardData = (CheckIn) marker.getTag();
                Log.w("GMapWindowAdapter", "Successfully saved marker");
            } catch(Exception e) {
                Log.e("GMapWindowAdapter", "Error: couldn't convert tag to marker.");
                e.printStackTrace();
            }


        }
        return false;
    }
}
