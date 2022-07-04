package com.homework.book_sns.javaclass;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.homework.book_sns.BuildConfig;
import com.homework.book_sns.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class MyImageFunc {
    static final String TAG = "MyImageFunc";
    static File file;
    static final int MY_PERMISSIONS_REQUEST_CAMERA=1001;

    public static Bitmap imageBitmap;

    static public Intent upload_single_photo() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);


        return  intent;
    }

    static public Uri result_single_photo(Intent data) {
        Uri imageUri = data.getData();
        return imageUri;
    }


    static public Intent upload_multi_photo() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        return  intent;
    }

    static public ArrayList<Uri> result_multi_photo(Intent data, ArrayList<Uri> uriArrayList, Context context) {
        if(data == null) { // 선택 안했을 때
            return null;
        } else {
            
            if(data.getClipData() == null) { //한 장
                Uri imageUri = data.getData();

                if(dpCheck_arList(uriArrayList, imageUri)) { // 중복 체크
                    Toast.makeText(context, "중복된 사진이 존재합니다.", Toast.LENGTH_SHORT).show();
                    return null;
                }

                uriArrayList.add(imageUri); // uri 추가
                
            } else { //여러장

                ClipData clipData = data.getClipData();

                int upload_count;
                upload_count = clipData.getItemCount();

                for (int i=0; i<upload_count; i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    try {
                        if(dpCheck_arList(uriArrayList, imageUri)) { // 중복 체크
                            Toast.makeText(context, "중복된 사진이 존재합니다.", Toast.LENGTH_SHORT).show();
                            continue;
                        }
                        uriArrayList.add(imageUri); // uri 추가
                    } catch (Exception e) {
                        Log.d(TAG, "onActivityResult: 파일 에러"+ e);
                        return null;
                    }
                }
            }
            return uriArrayList; // 배열 출력
        }

    }

    static public Intent take_picture(Context context, Activity activity) {

        int permssionCheck = ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA);

        if (permssionCheck!= PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(context,"권한 승인이 필요합니다",Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(context,"사용을 위해 카메라 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
                Toast.makeText(context,"사용을 위해 카메라 권한이 필요합니다.",Toast.LENGTH_LONG).show();

            }
        }

        file = null;
        Uri uri;

        try {
            file = createFile(context);
            if(file.exists()) {
                file.delete();
            }

            file.createNewFile();
        } catch (Exception e) {
            Log.d(TAG, "take_picture: "+e);
            return null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file);
        } else {
            uri = Uri.fromFile(file);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return  intent;
    }

    static private File createFile(Context context) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date());
        String filename = timeStamp+".jpg";
        File outFile = new File(context.getExternalFilesDir(null), filename);

        return outFile;
    }

    static public Uri take_picture_result() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        Uri uri = Uri.fromFile(file);
        return uri;
    }

    static public void set_UriImage(ImageView imageView, Uri uri, Context context) {
        Glide.with(context)
                .load(uri)
                .into(imageView);

//        Glide.with(getApplicationContext())
//                .load(image_url)
//                .error(R.drawable.ic_baseline_error_24)
//                .override(80,80)
//                .into(civ_profile);
    }




    static public String getBase64Image_FromBitmap(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
        return encodedImage;
    }

    static public Bitmap getBitmapImage_FromUri(Uri uri, Context context) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            return bitmap;
        } catch (IOException e) {
            Log.d(TAG, "getBitmapImage_FromUri: "+e);
            return null;
        }
    }

    static public Bitmap getBitmap_FromBase64Image(String base64Image) {
        byte[] encodeByte = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        return bitmap;
    }

    static public byte[] getByteArray_FromBimapImage(Bitmap imageBitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        float scale = (float) (1024/(float)imageBitmap.getWidth());
        int image_w = (int) (imageBitmap.getWidth() * scale);
        int image_h = (int) (imageBitmap.getHeight() * scale);
        Bitmap resize = Bitmap.createScaledBitmap(imageBitmap, image_w, image_h, true);
        resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return  byteArray;
    }

    static public Bitmap getBitmap_FromByteArrayImage(byte[] byteArrayImage) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArrayImage, 0, byteArrayImage.length);
        return  bitmap;
    }

    //url로부터 콜백으로 받아와야되기때문에 액티비티에 따로 작성하자.
//    static public Bitmap getBitmap_FromURL (String imageUrl,  Context mContext) {
//        Glide.with(mContext).asBitmap().load(imageUrl)
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        imageBitmap = resource;
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                    }
//                });
//        return imageBitmap;
//    }

    static public ArrayList<String> getBase64ImageArray_FromBitmapArray (ArrayList<Bitmap> bitmapArrayList, Context context) {
        ArrayList<String> stringArrayList = new ArrayList<>();

        for(int i = 0; i < bitmapArrayList.size(); i++) {
            Bitmap bitmap = bitmapArrayList.get(i); // 1. bitmap
            String base64Image = getBase64Image_FromBitmap(bitmap); // 2. bitmap을 base64로 바꿔준다.
            stringArrayList.add(base64Image); // 3. 그걸 배열에 넣어준다.
        }

        return stringArrayList;

    }

    static public ArrayList<String> getBase64ImageArray_FromUriArray (ArrayList<Uri> uriArrayList, Context context) {
        ArrayList<String> stringArrayList = new ArrayList<>();

        for(int i = 0; i < uriArrayList.size(); i++) {
            Bitmap bitmap = getBitmapImage_FromUri(uriArrayList.get(i), context); // 1. uri 데이터를 bitmap으로 바꾸고
            String base64Image = getBase64Image_FromBitmap(bitmap); // 2. bitmap을 base64로 바꿔준다.
            stringArrayList.add(base64Image); // 3. 그걸 배열에 넣어준다.
        }

        return stringArrayList;
    }

    static public String getJsonString_FromStringArrayList(ArrayList<String> stringArrayList) {
        JSONObject jsonObject = new JSONObject();

        try {
            for (int i =0; i < stringArrayList.size(); i++) {
                String index = Integer.toString(i);
                jsonObject.put(index, stringArrayList.get(i));
            }
        } catch (Exception e) {

        }

        return jsonObject.toString();
    }

    static public boolean dpCheck_arList(ArrayList<Uri> uriArrayList , Uri uri) {
        uriArrayList.add(uri);
        Set<Uri> numSet = new HashSet<>(uriArrayList);

        if(numSet.size() != uriArrayList.size()) {
            uriArrayList.remove(uriArrayList.size()-1);
            return true;
        }

        return false;
    }

    static public boolean dpCheck_bitmapList(ArrayList<Bitmap> bitmapArrayList , Bitmap bitmap) {
        bitmapArrayList.add(bitmap);
        Set<Bitmap> numSet = new HashSet<>(bitmapArrayList);

        if(numSet.size() != bitmapArrayList.size()) {
            bitmapArrayList.remove(bitmapArrayList.size()-1);
            return true;
        }

        return false;
    }
}
