package courses.pluralsight.com.tabianconsulting.issues;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import courses.pluralsight.com.tabianconsulting.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 4/17/2018.
 */

public class SpinnerAdapter extends ArrayAdapter<String> {

    String[] mSpinnerText, mSpinnerImageStrings;
    int[] mSpinnerImageIntegers;
    Context mContext;
    String selectedText = "";

    public SpinnerAdapter(@NonNull Context context, String[] spinnerText, int[] spinnerImages) {
        super(context, R.layout.layout_spinner_image_and_text);
        mSpinnerText = spinnerText;
        mSpinnerImageIntegers = spinnerImages;
        mContext = context;
        selectedText = spinnerText[0];
    }

    public SpinnerAdapter(@NonNull Context context, String[] spinnerText, String[] spinnerImages) {
        super(context, R.layout.layout_spinner_image_and_text);
        mSpinnerText = spinnerText;
        mSpinnerImageStrings = spinnerImages;
        mContext = context;
        selectedText = spinnerText[0];
    }

    @Override
    public int getCount() {
        return mSpinnerText.length;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.layout_spinner_image_and_text, parent, false);
            mViewHolder.mImage = convertView.findViewById(R.id.image);
            mViewHolder.mText = convertView.findViewById(R.id.text);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .centerInside()
                .override(50, 50);

        if(mSpinnerImageIntegers != null){
            Glide.with(mContext)
                    .setDefaultRequestOptions(options)
                    .load(mSpinnerImageIntegers[position])
                    .into(mViewHolder.mImage);
        }
        else if(mSpinnerImageStrings != null){
            Glide.with(mContext)
                    .setDefaultRequestOptions(options)
                    .load(mSpinnerImageStrings[position])
                    .into(mViewHolder.mImage);
        }


        mViewHolder.mText.setText(mSpinnerText[position]);

        return convertView;
    }

    @Override
    public int getPosition(@Nullable String item) {
        for(int i = 0; i < mSpinnerImageStrings.length; i++){
            if(mSpinnerText[i].equals(item)){
                return i;
            }
        }
        return super.getPosition(item);
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return mSpinnerText[position];
    }

    public String getSelectedText(){
        return selectedText;
    }
    public void setSelectedText(String text){
        selectedText = text;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    private static class ViewHolder {
        CircleImageView mImage;
        TextView mText;
    }
}














