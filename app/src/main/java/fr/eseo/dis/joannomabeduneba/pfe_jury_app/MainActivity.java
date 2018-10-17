package fr.eseo.dis.joannomabeduneba.pfe_jury_app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.PFEDatabase;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.User;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.utils.OnSwipeTouchListener;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Button mDcButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerView);
        mDcButton = findViewById(R.id.dcButton);
        mRecyclerView.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeBottom() {
                mDcButton.setVisibility(View.VISIBLE);
                mDcButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new UserDcTask().execute();
                    }
                });
            }

        });
    }

    public class UserDcTask extends AsyncTask<Void, Void, Boolean> {

        UserDcTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            List<User> users = PFEDatabase.getInstance(Application.getAppContext()).getUserDao().getAllUsers();

            if( users != null ) {
                for(User user : users){
                    user.setLogged(false);
                    PFEDatabase.getInstance(Application.getAppContext()).getUserDao().update(user);
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(myIntent);
                finish();
            }
        }

    }
}
