package bruca.prads.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import bruca.prads.R;
import bruca.prads.activities.MainActivity;
import bruca.prads.activities.SelfEvaluationTests;
import bruca.prads.activities.SelfEvaluationTestsActivity;
import bruca.prads.adapters.StudiesAdapter;
import bruca.prads.adapters.TestsFragmentAdapter;
import bruca.prads.helpers.Note;

/**
 * Created by Emo on 5/8/2017.
 */

public class TestsFragment extends Fragment implements TestsFragmentAdapter.TestsFragmentAdapterListener {
    View rootView;
    private GridView gridView;
    private RecyclerView recyclerView;
    ArrayList<Drawable> allDrawableImages = new ArrayList<>();
    private List<Note> notes = new ArrayList<>();
    private TypedArray allImages;
    private StudiesAdapter mAdapter;
    int [] module_images = { R.drawable.wellbeing, R.drawable.depression, R.drawable.anxiety,
    R.drawable.stress};

        public TestsFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static TestsFragment newInstance() {
            TestsFragment fragment = new TestsFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.tests_fragment, container, false);

            getAllWidgets(rootView);
            setAdapter();
            return rootView;
        }

    public void getAllWidgets(View view){
        //gridView = (GridView) view.findViewById(R.id.gridview2);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_tests);
        //allImages = getResources().obtainTypedArray(R.array.icons);
        //int[] img = getResources().getIntArray(R.array.icons);
        allImages = getResources().obtainTypedArray(R.array.icons);
    }

    private void setAdapter()
    {
        TestsFragmentAdapter testsFragmentAdapter = new TestsFragmentAdapter(
                SelfEvaluationTestsActivity.getInstance(), module_images,this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(MainActivity.getInstance(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new TestsFragment.GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(testsFragmentAdapter);
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onIconClicked(int position) {
       // if (mAdapter.getSelectedItemCount() > 0) {
        //Note note = notes.get(position);
        //note.setRead(true);
       // notes.set(position, note);
        //mAdapter.notifyDataSetChanged();
        if(position == 0){

            Intent intent = new Intent(SelfEvaluationTestsActivity.getInstance(), SelfEvaluationTests.class);
            intent.putExtra("test", "who");
            startActivity(intent);
            SelfEvaluationTestsActivity.getInstance().finish();

        } else if(position == 1){
            Intent intent = new Intent(SelfEvaluationTestsActivity.getInstance(), SelfEvaluationTests.class);
            intent.putExtra("test", "depression");
            startActivity(intent);
            SelfEvaluationTestsActivity.getInstance().finish();

        } else if(position == 2){
            Intent intent = new Intent(SelfEvaluationTestsActivity.getInstance(), SelfEvaluationTests.class);
            intent.putExtra("test", "anxiety");
            startActivity(intent);
            SelfEvaluationTestsActivity.getInstance().finish();

        } else if (position == 3){
            Intent intent = new Intent(SelfEvaluationTestsActivity.getInstance(), SelfEvaluationTests.class);
            intent.putExtra("test", "stress");
            startActivity(intent);
            SelfEvaluationTestsActivity.getInstance().finish();
        }



    }

    @Override
    public void onIconImportantClicked(int position) {

    }

    @Override
    public void onMessageRowClicked(int position) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        //if (mAdapter.getSelectedItemCount() > 0) {
          //  enableActionMode(position);
        //} else {
            // read the message which removes bold from the row
        //}
    }

    @Override
    public void onRowLongClicked(int position) {

    }
}
