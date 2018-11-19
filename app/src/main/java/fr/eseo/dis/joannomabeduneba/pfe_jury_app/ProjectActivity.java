package fr.eseo.dis.joannomabeduneba.pfe_jury_app;

import android.Manifest;
import android.arch.persistence.room.util.StringUtil;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import fr.eseo.dis.joannomabeduneba.pfe_jury_app.adapters.StudentsAdapter;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.PFEDatabase;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.Project;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.User;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.UserProjectJoin;

public class ProjectActivity extends AppCompatActivity {

    private final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private TextView gradeView;
    private StudentsAdapter mAdapter;
    private int projectId;
    private List<User> usersList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        }

        Intent intent = getIntent();
        final Project project = (Project) intent.getSerializableExtra("project");
        projectId = project.getProjectId();
        TextView titleView = findViewById(R.id.titleView);
        titleView.append(project.getProject());

        TextView descView = findViewById(R.id.descView);
        descView.append(project.getDescription());

        LoadStudentsTask loadStudentsTask = new LoadStudentsTask(project.getProjectId());
        loadStudentsTask.execute();

        gradeView = findViewById(R.id.gradeView);
        final PutGradeTask putGradeTask = new PutGradeTask(project.getProjectId(), null);
        putGradeTask.execute();
        gradeView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    if(isNumeric(gradeView.getText().toString())){
                        PutGradeTask putGradeTask = new PutGradeTask(project.getProjectId(), Integer.parseInt(gradeView.getText().toString()));
                        putGradeTask.execute();
                    }
                }
                return false;
            }
        });

        Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath() + File.separator + "poster.png");
        ImageView posterView = findViewById(R.id.posterView);
        posterView.setImageBitmap(bmp);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void userItemClick(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Evaluation");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PutGradeTask putGradeTask = new PutGradeTask(projectId, Integer.parseInt(input.getText().toString()));
                putGradeTask.setUserId(usersList.get(pos).getUid());
                putGradeTask.execute();

                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setHasFixedSize(true);

                LinearLayoutManager layoutManager = new LinearLayoutManager(ProjectActivity.this);
                recyclerView.setLayoutManager(layoutManager);

                mAdapter = new StudentsAdapter(new ArrayList<>(usersList), projectId,  ProjectActivity.this);
                recyclerView.setAdapter(mAdapter);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }



    public class LoadStudentsTask extends AsyncTask<Void, Void, Boolean> {

        private int projectId;

        public LoadStudentsTask(int projectId) {
            this.projectId = projectId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            usersList = PFEDatabase.getInstance(Application.getAppContext()).getUserProjectJoinDao().getStudentsForProject(projectId);

            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);

            LinearLayoutManager layoutManager = new LinearLayoutManager(ProjectActivity.this);
            recyclerView.setLayoutManager(layoutManager);

            mAdapter = new StudentsAdapter(new ArrayList<>(usersList), projectId,  ProjectActivity.this);
            recyclerView.setAdapter(mAdapter);

            return usersList != null;
        }


        @Override
        protected void onPostExecute(Boolean success) {

        }

        @Override
        protected void onCancelled() {

        }
    }

    public class PutGradeTask extends AsyncTask<Void, Void, Boolean> {

        private int projectId;
        private Integer userId;
        private Integer grade;

        public PutGradeTask(int projectId, Integer grade) {
            this.projectId = projectId;
            this.grade = grade;
        }

        public void setUserId(Integer userId){
            this.userId = userId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(userId == null){
                User user = PFEDatabase.getInstance(Application.getAppContext()).getUserDao().getLoggedUser();
                userId = user.getUid();
            }
            UserProjectJoin project = PFEDatabase.getInstance(Application.getAppContext()).getUserProjectJoinDao().getUserProjectJoin(userId, projectId);

            if(project == null){
                project = new UserProjectJoin(userId, projectId, null, true, false, null);
                PFEDatabase.getInstance(Application.getAppContext()).getUserProjectJoinDao().insert(project);
            }

            if(grade == null){

                if(project.note == null || project.note.equals("null")){
                    return false;
                } else {
                    grade = Integer.valueOf(project.note);
                    return true;
                }
            }

            project.note = String.valueOf(grade);
            PFEDatabase.getInstance(Application.getAppContext()).getUserProjectJoinDao().update(project);
            return true;
        }


        @Override
        protected void onPostExecute(Boolean success) {
            if(success){
                gradeView.setText(Integer.toString(grade));
            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
