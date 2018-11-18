package fr.eseo.dis.joannomabeduneba.pfe_jury_app;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.Jury;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.PFEDatabase;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.Project;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.User;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.UserProjectJoin;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.utils.HttpUtils;


public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private DrawerLayout mDrawerLayout;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new UserLoadJuriesTask().execute();

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mProgressView = findViewById(R.id.progress);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.name_header);
        navUsername.setText(getIntent().getStringExtra("name"));

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        showProgress(true);

                        switch (menuItem.getItemId()){
                            case R.id.nav_projects:
                                LoadProjectTask projectTask = new LoadProjectTask(2);
                                projectTask.execute((Void) null);
                                break;
                            case R.id.nav_juries:

                                break;
                        }


                        return true;
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.kebab_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_disconnect:
                new UserDcTask().execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
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

                p = new Project(project.getInt("projectId"),
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


    private class LoadProjectTask extends AsyncTask<Void, Void, Boolean> {

        private Project mProject;
        private int idProject;

        public LoadProjectTask(int idProject) {
            this.idProject = idProject;
         }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
            }

            User user = PFEDatabase
                    .getInstance(Application.getAppContext())
                    .getUserDao()
                    .getLoggedUser();

            mProject = PFEDatabase
                    .getInstance(Application.getAppContext())
                    .getProjectDao()
                    .getProject(idProject);

            LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
            parameters.put("q", "POSTR");
            parameters.put("user", user.getName());
            parameters.put("proj", String.valueOf(mProject.getProjectId()));
            parameters.put("token", user.getToken());

            JSONObject response = HttpUtils.executeRequest("GET", HttpUtils.URL, parameters,true);

            return HttpUtils.requestOK(response);
        }


        @Override
        protected void onPostExecute(Boolean success) {
            showProgress(false);
            if(success){
                Intent myIntent = new Intent(MainActivity.this, ProjectActivity.class);
                myIntent.putExtra("project", mProject);
                startActivity(myIntent);
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }


}
