package courses.pluralsight.com.tabianconsulting.issues;

import java.util.ArrayList;

import courses.pluralsight.com.tabianconsulting.models.Issue;
import courses.pluralsight.com.tabianconsulting.models.Project;


/**
 * Created by User on 4/16/2018.
 */

public interface IIssues {

    void showProgressBar();

    void hideProgressBar();

    void buildSnackbar(String message);

    void getProjects();

    void deleteIssuesFromProject(ArrayList<Issue> issues, Project project);
}
