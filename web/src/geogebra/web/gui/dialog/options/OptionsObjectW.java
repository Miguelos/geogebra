package geogebra.web.gui.dialog.options;

import geogebra.web.main.AppW;
import geogebra.web.openjdk.awt.geom.Dimension;

public class OptionsObjectW extends
        geogebra.common.gui.dialog.options.OptionsObject implements OptionPanelW {

	public OptionsObjectW(AppW app) {
	    this.app = app;
	    
	    kernel = app.getKernel();
	    
	    // build GUI
	    initGUI();
    }

	private void initGUI() {
	    // TODO Auto-generated method stub
	    
    }

	public Dimension getPreferredSize() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public void setMinimumSize(Dimension preferredSize) {
	    // TODO Auto-generated method stub
	    
    }

	public void updateGUI() {
	    // TODO Auto-generated method stub
	    
    }

}
