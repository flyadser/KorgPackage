package polprzewodnikowy.korgpkgedit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import polprzewodnikowy.korgpkg.Chunk;
import polprzewodnikowy.korgpkg.PackageReader;
import polprzewodnikowy.korgpkg.PackageWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by korgeaux on 31.05.2016.
 */
public class MainController {

    private Stage stage;
    public ListView chunksListView;
    public Label statusLabel;

    public void setup(Stage stage) {
        this.stage = stage;
    }

    public void refreshList() {
        ObservableList<Chunk> chunks = chunksListView.getItems();
        chunksListView.setItems(null);
        chunksListView.setItems(chunks);
    }

    public void openPkgAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open pkg File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PKG file", "*.pkg"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            List<Chunk> chunks = new PackageReader(file).load();
            chunksListView.setItems(FXCollections.observableList(chunks));
            statusLabel.setText("Finished reading package");
        }
    }

    public void savePkgAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save pkg File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PKG file", "*.pkg"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            PackageWriter packageWriter = new PackageWriter(file);
            List<Chunk> chunks = new ArrayList<>(chunksListView.getItems());
            packageWriter.save(chunks);
            statusLabel.setText("Finished saving package");
        }
    }

    public void closeAppAction() {
        stage.close();
    }

    public void editChunkAction() {
        Chunk chunk = (Chunk) chunksListView.getSelectionModel().getSelectedItem();
        if (chunk != null) {
            String view;
            switch (chunk.getId()) {
                case Chunk.HEADER:
                    view = "HeaderEditWindow.fxml";
                    break;
                case Chunk.UPDATE_KERNEL:
                case Chunk.UPDATE_RAMDISK:
                case Chunk.UPDATE_INSTALLER_APP:
                case Chunk.UPDATE_INSTALLER_APP_CONFIG:
                case Chunk.SERVICE_KERNEL:
                case Chunk.SERVICE_RAMDISK:
                case Chunk.SERVICE_APP:
                case Chunk.SERVICE_APP_CONFIG:
                case Chunk.UPDATE_LAUNCHER_APP:
                case Chunk.UPDATE_LAUNCHER_APP_CONFIG:
                case Chunk.MLO:
                case Chunk.UBOOT:
                case Chunk.USER_KERNEL:
                    view = "DataEditWindow.fxml";
                    break;
                case Chunk.INSTALLER_SCRIPT:
                    view = "InstallerScriptEditWindow.fxml";
                    break;
                case Chunk.DIRECTORY:
                    view = "DirectoryEditWindow.fxml";
                    break;
                case Chunk.FILE:
                    view = "FileEditWindow.fxml";
                    break;
                case Chunk.ROOT_FS:
                    view = "RootFSEditWindow.fxml";
                    break;
                default:
                    return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(view));
                Parent root = loader.load();
                ChunkEditController controller = loader.getController();
                Stage editWindow = new Stage();
                controller.setup(editWindow, chunk);
                editWindow.setScene(new Scene(root));
                editWindow.setResizable(false);
                editWindow.initModality(Modality.APPLICATION_MODAL);
                editWindow.showAndWait();
                refreshList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exportChunkAction() {
        Chunk chunk = (Chunk) chunksListView.getSelectionModel().getSelectedItem();
        if (chunk != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose directory");
            File dir = directoryChooser.showDialog(stage);
            if (dir != null) {
                try {
                    ObservableList<Chunk> chunks = chunksListView.getSelectionModel().getSelectedItems();
                    for (Chunk c : chunks) {
                        c.export(dir.getPath());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeChunkAction() {
        Chunk chunk = (Chunk) chunksListView.getSelectionModel().getSelectedItem();
        if (chunk != null) {
            chunksListView.getItems().removeAll(chunksListView.getSelectionModel().getSelectedItems());
        }
    }

    public void showAboutAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("AboutWindow.fxml"));
            Parent root = loader.load();
            Stage editWindow = new Stage();
            editWindow.setScene(new Scene(root));
            editWindow.setResizable(false);
            editWindow.initModality(Modality.APPLICATION_MODAL);
            editWindow.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
