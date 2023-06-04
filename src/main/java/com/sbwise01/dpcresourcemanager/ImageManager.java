package com.sbwise01.dpcresourcemanager;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author bwise
 */
public class ImageManager {
    private static final String BASE_IMAGE_URL = "http://192.168.10.101/dpcimages/";
    private static final String BASE_IMAGE_PATH = System.getenv("HOME") + "/VirtualBox VMs/images";

    public static String getImageName(String imageName) throws IOException, InterruptedException {
        String imageFileName = imageName + ".vdi";
        String imagePath = BASE_IMAGE_PATH + "/" + imageFileName;
        String imageUrl = BASE_IMAGE_URL + imageFileName;

        // Check that local image cache path exists, if not create it
        File cachePath = new File(BASE_IMAGE_PATH);
        if (!cachePath.exists()) {
            cachePath.mkdir();
        }

        // Check that local image exists, if not fetch it
        File image = new File(imagePath);
        if (!image.exists()) {
            ProcessOutput po = ProcessOutput.runCommand("curl","-s","-o",imagePath,imageUrl);
        }

        return imagePath;
    }
    
    public static String getOsType(String imageName) {
        String osType;

        if (imageName.contains("Centos")) {
            osType = "RedHat_64";
        } else if (imageName.contains("Ubuntu")) {
            osType = "Ubuntu_64";
        } else {
            osType = "default";
        }

        return osType;
    }
}
