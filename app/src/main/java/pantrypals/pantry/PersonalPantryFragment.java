package pantrypals.pantry;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.databaes.pantrypals.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalPantryFragment extends Fragment {
    public static final String FIRST_COLUMN = "name";
    public static final String SECOND_COLUMN = "amount";
    public static final String THIRD_COLUMN = "unit";
    public static final String FOURTH_COLUMN = "expiration";

    private ArrayList<HashMap<String, String>> items;
    private PantryItemsAdapter adapter;


    public PersonalPantryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_pantry, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.fab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addButtonClick(view);
            }
        });

        setupItemListView(view);

        super.onViewCreated(view, savedInstanceState);
    }

    private void setupItemListView(View view){
        ListView itemListView = (ListView) view.findViewById(R.id.itemListView);
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editModal(view);
            }
        });
        items = new ArrayList<HashMap<String, String>>();
        adapter = new PantryItemsAdapter(getActivity(), items);
        itemListView.setAdapter(adapter);


    }

    private void addButtonClick(View v){
        //builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.modal_add_item));

        //https://stackoverflow.com/questions/18371883/how-to-create-modal-dialog-box-in-android
        LayoutInflater inflater = (LayoutInflater)getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.modal_add_item, null);

        setMeasurementSpinner(dialogView);
        builder.setView(dialogView);

        builder.show();
    }

    private void setMeasurementSpinner(View v){
        Spinner units = v.findViewById(R.id.measurement_unit);
        List<String> unitsList = Arrays.asList(getResources().getStringArray(R.array.units));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, unitsList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        units.setAdapter(dataAdapter);
    }

    private void editModal(View v){
        //builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.modal_edit_item));

        //https://stackoverflow.com/questions/18371883/how-to-create-modal-dialog-box-in-android
        LayoutInflater inflater = (LayoutInflater)getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.modal_edit_item, null);

        builder.setView(dialogView);

        builder.show();
    }



}
