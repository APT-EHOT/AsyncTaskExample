package ru.matchin.asynctaskexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

        verifyStoragePermissions(this);
        grantAccessToAllStorage();

        // Обработка нажатий на RadioButton
        binding.rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (binding.rb1.getId() == i) {
                    try {
                        Bitmap result = new MyTask().execute().get();
                        binding.imageView.setImageBitmap(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (binding.rb2.getId() == i) {
                    try {
                        Bitmap result = new MyInternetAsyncTask()
                            .execute("https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Cat03.jpg/1200px-Cat03.jpg")
                            .get();
                        binding.imageView.setImageBitmap(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Использование пикассо для загрузки изображений
                    Picasso
                        .get()
                        .load("https://icatcare.org/app/uploads/2018/07/Thinking-of-getting-a-cat.png")
                        .into(binding.imageView);
                }

            }
        });
    }

    // Загрузка картинки из локальной памяти
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

    // Загрузка картинки из интернета и сохранение ее в память
    private class MyInternetAsyncTask extends AsyncTask<String, Void, Bitmap> {

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
                    "downloaded.png"
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

    private static final String[] PERMISSIONS = {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.MANAGE_EXTERNAL_STORAGE
    };

    // Выдача разрешения на доступ ко всей памяти устройства через настройки (Android 11+)
    public void grantAccessToAllStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent();
                intent.setAction(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                );
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    // Вызов диалогового окна для выдачи пермишна на память
    public static void verifyStoragePermissions(Activity activity) {
        int grantedPermission = ActivityCompat
            .checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (grantedPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS,
                1 // Ваш ID для запроса permissions и различения их между собой
            );
        }
    }
}