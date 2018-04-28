package courses.pluralsight.com.tabianconsulting.issues;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import courses.pluralsight.com.tabianconsulting.R;


/**
 * Created by User on 4/16/2018.
 */

public class IssuesFragment extends Fragment  {

    private static final String TAG = "IssuesFragment";

    //widgets


    //vars


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_issues, container, false);


        return view;
    }

}
















