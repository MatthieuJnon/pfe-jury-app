package fr.eseo.dis.joannomabeduneba.pfe_jury_app.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import fr.eseo.dis.joannomabeduneba.pfe_jury_app.Application;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.MainActivity;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.ProjectActivity;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.R;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.PFEDatabase;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.User;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.UserProjectJoin;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.MyViewHolder> {

    ArrayList<User> mUsersList;
    int projectId;
    WeakReference<Context> mContextWeakReference;

    public StudentsAdapter(ArrayList<User> usersList, int projectId, Context context) {
        mUsersList = usersList;
        this.projectId = projectId;
        this.mContextWeakReference = new WeakReference<Context>(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = mContextWeakReference.get();

        if (context != null) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_item, parent, false);

            return new MyViewHolder(itemView, context);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        Context context = mContextWeakReference.get();

        if (context == null) {
            return;
        }

        User currentUser = mUsersList.get(position);

        holder.TvName.setText(currentUser.getForename() + " " + currentUser.getLastname());

        LoadGradeTask loadGradeTask = new LoadGradeTask(projectId, currentUser.getUid(), holder);
        loadGradeTask.execute();
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    //holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView TvName;
        public TextView note;
        public LinearLayout ll;

        public MyViewHolder(View itemView, final Context context) {

            super(itemView);
            TvName = (TextView) itemView.findViewById(R.id.tv_name);
            note = (TextView) itemView.findViewById(R.id.tv_note);
            ll = (LinearLayout) itemView.findViewById(R.id.ll_layout);

            ll.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    ((ProjectActivity) context).userItemClick(getAdapterPosition());
                }
            });
        }
    }

    public class LoadGradeTask extends AsyncTask<Void, Void, Boolean> {

        private int projectId;
        private int userId;
        private UserProjectJoin project;
        private MyViewHolder holder;

        public LoadGradeTask(int projectId, int userId, MyViewHolder holder) {
            this.projectId = projectId;
            this.userId = userId;
            this.holder = holder;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            project = PFEDatabase.getInstance(Application.getAppContext()).getUserProjectJoinDao().getUserProjectJoin(this.userId, this.projectId);

            if(project == null){
                project = new UserProjectJoin(userId, projectId, null, true, false, null);
                PFEDatabase.getInstance(Application.getAppContext()).getUserProjectJoinDao().insert(project);
            }

            if(project.note == null){
                project.note = "N/A";
            }

            PFEDatabase.getInstance(Application.getAppContext()).getUserProjectJoinDao().update(project);
            return true;
        }


        @Override
        protected void onPostExecute(Boolean success) {
            if(success){
                holder.note.setText(project.note);
            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
