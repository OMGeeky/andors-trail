package com.gpl.rpg.AndorsTrail_beta2.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.provider.DocumentFile;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gpl.rpg.AndorsTrail_beta2.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail_beta2.AndorsTrailPreferences;
import com.gpl.rpg.AndorsTrail_beta2.R;
import com.gpl.rpg.AndorsTrail_beta2.controller.Constants;
import com.gpl.rpg.AndorsTrail_beta2.model.ModelContainer;
import com.gpl.rpg.AndorsTrail_beta2.resource.tiles.TileManager;
import com.gpl.rpg.AndorsTrail_beta2.savegames.Savegames;
import com.gpl.rpg.AndorsTrail_beta2.savegames.Savegames.FileHeader;
import com.gpl.rpg.AndorsTrail_beta2.util.AndroidStorage;
import com.gpl.rpg.AndorsTrail_beta2.util.ThemeHelper;
import com.gpl.rpg.AndorsTrail_beta2.view.CustomDialogFactory;

public final class LoadSaveActivity extends AndorsTrailBaseActivity implements OnClickListener {
    private boolean isLoading = true;
    //region special slot numbers
    private static final int SLOT_NUMBER_CREATE_NEW_SLOT = -1;
    private static final int SLOT_NUMBER_EXPORT_SAVEGAMES = -2;
    private static final int SLOT_NUMBER_IMPORT_SAVEGAMES = -3;
    private static final int SLOT_NUMBER_IMPORT_WORLDMAP = -4;
    private static final int SLOT_NUMBER_FIRST_SLOT = 1;
    //endregion
    private ModelContainer model;
    private TileManager tileManager;
    private AndorsTrailPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.getDialogTheme());
        super.onCreate(savedInstanceState);

        final AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        app.setWindowParameters(this);
        this.model = app.getWorld().model;
        this.preferences = app.getPreferences();
        this.tileManager = app.getWorld().tileManager;

        String loadsave = getIntent().getData().getLastPathSegment();
        isLoading = (loadsave.equalsIgnoreCase("load"));

        setContentView(R.layout.loadsave);

        TextView tv = (TextView) findViewById(R.id.loadsave_title);
        if (isLoading) {
            tv.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_search, 0, 0, 0);
            tv.setText(R.string.loadsave_title_load);
        } else {
            tv.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_save, 0, 0, 0);
            tv.setText(R.string.loadsave_title_save);
        }

        ViewGroup slotList = (ViewGroup) findViewById(R.id.loadsave_slot_list);
        Button slotTemplateButton = (Button) findViewById(R.id.loadsave_slot_n);
        LayoutParams params = slotTemplateButton.getLayoutParams();
        slotList.removeView(slotTemplateButton);

        ViewGroup newSlotContainer = (ViewGroup) findViewById(R.id.loadsave_save_to_new_slot_container);
        Button createNewSlot = (Button) findViewById(R.id.loadsave_save_to_new_slot);

        Button exportSaves = (Button) findViewById(R.id.loadsave_export_save);
        Button importSaves = (Button) findViewById(R.id.loadsave_import_save);
        Button importWorldmap = (Button) findViewById(R.id.loadsave_import_worldmap);

        exportSaves.setTag(SLOT_NUMBER_EXPORT_SAVEGAMES);
        importSaves.setTag(SLOT_NUMBER_IMPORT_SAVEGAMES);
        importWorldmap.setTag(SLOT_NUMBER_IMPORT_WORLDMAP);

        ViewGroup exportContainer = (ViewGroup) findViewById(R.id.loadsave_export_save_container);
        ViewGroup importContainer = (ViewGroup) findViewById(R.id.loadsave_import_save_container);
        ViewGroup importWorldmapContainer = (ViewGroup) findViewById(R.id.loadsave_import_worldmap_container);


        addSavegameSlotButtons(slotList, params, Savegames.getUsedSavegameSlots(this));

        checkAndRequestPermissions();

        if (!isLoading) {
            createNewSlot.setTag(SLOT_NUMBER_CREATE_NEW_SLOT);
            createNewSlot.setOnClickListener(this);
            newSlotContainer.setVisibility(View.VISIBLE);
            exportContainer.setVisibility(View.GONE);
            importContainer.setVisibility(View.GONE);
            importWorldmapContainer.setVisibility(View.GONE);
        } else {
            newSlotContainer.setVisibility(View.GONE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                exportSaves.setOnClickListener(this);
                importSaves.setOnClickListener(this);
                importWorldmap.setOnClickListener(this);
                exportContainer.setVisibility(View.VISIBLE);
                importContainer.setVisibility(View.VISIBLE);
                importWorldmapContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    private static final int READ_EXTERNAL_STORAGE_REQUEST = 1;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST = 2;

    @TargetApi(23)
    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST);
            }
            if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.storage_permissions_mandatory, Toast.LENGTH_LONG).show();
            ((AndorsTrailApplication) getApplication()).discardWorld();
            finish();
        }
    }

    private void addSavegameSlotButtons(ViewGroup parent, LayoutParams params, List<Integer> usedSavegameSlots) {
        int unused = 1;
        for (int slot : usedSavegameSlots) {
            final FileHeader header = Savegames.quickload(this, slot);
            if (header == null) continue;

            while (unused < slot) {
                Button b = new Button(this);
                b.setLayoutParams(params);
                b.setTag(unused);
                b.setOnClickListener(this);
                b.setText(getString(R.string.loadsave_empty_slot, unused));
                tileManager.setImageViewTileForPlayer(getResources(), b, header.iconID);
                parent.addView(b, params);
                unused++;
            }
            unused++;

            Button b = new Button(this);
            b.setLayoutParams(params);
            b.setTag(slot);
            b.setOnClickListener(this);
            b.setText(slot + ". " + header.describe());
            tileManager.setImageViewTileForPlayer(getResources(), b, header.iconID);
            parent.addView(b, params);
        }
    }


    private void completeLoadSaveActivity(int slot) {
        Intent i = new Intent();
        if (slot == SLOT_NUMBER_CREATE_NEW_SLOT) {
            List<Integer> usedSlots = Savegames.getUsedSavegameSlots(this);
            if (usedSlots.isEmpty())
                slot = SLOT_NUMBER_FIRST_SLOT;
            else slot = Collections.max(usedSlots) + 1;
        } else if (slot == SLOT_NUMBER_EXPORT_SAVEGAMES
                || slot == SLOT_NUMBER_IMPORT_SAVEGAMES
                || slot == SLOT_NUMBER_IMPORT_WORLDMAP) {
            i.putExtra("import_export", true);
        } else if (slot < SLOT_NUMBER_FIRST_SLOT)
            slot = SLOT_NUMBER_FIRST_SLOT;

        i.putExtra("slot", slot);
        setResult(Activity.RESULT_OK, i);
        LoadSaveActivity.this.finish();
    }

    private String getConfirmOverwriteQuestion(int slot) {
        if (isLoading)
            return null; //if we're creating a new slot
        return getConfirmOverwriteQuestionSub(slot);
    }

    private String getConfirmOverwriteQuestionSub(int slot) {
        if (slot == SLOT_NUMBER_CREATE_NEW_SLOT)
            return null;
        if (!Savegames.getSlotFile(slot, this).exists())
            return null;

        if (preferences.displayOverwriteSavegame == AndorsTrailPreferences.CONFIRM_OVERWRITE_SAVEGAME_ALWAYS) {
            return getString(R.string.loadsave_save_overwrite_confirmation_all);
        }
        if (preferences.displayOverwriteSavegame == AndorsTrailPreferences.CONFIRM_OVERWRITE_SAVEGAME_NEVER) {
            return null;
        }

        final String currentPlayerName = model.player.getName();
        final FileHeader header = Savegames.quickload(this, slot);
        if (header == null) return null;

        final String savedPlayerName = header.playerName;
        if (currentPlayerName.equals(savedPlayerName)) return null; //if the names match

        return getString(R.string.loadsave_save_overwrite_confirmation, savedPlayerName, currentPlayerName);
    }

    @Override
    public void onClick(View view) {
        final int slot = (Integer) view.getTag();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switch (slot) {
                case SLOT_NUMBER_IMPORT_WORLDMAP:
                    clickImportWorldmap();
                    return;
                case SLOT_NUMBER_IMPORT_SAVEGAMES:
                    clickImportSaveGames();
                    return;
                case SLOT_NUMBER_EXPORT_SAVEGAMES:
                    clickExportSaveGames();
                    return;
            }
        }
        if (!isLoading
                && slot != SLOT_NUMBER_CREATE_NEW_SLOT
                && AndorsTrailApplication.CURRENT_VERSION == AndorsTrailApplication.DEVELOPMENT_INCOMPATIBLE_SAVEGAME_VERSION) {
            if (!isOverwriteTargetInIncompatibleVersion(slot)) {
                saveOrOverwriteSavegame(slot);
            }
        } else if (isLoading) {
            loadSaveGame(slot);
        } else {
            saveOrOverwriteSavegame(slot);
        }
    }

    private void saveOrOverwriteSavegame(int slot) {
        final String message = getConfirmOverwriteQuestion(slot);
        if (message != null) {
            showConfirmoverwriteQuestion(slot, message);
        } else {
            completeLoadSaveActivity(slot);
        }
    }

    private boolean isOverwriteTargetInIncompatibleVersion(int slot) {
        final FileHeader header = Savegames.quickload(this, slot);
        if (header != null && header.fileversion != AndorsTrailApplication.DEVELOPMENT_INCOMPATIBLE_SAVEGAME_VERSION) {
            final Dialog d = CustomDialogFactory.createDialog(this,
                    "Overwriting not allowed",
                    getResources().getDrawable(android.R.drawable.ic_dialog_alert),
                    "You are currently using a development version of Andor's trail. Overwriting a regular savegame is not allowed in development mode.",
                    null,
                    true);
            CustomDialogFactory.addDismissButton(d, android.R.string.ok);
            CustomDialogFactory.show(d);
            return true;
        }
        return false;
    }

    //region Imports/Exports


    private void exportSaveGames(Intent data) {
        Uri uri = data.getData();

        Context context = getApplicationContext();
        ContentResolver resolver = AndorsTrailApplication.getApplicationFromActivity(this).getContentResolver();

        File storageDir = AndroidStorage.getStorageDirectory(context, Constants.FILENAME_SAVEGAME_DIRECTORY);
        DocumentFile source = DocumentFile.fromFile(storageDir);
        DocumentFile target = DocumentFile.fromTreeUri(context, uri);
        if (target == null) {
            //TODO: handle the target being null
            return;
        }

        //TODO: check if files exist already in directory (saves & worldmap folder)

        DocumentFile[] files = source.listFiles();
        for (DocumentFile file : files) {
            if (file.isFile()) {
                copySavegame(target, file, resolver);
            } else if (file.isDirectory()) {
                DocumentFile targetWorlmap = target.createDirectory(Constants.FILENAME_WORLDMAP_DIRECTORY);
                if (targetWorlmap == null)
                    //TODO: handle not being able to create this folder instead of just skipping the export of the worldmap
                    continue;

                DocumentFile[] worldmapFiles = file.listFiles();
                for (DocumentFile f : worldmapFiles) {
                    copyWorldmapFile(targetWorlmap, f, resolver);
                }
            }
        }

        //TODO: notify user of successful export (to location?)
        completeLoadSaveActivity(SLOT_NUMBER_EXPORT_SAVEGAMES);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void importSaveGames(Intent data) {
        Uri uri = data.getData();
        ClipData uris = data.getClipData();

        if (uri == null && uris == null) {
            //no file was selected
            return;
        }

        Context context = getApplicationContext();
        ContentResolver resolver = context.getContentResolver();

        File storageDir = AndroidStorage.getStorageDirectory(context, Constants.FILENAME_SAVEGAME_DIRECTORY);
        DocumentFile appSavegameFolder = DocumentFile.fromFile(storageDir);


        if (uri != null) {
            importSaveGameFromUri(context, resolver, appSavegameFolder, uri, new ArrayList<>());
        } else {

            int count = uris.getItemCount();
            List<Integer> ints = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                ClipData.Item item = uris.getItemAt(i);
                if (item == null) {
                    continue;
                }
                Uri itemUri = item.getUri();
                importSaveGameFromUri(context, resolver, appSavegameFolder, itemUri, ints);
            }
        }

        //TODO: Notify user of successful import
    }

    private void completeSavegameImportAndCheckIfDone(List<Integer> importsNeedingConfirmation, int slot) {
        importsNeedingConfirmation.remove((Object) slot);
        if (importsNeedingConfirmation.isEmpty()) {
            completeLoadSaveActivity(SLOT_NUMBER_IMPORT_SAVEGAMES);
        }
    }

    private void importSaveGameFromUri(Context context, ContentResolver resolver, DocumentFile appSavegameFolder, Uri itemUri, List<Integer> importsNeedingConfirmation) {
        DocumentFile itemFile = DocumentFile.fromSingleUri(context, itemUri);
        if (itemFile == null || !itemFile.isFile()) {
            return;
        }

        String fileName = itemFile.getName();
        if (fileName == null || !fileName.startsWith(Constants.FILENAME_SAVEGAME_FILENAME_PREFIX)) {
            //TODO: Maybe output a message that the file didn't have the right name?
            return;
        }
        String slotStr = fileName.substring(Constants.FILENAME_SAVEGAME_FILENAME_PREFIX.length());
        int slot;
        try {
            slot = Integer.parseInt(slotStr);
        } catch (NumberFormatException e) {
            //TODO: Maybe output a message that the file didn't have the right name?
            return;
        }

        String confirmOverwriteQuestion = getConfirmOverwriteQuestionSub(slot);
        if (confirmOverwriteQuestion == null ) {
            importSaveGameFile(resolver, appSavegameFolder, itemFile, slot);
        } else {
            importsNeedingConfirmation.add(slot);
            showConfirmOverwriteByImportQuestion(resolver, appSavegameFolder, itemFile, slot, confirmOverwriteQuestion, importsNeedingConfirmation);
        }
    }

    private void importSaveGameFile(ContentResolver resolver, DocumentFile appSavegameFolder, DocumentFile itemFile, int slot) {
        String targetName = Savegames.getSlotFileName(slot);
        DocumentFile targetFile = appSavegameFolder.createFile(Constants.NO_FILE_EXTENSION_MIME_TYPE, targetName);
        if (targetFile == null) {
            return;
        }

        String fileName = targetFile.getName();
        if (!targetName.equals(fileName)) {
            //TODO: handle what should happen if the target file has a wrong name
            return;
        }

        try {
            AndroidStorage.copyDocumentFile(itemFile, resolver, targetFile);
        } catch (IOException e) {
            //TODO: output error message to user
            e.printStackTrace();
        }
    }

    private void importWorldmap(Intent data) {
        Uri uri = data.getData();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void clickExportSaveGames() {
        startActivityForResult(AndroidStorage.getNewOpenDirectoryIntent(), -SLOT_NUMBER_EXPORT_SAVEGAMES);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void clickImportSaveGames() {
        startActivityForResult(AndroidStorage.getNewSelectMultipleSavegameFilesIntent(), -SLOT_NUMBER_IMPORT_SAVEGAMES);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void clickImportWorldmap() {
        startActivityForResult(AndroidStorage.getNewOpenDirectoryIntent(), -SLOT_NUMBER_IMPORT_WORLDMAP);

    }

    public void copySavegame(DocumentFile targetFolder, DocumentFile sourceFile, ContentResolver resolver) {
        try {
            AndroidStorage.copyDocumentFileToNew(sourceFile, resolver, targetFolder, Constants.NO_FILE_EXTENSION_MIME_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void copyWorldmapFile(DocumentFile targetFolder, DocumentFile sourceFile, ContentResolver resolver) {
        try {
            String mimeType = AndroidStorage.getMimeType(resolver, sourceFile.getUri());
            AndroidStorage.copyDocumentFileToNew(sourceFile, resolver, targetFolder, mimeType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showConfirmOverwriteByImportQuestion(ContentResolver resolver, DocumentFile appSavegameFolder, DocumentFile itemFile, final int slot, String message, List<Integer> importsNeedingConfirmation) {
        final String title =
                getString(R.string.loadsave_save_overwrite_confirmation_title) + ' '
                        + getString(R.string.loadsave_save_overwrite_confirmation_slot, slot);
        final Dialog d = CustomDialogFactory.createDialog(this,
                title,
                getResources().getDrawable(android.R.drawable.ic_dialog_alert),
                message,
                null,
                true);

        CustomDialogFactory.addButton(d, android.R.string.yes, v -> {
            importSaveGameFile(resolver, appSavegameFolder, itemFile, slot);
//            completeSavegameImportAndCheckIfDone(importsNeedingConfirmation, slot);
        });
        //TODO: insert an 'Yes to all' button or a checkbox to allow the current import to overwrite all files, not just one
        CustomDialogFactory.addDismissButton(d, android.R.string.no);
        CustomDialogFactory.setDismissListener(d, v -> {
            completeSavegameImportAndCheckIfDone(importsNeedingConfirmation, slot);
        });
        CustomDialogFactory.show(d);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK)
            return;

//        Uri uri = data.getData();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switch (-requestCode) {
                case SLOT_NUMBER_EXPORT_SAVEGAMES:
                    exportSaveGames(data);
                    return;
                case SLOT_NUMBER_IMPORT_SAVEGAMES:
                    importSaveGames(data);
                    return;
                case SLOT_NUMBER_IMPORT_WORLDMAP:
                    importWorldmap(data);
                    return;
            }
        }

    }


    //endregion

    private void loadSaveGame(int slot) {
        if (!Savegames.getSlotFile(slot, this).exists()) {
            showErrorLoadingEmptySlot();
        } else {
            final FileHeader header = Savegames.quickload(this, slot);
            if (header != null && !header.hasUnlimitedSaves) {
                showSlotGetsDeletedOnLoadWarning(slot);
            } else {
                completeLoadSaveActivity(slot);
            }
        }
    }

    private void showErrorLoadingEmptySlot() {
        final Dialog d = CustomDialogFactory.createDialog(this,
                getString(R.string.startscreen_error_loading_game),
                getResources().getDrawable(android.R.drawable.ic_dialog_alert),
                getString(R.string.startscreen_error_loading_empty_slot),
                null,
                true);
        CustomDialogFactory.addDismissButton(d, android.R.string.ok);
        CustomDialogFactory.show(d);
    }

    private void showSlotGetsDeletedOnLoadWarning(final int slot) {
        final Dialog d = CustomDialogFactory.createDialog(this,
                getString(R.string.startscreen_attention_slot_gets_delete_on_load),
                getResources().getDrawable(android.R.drawable.ic_dialog_alert),
                getString(R.string.startscreen_attention_message_slot_gets_delete_on_load),
                null,
                true);
        CustomDialogFactory.addButton(d, android.R.string.ok, v -> completeLoadSaveActivity(slot));
        CustomDialogFactory.show(d);
    }

    private void showConfirmoverwriteQuestion(final int slot, String message) {
        final String title =
                getString(R.string.loadsave_save_overwrite_confirmation_title) + ' '
                        + getString(R.string.loadsave_save_overwrite_confirmation_slot, slot);
        final Dialog d = CustomDialogFactory.createDialog(this,
                title,
                getResources().getDrawable(android.R.drawable.ic_dialog_alert),
                message,
                null,
                true);

        CustomDialogFactory.addButton(d, android.R.string.yes, v -> completeLoadSaveActivity(slot));
        CustomDialogFactory.addDismissButton(d, android.R.string.no);
        CustomDialogFactory.show(d);
    }

}
