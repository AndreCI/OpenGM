package ch.epfl.sweng.opengm.groups;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFGroup;

public class GroupCardViewAdapter extends RecyclerView.Adapter<GroupCardViewAdapter.GroupViewHolder>{

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView groupName;
        ImageView groupPhoto;

        GroupViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.group_card_view);
            groupName = (TextView) itemView.findViewById(R.id.group_name);
            groupPhoto = (ImageView) itemView.findViewById(R.id.group_photo);
        }
    }

    List<PFGroup> groups;

    GroupCardViewAdapter(List<PFGroup> groups){
        this.groups = groups;
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_card_layout, parent, false);

//        ViewGroup.LayoutParams lpm = parent.getLayoutParams();
//        lpm.height = 100;
//        parent.setLayoutParams(lpm);    // FIXME: Attention, ceci est la "card VIEW globale" (qui contient les cards à l'intérieur)

//        CardView groupCardView = (CardView) parent.findViewById(R.id.group_card_view);      // FIXME: null pointer exception
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) groupCardView.getLayoutParams();
//        params.height = 300;
//        groupCardView.setLayoutParams(params);

        GroupViewHolder gvh = new GroupViewHolder(v);
        return gvh;
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.cardView.getLayoutParams();

        // TODO: get the screen size in pixels, within a static context
//        DisplayMetrics metrics = new DisplayMetrics();
//        MyGroupsActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        int screenWidth = metrics.widthPixels;
        int screenWidth = 1080; // (only for Nexus 5 which is 1080x1920)

        layoutParams.height = screenWidth/2;
        holder.cardView.setLayoutParams(layoutParams);

        holder.cardView.setTag(position);
        holder.groupName.setText(groups.get(position).getName());
//        holder.groupPhoto.setImageResource(groups.get(position).getPhoto);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}