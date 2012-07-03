package geogebra.web.gui.menubar;

import geogebra.common.main.AbstractApplication;
import geogebra.common.util.Language;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.Application;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class LanguageMenuW extends MenuBar {
	
	private AbstractApplication app;
	
	private MenuBar atoDMenuBar;
	private MenuBar etoIMenuBar;
	private MenuBar jtoQMenuBar;
	private MenuBar rtoZMenuBar;
	

	
	public LanguageMenuW(AbstractApplication app) {
		
		super(true);
		this.app = app;
		addStyleName("GeoGebraMenuBar");
	    initActions();		
	}
	
	private void initActions() {
		//add the sub-sub language menu list

		//add here sub-sub menu for language from A-D
		atoDMenuBar = new MenuBar(true);
		atoDMenuBar.addStyleName("GeoGebraMenuBar");
		addItem("A - D", atoDMenuBar);
		
		//add here sub-sub menu for language from E-I
		etoIMenuBar = new MenuBar(true);
		etoIMenuBar.addStyleName("GeoGebraMenuBar");
		addItem("E - I", etoIMenuBar);
		
		
		//add here sub-sub menu for language from J-Q
		jtoQMenuBar = new MenuBar(true);
		jtoQMenuBar.addStyleName("GeoGebraMenuBar");
		addItem("J - Q", jtoQMenuBar);

		//add here sub-sub menu for language from R-Z
		rtoZMenuBar = new MenuBar(true);
		rtoZMenuBar.addStyleName("GeoGebraMenuBar");
		addItem("R - Z", rtoZMenuBar);
		
		addItems();
	}
	
	
	
	private void addItems() {

		for(int i=0; i < Application.getSupportedLanguages().size(); i++) {			
			String languageCode = Application.getSupportedLanguages().get(i);

			String lang = Application.languageCodeVariationCrossReferencing(languageCode.replace(Application.AN_UNDERSCORE, ""));

			if (Language.isEnabledInGWT(lang)) {

				String languageName = Language.getDisplayName(lang);

				if(languageName != null) {

					char ch = languageName.toUpperCase().charAt(0);
					
					AbstractApplication.debug("Supported Languages: " + languageCode);										

					if(ch <= 'D') {

						atoDMenuBar.addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),languageName),true,new LanguageCommand(languageCode));
					} else if(ch <= 'I') {
						etoIMenuBar.addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),languageName),true,new LanguageCommand(languageCode));
					} else if(ch <= 'Q') {
						jtoQMenuBar.addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),languageName),true,new LanguageCommand(languageCode));
					} else {
						rtoZMenuBar.addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),languageName),true,new LanguageCommand(languageCode));
					}

				}
			}
		}

	}
}