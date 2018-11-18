package fr.eseo.dis.joannomabeduneba.pfe_jury_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.Project;

public class ProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Intent intent = getIntent();
        Project project = (Project) intent.getSerializableExtra("project");

        TextView titleView = findViewById(R.id.titleView);
        titleView.append(project.getProject());

        TextView descView = findViewById(R.id.descView);
        descView.append(project.getDescription());

        Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath() + File.separator + "poster.png");
        ImageView posterView = findViewById(R.id.posterView);
        posterView.setImageBitmap(bmp);

    }



//    public class LoadProjectTask extends AsyncTask<Void, Void, String> {
//
//        @Override
//        protected String doInBackground(Void... params) {
//
//        }
//
//
//        @Override
//        protected void onPostExecute(String name) {
//
//        }
//
//        @Override
//        protected void onCancelled() {
//
//        }
//    }
}
