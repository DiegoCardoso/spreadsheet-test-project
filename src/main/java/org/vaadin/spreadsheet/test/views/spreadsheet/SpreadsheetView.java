package org.vaadin.spreadsheet.test.views.spreadsheet;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.spreadsheet.Spreadsheet;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spreadsheet.test.views.MainLayout;

@PageTitle("Spreadsheet")
@Route(value = "spreadsheet", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class SpreadsheetView extends VerticalLayout implements Receiver {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SpreadsheetView.class);

    private Spreadsheet spreadsheet;
    private File uploadedFile;
    private File previousFile;

    public SpreadsheetView() {
        setSpacing(true);
        setSizeFull();

        spreadsheet = new Spreadsheet();
        spreadsheet.setSizeFull();

        var upload = createUploadButton();
        var resetSheet = createResetButton();

        add(new HorizontalLayout(resetSheet, upload), spreadsheet);
    }

    private Button createResetButton() {
        var resetSheet = new Button("Reset spreadsheet",
                e -> spreadsheet.reset());
        return resetSheet;
    }

    private Upload createUploadButton() {
        var upload = new Upload(this);
        upload.addSucceededListener(event -> {
            loadFile(uploadedFile);
            upload.clearFileList();
        });
        return upload;
    }

    @Override
    public OutputStream receiveUpload(String fileName, String mimeType) {
        try {
            File file = new File(fileName);
            file.deleteOnExit();
            uploadedFile = file;
            FileOutputStream fos = new FileOutputStream(uploadedFile);
            return fos;
        } catch (FileNotFoundException e) {
            LOGGER.warn("ERROR reading file " + fileName, e);
        }
        return null;
    }

    private void loadFile(File file) {
        try {
            if (spreadsheet == null) {
                spreadsheet = new Spreadsheet(file);
                spreadsheet.setSizeFull();
            } else {
                if (previousFile == null || !previousFile.getAbsolutePath()
                        .equals(file.getAbsolutePath())) {
                    spreadsheet.read(file);
                }
            }
            previousFile = file;
        } catch (Exception e) {
            LOGGER.warn("ERROR reading file " + file, e);
        }
    }

}
