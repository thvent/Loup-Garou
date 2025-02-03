package org.garou.gui.utils;

import javax.swing.Icon;
import javax.swing.JLabel;

public class JResizableLabel extends JLabel {
    
    public JResizableLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
    }
    
    public JResizableLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }
    
    public JResizableLabel(String text) {
        super(text);
    }
    
    public JResizableLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
    }
    
    public JResizableLabel(Icon image) {
        super(image);
    }

}
