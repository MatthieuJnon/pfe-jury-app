package fr.eseo.dis.joannomabeduneba.pfe_jury_app;

import android.Manifest;
import android.arch.persistence.room.util.StringUtil;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.regex.Pattern;

import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.Project;

public class ProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }

        Intent intent = getIntent();
        Project project = (Project) intent.getSerializableExtra("project");

        TextView titleView = findViewById(R.id.titleView);
        titleView.append(project.getProject());

        TextView descView = findViewById(R.id.descView);
        descView.append(project.getDescription());

        final TextView textView = findViewById(R.id.gradeView);
        textView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    if(isNumeric(textView.getText().toString())){
                        System.out.println(textView.getText());
                        //TODO INSERT TO DB
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

    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
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
