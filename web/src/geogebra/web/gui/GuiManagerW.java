package geogebra.web.gui;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.dialog.DialogManager;
import geogebra.common.gui.view.properties.PropertiesView;
import geogebra.common.javax.swing.GTextComponent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.app.GGWMenuBar;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.dialog.ImageFileInputDialog;
import geogebra.web.gui.dialog.InputDialogOpenURL;
import geogebra.web.gui.inputbar.AlgebraInputW;
import geogebra.web.gui.inputbar.InputBarHelpPanelW;
import geogebra.web.gui.layout.LayoutW;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.gui.properties.PropertiesViewW;
import geogebra.web.gui.util.GeoGebraFileChooser;
import geogebra.web.gui.view.algebra.AlgebraController;
import geogebra.web.gui.view.algebra.AlgebraView;
import geogebra.web.main.Application;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.AbsolutePanel;

public class GuiManagerW extends GuiManager {

	private DialogManagerW dialogManager;

	protected Kernel kernel;

	private AlgebraController algebraController;
	private AlgebraView algebraView;

	private AbsolutePanel main;

	private int width;
	private int height;

	private LayoutW layout;

	public GuiManagerW(AbstractApplication app) {
		this.app = app;
		this.kernel = app.getKernel();
		
		//AGdialogManagerFactory = new DialogManager.Factory();
	}

	public void redo() {
		app.setWaitCursor();
		kernel.redo();
		updateActions();
		app.setDefaultCursor();
	}

	public void undo() {
		app.setWaitCursor();
		kernel.undo();
		updateActions();
		app.setDefaultCursor();
	}

