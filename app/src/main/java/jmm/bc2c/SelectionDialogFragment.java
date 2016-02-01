package jmm.bc2c;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class SelectionDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String SelectionOptions[] = {
                getString(R.string.selected_nothing),
                getString(R.string.selected_name),
                getString(R.string.selected_phone)
        };

        builder.setTitle(getString(R.string.selected_data_question))
                .setItems(SelectionOptions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichItem) {
                        PhotoActivity callingActivity = (PhotoActivity) getActivity();
                        callingActivity.saveSelection(whichItem);
                    }
                });

        return builder.create();
    }

}
