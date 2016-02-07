package nanborklabs.location;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    Toolbar toolbar;
    String college_name;
    String routeId;
    String userName;
    EditText user_name_box;
    EditText college_box;
    EditText Route_box;
    Button sbButton;
    public static String PREFS = "MyPrefs";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //setting-Up Views
        setupViews();
        toolbar.setTitle("Welcome");
        toolbar.setSubtitle("Login Page");
        sbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getvalues()){
                    startActivity(new Intent(getApplicationContext(),MapHomeActivity.class));
                }
            }
        });


    }



    private boolean getvalues() {


        userName=user_name_box.getText().toString();
        college_name=college_box.getText().toString();
        routeId=Route_box.getText().toString();


        if(isBoxesNULL()){
            Toast.makeText(getApplicationContext(),"Enter Missing Fields",Toast.LENGTH_SHORT).show();
            return false;

        }
        else{
           return writeToSharedPrefs();
        }

    }

    /**
     * A Function to Write user name ,College , Route ID to file,
     * Writes to shared pefrences
     * @link Static String FILB_NAME
     * @return boolean(Suceessfull write)
     *
     */


    private boolean writeToSharedPrefs() {

        SharedPreferences sp = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString("User_name", userName);
        spEditor.putString("College_name", college_name);
        spEditor.putString("Route_id", routeId);
        return spEditor.commit();
    }

    /** A function To check Whether Editbox files are empty or not
     * this function does not validate anything , just check fo null values
     * @return
     */

    private boolean isBoxesNULL() {


        if ((userName.length() == 0 || college_name.length() == 0 || routeId.length() == 0))
            return true;
        else return false;
    }


    /**
     * getting References to view
     * perForm any animation transiiton with this View Objects
     *
     */




    private void setupViews() {
         user_name_box=(EditText)findViewById(R.id.email_box);
         college_box=(EditText)findViewById(R.id.college_name_box);
         Route_box=(EditText)findViewById(R.id.route_box);
         sbButton=(Button)findViewById(R.id.start_button_submit);
        toolbar=(Toolbar)findViewById(R.id.toolbar);


    }




}
