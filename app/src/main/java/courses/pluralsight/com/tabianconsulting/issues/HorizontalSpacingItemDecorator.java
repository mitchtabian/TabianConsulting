package courses.pluralsight.com.tabianconsulting.issues;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by User on 4/17/2018.
 */

public class HorizontalSpacingItemDecorator extends RecyclerView.ItemDecoration {

    private final int horizontalSpacing;

    public HorizontalSpacingItemDecorator(int horizontalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = horizontalSpacing;
    }
}
