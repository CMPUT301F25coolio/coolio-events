package com.example.coolioevents.organizer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Camera class handles camera operations for taking pictures and save them as event posters
 *
 * PURPOSE:
 * Launch camera to take pictures
 * Save captured images to device storage
 * Compress and optimize images
 * Return image path/URI for event details
 *
 * OUTSTANDING ISSUES:
 * This class is not fully complete, still need to testing and debugging,
 * which  will be complete in project part 4
 */
public class Camera {
    public static final int REQUEST_IMAGE_CAPTURE = 1001;
    public static final int REQUEST_IMAGE_PICK = 1002;

    private Context context;
    private String currentPhotoPath;
    private Uri photoUri;

    /**
     * This method is the constructor for Camera
     * @param context
     *      The context of the calling activity
     */
    public Camera(Context context) {
        this.context = context;
    }

    /**
     * This method is to launch the camera to take a picture
     *
     * @param activity
     *      The activity to receive the result
     * @return
     *      true if camera launched successfully, false otherwise
     */
    public boolean takePicture(Activity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(context, "Error creating image file", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(context,
                        "com.example.coolio_events.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                return true;
            }
        } else {
            Toast.makeText(context, "No camera app available", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * This method is to create an image file for storing the photo
     *
     * @return
     *      The created file
     * @throws IOException
     *      if file creation fails
     */
    private File createImageFile() throws IOException {
        // Create an image file name with timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "EVENT_POSTER_" + timeStamp + "_";

        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save the file path for later use
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * This method compress an image file to reduce size
     *
     * @param imagePath
     *      Path to the image file
     */
    private void compressImage(String imagePath) {
        try {
            // Load the image
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

            if (bitmap == null) {
                return;
            }

            // Calculate new dimensions (max 1024x1024)
            int maxSize = 1024;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            float ratio = Math.min(
                    (float) maxSize / width,
                    (float) maxSize / height
            );

            int newWidth = Math.round(width * ratio);
            int newHeight = Math.round(height * ratio);

            // Resize if necessary
            Bitmap resizedBitmap = bitmap;
            if (ratio < 1) {
                resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            }

            // Save compressed image
            FileOutputStream out = new FileOutputStream(imagePath);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();

            // Clean up
            if (resizedBitmap != bitmap) {
                bitmap.recycle();
            }
            resizedBitmap.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This method gets a bitmap from the saved photo path
     *
     * @return
     *      Bitmap of the saved photo
     */
    public Bitmap getSavedPhotoBitmap() {
        if (currentPhotoPath != null) {
            return BitmapFactory.decodeFile(currentPhotoPath);
        }
        return null;
    }


    /**
     * This method gets the current photo path
     *
     * @return currentPhotoPath
     *      Path to the current photo
     */
    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    /**
     * This method gets the photo uri
     * @return photoUri
     *      uri of the photo
     */
    public Uri getPhotoUri() {
        return photoUri;
    }

    /**
     * This method deletes the current photo file
     */
    public void deleteCurrentPhoto() {
        if (currentPhotoPath != null) {
            File file = new File(currentPhotoPath);
            if (file.exists()) {
                file.delete();
            }
            currentPhotoPath = null;
            photoUri = null;
        }
    }

    /**
     * This method load a bitmap from a file path with proper scaling
     *
     * @param imagePath
     *      Path to the image
     * @param reqWidth
     *      Required width
     * @param reqHeight
     *      Required height
     * @return
     *      Scaled bitmap
     */
    public static Bitmap loadScaledBitmap(String imagePath, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    /**
     * This method calculate sample size for bitmap loading
     *
     * @param options
     *      BitmapFactory options
     * @param reqWidth
     *      Required width
     * @param reqHeight
     *      Required height
     * @return
     *      Sample size
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
