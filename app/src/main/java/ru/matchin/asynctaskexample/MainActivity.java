package ru.matchin.asynctaskexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import ru.matchin.asynctaskexample.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MyInternetAsyncTask task = new MyInternetAsyncTask();
        try {
            binding.imageView.setImageBitmap(
                task
                    .execute("https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Cat03.jpg/1200px-Cat03.jpg") // Запускает AsyncTask
                    .get() // возвращает результат выполнения AsyncTask
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bitmap = null;
            File file = new File(
                Environment
                    .getExternalStorageDirectory()
                    .getAbsoluteFile(),
                "picture.jpg"
            );
            try {
                bitmap = BitmapFactory
                    .decodeFile(file.getAbsolutePath());
                binding.imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }

    private class MyInternetAsyncTask
        extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(strings[0]);
                bitmap = BitmapFactory
                    .decodeStream(url.openStream());
                File file = new File(
                    Environment
                        .getExternalStorageDirectory()
                        .getAbsoluteFile(),
                    "downloaded"
                );
                if (!file.isFile())
                    file.createNewFile();

                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
                return bitmap;
            }
            return bitmap;
        }
    }
}