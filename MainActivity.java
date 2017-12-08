package com.example.anjupatil.first;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonChoose;
    private Button buttonUpload;
    private Button buttonResult;
    private ImageView imageView;
   private TextView demoValue;
    private EditText editTextName;

    private Bitmap bitmap;

    private int PICK_IMAGE_REQUEST = 1;
    DatabaseReference rootRef,demoRef;
    ProgressDialog pd;
    private Uri file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        buttonResult = (Button) findViewById(R.id.buttonResult);
        editTextName = (EditText) findViewById(R.id.editText);
        demoValue = (TextView) findViewById(R.id.result);
        imageView  = (ImageView) findViewById(R.id.imageView);

        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);

        buttonResult.setOnClickListener(this);




    }

    @Override
    public void onClick(View view) {
        if(view == buttonChoose){
            showFileChooser();
        }else if(view == buttonUpload){
            pd=new ProgressDialog(this);
            pd.setCancelable(false);
            pd.show();
            uploadImage();
        }
        else if(view==buttonResult)
        {
            showResult();
        }

    }

    private void uploadImage() {
        if(file!=null)
        {
            FirebaseStorage storage=FirebaseStorage.getInstance();
            StorageReference reference=storage.getReferenceFromUrl("gs://first-f8e42.appspot.com");
            StorageReference imagesRef=reference.child("images/"+editTextName.getText().toString());
            UploadTask uploadTask = imagesRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(MainActivity.this, "Error : "+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                    Toast.makeText(MainActivity.this, "Uploading Done!!!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    private void showResult() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference demoref = rootRef.child("face");
        //DatabaseReference ref=demoref.child("cat");
        demoref.child("faces").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int value = dataSnapshot.getValue(Integer.class);
                demoValue.setText("no of faces in video:"+String.valueOf(value));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }






    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            file = data.getData();

            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), file);
                //Setting the Bitmap to ImageView
                //imageView.setImageBitmap(bitmap);
                imageView.setImageURI(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}