package com.example.stempedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    Button btnSelect;
    FloatingActionButton upload;
    TextView fileSelected;
    Uri pdfLink;
    long id=0;
    StorageReference mStorageRef;
    DatabaseReference myRef;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSelect=findViewById(R.id.Select);
        upload=findViewById(R.id.upload);
        fileSelected=findViewById(R.id.fileSelected);
        progressBar=findViewById(R.id.progressBar);



        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE )== PackageManager.PERMISSION_GRANTED)
                {
                    selectFile();
                }
                else
                {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},6);
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pdfLink!=null)
                    uploadFile();
                else
                    Toast.makeText(MainActivity.this, "Select a file", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadFile() {
        progressBar.setProgress(0);
        Uri file = pdfLink;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        ++id;
      //  final String fileName=System.currentTimeMillis()+".pdf";
        final String fileName1=System.currentTimeMillis()+"";


        mStorageRef.child("Uploads").child(pdfLink.getLastPathSegment()).putFile(file)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                // complete the rest of your code

                                myRef = FirebaseDatabase.getInstance().getReference();
                                myRef.child(fileName1).setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "File uploaded Successfully ", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, "File Not uploaded va Successfully " + task, Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(MainActivity.this, "File Not uploaded Successfully "+exception, Toast.LENGTH_SHORT).show();
                        System.out.println((exception));
                        Log.i("my",exception.toString());


                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                int c= (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
              progressBar.setProgress(c);

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==6 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            selectFile();
        }
        else
        {
            Toast.makeText(this, "Allow permission to access files", Toast.LENGTH_SHORT).show();
        }

    }

    private void selectFile()
    {
        Intent a=new Intent();
        a.setType("application/pdf");
        a.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(a,7);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==7 &&  resultCode==RESULT_OK  && data!=null)
        {
            pdfLink=data.getData();
            fileSelected.setText(" "+data.getData().getLastPathSegment() );
        }
        else
        {
            Toast.makeText(this, "Please Select a file", Toast.LENGTH_SHORT).show();
        }
    }

}
