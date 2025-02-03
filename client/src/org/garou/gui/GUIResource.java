
package org.garou.gui;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.garou.model.Role;

public class GUIResource {

    private GUIResource() {}


    public static Image getRolePicture(Role role) throws IOException {
        return getImage(role.getName() + ".png");
    }

    public static Image getImage(String file) throws IOException {
        return ImageIO.read(GUIResource.class.getResource("/assets/imgs/" + file));
    }

}