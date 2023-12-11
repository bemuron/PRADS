package bruca.prads.fragments;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bruca.prads.R;
import bruca.prads.models.SquareImageView;

/**
 * Created by Emo on 5/8/2017.
 */

public class DisplayStudyTipsfragment extends Fragment {
    View rootView;
    int mCurrentPage;
    private String title,articleContent,toolbarTitle,bgImage;
    private TextView lblCount;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //getting arguments from the bundle object
        Bundle data = getArguments();

        //getting the title and content
        title = data.getString("Title");
        articleContent = data.getString("Content");
        bgImage = data.getString("bgImage");
    }


        public DisplayStudyTipsfragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            rootView = inflater.inflate(R.layout.study_tips_articles, container, false);
            SquareImageView backgroundImage = (SquareImageView)rootView.findViewById(R.id.bkImage);
            CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);
            //TextView article_name = (TextView)rootView.findViewById(R.id.article_name);
            TextView article = (TextView) rootView.findViewById(R.id.article);

            //set the texts
            //lblCount.setText(count);
            //article_name.setText(title);
            //article_name.setTextSize(10);

            article.setText(articleContent);
            article.setTextSize(20);

            collapsingToolbar.setTitle(title);
            int imageId = getResources().getIdentifier(bgImage,
                    "drawable", getActivity().getPackageName());
            backgroundImage.setImageResource(imageId);
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
            collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

            return rootView;
        }

}
