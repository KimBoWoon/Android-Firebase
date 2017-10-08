package com.android.practice.firebase_storage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

/**
 * Created by Null on 2017-10-08.
 */

public class MainPresenter implements MainContract.UserAction {
    private MainContract.View view;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String firebaseStorageUrl = "yourFirebaseStorageUrl";
    private String firebaseChildName = "yourFirebaseChildName";
    private String imageName = "yourImageName";

    public MainPresenter(MainContract.View view) {
        this.view = view;

        initStorage();
        getDownloadUrl();
    }

    // 저장소 초기화
    public void initStorage() {
        // 스토리지 생성
        firebaseStorage = FirebaseStorage.getInstance();
        // 스토리지 데이터를 가져올 root 까지 참조를 생성
        storageReference = firebaseStorage.getReferenceFromUrl(firebaseStorageUrl).child(firebaseChildName);

        Log.i("MainPresenter", storageReference.getBucket());
        Log.i("MainPresenter", storageReference.getName());
        Log.i("MainPresenter", storageReference.getPath());

        // URL 획득
        storageReference.getDownloadUrl().addOnSuccessListener((MainActivity) view, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("MainPresenter", uri.toString());
            }
        });
    }

    // 파일 업로드
    public void imageUpload() {
        // sd카드에 이미지가 존재하는 전재 (uploaddata.jpg)
//        Uri uriFile = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + imageName));
        // 로컬 저장소에 있는 파일 업로드 (uploaddata.jpg)
        Uri uriFile = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + imageName));
        // 메타 정보 구성
        StorageMetadata meta = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();
        // 업로드
        UploadTask uploadTask = storageReference
                .child(uriFile.getLastPathSegment()).putFile(uriFile, meta);
        // 업로드 상황 이벤트 등록
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress
                        = (100.0 * taskSnapshot.getBytesTransferred())
                        / taskSnapshot.getTotalByteCount();
                Log.i("MainPresenter", "진행률 : " + progress);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downUri = taskSnapshot.getMetadata().getDownloadUrl();
                Log.i("MainPresenter", "실제경로 : " + downUri);
            }
        });
    }

    public void imageDownload() {
        try {
            final File localFile = File.createTempFile("images", "jpg", new File(Environment.getExternalStorageDirectory().getAbsolutePath()));

            storageReference.child(firebaseChildName).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    ImageView image = (ImageView) ((MainActivity) view).findViewById(R.id.main_img);
                    image.setImageBitmap(bitmap);
                    Log.i("Download", "Success");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.i("Download", "Failed");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getDownloadUrl() {
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("FileUrl", uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("FileUrl", "Failed");
            }
        });
    }
}
