package com.gpl.rpg.AndorsTrail.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.AndorsTrailPreferences;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;
import com.gpl.rpg.AndorsTrail.savegames.Savegames;
import com.gpl.rpg.AndorsTrail.savegames.Savegames.FileHeader;
import com.gpl.rpg.AndorsTrail.util.AndroidStorage;
import com.gpl.rpg.AndorsTrail.util.ThemeHelper;
import com.gpl.rpg.AndorsTrail.view.CustomDialogFactory;

public final class LoadSaveActivity extends AndorsTrailBaseActivity implements OnClickListener {
    private boolean isLoading = true;
    //region special slot numbers
    private static final int SLOT_NUMBER_CREATE_NEW_SLOT = -1;
    public static final int SLOT_NUMBER_EXPORT_SAVEGAMES = -2;
    public static final int SLOT_NUMBER_IMPORT_SAVEGAMES = -3;
    public static final int SLOT_NUMBER_IMPORT_WORLDMAP = -4;
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

        ViewGroup exportImportContainer = (ViewGroup) findViewById(R.id.loadsave_export_import_save_container);


        addSavegameSlotButtons(slotList, params, Savegames.getUsedSavegameSlots(this));

        checkAndRequestPermissions();

        if (!isLoading) {
            createNewSlot.setTag(SLOT_NUMBER_CREATE_NEW_SLOT);
            createNewSlot.setOnClickListener(this);
            newSlotContainer.setVisibility(View.VISIBLE);
            exportImportContainer.setVisibility(View.GONE);
        } else {
            newSlotContainer.setVisibility(View.GONE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                exportSaves.setOnClickListener(this);
                importSaves.setOnClickListener(this);
                importWorldmap.setOnClickListener(this);
                exportImportContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    private static final int READ_EXTERNAL_STORAGE_REQUEST = 1;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST = 2;

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
            return null;

        return getConfirmOverwriteQuestionIgnoringLoading(slot);
    }

    private String getConfirmOverwriteQuestionIgnoringLoading(int slot) {
        if (slot == SLOT_NUMBER_CREATE_NEW_SLOT)
            return null;//creating a new savegame

        if (!Savegames.getSlotFile(slot, this).exists())
            return null;//nothing in slot to overwrite

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
            final Dialog d = CustomDialogFactory.createErrorDialog(this, "Overwriting not allowed", "You are currently using a development version of Andor's trail. Overwriting a regular savegame is not allowed in development mode.");
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
            return;
        }

        DocumentFile[] files = source.listFiles();

        boolean hasExistingFiles = false;
        for (DocumentFile file :
                files) {
            String fileName = file.getName();
            if (fileName == null)
                continue;

            DocumentFile existingFile = target.findFile(fileName);
            if (existingFile != null) {
                hasExistingFiles = true;
                break;
            }
        }

        if (hasExistingFiles) {
            showConfirmOverwriteByExportQuestion(resolver, target, files);
        } else {
            exportSaveGamesFolderContentToFolder(resolver, target, files);
        }
    }

