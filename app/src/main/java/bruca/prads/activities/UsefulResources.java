package bruca.prads.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import bruca.prads.R;
import bruca.prads.helpers.URLSpanNoUnderline;
import database.FeedReaderDbHelper;

/**
 * Created by Emo on 5/25/2016.
 */
public class UsefulResources extends AppCompatActivity{
    private TextView depression_link_1, depression_link_2, depression_link_3,depression_link_4,
            anxiety_link_1,anxiety_link_2,anxiety_link_3,anxiety_link_4,
    stress_link1,stress_link2,stress_link3,stress_link4,
    wellbeing_link1,wellbeing_link2,wellbeing_link3,wellbeing_link4;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_useful_resources);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        depression_link_1 = (TextView) findViewById(R.id.depression_link_1);
        depression_link_2 = (TextView) findViewById(R.id.depression_link_2);
        depression_link_3 = (TextView) findViewById(R.id.depression_link_3);
        depression_link_4 = (TextView) findViewById(R.id.depression_link_4);

        anxiety_link_1 = (TextView) findViewById(R.id.anxiety_link_1);
        anxiety_link_2 = (TextView) findViewById(R.id.anxiety_link_2);
        anxiety_link_3 = (TextView) findViewById(R.id.anxiety_link_3);
        anxiety_link_4 = (TextView) findViewById(R.id.anxiety_link_4);

        stress_link1 = (TextView) findViewById(R.id.stress_link_1);
        stress_link2 = (TextView) findViewById(R.id.stress_link_2);
        stress_link3 = (TextView) findViewById(R.id.stress_link_3);
        stress_link4 = (TextView) findViewById(R.id.stress_link_4);

        wellbeing_link1 = (TextView) findViewById(R.id.wellbeing_link_1);
        wellbeing_link2 = (TextView) findViewById(R.id.wellbeing_link_2);
        wellbeing_link3 = (TextView) findViewById(R.id.wellbeing_link_3);
        wellbeing_link4 = (TextView) findViewById(R.id.wellbeing_link_4);

        setUpDepressionLinks();
        setUpAnxietyLinks();
        setUpStressLinks();
        setUpWellbeingLinks();

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //prepare the depression resources links
    private void setUpDepressionLinks(){
        String d_val1 = "<html><b><a href=\"http://www.healthline.com/health/how-to-avoid-depression#1\">" +
                "How to avoid depression</a></b></html>";
        String d_val2 = "<html><b><a href=\"http://www.mayoclinic.org/diseases-conditions/depression/manage/ptc-20321574\">" +
                "Depression (major depressive disorder)</a></b></html>";
        String d_val3 = "<html><b><a href=\"http://oxfordmedicine.com/view/10.1093/9780195173642.001.0001/med-9780195173642-chapter-4\">" +
                "Prevention of Depression and Bipolar Disorder</a></b></html>";
        String d_val4 = "<html><b><a href=\"http://www.nhs.uk/Livewell/studenthealth/Pages/Mentalhealth.aspx\">" +
                "Student mental health</a></b></html>";

        Spannable d_spannedText1 = (Spannable)fromHtml(d_val1);
        Spannable d_spannedText2 = (Spannable)fromHtml(d_val2);
        Spannable d_spannedText3 = (Spannable)fromHtml(d_val3);
        Spannable d_spannedText4 = (Spannable)fromHtml(d_val4);
        //if (depression_link_1 != null) {
        depression_link_1.setMovementMethod(LinkMovementMethod.getInstance());
        depression_link_2.setMovementMethod(LinkMovementMethod.getInstance());
        depression_link_3.setMovementMethod(LinkMovementMethod.getInstance());
        depression_link_4.setMovementMethod(LinkMovementMethod.getInstance());
        //}

        Spannable processedText1 = removeUnderlines(d_spannedText1);
        Spannable processedText2 = removeUnderlines(d_spannedText2);
        Spannable processedText3 = removeUnderlines(d_spannedText3);
        Spannable processedText4 = removeUnderlines(d_spannedText4);
        depression_link_1.setText(processedText1);
        depression_link_2.setText(processedText2);
        depression_link_3.setText(processedText3);
        depression_link_4.setText(processedText4);
    }

    private void setUpAnxietyLinks(){
        String val1 = "<html><b><a href=\"http://www.healthline.com/health/anxiety-prevention#overview1\">" +
                "Anxiety Prevention</a></b></html>";
        String val2 = "<html><b><a href=\"http://www.moodjuice.scot.nhs.uk/anxiety.asp\">" +
                "Anxiety Self-help guide</a></b></html>";
        String val3 = "<html><b><a href=\"http://www.mayoclinic.org/diseases-conditions/generalized-anxiety-disorder/basics/prevention/con-20024562\">" +
                "Generalized anxiety disorder</a></b></html>";
        String val4 = "<html><b><a href=\"http://www.calmclinic.com/panic/prevention\">" +
                "7 Tips for Panic Attack Prevention</a></b></html>";

        Spannable spannedText1 = (Spannable)fromHtml(val1);
        Spannable spannedText2 = (Spannable)fromHtml(val2);
        Spannable spannedText3 = (Spannable)fromHtml(val3);
        Spannable spannedText4 = (Spannable)fromHtml(val4);
        //if (depression_link_1 != null) {
        anxiety_link_1.setMovementMethod(LinkMovementMethod.getInstance());
        anxiety_link_2.setMovementMethod(LinkMovementMethod.getInstance());
        anxiety_link_3.setMovementMethod(LinkMovementMethod.getInstance());
        anxiety_link_4.setMovementMethod(LinkMovementMethod.getInstance());
        //}

        Spannable processedText1 = removeUnderlines(spannedText1);
        Spannable processedText2 = removeUnderlines(spannedText2);
        Spannable processedText3 = removeUnderlines(spannedText3);
        Spannable processedText4 = removeUnderlines(spannedText4);
        anxiety_link_1.setText(processedText1);
        anxiety_link_2.setText(processedText2);
        anxiety_link_3.setText(processedText3);
        anxiety_link_4.setText(processedText4);
    }

    private void setUpStressLinks(){
        String val1 = "<html><b><a href=\"http://www.nhs.uk/Conditions/stress-anxiety-depression/pages/reduce-stress.aspx\">" +
                "10 stress busters</a></b></html>";
        String val2 = "<html><b><a href=\"http://www.healthline.com/health/stress-prevention#overview1\">" +
                "Preventing stress</a></b></html>";
        String val3 = "<html><b><a href=\"https://www.helpguide.org/articles/stress/stress-management.htm\">" +
                "Stress management</a></b></html>";
        String val4 = "<html><b><a href=\"https://www.skillsyouneed.com/ps/avoiding-stress.html\">" +
                "Avoiding Stress</a></b></html>";

        Spannable spannedText1 = (Spannable)fromHtml(val1);
        Spannable spannedText2 = (Spannable)fromHtml(val2);
        Spannable spannedText3 = (Spannable)fromHtml(val3);
        Spannable spannedText4 = (Spannable)fromHtml(val4);
        //if (depression_link_1 != null) {
        stress_link1.setMovementMethod(LinkMovementMethod.getInstance());
        stress_link2.setMovementMethod(LinkMovementMethod.getInstance());
        stress_link3.setMovementMethod(LinkMovementMethod.getInstance());
        stress_link4.setMovementMethod(LinkMovementMethod.getInstance());
        //}

        Spannable processedText1 = removeUnderlines(spannedText1);
        Spannable processedText2 = removeUnderlines(spannedText2);
        Spannable processedText3 = removeUnderlines(spannedText3);
        Spannable processedText4 = removeUnderlines(spannedText4);
        stress_link1.setText(processedText1);
        stress_link2.setText(processedText2);
        stress_link3.setText(processedText3);
        stress_link4.setText(processedText4);
    }

    private void setUpWellbeingLinks(){
        String val1 = "<html><b><a href=\"http://www.nhs.uk/Conditions/stress-anxiety-depression/Pages/improve-mental-wellbeing.aspx\">" +
                "5 steps to mental wellbeing</a></b></html>";
        String val2 = "<html><b><a href=\"https://mind.org.uk/information-support/tips-for-everyday-living/wellbeing/#.WWoqQlGQy00\">" +
                "How to improve your mental wellbeing</a></b></html>";
        String val3 = "<html><b><a href=\"http://www.wellscotland.info/guidance/How-to-measure-mental-wellbeing\">" +
                "How to measure mental wellbeing</a></b></html>";
        String val4 = "<html><b><a href=\"http://www.nhs.uk/Conditions/stress-anxiety-depression/Pages/give-for-mental-wellbeing.aspx\">" +
                "Give for mental wellbeing</a></b></html>";

        Spannable spannedText1 = (Spannable)fromHtml(val1);
        Spannable spannedText2 = (Spannable)fromHtml(val2);
        Spannable spannedText3 = (Spannable)fromHtml(val3);
        Spannable spannedText4 = (Spannable)fromHtml(val4);

        wellbeing_link1.setMovementMethod(LinkMovementMethod.getInstance());
        wellbeing_link2.setMovementMethod(LinkMovementMethod.getInstance());
        wellbeing_link3.setMovementMethod(LinkMovementMethod.getInstance());
        wellbeing_link4.setMovementMethod(LinkMovementMethod.getInstance());

        Spannable processedText1 = removeUnderlines(spannedText1);
        Spannable processedText2 = removeUnderlines(spannedText2);
        Spannable processedText3 = removeUnderlines(spannedText3);
        Spannable processedText4 = removeUnderlines(spannedText4);
        wellbeing_link1.setText(processedText1);
        wellbeing_link2.setText(processedText2);
        wellbeing_link3.setText(processedText3);
        wellbeing_link4.setText(processedText4);
    }

    //@SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY); // for 24 api and more
        } else {
            result = Html.fromHtml(html); // or for older api
        }
        return result;
    }

    public static Spannable removeUnderlines(Spannable p_Text) {
        URLSpan[] spans = p_Text.getSpans(0, p_Text.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = p_Text.getSpanStart(span);
            int end = p_Text.getSpanEnd(span);
            p_Text.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            p_Text.setSpan(span, start, end, 0);
        }
        return p_Text;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
