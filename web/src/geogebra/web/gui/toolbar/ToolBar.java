package geogebra.web.gui.toolbar;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.gui.toolbar.ToolbarItem;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.main.App;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.main.AppW;

import java.util.ArrayList;
import java.util.Vector;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;



/**
 * @author gabor
 * 
 * Toolbar for GeoGebraWeb
 *
 */
public class ToolBar extends MenuBar {
	
	/**
	 * Integer used to indicate a separator in the toolbar.
	 */
	public static final Integer SEPARATOR = new Integer(-1);
	
	private AppW app;
	private int mode;

	private ArrayList<ModeToggleMenu> modeToggleMenus;
	
//	public ToolBar(Application app) {
//		this.app = app;
//	}
	
	/**
	 * Creates general toolbar.
	 * There is no app parameter here, because of UiBinder.
	 * After instantiate the ToolBar, call init(Application app) as well.
	 */
	public ToolBar() {
		super();
		setFocusOnHoverEnabled(false);
		this.setHeight("55px");  //toolbar's height
	}
	
	/**
	 * Initialisation of the ToolBar object
	 * 
	 * @param app
	 */
	public void init(AppW app){
		this.app = app;
	}
	
	/**
	 * Creates a toolbar using the current strToolBarDefinition.
	 */
	public void buildGui() {
		mode = -1;
		
		ModeToggleButtonGroup bg = new ModeToggleButtonGroup();
		modeToggleMenus = new ArrayList<ModeToggleMenu>();
		
		clearItems();
		
		addCustomModesToToolbar(bg);
		
		setMode(app.getMode());
		
	}
	
	/**
	 * Sets toolbar mode. This will change the selected toolbar icon.
	 * @param newMode see EuclidianConstants for mode numbers
	 * 
	 * 
	 * @return actual mode number selected (might be different if it's not available)
	 */
	public int setMode(int newMode) {
		boolean success = false;
		int tmpMode = newMode;
		// there is no special icon/button for the selection listener mode, use
		// the
		// move mode button instead
		if (tmpMode == EuclidianConstants.MODE_SELECTION_LISTENER) {
			tmpMode = EuclidianConstants.MODE_MOVE;
		}

		if (modeToggleMenus != null) {
			for (int i = 0; i < modeToggleMenus.size(); i++) {
				ModeToggleMenu mtm = modeToggleMenus.get(i);
				if (mtm.selectMode(tmpMode)) {
					success = true;
					break;
				}
			}


			if (!success) {
					mode = setMode(getFirstMode());
				
			}
			
			this.mode = tmpMode;

		}

		return tmpMode;
	}

	/**
	 * @return currently selected mode
	 */
	public int getSelectedMode() {
		return mode;
	}
	
	/**
	 * @return first mode in this toolbar
	 */
	public int getFirstMode() {
		if (modeToggleMenus == null || modeToggleMenus.size() == 0) {
			return -1;
		}
		ModeToggleMenu mtm = modeToggleMenus.get(0);
		return mtm.getFirstMode();
	}

	
	/**
	 * Adds the given modes to a two-dimensional toolbar. The toolbar definition
	 * string looks like "0 , 1 2 | 3 4 5 || 7 8 9" where the int values are
	 * mode numbers, "," adds a separator within a menu, "|" starts a new menu
	 * and "||" adds a separator before starting a new menu.
	 * 
	 */
	private void addCustomModesToToolbar(ModeToggleButtonGroup bg) {
		Vector<ToolbarItem> toolbarVec;
		
		try {
			//AGif (dockPanel != null) {
			//AG	toolbarVec = parseToolbarString(dockPanel.getToolbarString());
			//AG} else {
				toolbarVec = parseToolbarString(app.getGuiManager()
						.getToolbarDefinition());
			//AG}
		} catch (Exception e) {
			//AGif (dockPanel != null) {
			//AG	AbstractApplication.debug("invalid toolbar string: "
			//AG			+ dockPanel.getToolbarString());
			//AG} else {
				App.debug("invalid toolbar string: "
						+ app.getGuiManager().getToolbarDefinition());
			//}
			toolbarVec = parseToolbarString(getDefaultToolbarString());
		}
		
		// set toolbar
		for (int i = 0; i < toolbarVec.size(); i++) {
			ToolbarItem ob = toolbarVec.get(i);

			// separator between menus
			if (ob.getMode() == ToolBar.SEPARATOR) {
				addSeparator();
				continue;
			}

			// new menu
			Vector<Integer> menu = ob.getMenu();
			final ModeToggleMenu tm = new ModeToggleMenu(app, this, bg);
			modeToggleMenus.add(tm);

			for (int k = 0; k < menu.size(); k++) {
				// separator
				int addMode = menu.get(k).intValue();
				if (addMode < 0) {
					// separator within menu:
					tm.addSeparator();
				} else { // standard case: add mode

					// check mode
					if (!"".equals(app.getToolName(addMode))) {
						Command com = null;
						String item_text = "<table><tr><td>"
						        + GGWToolBar.getImageHtml(addMode)
						        + "</td><td><span class=\"toolbar_menuitem_label\">"
						        + app.getToolName(addMode)
						        + "</span></td></tr></table>";
						final MenuItem item = new MenuItem(item_text, true, com);
						com = new Command(){
							public void execute() {
								tm.selectMenuItem(item);
                            }
						};
						item.setCommand(com);
						item.addStyleName("toolbar_menuitem");
						item.getElement().setAttribute("mode", addMode+"");
						//item.setStyleName("toolbar_menuitem");
						tm.addItem(item);
						//tm.addItem(GGWToolBar.getImageHtml(addMode)+app.getToolName(addMode), true, com);
						
						tm.addMode(addMode);
					}
				}
			}

			
			if (tm.getToolsCount() > 0){
			//	tm.setButton(newItem);
				addItem(tm.getButton());
			}
		}
    }
	
