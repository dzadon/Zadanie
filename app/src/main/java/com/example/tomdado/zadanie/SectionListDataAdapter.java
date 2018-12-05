package com.example.tomdado.zadanie;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SectionListDataAdapter extends RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder>{

    private ArrayList<SingleItemModel> itemModels;
    private Context mContext;
    private static final int TYPE_ONE = 1;
    private static final int TYPE_TWO = 2;
    private View viewSingleCard;
    private View viewSingleCardProfile;

    public SectionListDataAdapter(ArrayList<SingleItemModel> itemModels, Context mContext) {
        this.itemModels = itemModels;
        this.mContext = mContext;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        viewSingleCard =LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_card, parent, false);
        viewSingleCardProfile = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_card_profile, parent, false);
        if (viewType == TYPE_ONE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_card_profile, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_card, parent, false);
        }
        SingleItemRowHolder singleItemRowHolder = new SingleItemRowHolder(view);
        return singleItemRowHolder;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int position) {
        SingleItemModel itemModel = itemModels.get(position);
        if (itemModel.isProfileView()) {
            holder.postTextViewName.setText("Name " + itemModel.getAuthor());
            holder.postTextViewRegDatetime.setText("DateTime of registration " + itemModel.getDateTimeOfRegistration());
            holder.postTextViewNumberOfPosts.setText("Number of posts " + itemModel.getNumberOfPosts());
        } else {
            holder.postAuthor.setText("Author: " + itemModel.getAuthor());
            holder.postTime.setText("DateTime " + itemModel.getDateTimeOfPost());
            holder.imageView_post.setImageURI(Uri.parse("http://mobv.mcomputing.eu/upload/v/"+ itemModel.getUrl()));
        }
    }

    // determine which layout to use for the row
    @Override
    public int getItemViewType(int position) {
        SingleItemModel singleItemModel = itemModels.get(position);
        if (singleItemModel.isProfileView()) {
            return TYPE_ONE;
        } else {
            return TYPE_TWO;
        }
    }


    @Override
    public int getItemCount() {
        return (null != itemModels ? itemModels.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView postAuthor;
        protected TextView postTime;
        protected ImageView imageView_post;
        protected TextView postTextViewName;
        protected TextView postTextViewRegDatetime;
        protected TextView postTextViewNumberOfPosts;

        public SingleItemRowHolder(View itemView) {
            super(itemView);
            this.postAuthor = itemView.findViewById(R.id.postAuthor);
            this.postTime = itemView.findViewById(R.id.postTime);
            this.imageView_post = itemView.findViewById(R.id.imageView_post);
            this.postTextViewName = itemView.findViewById(R.id.postTextViewName);
            this.postTextViewRegDatetime = itemView.findViewById(R.id.postTextViewRegDatetime);
            this.postTextViewNumberOfPosts = itemView.findViewById(R.id.postTextViewNumberOfPosts);
        }
    }

}
