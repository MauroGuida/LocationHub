package com.jdt.locationhub.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jdt.locationhub.R;
import com.jdt.locationhub.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    //Viewholder class for initializing views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView usernameTextV;
        private final TextView distanceTextV;
        private final TextView latlongTextV;
        private final TextView addressLineTextV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextV = itemView.findViewById(R.id.username_TextV_UserCardView);
            distanceTextV = itemView.findViewById(R.id.distance_TextV_UserCardView);
            latlongTextV = itemView.findViewById(R.id.latlong_TextV_UserCardView);
            addressLineTextV = itemView.findViewById(R.id.addressLine_TextV_UserCardView);
        }
    }

    private final Context context;
    private final List<User> userList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User u = userList.get(position);

        holder.usernameTextV.setText(u.getUsername());
        holder.distanceTextV.setText(context.getResources().getString(R.string.user_distance, u.getDistance()));
        if (u.getPosition() != null && !u.isPrivate()) {
            holder.latlongTextV.setText(context.getResources().getString(R.string.LatLon,
                    String.valueOf(u.getPosition().getLatitude()),
                    String.valueOf(u.getPosition().getLongitude())));
            holder.addressLineTextV.setText(u.getPosition().getAddressLine());
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
