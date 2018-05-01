package courses.pluralsight.com.tabianconsulting.issues;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import courses.pluralsight.com.tabianconsulting.R;
import courses.pluralsight.com.tabianconsulting.models.Project;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 4/16/2018.
 */

public class ProjectsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        Filterable {

    private ArrayList<Project> mProjects = new ArrayList<>();
    private ArrayList<Project> mFilteredProjects = new ArrayList<>();
    private Context mContext;

    public ProjectsRecyclerViewAdapter(ArrayList<Project> projects, Context context) {
        mProjects = projects;
        mContext = context;
        mFilteredProjects = projects;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_project_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Project project = mFilteredProjects.get(position);

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.default_avatar)
                .centerInside()
                .override(100, 100);

        Glide.with(mContext)
                .setDefaultRequestOptions(options)
                .load(project.getAvatar())
                .into(((ViewHolder)holder).avatar);

        ((ViewHolder)holder).name.setText(project.getName());
        ((ViewHolder)holder).created.setText(project.getTime_created().toString());

    }

    @Override
    public int getItemCount() {
        return mFilteredProjects.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredProjects = mProjects;
                } else {
                    ArrayList<Project> filteredList = new ArrayList<>();

                    for (int i = 0; i < mProjects.size(); i++) {
                        if (mProjects.get(i).getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(mProjects.get(i));
                        } else if (mProjects.get(i).getDescription().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(mProjects.get(i));
                        }
                    }
                    mFilteredProjects = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredProjects;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredProjects = (ArrayList<Project>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView avatar;
        TextView name, created;


        public ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.project_name);
            created = itemView.findViewById(R.id.created);

        }

    }

}








