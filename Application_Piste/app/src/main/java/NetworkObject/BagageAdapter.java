package NetworkObject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.floryan.application_piste.R;

import java.util.List;

public class BagageAdapter extends ArrayAdapter<Bagage> {
    public BagageAdapter(@NonNull Context context, @NonNull List<Bagage> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Bagage b = getItem(position);
        if (b != null) {
            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.bagage_list_layout, parent, false);

            TextView tv = convertView.findViewById(R.id.bagageTV);
            tv.setText(b.getNumeroBagage());

            tv = convertView.findViewById(R.id.poidsTV);
            tv.setText(String.format("%s", b.getPoids()));

            CheckBox cb = convertView.findViewById(R.id.charge);
            cb.setChecked(b.isChager());
            cb.setOnClickListener(view -> {
                b.setCharger(((CheckBox) view).isChecked() ? 'C' : 'N');
            });

            RadioButton rb = convertView.findViewById(R.id.valise);
            rb.setChecked(b.isValise());
        }
        return convertView;
    }
}
