package courses.pluralsight.com.tabianconsulting.utility;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;

import courses.pluralsight.com.tabianconsulting.AdminActivity;
import courses.pluralsight.com.tabianconsulting.R;
import courses.pluralsight.com.tabianconsulting.models.User;

/**
 * Created by User on 10/25/2017.
 */

public class EmployeesAdapter extends RecyclerView.Adapter<EmployeesAdapter.ViewHolder>{

    private static final String TAG = "EmployeesAdapter";

    private ArrayList<User> mUsers;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView profileImage;
        public TextView name, department;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImage = (ImageView) itemView.findViewById(R.id.profile_image);
            name = (TextView) itemView.findViewById(R.id.name);
            department = (TextView) itemView.findViewById(R.id.department);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: selected employee: " + mUsers.get(getAdapterPosition()));

                    //open a dialog for selecting a department
                    ((AdminActivity)mContext).setDepartmentDialog(mUsers.get(getAdapterPosition()));
                }
            });
        }
    }

    public EmployeesAdapter(Context context, ArrayList<User> users) {
        mUsers = users;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflate the custom layout
        View view = inflater.inflate(R.layout.layout_employee_listitem, parent, false);

        //return a new holder instance
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) throws NullPointerException{
        ImageLoader.getInstance().displayImage(mUsers.get(position).getProfile_image(), holder.profileImage);
        holder.name.setText(mUsers.get(position).getName());
        holder.department.setText(mUsers.get(position).getDepartment());
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
