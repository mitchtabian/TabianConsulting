package courses.pluralsight.com.tabianconsulting.issues;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import courses.pluralsight.com.tabianconsulting.R;


/**
 * Created by User on 4/17/2018.
 */

public class AttachmentRecyclerViewAdapter extends SelectableAdapter<RecyclerView.ViewHolder> {


    private ArrayList<String> mImages = new ArrayList<>();
    private Context mContext;
    private IIssueDetail mIissueDetail;
    private IsAttachmentsSelected mIsAttachmentsSelected;


    public AttachmentRecyclerViewAdapter(Context context, ArrayList<String> images, IsAttachmentsSelected isAttachmentsSelected) {
        mImages = images;
        mContext = context;
        mIsAttachmentsSelected = isAttachmentsSelected;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_attachment_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view, mIsAttachmentsSelected);
        return holder;
    }

    @Override
    public void onBindViewHolder(final @NonNull RecyclerView.ViewHolder holder, final int position) {

        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.ic_launcher)
                .format(DecodeFormat.PREFER_RGB_565)
                .centerCrop();

        Glide.with(mContext)
                .setDefaultRequestOptions(options)
                .load(mImages.get(position))
                .thumbnail(0.1f)
                .into(((ViewHolder)holder).image);

        ((ViewHolder)holder).attachmentOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }




    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mIissueDetail = (IIssueDetail)mContext;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener{

        ImageView image;
        View attachmentOverlay;

        IsAttachmentsSelected isAttachmentSelected;


        public ViewHolder(View itemView, IsAttachmentsSelected isAttachmentSelected) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            attachmentOverlay = itemView.findViewById(R.id.attachment_overlay);

            this.isAttachmentSelected = isAttachmentSelected;

            image.setOnClickListener(this);
            image.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {

            toggleSelection(getAdapterPosition());

            if(getSelectedItemCount() > 0){
                isAttachmentSelected.isSelected(true);
            }

            return false;
        }

        @Override
        public void onClick(View view) {
            if(getSelectedItemCount() > 0){
                toggleSelection(getAdapterPosition());

                if(getSelectedItemCount() == 0){
                    isAttachmentSelected.isSelected(false);
                }
            }
            else{
                mIissueDetail.inflateFullScreenImageFragment(mImages.get(getAdapterPosition()));
            }
        }
    }

    public interface IsAttachmentsSelected{
        void isSelected(boolean isSelected);
    }

}


















