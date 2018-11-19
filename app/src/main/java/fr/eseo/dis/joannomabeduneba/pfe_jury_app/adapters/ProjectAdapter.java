package fr.eseo.dis.joannomabeduneba.pfe_jury_app.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.eseo.dis.joannomabeduneba.pfe_jury_app.R;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.Project;


public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>{

    private final Context context;

    private List<Project> projects;

    public ProjectAdapter(Context context) {
        this.context = context;
        projects = new ArrayList<>();
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View projectView = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_card_layout, parent, false);
        return new ProjectViewHolder(projectView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder projectViewHolder, int i) {
        Project project = projects.get(i);
        projectViewHolder.projectName.setText(project.getProject());
        projectViewHolder.projectDescrip.setText(project.getDescription());
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    class ProjectViewHolder extends RecyclerView.ViewHolder {

        private final TextView projectName;
        private final TextView projectDescrip;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public ProjectViewHolder(View view) {
            super(view);
            projectName = view.findViewById(R.id.project_name);
            projectDescrip = view.findViewById(R.id.project_descrip);
        }
    }
}
