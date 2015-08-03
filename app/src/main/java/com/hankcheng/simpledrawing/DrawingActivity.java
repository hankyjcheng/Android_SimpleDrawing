package com.hankcheng.simpledrawing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hankcheng.simpledrawing.utils.BackgroundView;
import com.hankcheng.simpledrawing.utils.DrawingView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class DrawingActivity extends ActionBarActivity {

    public DrawingView drawView;
    public BackgroundView backgroundView;
    private ImageButton currPaint;
    private float smallBrush, mediumBrush, largeBrush;

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String fileName;
    private boolean newFile;

    private int RESULT_LOAD_IMAGE = 100;
    private int RESULT_NEW_FILE = 200;
    private String imgDecodableString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        fileName = "New File";
        newFile = true;
        getSupportActionBar().setTitle(fileName);
        mDrawerList = (ListView) findViewById(R.id.navList);
        addDrawerItems();
        setupDrawer();

        drawView = (DrawingView) findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
        backgroundView = (BackgroundView) findViewById(R.id.backgroundImage);
        backgroundView.setBackgroundColor(Color.parseColor("#FFFFFF"));

        currPaint = (ImageButton) paintLayout.getChildAt(0);
        currPaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.paint_pressed));
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        drawView.setBrushSize(mediumBrush);
        Log.d("STATUS: ", "onCreate");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void addDrawerItems() {
        String home = this.getApplicationContext().getString(R.string.home);
        String new_file = this.getApplicationContext().getString(R.string.new_file);
        String open_file = this.getApplicationContext().getString(R.string.open_file);
        String save = this.getApplicationContext().getString(R.string.save);
        String save_as = this.getApplicationContext().getString(R.string.save_as);
        String[] listArray = {home, new_file, open_file, save, save_as};
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Home
                if (position == 0) {
                    // Not implemented yet.
                }
                // New File
                else if (position == 1) {
                    onNewButtonClick();
                }
                // Open File
                else if (position == 2) {
                    onLoadButtonClick();
                }
                // Save
                else if (position == 3) {
                    onSaveButtonClick();
                }
                // Save As
                else if (position == 4) {
                    saveAsEditText();
                }
                mDrawerLayout.closeDrawers();
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // creates call to onPrepareOptionsMenu()
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void paintClicked(View view) {
        //use chosen color
        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());
        if (view != currPaint) {
            //update color
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.paint_pressed));
            currPaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.paint));
            currPaint = (ImageButton) view;
        }
    }

    private void onDrawButtonClick() {
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setTitle("Brush size:");
        brushDialog.setContentView(R.layout.brush_chooser);
        ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
        smallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setBrushSize(smallBrush);
                drawView.setLastBrushSize(smallBrush);
                drawView.setErase(false);
                brushDialog.dismiss();
            }
        });
        ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
        mediumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setBrushSize(mediumBrush);
                drawView.setLastBrushSize(mediumBrush);
                drawView.setErase(false);
                brushDialog.dismiss();
            }
        });
        ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
        largeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setBrushSize(largeBrush);
                drawView.setLastBrushSize(largeBrush);
                drawView.setErase(false);
                brushDialog.dismiss();
            }
        });
        brushDialog.show();
    }

    private void onEraseButtonClick() {
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setTitle("Eraser size:");
        brushDialog.setContentView(R.layout.brush_chooser);
        ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
        smallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setErase(true);
                drawView.setBrushSize(smallBrush);
                brushDialog.dismiss();
            }
        });
        ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
        mediumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setErase(true);
                drawView.setBrushSize(mediumBrush);
                brushDialog.dismiss();
            }
        });
        ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
        largeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setErase(true);
                drawView.setBrushSize(largeBrush);
                brushDialog.dismiss();
            }
        });
        brushDialog.show();
    }

    private void onNewButtonClick() {
        //new button
        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle("New drawing");
        newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startNewFileActivity();
                //drawView.startNew();
                //backgroundView.startNew();
                /*
                fileName = "New File";
                newFile = true;
                */
                //getSupportActionBar().setTitle(fileName);
                dialog.dismiss();
            }
        });
        newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        newDialog.show();
    }

    private void startNewFileActivity() {
        Intent intent = new Intent(getApplicationContext(), NewFileActivity.class);
        startActivityForResult(intent, RESULT_NEW_FILE);
    }

    private void onSaveButtonClick() {
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save drawing");
        saveDialog.setMessage("Save drawing to device Gallery?");
        saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (newFile) {
                    saveAsEditText();
                }
                else {
                    drawView.setDrawingCacheEnabled(true);
                    if (saveImageToExternalStorage(combineBitmap(backgroundView.getCanvasBitmap(), drawView.getDrawingCache()), fileName)) {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/SimpleDrawing")));
                        Toast savedToast = Toast.makeText(getApplicationContext(), "Drawing saved!", Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else {
                        Toast unsavedToast = Toast.makeText(getApplicationContext(), "Image could not be saved.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }
                    drawView.destroyDrawingCache();
                }
            }
        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        saveDialog.show();
    }

    private void saveAsEditText() {
        LayoutInflater layoutInflater = LayoutInflater.from(DrawingActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog_save_as, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DrawingActivity.this);
        alertDialogBuilder.setView(promptView);
        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String resultText = editText.getText().toString();
                if (resultText.length() == 0) {
                    Toast.makeText(getApplicationContext(), "File name must not be empty.", Toast.LENGTH_SHORT).show();
                }
                else if (resultText.length() > 15) {
                    Toast.makeText(getApplicationContext(), "File name must not exceed 15 characters.", Toast.LENGTH_SHORT).show();
                }
                else if (resultText.charAt(0) < 48 || (resultText.charAt(0) > 57 && resultText.charAt(0) < 65) ||
                        (resultText.charAt(0) > 90 && resultText.charAt(0) < 97) || resultText.charAt(0) > 122) {
                    Toast warningToast = Toast.makeText(getApplicationContext(), "File name cannot start with a special character.", Toast.LENGTH_SHORT);
                    warningToast.show();
                }
                else {
                    drawView.setDrawingCacheEnabled(true);
                    if (saveImageToExternalStorage(combineBitmap(backgroundView.getCanvasBitmap(), drawView.getDrawingCache()), resultText)) {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/SimpleDrawing")));
                        Toast.makeText(getApplicationContext(), "Drawing saved!", Toast.LENGTH_SHORT).show();
                        fileName = resultText;
                        newFile = false;
                        getSupportActionBar().setTitle(fileName);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Image could not be saved.", Toast.LENGTH_SHORT).show();
                    }
                    drawView.destroyDrawingCache();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private Bitmap combineBitmap(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }

    private boolean saveImageToExternalStorage(Bitmap image, String name) {
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/SimpleDrawing";
        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            OutputStream fOut = null;
            File file = new File(fullPath, name + ".png");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            fOut = new FileOutputStream(file);
            // 100 means no compression, the lower you go, the stronger the compression
            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            return true;
        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
            return false;
        }
    }

    private void onLoadButtonClick() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // Load Image
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                drawView.setDrawCanvas(BitmapFactory.decodeFile(imgDecodableString));
                backgroundView.startNew();
                fileName = imgDecodableString.substring(imgDecodableString.lastIndexOf("/") + 1, imgDecodableString.lastIndexOf("."));
                newFile = false;
                getSupportActionBar().setTitle(fileName);
            }
            // Create New File
            if (requestCode == RESULT_NEW_FILE) {
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String backgroundImageString = bundle.getString("image_string");
                    if (backgroundImageString != null) {
                        backgroundView.setBackgroundCanvas(BitmapFactory.decodeFile(backgroundImageString));
                    }
                    else {
                        backgroundView.startNew();
                    }
                    drawView.startNew();
                    newFile = true;
                    fileName = "New File";
                    getSupportActionBar().setTitle(fileName);
                }
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d("STATUS: ", "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("STATUS: ", "onResume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.draw_button) {
            onDrawButtonClick();
            return true;
        }
        else if (id == R.id.erase_button) {
            onEraseButtonClick();
            return true;
        }
        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
