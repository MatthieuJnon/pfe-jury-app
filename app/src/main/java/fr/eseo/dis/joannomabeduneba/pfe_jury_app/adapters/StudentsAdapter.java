package fr.eseo.dis.joannomabeduneba.pfe_jury_app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import fr.eseo.dis.joannomabeduneba.pfe_jury_app.MainActivity;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.ProjectActivity;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.R;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.User;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.MyViewHolder> {

    ArrayList<User> mUsersList;
    WeakReference<Context> mContextWeakReference;

    public StudentsAdapter(ArrayList<User> usersList, Context context) {
        mUsersList = usersList;
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

        holder.TvName.setText(currentUser.getForename() + currentUser.getLastname());

    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    //holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView TvName;
        public LinearLayout ll;

        public MyViewHolder(View itemView, final Context context) {

            super(itemView);
            TvName = (TextView) itemView.findViewById(R.id.tv_name);
            ll = (LinearLayout) itemView.findViewById(R.id.ll_layout);

            ll.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    ((ProjectActivity) context).userItemClick(getAdapterPosition());
                }
            });
        }
    }
}
