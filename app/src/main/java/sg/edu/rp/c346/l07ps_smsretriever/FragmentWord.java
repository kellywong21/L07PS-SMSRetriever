package sg.edu.rp.c346.l07ps_smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentWord extends Fragment {

    TextView tvWordSMS;
    EditText etWord;
    Button btnRetrieveWord;

    public FragmentWord() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_word,container,false);
        tvWordSMS = view.findViewById(R.id.tvWordSMS);
        etWord = view.findViewById(R.id.etWord);
        btnRetrieveWord = view.findViewById(R.id.btnRetrieveWord);

        btnRetrieveWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word = etWord.getText().toString();

                int permissionCheck = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);
                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_SMS},0);
                    return;
                }
                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date","address","body","type"};
                ContentResolver cr = getActivity().getContentResolver();
                String filter = "body LIKE ?";
                String[] filterArgs = {"%" + word + "%"};
                Cursor cursor = cr.query(uri,reqCols,filter,filterArgs,null);
                String smsBody = "";
                if (cursor.moveToFirst()){
                    do{
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MM yyyy aa",dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("2")){
                            type = "Sent:";
                            smsBody += type + " " + address + "\n at " + date + "\n\"" + body + "\"\n\n";
                        }
                    }while (cursor.moveToNext());
                }
                tvWordSMS.setText(smsBody);
            }
        });

        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    btnRetrieveWord.performClick();
                }else{
                    Toast.makeText(getActivity(),"Permission not granted",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
