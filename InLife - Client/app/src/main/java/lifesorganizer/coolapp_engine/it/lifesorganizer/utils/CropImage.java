package lifesorganizer.coolapp_engine.it.lifesorganizer.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.PermissionRequest;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lifesorganizer.coolapp_engine.it.lifesorganizer.MyProfile;
import lifesorganizer.coolapp_engine.it.lifesorganizer.R;

public class CropImage extends AppCompatActivity {

    private CropImageView mCropView;
    private Uri sourceUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        mCropView = findViewById(R.id.cropImageView);
        mCropView.setGuideShowMode(CropImageView.ShowMode.SHOW_ON_TOUCH);
        mCropView.setCustomRatio(10, 7);
        mCropView.setCompressFormat(Bitmap.CompressFormat.JPEG);
        mCropView.setOutputWidth(1000);
        mCropView.setCompressQuality(50);

        getImageFromMemory();
    }

    public void cropGetImage(View view){
        getImageFromMemory();
    }

    public void cropRotateLeft(View view){
        mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
    }

    public void cropRotateRight(View view){
        mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
    }

    public void cropDone(View view){
        mCropView.crop(sourceUri).execute(mCropCallback);
    }

    private void getImageFromMemory(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 101:
                    Uri selectedImage = data.getData();
                    sourceUri = selectedImage;
                    mCropView.load(selectedImage).execute(mLoadCallback);
            }
        } else if(sourceUri == null) finish();
    }

    // Callbacks ///////////////////////////////////////////////////////////////////////////////////

    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override public void onSuccess() {
        }

        @Override public void onError(Throwable e) {
        }
    };

    private final CropCallback mCropCallback = new CropCallback() {
        @Override public void onSuccess(Bitmap cropped) {
            String pathTemp = Utils.bitmapToTempFile(CropImage.this, cropped, "tempImmagineProfilo");
            Intent data = new Intent();
            data.putExtra("croppedImagePath", pathTemp);
            setResult(Activity.RESULT_OK, data);
            finish();
        }

        @Override public void onError(Throwable e) {
        }
    };

    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override public void onSuccess(Uri outputUri) {
        }

        @Override public void onError(Throwable e) {
        }
    };
}
