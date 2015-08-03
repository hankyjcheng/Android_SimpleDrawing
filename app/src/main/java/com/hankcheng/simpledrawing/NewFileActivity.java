package com.hankcheng.simpledrawing;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hankcheng.simpledrawing.utils.BackgroundView;


public class NewFileActivity extends ActionBarActivity implements View.OnClickListener {

    private int RESULT_LOAD_IMG = 100;
    private String imageDecodableString;
    private BackgroundView backgroundPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_file);
        getSupportActionBar().setTitle("Create New File");

        Button whiteButton = (Button) findViewById(R.id.white_background);
        whiteButton.setOnClickListener(this);
        Button deviceButton = (Button) findViewById(R.id.choose_device);
        deviceButton.setOnClickListener(this);
        Button cancelButton = (Button) findViewById(R.id.cancel_file);
        cancelButton.setOnClickListener(this);
        Button createButton = (Button) findViewById(R.id.create_file);
        createButton.setOnClickListener(this);

        backgroundPreview = (BackgroundView) findViewById(R.id.backgroundPreview);
        backgroundPreview.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.white_background) {
            onWhiteBackgroundClick();
        }
        else if (view.getId() == R.id.choose_device) {
            onChooseDeviceClick();
        }
        else if (view.getId() == R.id.cancel_file) {
            finish();
        }
        else if (view.getId() == R.id.create_file) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("image_string", imageDecodableString);
            setResult(RESULT_OK, returnIntent);
            //startActivity(returnIntent);
            finish();
        }
    }

    private void onWhiteBackgroundClick() {
        backgroundPreview.startNew();
    }

    private void onChooseDeviceClick() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imageDecodableString = cursor.getString(columnIndex);
                cursor.close();

                backgroundPreview.setBackgroundCanvas(BitmapFactory.decodeFile(imageDecodableString));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }
}
