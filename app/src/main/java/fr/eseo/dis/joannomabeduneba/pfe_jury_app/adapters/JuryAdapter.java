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
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.Jury;

public class JuryAdapter extends RecyclerView.Adapter<JuryAdapter.JuryViewHolder>{

    private final Context context;

    private List<Jury> juries;

    public JuryAdapter(Context context) {
        this.context = context;
        juries = new ArrayList<>();
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public JuryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View juryView = LayoutInflater.from(parent.getContext()).inflate(R.layout.jury_card_layout, parent, false);
        return new JuryViewHolder(juryView);
    }

    @Override
    public void onBindViewHolder(@NonNull JuryViewHolder juryViewHolder, int i) {
        Jury jury = juries.get(i);
        juryViewHolder.idJury.setText(Integer.toString(jury.juryId));
        juryViewHolder.dateJury.setText(jury.date.toString());
    }

    @Override
    public int getItemCount() {
        return juries.size();
    }

    public void setJuries(List<Jury> juries) {
        this.juries = juries;
    }

    class JuryViewHolder extends RecyclerView.ViewHolder {

        private final TextView idJury;
        private final TextView dateJury;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public JuryViewHolder(View view) {
            super(view);
            idJury = view.findViewById(R.id.jury_name);
            dateJury = view.findViewById(R.id.jury_date);
        }
    }
}
