package com.efunhub.ticktok.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;

import com.arthenica.mobileffmpeg.LogCallback;
import com.arthenica.mobileffmpeg.LogMessage;
import com.arthenica.mobileffmpeg.Statistics;
import com.arthenica.mobileffmpeg.StatisticsCallback;
import com.coremedia.iso.boxes.Container;
import com.daasuu.gpuv.composer.FillMode;
import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.daasuu.gpuv.composer.Rotation;
import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.filter.GlFilterGroup;
import com.daasuu.gpuv.egl.filter.GlMonochromeFilter;
import com.daasuu.gpuv.egl.filter.GlVignetteFilter;
import com.efunhub.ticktok.R;

import com.efunhub.ticktok.effect.FilterType;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;



public class MergeVideoAudioActivity extends AppCompatActivity {

    private String TAG="MergeVideoAudioActivity";
    private final String KEY_VIDEO_URI = "video_uri";
    String videoUri;
    String musicPath;
    private ProgressDialog progressDialog;

    String filePathMergeVideoAudio="";
    Boolean result;

    private String muteVideofilepath;

    //mute video
    private com.daasuu.gpuv.composer.GPUMp4Composer GPUMp4Composer;

    private GlFilter filter;
    private GlFilter glFilter = new GlFilterGroup(new GlMonochromeFilter(), new GlVignetteFilter());
    private String trimeAudioPath;
    private Uri contentUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_video_audio);



        if (getIntent().hasExtra(KEY_VIDEO_URI)) {
             videoUri = getIntent().getStringExtra(KEY_VIDEO_URI);
             musicPath = getIntent().getStringExtra("music");
        }

        startCodec(videoUri);

    }

    public boolean mux(String videoFile, String audioFile) {
        com.googlecode.mp4parser.authoring.Movie video;
        try {
            video = new MovieCreator().build(videoFile);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        Movie audio;
        try {
            audio = new MovieCreator().build(audioFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }

        Track audioTrack = audio.getTracks().get(0);
        video.addTrack(audioTrack);

        Container out = new DefaultMp4Builder().build(video);


      /*  File myDirectory = new File(Environment.getExternalStorageDirectory(), "Merge");
        if (!myDirectory.exists()) {
            myDirectory.mkdirs();
        }
        filePathMergeVideoAudio = myDirectory + "/video" + System.currentTimeMillis() + ".mp4";*/
        filePathMergeVideoAudio=getVideoFilePath();
        try {
            RandomAccessFile ram = new RandomAccessFile(String.format(filePathMergeVideoAudio), "rw");
            FileChannel fc = ram.getChannel();
            out.writeContainer(fc);
            ram.close();
        } catch (IOException e) {
            e.printStackTrace();
            // return null;
        }
        Log.d("filePathMergeVideoAudio",filePathMergeVideoAudio);
        // return filePathMergeVideoAudio;
        return true;
    }




    public  String getVideoFilePath() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("videoDir", Context.MODE_PRIVATE);
        File file = new File(directory,System.currentTimeMillis() + ".mp4");
        Uri imgUri = Uri.fromFile(file);
        String fileAbsolutePath = file.getAbsolutePath();
        return fileAbsolutePath;
    }


    private void startCodec(String sourceVideoPath) {

        final List<FilterType> filterTypes = FilterType.createFilterList();


        glFilter = null;
        glFilter = FilterType.createGlFilter(filterTypes.get(37), this);


        muteVideofilepath= getVideoFilePath();
        // final ProgressBar progressBar = findViewById(R.id.progress_bar);
        // progressBar.setMax(100);


        // findViewById(R.id.start_play_movie).setEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        progressDialog.show();

        GPUMp4Composer = null;
        GPUMp4Composer = new GPUMp4Composer(sourceVideoPath, muteVideofilepath)
                .rotation(Rotation.NORMAL)
                .size(720, 720)
                .fillMode(FillMode.PRESERVE_ASPECT_CROP)
                .mute(true)
                .filter(glFilter)
                // .videoBitrate((int) (0.25 * 30 * 720 * 720))
                // .mute(muteCheckBox.isChecked())
                // .flipHorizontal(flipHorizontalCheckBox.isChecked())
                // .flipVertical(flipVerticalCheckBox.isChecked())
                .listener(new GPUMp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {
                        Log.d(TAG, "onProgress = " + progress);
                        //  runOnUiThread(() -> progressBar.setProgress((int) (progress * 100)));
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted()");
                        // exportMp4ToGallery(getApplicationContext(), muteVideofilepath);
                        runOnUiThread(() -> {

                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            //working
                           /* progressDialog = new ProgressDialog(MergeVideoAudioActivity.this);
                            progressDialog.setTitle(null);
                            progressDialog.setCancelable(false);

                            progressDialog.show();

                            result= mux(muteVideofilepath,musicPath);
                            Log.d("result",""+result);
                            if(result)
                            {
                                progressDialog.dismiss();
                                startActivity(new Intent(MergeVideoAudioActivity.this,EditVideoActivity.class).putExtra("video_uri",filePathMergeVideoAudio));

                            }*/
                            executeSpeedVideoCommand();

                        });
                    }

                    @Override
                    public void onCanceled() {

                    }

                    @Override
                    public void onFailed(Exception exception) {
                        Log.d(TAG, "onFailed()");
                    }
                })
                .start();

    }

    private void executeSpeedVideoCommand() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);
        progressDialog.setCancelable(false);

        Uri uri=Uri.parse(muteVideofilepath);
        File file = new File(String.valueOf(uri));
        // String yourRealPath = getPath(MergeVideoAudioActivity.this, uri);


        Uri urimusicPath=Uri.parse(musicPath);
         File fileurimusicPath = new File(String.valueOf(urimusicPath));

        Log.d(TAG, "startTrim: src: " + file);

        trimeAudioPath = getVideoWithSubtitlesFile().getAbsolutePath();




      //audio is larger
       // String[] complexcommand = new String[]{"-i", file.getAbsolutePath(),"-i", String.valueOf(fileurimusicPath),"-map","1:a","-map","0:v","-codec","copy","-shortest",trimeAudioPath};
        //execFFmpegBinary(complexcommand);

       String[] complexCommand=new String[]{"-i", file.getAbsolutePath(), "-stream_loop" ,"-1"," -i ",String.valueOf(fileurimusicPath) ,"-shortest", "-map", "0:v:0", "-map","1:a:0", "-y", trimeAudioPath};
        execFFmpegBinary(complexCommand);


    }
    public File getVideoWithSubtitlesFile() {
        return new File(this.getFilesDir(), "audio-trime.mp4");
    }

    private void execFFmpegBinary(final String[] command) {

        Config.enableLogCallback(new LogCallback() {
            @Override
            public void apply(LogMessage message) {
                Log.e(Config.TAG, message.getText());
            }
        });
        Config.enableStatisticsCallback(new StatisticsCallback() {
            @Override
            public void apply(Statistics newStatistics) {
                Log.e(Config.TAG, String.format("frame: %d, time: %d", newStatistics.getVideoFrameNumber(), newStatistics.getTime()));
                Log.d(TAG, "Started command : ffmpeg " + Arrays.toString(command));
               /* if (choice == 8)
                    progressDialog.setMessage("progress : splitting video " + newStatistics.toString());
                else if (choice == 9)
                    progressDialog.setMessage("progress : reversing splitted videos " + newStatistics.toString());
                else if (choice == 10)
                    progressDialog.setMessage("progress : concatenating reversed videos " + newStatistics.toString());
                else
                    progressDialog.setMessage("progress : " + newStatistics.toString());
                Log.d(TAG, "progress : " + newStatistics.toString());*/
            }
        });
        Log.d(TAG, "Started command : ffmpeg " + Arrays.toString(command));
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        long executionId = com.arthenica.mobileffmpeg.FFmpeg.executeAsync(command, (executionId1, returnCode) -> {
            if (returnCode == Config.RETURN_CODE_SUCCESS) {
                Log.d(TAG, "Finished command : ffmpeg " + Arrays.toString(command));
                if (progressDialog != null)
                    progressDialog.dismiss();
                startActivity(new Intent(MergeVideoAudioActivity.this,EditVideoActivity.class).putExtra("video_uri",trimeAudioPath));
                    /*progressDialog = new ProgressDialog(MergeVideoAudioActivity.this);
                            progressDialog.setTitle(null);
                            progressDialog.setCancelable(false);

                            progressDialog.show();

                            result= mux(muteVideofilepath,trimeAudioPath);
                            Log.d("result",""+result);
                            if(result)
                            {
                                progressDialog.dismiss();
                                startActivity(new Intent(MergeVideoAudioActivity.this,EditVideoActivity.class).putExtra("video_uri",filePathMergeVideoAudio));

                            }*/
            } else if (returnCode == Config.RETURN_CODE_CANCEL) {
                Log.e(TAG, "Async command execution cancelled by user.");
                if (progressDialog != null)
                    progressDialog.dismiss();
            } else {
                Log.e(TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });
        Log.e(TAG, "execFFmpegMergeVideo executionId-" + executionId);
    }



    @SuppressLint("NewApi")
    public  String getPath( final Uri uri) {
        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String selection = null;
        String[] selectionArgs = null;
        // DocumentProvider
        if (isKitKat ) {
            // ExternalStorageProvider

            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                String fullPath = getPathFromExtSD(split);
                if (fullPath != "") {
                    return fullPath;
                } else {
                    return null;
                }
            }


            // DownloadsProvider

            if (isDownloadsDocument(uri)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    final String id;
                    Cursor cursor = null;
                    try {
                        cursor = this.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            String fileName = cursor.getString(0);
                            String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                            if (!TextUtils.isEmpty(path)) {
                                return path;
                            }
                        }
                    }
                    finally {
                        if (cursor != null)
                            cursor.close();
                    }
                    id = DocumentsContract.getDocumentId(uri);
                    if (!TextUtils.isEmpty(id)) {
                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:", "");
                        }
                        String[] contentUriPrefixesToTry = new String[]{
                                "content://downloads/public_downloads",
                                "content://downloads/my_downloads"
                        };
                        for (String contentUriPrefix : contentUriPrefixesToTry) {
                            try {
                                final Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));


                                return getDataColumn(this, contentUri, null, null);
                            } catch (NumberFormatException e) {
                                //In Android 8 and Android P the id is not a number
                                return uri.getPath().replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "");
                            }
                        }


                    }
                }
                else {
                    final String id = DocumentsContract.getDocumentId(uri);

                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                    try {
                        contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (contentUri != null) {

                        return getDataColumn(this, contentUri, null, null);
                    }
                }
            }


            // MediaProvider
            if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;

                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};


                return getDataColumn(this, contentUri, selection,
                        selectionArgs);
            }

            if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri);
            }

            if(isWhatsAppFile(uri)){
                return getFilePathForWhatsApp(uri);
            }


            if ("content".equalsIgnoreCase(uri.getScheme())) {

                if (isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment();
                }
                if (isGoogleDriveUri(uri)) {
                    return getDriveFilePath(uri);
                }
                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {

                    // return getFilePathFromURI(context,uri);
                    return copyFileToInternalStorage(uri,"userfiles");
                    // return getRealPathFromURI(context,uri);
                }
                else
                {
                    return getDataColumn(this, uri, null, null);
                }

            }
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }
        else {

            if(isWhatsAppFile(uri)){
                return getFilePathForWhatsApp(uri);
            }

            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = {
                        MediaStore.Images.Media.DATA
                };
                Cursor cursor = null;
                try {
                    cursor = this.getContentResolver()
                            .query(uri, projection, selection, selectionArgs, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }




        return null;
    }

    private  boolean fileExists(String filePath) {
        File file = new File(filePath);

        return file.exists();
    }

    private String getPathFromExtSD(String[] pathData) {
        final String type = pathData[0];
        final String relativePath = "/" + pathData[1];
        String fullPath = "";

        // on my Sony devices (4.4.4 & 5.1.1), `type` is a dynamic string
        // something like "71F8-2C0A", some kind of unique id per storage
        // don't know any API that can get the root path of that storage based on its id.
        //
        // so no "primary" type, but let the check here for other devices
        if ("primary".equalsIgnoreCase(type)) {
            fullPath = Environment.getExternalStorageDirectory() + relativePath;
            if (fileExists(fullPath)) {
                return fullPath;
            }
        }

        // Environment.isExternalStorageRemovable() is `true` for external and internal storage
        // so we cannot relay on it.
        //
        // instead, for each possible path, check if file exists
        // we'll start with secondary storage as this could be our (physically) removable sd card
        fullPath = System.getenv("SECONDARY_STORAGE") + relativePath;
        if (fileExists(fullPath)) {
            return fullPath;
        }

        fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath;
        if (fileExists(fullPath)) {
            return fullPath;
        }

        return fullPath;
    }

    private String getDriveFilePath(Uri uri) {
        Uri returnUri = uri;
        Cursor returnCursor = this.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(this.getCacheDir(), name);
        try {
            InputStream inputStream = this.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }

    /***
     * Used for Android Q+
     * @param uri
     * @param newDirName if you want to create a directory, you can set this variable
     * @return
     */
    private String copyFileToInternalStorage(Uri uri,String newDirName) {
        Uri returnUri = uri;

        Cursor returnCursor = this.getContentResolver().query(returnUri, new String[]{
                OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE
        }, null, null, null);


        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));

        File output;
        if(!newDirName.equals("")) {
            File dir = new File(this.getFilesDir() + "/" + newDirName);
            if (!dir.exists()) {
                dir.mkdir();
            }
            output = new File(this.getFilesDir() + "/" + newDirName + "/" + name);
        }
        else{
            output = new File(this.getFilesDir() + "/" + name);
        }
        try {
            InputStream inputStream = this.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(output);
            int read = 0;
            int bufferSize = 1024;
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }

            inputStream.close();
            outputStream.close();

        }
        catch (Exception e) {

            Log.e("Exception", e.getMessage());
        }

        return output.getPath();
    }

    private String getFilePathForWhatsApp(Uri uri){
        return  copyFileToInternalStorage(uri,"whatsapp");
    }

    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }

        return null;
    }

    private  boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private  boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private  boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private  boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public boolean isWhatsAppFile(Uri uri){
        return "com.whatsapp.provider.media".equals(uri.getAuthority());
    }

    private  boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

}