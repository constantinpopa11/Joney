package it.unitn.disi.joney;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        JobCategory jobCategory = (JobCategory) parent.getItemAtPosition(pos);
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + jobCategory.getName() + " " + jobCategory.getId(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

}
