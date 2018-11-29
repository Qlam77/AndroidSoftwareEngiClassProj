package ca.sclfitness.keeppace;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.sclfitness.keeppace.Dao.RecordDao;
import ca.sclfitness.keeppace.model.Record;

public class CloudBackupFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.upload_dialog)
                .setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        RecordDao recordDao = new RecordDao(getActivity());
//                        for (Record r : records) {
//                            recordDao.delete(r.getId());
//                        }
//                        recordDao.close();
                    }
                })
                .setNegativeButton(R.string.upload, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
