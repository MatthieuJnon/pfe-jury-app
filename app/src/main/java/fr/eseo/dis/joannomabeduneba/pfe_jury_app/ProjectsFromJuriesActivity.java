package fr.eseo.dis.joannomabeduneba.pfe_jury_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import fr.eseo.dis.joannomabeduneba.pfe_jury_app.adapters.ProjectAdapter;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.Jury;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.PFEDatabase;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.Project;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.User;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.utils.HttpUtils;

public class ProjectsFromJuriesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProjectAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View mProgressView;
    private Jury mJury;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_projects);

        Intent intent = getIntent();
        mJury = (Jury) intent.getSerializableExtra("jury");

        mRecyclerView = findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ProjectAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        new LoadProjectsTask().execute();

    }

    public void clickProject(Project p) {

        LoadProjectTask projectTask = new LoadProjectTask(p.getProjectId());
        projectTask.execute((Void) null);
    }

    public class LoadProjectsTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            List<Project> projects = PFEDatabase.getInstance(Application.getAppContext())
                    .getProjectDao()
                    .getAllProjects();

            List<Project> projectsToAdd = new ArrayList<>();

            for (int i = 0; i < projects.size(); i++) {
                if (projects.get(i).getJuryId() == mJury.juryId) {
                    projectsToAdd.add(projects.get(i));
                }
            }

            mAdapter.setProjects(projectsToAdd);
            return  true;

        }


        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onCancelled() {

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
            if (ContextCompat.checkSelfPermission(ProjectsFromJuriesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ProjectsFromJuriesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ProjectsFromJuriesActivity.this,
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
            if (success) {
                Intent myIntent = new Intent(ProjectsFromJuriesActivity.this, ProjectActivity.class);
                myIntent.putExtra("project", mProject);
                startActivity(myIntent);
            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
