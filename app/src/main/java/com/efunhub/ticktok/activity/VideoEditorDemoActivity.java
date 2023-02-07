package com.efunhub.ticktok.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.efunhub.ticktok.R;

import java.io.File;
import java.io.IOException;


import ly.img.android.pesdk.assets.filter.basic.FilterPackBasic;
import ly.img.android.pesdk.assets.font.basic.FontPackBasic;
import ly.img.android.pesdk.assets.frame.basic.FramePackBasic;
import ly.img.android.pesdk.assets.overlay.basic.OverlayPackBasic;
import ly.img.android.pesdk.assets.sticker.emoticons.StickerPackEmoticons;
import ly.img.android.pesdk.assets.sticker.shapes.StickerPackShapes;
import ly.img.android.pesdk.backend.model.EditorSDKResult;
import ly.img.android.pesdk.backend.model.state.LoadSettings;
import ly.img.android.pesdk.backend.model.state.SaveSettings;
import ly.img.android.pesdk.backend.model.state.VideoEditorSaveSettings;
import ly.img.android.pesdk.backend.model.state.manager.SettingsList;

import ly.img.android.pesdk.ui.activity.VideoEditorBuilder;
import ly.img.android.pesdk.ui.model.state.UiConfigFilter;
import ly.img.android.pesdk.ui.model.state.UiConfigFrame;
import ly.img.android.pesdk.ui.model.state.UiConfigOverlay;
import ly.img.android.pesdk.ui.model.state.UiConfigSticker;
import ly.img.android.pesdk.ui.model.state.UiConfigText;
import ly.img.android.pesdk.ui.utils.PermissionRequest;
import ly.img.android.serializer._3.IMGLYFileWriter;


public class VideoEditorDemoActivity extends Activity implements PermissionRequest.Response {

    // Important permission request for Android 6.0 and above, don't forget to add this!
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void permissionGranted() {}

    @Override
    public void permissionDenied() {
        /* TODO: The Permission was rejected by the user. The Editor was not opened,
         * Show a hint to the user and try again. */
    }

    public static int VESDK_RESULT = 1;
    public static int GALLERY_RESULT = 2;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private VideoEditorSaveSettings createVesdkSettingsList() {
        // Create a empty new SettingsList and apply the changes on this referance.
        VideoEditorSaveSettings settingsList = new VideoEditorSaveSettings();


        // If you include our asset Packs and you use our UI you also need to add them to the UI,
        // otherwise they are only available for the backend
        // See the specific feature sections of our guides if you want to know how to add our own Assets.

        settingsList.getSettingsModel(UiConfigFilter.class).setFilterList(
                FilterPackBasic.getFilterPack()
        );

        settingsList.getSettingsModel(UiConfigText.class).setFontList(
                FontPackBasic.getFontPack()
        );

        settingsList.getSettingsModel(UiConfigFrame.class).setFrameList(
                FramePackBasic.getFramePack()
        );

        settingsList.getSettingsModel(UiConfigOverlay.class).setOverlayList(
                OverlayPackBasic.getOverlayPack()
        );

        settingsList.getSettingsModel(UiConfigSticker.class).setStickerLists(
                StickerPackEmoticons.getStickerCategory(),
                StickerPackShapes.getStickerCategory()
        );

        return settingsList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openSystemGalleryToSelectAnVideo();
    }

    private void openSystemGalleryToSelectAnVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,"video/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, GALLERY_RESULT);
        } else {
            Toast.makeText(
                    this,
                    "No Gallery APP installed",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void openEditor(Uri inputSource) {

        VideoEditorSaveSettings settingsList;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            settingsList = createVesdkSettingsList();
        } else {
            Toast.makeText(this, "Video support needs Android 4.3", Toast.LENGTH_LONG).show();
            return;
        }

        // Set input image
        settingsList.getSettingsModel(LoadSettings.class).setSource(inputSource);

        // Set output video
        settingsList.getSettingsModel(SaveSettings.class).setOutputToGallery(Environment.DIRECTORY_DCIM);

       /* new VideoEditorBuilder(this)
                .setSettingsList(settingsList)
                .startActivityForResult(this, VESDK_RESULT);*/



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK && requestCode == GALLERY_RESULT) {
            // Open Editor with some uri in this case with an image selected from the system gallery.
            Uri selectedImage = intent.getData();
            openEditor(selectedImage);

        } else if (resultCode == RESULT_OK && requestCode == VESDK_RESULT) {
            // Editor has saved an Image.
            EditorSDKResult data = new EditorSDKResult(intent);

            Uri resultURI = data.getResultUri();
            Uri sourceURI = data.getSourceUri();

            // This adds the result and source image to Android's gallery
            data.notifyGallery(EditorSDKResult.UPDATE_RESULT & EditorSDKResult.UPDATE_SOURCE);

            Log.i("PESDK", "Source image is located here " + sourceURI);
            Log.i("PESDK", "Result image is located here " + resultURI);

            // TODO: Do something with the result image

            // OPTIONAL: read the latest state to save it as a serialisation
            SettingsList lastState = data.getSettingsList();
            try {
                new IMGLYFileWriter(lastState).writeJson(new File(
                        Environment.getExternalStorageDirectory(),
                        "serialisationReadyToReadWithPESDKFileReader.json"
                ));
            } catch (Exception e) { e.printStackTrace(); }

        } else if (resultCode == RESULT_CANCELED && requestCode == VESDK_RESULT) {
            // Editor was canceled
            EditorSDKResult data = new EditorSDKResult(intent);

            Uri sourceURI = data.getSourceUri();
            // TODO: Do something with the source...
        }
    }
}