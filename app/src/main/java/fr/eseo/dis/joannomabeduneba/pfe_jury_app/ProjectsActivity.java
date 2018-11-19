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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;

import fr.eseo.dis.joannomabeduneba.pfe_jury_app.adapters.ProjectAdapter;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.PFEDatabase;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.Project;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.User;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.utils.HttpUtils;

public class ProjectsActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private RecyclerView mRecyclerView;
    private ProjectAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DrawerLayout mDrawerLayout;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_projects);

        mRecyclerView = findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ProjectAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        new LoadProjects().execute();


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

                        switch (menuItem.getItemId()) {
                            case R.id.nav_projects:
                                ProjectsActivity.LoadProjectTask projectTask = new ProjectsActivity.LoadProjectTask(2);
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
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.kebab_menu, menu);
        return true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_disconnect:
                new ProjectsActivity.UserDcTask().execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                Intent myIntent = new Intent(ProjectsActivity.this, LoginActivity.class);
                ProjectsActivity.this.startActivity(myIntent);
                finish();
            }
        }
    }

    public class LoadProjects extends AsyncTask<Void, Void, Boolean> {

        LoadProjects() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mAdapter.setProjects(PFEDatabase.getInstance(Application.getAppContext())
                .getProjectDao()
                .getAllProjects());

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                mAdapter.notifyDataSetChanged();
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
            if (ContextCompat.checkSelfPermission(ProjectsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ProjectsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ProjectsActivity.this,
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

            JSONObject response = HttpUtils.executeRequest("GET", HttpUtils.URL, parameters, true);

            return HttpUtils.requestOK(response);
        }


        @Override
        protected void onPostExecute(Boolean success) {
            showProgress(false);
            if (success) {
                Intent myIntent = new Intent(ProjectsActivity.this, ProjectActivity.class);
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
