package bruca.prads.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.MenuItem;
import android.widget.TextView;

import bruca.prads.R;
import bruca.prads.helpers.URLSpanNoUnderline;

/**
 * Created by Emo on 5/25/2016.
 */
public class UsefulContacts extends AppCompatActivity{
    private TextView address, telephone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_useful_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        address = (TextView)findViewById(R.id.address_content);
        telephone = (TextView)findViewById(R.id.tel_content);
        setUpContacts();

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setUpContacts(){
        address.setText(R.string.uni_health_center_address);
        /*
        address.setText("Riccarton General Practice" +
        "\n" + "The Avenue" +
        "\n" + "Edinburgh Campus" +
        "\n" + "EH14 4AS");
*/
        telephone.setText("+44 (0)131 451 3010 ");

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
