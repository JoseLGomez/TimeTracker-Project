package iuAndroid;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import timetracker.iuandroid.R;

/**
 * Created by Erikk on 13/01/2016.
 */
public class ActivityAdapter extends ArrayAdapter<DadesActivitat> {
    private final Context context;
    private List<DadesActivitat> values;
    private final int layoutid;
    private final CallBacks callbacks;
    private final String tag = this.getClass().getSimpleName();
    public ActivityAdapter(Context context,int layoutid, List<DadesActivitat> values,CallBacks callbacks){
        super(context,layoutid,values);
        this.context = context;
        this.values = values;
        this.layoutid=layoutid;
        this.callbacks = callbacks;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(this.layoutid, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,this.context.getResources().getDimension(R.dimen.textsize));

        ImageView imageTipus = (ImageView) rowView.findViewById(R.id.tipus);
        final ImageView imagePlayPayse = (ImageView) rowView.findViewById(R.id.imgPlayPause);
        textView.setText(this.values.get(position).toString());
        if(this.values.get(position).isProjecte()){
            imageTipus.setImageResource(R.mipmap.folder);
            imagePlayPayse.setVisibility(View.INVISIBLE);
        }
        else {
            imageTipus.setImageResource(R.mipmap.task);
            imagePlayPayse.setVisibility(View.VISIBLE);
            if (this.values.get(position).isTasca()) {
                if (!values.get(position).isCronometreEngegat()) {
                    imagePlayPayse.setImageResource(R.mipmap.play);
                } else if (values.get(position).isCronometreEngegat()) {
                    imagePlayPayse.setImageResource(R.mipmap.pause);
                }
            }
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onClickTextView(position);
            }
        });
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (callbacks.onItemLongClickPropio(position)) {
                    //cb.setChecked(true);
                    LinearLayout lay = (LinearLayout) rowView.findViewById(R.id.layoutActivity);
                    lay.setBackgroundColor(Color.CYAN);
                    return true;
                }
                return false;
            }
        });
        imagePlayPayse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callbacks.onClickPlayPause(position)) {
                    if (!values.get(position).isCronometreEngegat()) {
                        imagePlayPayse.setImageResource(R.mipmap.pause);
                    } else if (values.get(position).isCronometreEngegat()) {
                        imagePlayPayse.setImageResource(R.mipmap.play);
                    }
                }
            }
        });
        imageTipus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (callbacks.onItemLongClickPropio(position)) {
                    //cb.setChecked(true);
                    LinearLayout lay = (LinearLayout) rowView.findViewById(R.id.layoutActivity);
                    lay.setBackgroundColor(Color.CYAN);
                    return true;
                }
                return false;
            }
        });

        return rowView;
    }
}