	@Override
	public void removeSpreadsheetTrace(GeoElement recordObject) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}

	@Override
	public void updateMenubarSelection() {
		GGWMenuBar.getMenubar().updateSelection();

	}
	
	@Override
	public void updateMenubar() {
		GeoGebraMenubarW menuBar = GGWMenuBar.getMenubar();
		if (menuBar != null)
			menuBar.updateMenubar();
	}
	
	@Override
	public void updateActions() {
		GGWMenuBar.getMenubar().updateSelection();

	}

	@Override
	public DialogManager getDialogManager() {
		if (dialogManager == null) {
			Application.debug("unimplemented");
			// dialogManager = new DialogManagerWeb(app);
		}
		return dialogManager;
	}

	@Override
	public void showPopupMenu(ArrayList<GeoElement> selectedGeos,
			EuclidianViewInterfaceCommon euclidianViewInterfaceCommon, GPoint mouseLoc) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}
	

	@Override
	public void showPopupChooseGeo(ArrayList<GeoElement> selectedGeos,
			ArrayList<GeoElement> geos, EuclidianViewInterfaceCommon view,
			geogebra.common.awt.GPoint p) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");
		
	}

	@Override
	public void setFocusedPanel(AbstractEvent event, boolean updatePropertiesView) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}

	@Override
	public void loadImage(GeoPoint loc, Object object, boolean altDown) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

		app.setWaitCursor();

		ImageFileInputDialog dialog = new ImageFileInputDialog((Application) app, loc);
		dialog.setVisible(true);
	}

	@Override
	public void updateFonts() {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}

	@Override
	public boolean isInputFieldSelectionListener() {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");
		return false;
	}

	@Override
	public void addSpreadsheetTrace(GeoElement tracegeo) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}



	@Override
	public GTextComponent getAlgebraInputTextField() {
		AbstractApplication.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showDrawingPadPopup(EuclidianViewInterfaceCommon view,
			GPoint mouseLoc) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}

	@Override
	public boolean hasSpreadsheetView() {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");
		return false;
	}

	@Override
	public void attachSpreadsheetView() {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}

	@Override
	public void setShowView(boolean b, int viewSpreadsheet) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}
	
	@Override
	public void setShowView(boolean b, int viewSpreadsheet, boolean isPermanent) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}
	


	@Override
	public boolean showView(int viewSpreadsheet) {
		AbstractApplication.debug("unimplemented method");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getConstructionProtocolData() {
		AbstractApplication.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getCasView() {
		AbstractApplication.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getSpreadsheetView() {
		AbstractApplication.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getProbabilityCalculator() {
		AbstractApplication.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getPlotPanelView(int id) {
		AbstractApplication.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doAfterRedefine(GeoElement geo) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}

	@Override
	public void updateSpreadsheetColumnWidths() {
		// TODO Auto-generated method stub
		Application.debug("unimplemented");
	}

	public void resize(int width, int height) {
		this.width = width;
		this.height = height;

		// experimental resize of canvas
		//app.getEuclidianView1().setPreferredSize(width, height);
		AbstractApplication.debug("why not use Settings for that?");
	}

	public void setToolBarDefinition(String toolBarDefinition) {
		strCustomToolbarDefinition = toolBarDefinition;
	}

	public String getToolbarDefinition() {
		if (strCustomToolbarDefinition == null) {
			return geogebra.web.gui.toolbar.ToolBar.getAllTools((Application) app);
		}
		return strCustomToolbarDefinition;
	}

	/**
	 * Initializes GuiManager for web
	 */
	public void initialize() {
		initAlgebraController(); // ? needed for keyboard input in EuclidianView in Desktop
		
		layout.initialize(app);
		//do nothing yet
	    // TODO Auto-generated method stub
	    
    }
	
	public void setLayout(LayoutW layout) {
		this.layout = layout;
	}

	public LayoutW getLayout() {
		return layout;
	}
	
	private GGWToolBar toolbarPanel = null;

	private InputBarHelpPanelW inputHelpPanel;

	private AlgebraInputW algebraInput;
	
	public GGWToolBar getToolbarPanel() {
		if (toolbarPanel == null) {
			toolbarPanel = ((Application)app).getAppFrame().getGGWToolbar();
			toolbarPanel.init((Application)app);
		}

		return toolbarPanel;
	}
	
	public void updateToolbar() {
		if (toolbarPanel != null) {
			toolbarPanel.buildGui();
		}

		if (layout != null) {
			//AGlayout.getDockManager().updateToolbars();
			getToolbarPanel().updateToolbarPanel();
		}
	}

	@Override
    public void updateAlgebraInput() {
	   AbstractApplication.debug("Implementation needed...");
    }
	
	

	public InputBarHelpPanelW getInputHelpPanel() {
		if (inputHelpPanel == null)
			inputHelpPanel = new InputBarHelpPanelW(app);
		return inputHelpPanel;
    }

	public void openCommandHelp(String command) {
	   AbstractApplication.debug("Implementation needed...");
    }

	public void openHelp(String wikiManual) {
		AbstractApplication.debug("Implementation needed...");
    }

	public void setShowAuxiliaryObjects(boolean flag) {
		// TODO: auto-generated method stub
		if (!hasAlgebraView())
			return;
		//getAlgebraView();
		//algebraView.setShowAuxiliaryObjects(flag);
	}

	public AlgebraView getAlgebraView() {
		if (algebraView == null) {
			initAlgebraController();
			algebraView = newAlgebraView(algebraController);
			//if (!app.isApplet()) {
				// allow drag & drop of files on algebraView
			//	algebraView.setDropTarget(new DropTarget(algebraView,
			//			new FileDropTargetListener(app)));
			//}
		}

		return algebraView;
	}

	private void initAlgebraController() {
		if (algebraController == null) {
			algebraController = new AlgebraController(app.getKernel());
		}
	}

	/**
	 * 
	 * @param algc
	 * @return new algebra view
	 */
	protected AlgebraView newAlgebraView(AlgebraController algc) {
		//if (USE_COMPRESSED_VIEW) {
		//	return new CompressedAlgebraView(algc, CV_UPDATES_PER_SECOND);
		//}
		return new AlgebraView(algc);
	}

	public void attachAlgebraView() {
		getAlgebraView();
		algebraView.attachView();
	}

	public void detachAlgebraView() {
		if (algebraView != null)
			algebraView.detachView();
	}
	
	public void setMode(int mode) {

		// can't move this after otherwise Object Properties doesn't work
		kernel.notifyModeChanged(mode);

		// select toolbar button, returns *actual* mode selected
//		int newMode = setToolbarMode(mode);
//		
//		if (mode != EuclidianConstants.MODE_SELECTION_LISTENER && newMode != mode) {
//			mode = newMode;
//			kernel.notifyModeChanged(mode);
//		}
	}
	
	private PropertiesView propertiesView;

	@Override
	public View getPropertiesView() {

		if (propertiesView == null) {
			// initPropertiesDialog();
			propertiesView = new PropertiesViewW((Application) app);
		}

		return propertiesView;
	}

	@Override
    public void updatePropertiesView() {
	    // TODO Auto-generated method stub
	    
    }
	

	@Override
	public void mousePressedForPropertiesView(){
		if(propertiesView !=null){
			propertiesView.mousePressedForPropertiesView();
		}
	}

	@Override
    public void mouseReleasedForPropertiesView(boolean creatorMode) {
	    // TODO Auto-generated method stub
	    
    }

	public void addAlgebraInput(AlgebraInputW ai) {
	    this.algebraInput = ai;
    }

	public AlgebraInputW getAlgebraInput() {
	    return algebraInput;
    }

	@Override
    public boolean save() {
		app.setWaitCursor();
		//String fileName = Window.prompt("File name", "Bunny");
		//do saving here if getBase64 will be good
		GeoGebraFileChooser fileChooser = ((DialogManagerW)app.getDialogManager()).getFileChooser();
		if (((Application) app).getFileName() != null) {
			fileChooser.setFileName(((Application) app).getFileName());
		} else {
			fileChooser.setFileName("");
		}
		
		if (((Application)app).getFileDescription() != null) {
			fileChooser.setDescription(((Application)app).getFileDescription());
		} else {
			fileChooser.setDescription("");
		}
		fileChooser.show();
	    return true;
    }

	@Override
	public void showPropertiesViewSliderTab(){
		// TODO Auto-generated method stub
	}

	@Override
    public void openURL() {
		InputDialogOpenURL id = new InputDialogOpenURL((Application)app);
		id.setVisible(true);	    
    }
	
	@Override
    protected boolean loadURL_GGB(String url){
		((Application)app).getAppFrame().fileLoader.getView().processFileName(url);
		return true;
	}
	
	@Override
    protected boolean loadURL_base64(String url){
    	// TODO Auto-generated method stub
		AbstractApplication.debug("implementation needed");  
		return true;
	}

	@Override
    protected boolean loadFromApplet(String url) throws Exception {
	    // TODO Auto-generated method stub
		AbstractApplication.debug("implementation needed");
	    return false;
    }

	@Override
    public void updateGUIafterLoadFile(boolean success, boolean isMacroFile) {
	    AbstractApplication.debug("unimplemented");
	    
    }

	@Override
    public void startEditing(GeoElement geoElement) {
	    AbstractApplication.debug("unimplemented");
	    
    }

	@Override
    public boolean noMenusOpen() {
	    AbstractApplication.debug("unimplemented");
	    return true;
    }

	@Override
    public void openFile() {
	    AbstractApplication.debug("unimplemented");
    }
}