	/**
	 * Parses a toolbar definition string like "0 , 1 2 | 3 4 5 || 7 8 9" where
	 * the int values are mode numbers, "," adds a separator within a menu, "|"
	 * starts a new menu and "||" adds a separator before starting a new menu.
	 * 
	 * @param toolbarString
	 *            toolbar definition string
	 * 
	 * @return toolbar as nested Vector objects with Integers for the modes.
	 *         Note: separators have negative values.
	 */
	public static Vector<ToolbarItem> parseToolbarString(String toolbarString) {
		String[] tokens = toolbarString.split(" ");
		Vector<ToolbarItem> toolbar = new Vector<ToolbarItem>();
		Vector<Integer> menu = new Vector<Integer>();

		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].equals("|")) { // start new menu
				if (menu.size() > 0)
					toolbar.add(new ToolbarItem(menu));
				menu = new Vector<Integer>();
			} else if (tokens[i].equals("||")) { // separator between menus
				if (menu.size() > 0)
					toolbar.add(new ToolbarItem(menu));

				// add separator between two menus
				// menu = new Vector();
				// menu.add(SEPARATOR);
				// toolbar.add(menu);
				toolbar.add(new ToolbarItem(SEPARATOR));

				// start next menu
				menu = new Vector<Integer>();
			} else if (tokens[i].equals(",")) { // separator within menu
				menu.add(SEPARATOR);
			} else { // add mode to menu
				try {
					if (tokens[i].length() > 0) {
						int mode = Integer.parseInt(tokens[i]);
						menu.add(new Integer(mode));
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}

		// add last menu to toolbar
		if (menu.size() > 0)
			toolbar.add(new ToolbarItem(menu));
		return toolbar;
	}

	
	
	/**
	 * @return The default definition of this toolbar with macros.
	 */
	public String getDefaultToolbarString() {
		//AGif (dockPanel != null) {
		//AG	return dockPanel.getDefaultToolbarString();
		//AG}
		return ToolBar.getAllTools(app);
	}

	

	/**
	 * @param app
	 * @return All tools as a toolbar definition string
	 */
	public static String getAllTools(AppW app) {
		StringBuilder sb = new StringBuilder();
	
		sb.append(geogebra.common.gui.toolbar.ToolBar.getAllToolsNoMacros(false, true));
	
		// macros
		Kernel kernel = app.getKernel();
		int macroNumber = kernel.getMacroNumber();
	
		// check if at least one macro is shown
		// to avoid strange GUI
		boolean at_least_one_shown = false;
		for (int i = 0; i < macroNumber; i++) {
			Macro macro = kernel.getMacro(i);
			if (macro.isShowInToolBar()) {
				at_least_one_shown = true;
				break;
			}
		}
	
		if (macroNumber > 0 && at_least_one_shown) {
			sb.append(" || ");
			for (int i = 0; i < macroNumber; i++) {
				Macro macro = kernel.getMacro(i);
				if (macro.isShowInToolBar()) {
					sb.append(i + EuclidianConstants.MACRO_MODE_ID_OFFSET);
					sb.append(" ");
				}
			}
		}
	
		return sb.toString();
	}
	
	public ArrayList<ModeToggleMenu> getModeToggleMenus(){
		return modeToggleMenus;
	}

	@Override
	public void onBrowserEvent(Event event) {
		MenuItem item = findItem(DOM.eventGetTarget(event));

		if ((DOM.eventGetType(event) == Event.ONCLICK) && (item != null)
		        && !((ModeToggleMenu.MyJToggleButton)item).isTriangleHighlighted()) {
			((ModeToggleMenu) item.getSubMenu()).selectMenuItem(item);

		} else
			super.onBrowserEvent(event);
	}

	
	private MenuItem findItem(Element hItem) {
		for (MenuItem item : getItems()) {
			if (DOM.isOrHasChild(item.getElement(), hItem)) {
				return item;
			}
		}
		return null;
	}
}
