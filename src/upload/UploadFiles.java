package upload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import static upload.InputTypes.REGULAR;

public class UploadFiles {
    public static void uploadFile(File sourceFile) throws IOException {
        File targetDirectoryFile = new File("reasoner-input/");
        File targetFile = new File(targetDirectoryFile, sourceFile.getName());
        Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static ArrayList<SimpleInput> getAllInputFiles() {
        ArrayList<SimpleInput> res = new ArrayList<>();
        File directory = new File("reasoner-input/");
        for (String fileName : directory.list()) {
            String name = getFileNameWithoutExtension(fileName);
            String extension = getFileExtension(fileName);
            if(!extension.equals(".gitkeep")) {
                res.add(new SimpleInput(REGULAR, name, extension));
            }
        }
        return res;
    }

    private static String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return fileName.substring(0, lastDotIndex);
        }
        return "unknown";
    }

    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return fileName.substring(lastDotIndex);
        }
        return "unknown";
    }
}
