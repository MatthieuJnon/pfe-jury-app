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

import com.google.gson.JsonObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.Jury;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.PFEDatabase;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.Project;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.User;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.UserProjectJoin;
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
            if (users != null) {
                for (User user : users) {
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
            Log.w("INFO", "LOADING JURIES TASK");
            User user = PFEDatabase
                    .getInstance(Application.getAppContext())
                    .getUserDao()
                    .getLoggedUser();

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("q", "LIPRJ");
            params.put("user", user.getName());
            params.put("token", user.getToken());

            // First we get all projects
            JSONObject res = HttpUtils.executeRequest("GET", HttpUtils.URL, params);

            if (HttpUtils.requestOK(res)) {
                JSONArray projects;
                try {
                    projects = res.getJSONArray("projects");
                } catch (JSONException e) {
                    return false;
                }

                for (int i = 0; i < projects.length(); i++) {
                    JSONObject project;
                    try {
                        project = projects.getJSONObject(i);
                        addProject(project);
                    } catch (JSONException e) {
                        Log.e("errAddProject", e.getMessage());
                        continue;
                    }

                }

            }

            // Then we get all juries that the connected user is a member of
            params = new LinkedHashMap<>();
            params.put("q", "MYJUR");
            params.put("user", user.getName());
            params.put("token", user.getToken());

            res = HttpUtils.executeRequest("GET", HttpUtils.URL, params);

            if(HttpUtils.requestOK(res)) {
                JSONArray juries;
                try {
                    juries = res.getJSONArray("juries");
                } catch (JSONException e) {
                    Log.e("err getting jurys array", e.getMessage());
                    return false;
                }

                for (int i = 0; i < juries.length(); i++) {
                    JSONObject jury;
                    try {
                        jury = juries.getJSONObject(i);
                        addJury(jury);
                    } catch (JSONException e) {
                        Log.e("errAddJury", e.getMessage());
                        continue;
                    }
                }
            }

            return false;
        }

        private void addProject(JSONObject project) throws JSONException {

            Project p = PFEDatabase.getInstance(Application.getAppContext())
                    .getProjectDao()
                    .getProjectFromTitle(project.getString("title"));

            if (p == null) {
                Log.i("NEW PROJECT", project.getString("title"));

                p = new Project(0,
                        project.getString("title"),
                        project.getInt("confid"),
                        project.getString("descrip"),
                        0);

                PFEDatabase.getInstance(Application.getAppContext())
                        .getProjectDao()
                        .insert(p);
                // We fetch the newly created project to get an up to date id
                p = PFEDatabase.getInstance(Application.getAppContext())
                        .getProjectDao()
                        .getProjectFromTitle(project.getString("title"));
            }

            JSONObject supervisor = project.getJSONObject("supervisor");

            User u = PFEDatabase.getInstance(Application.getAppContext())
                    .getUserDao()
                    .getUserFromFullName(supervisor.getString("forename"),
                            supervisor.getString("surname"));


            if (u == null) {
                String username = formatName(supervisor.getString("surname"),
                        supervisor.getString("forename"));
                u = new User(0,
                        username,
                        null,
                        "null",
                        supervisor.getString("forename"),
                        supervisor.getString("surname"),
                        false,
                        null);

                Log.i("NEW USR", username);

                PFEDatabase.getInstance(Application.getAppContext())
                        .getUserDao()
                        .insert(u);

                // We fetch the newly created user to get an up to date id.
                u = PFEDatabase.getInstance(Application.getAppContext())
                        .getUserDao()
                        .getUserFromName(username).get(0);

            }

            UserProjectJoin uPJ = PFEDatabase.getInstance(Application.getAppContext())
                    .getUserProjectJoinDao()
                    .getUserProjectJoin(u.getUid(), p.getProjectId());

            if (uPJ == null) {
                PFEDatabase.getInstance(Application.getAppContext())
                        .getUserProjectJoinDao()
                        .insert(new UserProjectJoin(u.getUid(),
                                p.getProjectId(),
                                null,
                                true,
                                false));
            }

        }

        private void addJury(JSONObject jury) throws JSONException {
            // Add jury

            Jury j = PFEDatabase.getInstance(Application.getAppContext())
                    .getJuryDao()
                    .getJury(Integer.parseInt(jury.getString("idJury")));

            if (j == null) {
                Log.i("NEW JURY", jury.getString("idJury"));

                String dateString = jury.getString("date");

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, Integer.parseInt(dateString.substring(0,4)));
                cal.set(Calendar.MONTH, Integer.parseInt(dateString.substring(5,7)));
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateString.substring(8,10)));
                Date date = cal.getTime();

                j = new Jury( Integer.parseInt(jury.getString("idJury")),
                        date);

                PFEDatabase.getInstance(Application.getAppContext())
                        .getJuryDao()
                        .insert(j);

                j = PFEDatabase.getInstance(Application.getAppContext())
                        .getJuryDao()
                        .getJury(Integer.parseInt(jury.getString("idJury")));

            }

            JSONArray projects = jury.getJSONObject("info").getJSONArray("projects");

            for (int i = 0; i < projects.length(); i++) {
                JSONObject project;
                try {
                    project = projects.getJSONObject(i);
                } catch (JSONException e) {
                    Log.e("errAddProject2Jury", e.getMessage());
                    continue;
                }

                Project p = PFEDatabase.getInstance(Application.getAppContext())
                        .getProjectDao()
                        .getProjectFromTitle(project.getString("title"));

                p.setJuryId(j.juryId);

                PFEDatabase.getInstance(Application.getAppContext())
                        .getProjectDao()
                        .update(p);

            }

            // fetch all projects from jury and update them with jury id
        }

        private String formatName(String lastname, String firstname) {
            return lastname.substring(0,
                    Math.min(lastname.length(), 5))
                    .toLowerCase()
                    .concat(firstname
                            .substring(0, Math.min(firstname.length(), 3))
                            .toLowerCase());
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                return;
            }
        }

    }


}
