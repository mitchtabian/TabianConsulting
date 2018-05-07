package courses.pluralsight.com.tabianconsulting.issues;

import java.util.ArrayList;

import courses.pluralsight.com.tabianconsulting.models.Issue;
import courses.pluralsight.com.tabianconsulting.models.Project;

<<<<<<< HEAD
=======

>>>>>>> Module_7.5_End
/**
 * Created by User on 4/16/2018.
 */

public interface IIssues {

    void showProgressBar();

    void hideProgressBar();

    void buildSnackbar(String message);

    void getProjects();

<<<<<<< HEAD
    void deleteIssuesAndAttachments(ArrayList<Issue> issues, Project project);
=======
    void deleteIssuesFromProject(ArrayList<Issue> issues, Project project);
>>>>>>> Module_7.5_End
}
