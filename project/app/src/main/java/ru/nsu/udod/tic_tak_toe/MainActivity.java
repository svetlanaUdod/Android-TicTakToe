package ru.nsu.udod.tic_tak_toe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Uri img1 = null;
    private Uri img2 = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.get_photo1).setOnClickListener(v -> {
            onImageClick(v, 1);
        });
        findViewById(R.id.get_photo2).setOnClickListener(v -> {
            onImageClick(v, 2);
        });
    }

    public final void onPlayClick(View view) {
        EditText player1 = findViewById(R.id.player1_name);
        EditText player2 = findViewById(R.id.player2_name);
        String name1 = player1.getText().toString();
        String name2 = player2.getText().toString();

        if (name1.length() > 0 && name2.length() > 0) {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("player1", name1);
            intent.putExtra("player2", name2);
            if (img1 != null)
                intent.putExtra("img1", img1.toString());
            if (img2 != null)
                intent.putExtra("img2", img2.toString());
            startActivity(intent);
        }
    }

    public final void onImageClick(View view, int requestCode) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, requestCode);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;
        ImageView imageView;
        Uri selectedImage;

        if (requestCode == 1 || requestCode == 2) {
            if (resultCode == RESULT_OK) {
                selectedImage = imageReturnedIntent.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else return;
        } else return;

        if (requestCode == 1) {
            imageView = (ImageView) findViewById(R.id.get_photo1);
            img1 = selectedImage;
        } else {
            imageView = (ImageView) findViewById(R.id.get_photo2);
            img2 = selectedImage;
        }
        imageView.setImageBitmap(bitmap);
    }
}