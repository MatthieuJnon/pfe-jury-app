package fr.eseo.dis.joannomabeduneba.pfe_jury_app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.LinkedHashMap;

import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.PFEDatabase;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.User;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.utils.HttpUtils;


public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerView);
        new UserLoadJuriesTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.kebab_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_disconnect) {
            new UserDcTask().execute();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
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


    public class UserLoadJuriesTask extends AsyncTask<Void, Void, Boolean> {


        UserLoadJuriesTask() {
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.w("INFO","LOADING JURIES TASK");
            User user = PFEDatabase
                        .getInstance(Application.getAppContext())
                        .getUserDao()
                        .getLoggedUser();

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("q", "LIPRJ");
            params.put("user", user.getName());
            params.put("token", user.getToken());

            JSONObject res = HttpUtils.executeRequest("GET", HttpUtils.URL, params);

            System.out.println(res);

            if (HttpUtils.requestOK(res)) {
                JSONArray projects;
                try {
                    projects = res.getJSONArray("projects");
                } catch (JSONException e) {
                    return false;
                }

                System.out.println(projects.length());
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                return;
            }
        }

    }


}
