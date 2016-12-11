package com.acmatics.securityguardexchange.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.acmatics.securityguardexchange.R;

import java.util.List;


/**
 * Created by Rubal on 28-07-2015.
 */
public class DrawerAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> menuName;
    private final List<Integer> imageId;

    public DrawerAdapter(Activity context, List<String> menuName, List<Integer> imageId) {
        super(context, R.layout.menu_list_item, menuName);
        this.context = context;
        this.menuName = menuName;
        this.imageId = imageId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.menu_list_item, null, true);
        if (position % 2 == 1) {
            rowView.setBackgroundResource(R.color.even_list_row_color);
        } else if (position % 2 == 0) {
            rowView.setBackgroundResource(R.color.white_color);
        }
        TextView txtTitle = (TextView) rowView.findViewById(R.id.menu_title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.image_icon);
        txtTitle.setText(menuName.get(position));
        imageView.setImageResource(imageId.get(position));
        return rowView;
    }
}