    private void exportSaveGamesFolderContentToFolder(ContentResolver resolver, DocumentFile target, DocumentFile[] files) {
        for (DocumentFile file : files) {
            String fileName = file.getName();
            DocumentFile existingFile = target.findFile(fileName);
            boolean hasExistingFile = existingFile != null && existingFile.exists();

            if (file.isFile()) {
                try {
                    if (hasExistingFile)
                        existingFile.delete();


                    AndroidStorage.copyDocumentFileToNewOrExistingFile(file, resolver, target);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (file.isDirectory()) {
                DocumentFile targetWorlmap = existingFile;
                //if the folder exists already, put the files in the existing folder. (should not happen because of check earlier)
                if (!hasExistingFile)
                    //create a new folder for the worldmap-files
                    targetWorlmap = target.createDirectory(Constants.FILENAME_WORLDMAP_DIRECTORY);

                if (targetWorlmap == null)//Unable to create worldmap folder for some reason
                    continue;

                DocumentFile[] worldmapFiles = file.listFiles();
                for (DocumentFile f : worldmapFiles) {
                    try {
                        AndroidStorage.copyDocumentFileToNewOrExistingFile(f, resolver, targetWorlmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

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

        List<Uri> uriList = new ArrayList<>();
        if (uri != null) {
            uriList.add(uri);
        } else {
            for (int i = 0; i < uris.getItemCount(); i++)
                uriList.add(uris.getItemAt(i).getUri());
        }
        importSaveGamesFromUris(context, resolver, appSavegameFolder, uriList);
    }

    private void importSaveGamesFromUris(Context context, ContentResolver resolver, DocumentFile appSavegameFolder, List<Uri> uriList) {
        int count = uriList.size();

        ArrayList<DocumentFile> alreadyExistingFiles = new ArrayList<>();
        ArrayList<DocumentFile> newFiles = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Uri item = uriList.get(i);
            DocumentFile itemFile = DocumentFile.fromSingleUri(context, item);
            boolean fileAlreadyExists = getExistsSavegameInOwnFiles(itemFile, appSavegameFolder);
            if (fileAlreadyExists)
                alreadyExistingFiles.add(itemFile);
            else
                newFiles.add(itemFile);
        }

        if (alreadyExistingFiles.size() > 0) {
            showConfirmOverwriteByImportQuestion(resolver, appSavegameFolder, alreadyExistingFiles, newFiles);
        } else {
            importSaveGames(resolver, appSavegameFolder, newFiles);
        }
    }

    private void importSaveGames(ContentResolver resolver, DocumentFile appSavegameFolder, List<DocumentFile> saveFiles) {
        for (DocumentFile file : saveFiles) {
            int slot = getSlotFromSavegameFileName(file.getName());
            importSaveGameFile(resolver, appSavegameFolder, file, slot);
        }
    }


    private void completeSavegameImportAndCheckIfDone(List<Integer> importsNeedingConfirmation, int slot) {
        importsNeedingConfirmation.remove((Object) slot);
        if (importsNeedingConfirmation.isEmpty()) {
            completeLoadSaveActivity(SLOT_NUMBER_IMPORT_SAVEGAMES);
        }
    }

    private boolean getExistsSavegameInOwnFiles(DocumentFile savegameFile, DocumentFile appSavegameFolder) {
        if (savegameFile == null)
            return false;

        DocumentFile foundFile = appSavegameFolder.findFile(Objects.requireNonNull(savegameFile.getName()));
        return foundFile != null && foundFile.exists();
    }

    private int getSlotFromSavegameFileName(String fileName) {
        if (fileName == null || !fileName.startsWith(Constants.FILENAME_SAVEGAME_FILENAME_PREFIX)) {
            //TODO: Maybe output a message that the file didn't have the right name?
            return -1;
        }
        String slotStr = fileName.substring(Constants.FILENAME_SAVEGAME_FILENAME_PREFIX.length());

        int slot;
        try {
            slot = Integer.parseInt(slotStr);
            return slot;
        } catch (NumberFormatException e) {
            //TODO: Maybe output a message that the file didn't have the right name?
            return -1;
        }
    }

    private void importSaveGameFile(ContentResolver resolver, DocumentFile appSavegameFolder, DocumentFile itemFile, int slot) {
        String targetName = Savegames.getSlotFileName(slot);
        DocumentFile targetFile = getOrCreateDocumentFile(appSavegameFolder, targetName);

        if (targetFile == null || !targetName.equals(targetFile.getName())) {
            showErrorImportingSaveGameUnknown();//TODO: maybe replace with a more specific error message
            return;
        }

        try {
            AndroidStorage.copyDocumentFile(itemFile, resolver, targetFile);
        } catch (IOException e) {
            showErrorImportingSaveGameUnknown();
            e.printStackTrace();
        }
    }

    private DocumentFile getOrCreateDocumentFile(DocumentFile folder, String targetName) {
        DocumentFile targetFile = folder.findFile(targetName);//try finding the file
        if (targetFile == null)//no file found, creating new one
            targetFile = folder.createFile(Constants.NO_FILE_EXTENSION_MIME_TYPE, targetName);
        return targetFile;
    }

    private void importWorldmap(Intent data) {
        Uri uri = data.getData();

        Context context = getApplicationContext();
        ContentResolver resolver = AndorsTrailApplication.getApplicationFromActivity(this).getContentResolver();

        File storageDir = AndroidStorage.getStorageDirectory(context, Constants.FILENAME_SAVEGAME_DIRECTORY);
        DocumentFile storageFolder = DocumentFile.fromFile(storageDir);
        DocumentFile ownWorldmapFolder = storageFolder.findFile(Constants.FILENAME_WORLDMAP_DIRECTORY);
        if (ownWorldmapFolder == null) {
            ownWorldmapFolder = storageFolder.createDirectory(Constants.FILENAME_WORLDMAP_DIRECTORY);
        }

        DocumentFile chosenFolder = DocumentFile.fromTreeUri(context, uri);
        if (chosenFolder == null || !chosenFolder.isDirectory()) {
            showErrorImportingWorldmapWrongDirectory();
            return;
        }
        if (!Constants.FILENAME_WORLDMAP_DIRECTORY.equals(chosenFolder.getName())) {
            //user did not select the worldmap folder directly
            DocumentFile file = chosenFolder.findFile(Constants.FILENAME_WORLDMAP_DIRECTORY);
            if (file == null || !file.isDirectory() || !Constants.FILENAME_WORLDMAP_DIRECTORY.equals(file.getName())) {
                //could not find a worldmap folder in the users selection
                showErrorImportingWorldmapWrongDirectory();
                return;
            }

            chosenFolder = file;
        }

        DocumentFile[] files = chosenFolder.listFiles();
        for (DocumentFile file : files) {
            if (file.isFile()) {
                try {
                    AndroidStorage.copyDocumentFileToNewOrExistingFile(file, resolver, ownWorldmapFolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        completeLoadSaveActivity(SLOT_NUMBER_IMPORT_WORLDMAP);
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

    private void showConfirmOverwriteByExportQuestion(ContentResolver resolver, DocumentFile targetFolder, DocumentFile[] files) {
        final Dialog d = CustomDialogFactory.createDialog(this,
                getString(R.string.loadsave_export_overwrite_confirmation_title),
                getResources().getDrawable(android.R.drawable.ic_dialog_alert),
                getString(R.string.loadsave_export_overwrite_confirmation),
                null,
                true);

        CustomDialogFactory.addButton(d, android.R.string.yes, v -> exportSaveGamesFolderContentToFolder(resolver, targetFolder, files));
        CustomDialogFactory.addDismissButton(d, android.R.string.no);

        CustomDialogFactory.show(d);
    }

    private void showConfirmOverwriteByImportQuestion(ContentResolver resolver,
                                                      DocumentFile appSavegameFolder,
                                                      List<DocumentFile> alreadyExistingFiles,
                                                      List<DocumentFile> newFiles) {
        final String title = getString(R.string.loadsave_import_overwrite_confirmation_title);
        String message = getString(R.string.loadsave_import_overwrite_confirmation);

        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        int amount = alreadyExistingFiles.size();

        Context context = AndorsTrailApplication.getApplicationFromActivity(this).getApplicationContext();

        for (int i = 0; i < amount && i < 3; i++) {
            DocumentFile alreadyExistingFile = alreadyExistingFiles.get(i);
            String alreadyExistingFileName = alreadyExistingFile.getName();
            FileHeader fileHeader = Savegames.quickload(context, getSlotFromSavegameFileName(alreadyExistingFileName));
            sb.append('\n');
            String fileHeaderDesription = "";
            if (fileHeader != null)
                fileHeaderDesription = fileHeader.describe();

            sb.append(getString(R.string.loadsave_import_overwrite_confirmation_file_pattern, alreadyExistingFileName, fileHeaderDesription));
//            sb.append(alreadyExistingFile.getName());
        }
        if (amount > 3) {
            sb.append("\n...");
        }
        message = message + sb;
        final Dialog d = CustomDialogFactory.createDialog(this,
                title,
                getResources().getDrawable(android.R.drawable.ic_dialog_alert),
                message,
                null,
                true);

        CustomDialogFactory.addButton(d, android.R.string.yes, v -> newFiles.addAll(alreadyExistingFiles));
        CustomDialogFactory.addDismissButton(d, android.R.string.no);
        CustomDialogFactory.setDismissListener(d, dialog -> {
            importSaveGames(resolver, appSavegameFolder, newFiles);
            completeLoadSaveActivity(SLOT_NUMBER_IMPORT_SAVEGAMES);
        });
        CustomDialogFactory.show(d);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK)
            return;

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

    //region show Dialogs

    private void showErrorImportingWorldmapWrongDirectory() {
        final Dialog d = CustomDialogFactory.createErrorDialog(this,
                getString(R.string.loadsave_import_worldmap_unsuccessfull),
                getString(R.string.loadsave_import_worldmap_unsuccessfull_wrong_directory));
        CustomDialogFactory.show(d);
    }

    private void showErrorImportingSaveGameUnknown() {
        final Dialog d = CustomDialogFactory.createErrorDialog(this,
                getString(R.string.loadsave_import_save_unsuccessfull),
                getString(R.string.loadsave_import_save_unsuccessfull_unknown));
        CustomDialogFactory.show(d);
    }

    private void showErrorLoadingEmptySlot() {
        final Dialog d = CustomDialogFactory.createErrorDialog(this,
                getString(R.string.startscreen_error_loading_game),
                getString(R.string.startscreen_error_loading_empty_slot));
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

    //endregion

}
