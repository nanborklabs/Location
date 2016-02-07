package nanborklabs.location;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by nandhu on 24/1/16.
 */
public class DetailsActivity extends Activity{



    String uname,cname,rname;
    public static String PREFS = "MyPrefs";


    /**
     * Called when the activity is starting.  This is where most initialization
     * should go: calling {@link #setContentView(int)} to inflate the
     * activity's UI, using {@link #findViewById} to programmatically interact
     * with widgets in the UI, calling
     * {@link #managedQuery(Uri, String[], String, String[], String)} to retrieve
     * cursors for data being displayed, etc.
     * <p/>
     * <p>You can call {@link #finish} from within this function, in
     * which case onDestroy() will be immediately called without any of the rest
     * of the activity lifecycle ({@link #onStart}, {@link #onResume},
     * {@link #onPause}, etc) executing.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * @see #onStart
     * @see #onSaveInstanceState
     * @see #onRestoreInstanceState
     * @see #onPostCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.details_page);

        TextView username=(TextView)findViewById(R.id.username);
        TextView collegename=(TextView)findViewById(R.id.collegename);
        TextView Routeid=(TextView)findViewById(R.id.routeid);

        readFromSharedprefs();
        username.setText(uname);
        collegename.setText(cname);
        Routeid.setText(rname);

    }

    private void readFromSharedprefs() {
        SharedPreferences sp=getSharedPreferences(PREFS,Context.MODE_PRIVATE);
        uname=sp.getString("User_name", "User Name");
        cname=sp.getString("College_name","College name");
        rname=sp.getString("Route_id","Route");



    }
}
