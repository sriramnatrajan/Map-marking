package com.example.taskapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.taskapp.LocationSearchActivity;
import com.example.taskapp.R;

import java.util.List;

/**
 * Created by hlink on 3/12/16.
 */

public class GooglePlacesAutocompleteAdapterNew extends BaseAdapter {


    private final Context applicationContext;
    private final List<LocationSearchActivity.placeModel> placeModels;

    public GooglePlacesAutocompleteAdapterNew(Context applicationContext, List<LocationSearchActivity.placeModel> placeModels) {
        this.applicationContext = applicationContext;
        this.placeModels = placeModels;
    }

    @Override
    public int getCount() {
        return placeModels.size();
    }

    @Override
    public LocationSearchActivity.placeModel getItem(int i) {
        return placeModels.get(i);
    }

    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        PlaceViewHolder viewholder;


        try {
            if (view==null){
                view = LayoutInflater.from(applicationContext).inflate(R.layout.adapter_google_places_autocomplete, parent, false);
                viewholder=new PlaceViewHolder(view);
                view.setTag(viewholder);
            }
            viewholder= (PlaceViewHolder) view.getTag();
        /*viewholder.txtArea.setText(placeModels.get(position).getSecondaryTitle());
        viewholder.txtTitle.setText(placeModels.get(position).getMainTitle());*/

            try {
                if (placeModels.size() > position) {
                    viewholder.txtArea.setText(placeModels.get(position).getSecondaryTitle());
                    viewholder.txtTitle.setText(placeModels.get(position).getMainTitle());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        viewholder.txtArea.setText(placeModels.get(position).getSecondaryTitle());
        return view;
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    private class PlaceViewHolder {
        TextView txtTitle;
        TextView txtArea;
        public PlaceViewHolder(View view) {
            txtArea = (TextView) view.findViewById(R.id.txtarea);
            txtTitle = (TextView) view.findViewById(R.id.txtAddress);
        }
    }
}
