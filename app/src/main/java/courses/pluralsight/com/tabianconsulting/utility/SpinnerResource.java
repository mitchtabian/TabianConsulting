package courses.pluralsight.com.tabianconsulting.utility;


import courses.pluralsight.com.tabianconsulting.R;
import courses.pluralsight.com.tabianconsulting.models.Issue;

/**
 * Created by User on 4/17/2018.
 */

public class SpinnerResource {

    public static final String[] issue_priorities_spinner =
            {Issue.HIGH, Issue.MEDIUM, Issue.LOW};

    public static final int[] issue_priority_images_spinner =
            {R.drawable.ic_high_priority, R.drawable.ic_medium_priority, R.drawable.ic_low_priority};

    public static final String[] issue_status_spinner =
            {Issue.IN_PROGRESS, Issue.DONE, Issue.IDLE};

    public static final int[] issue_type_images_spinner =
            {R.drawable.ic_task_blue, R.drawable.red_bug};

    public static final String[] issue_type_spinner =
            {Issue.TASK, Issue.BUG};
}
