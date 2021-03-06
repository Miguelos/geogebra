package geogebra.web.main;

import geogebra.common.GeoGebraConstants;
import geogebra.common.awt.GBufferedImage;
import geogebra.common.awt.GFont;
import geogebra.common.euclidian.DrawEquationInterface;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.factories.CASFactory;
import geogebra.common.factories.Factory;
import geogebra.common.factories.SwingFactory;
import geogebra.common.gui.menubar.MenuInterface;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.kernel.AnimationManager;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.UndoManager;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.main.FontManager;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.common.main.MyError;
import geogebra.common.main.SpreadsheetTableModel;
import geogebra.common.main.settings.Settings;
import geogebra.common.plugin.ScriptManager;
import geogebra.common.plugin.jython.PythonBridge;
import geogebra.common.sound.SoundManager;
import geogebra.common.util.AbstractImageManager;
import geogebra.common.util.GeoGebraLogger.LogDestination;
import geogebra.common.util.Language;
import geogebra.common.util.MD5EncrypterGWTImpl;
import geogebra.common.util.NormalizerMinimal;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;
import geogebra.web.css.GuiResources;
import geogebra.web.euclidian.EuclidianControllerW;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.SplashDialog;
import geogebra.web.gui.app.GGWCommandLine;
import geogebra.web.gui.app.GGWMenuBar;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.applet.GeoGebraFrame;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.infobar.InfoBarW;
import geogebra.web.gui.inputbar.AlgebraInputW;
import geogebra.web.gui.layout.panels.EuclidianDockPanelW;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.gui.menubar.LanguageCommand;
import geogebra.web.gui.view.algebra.AlgebraViewW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetTableModelW;
import geogebra.web.helper.JavaScriptInjector;
import geogebra.web.helper.ScriptLoadCallback;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.DynamicScriptElement;
import geogebra.web.io.ConstructionException;
import geogebra.web.io.MyXMLio;
import geogebra.web.javax.swing.GOptionPaneW;
import geogebra.web.kernel.AnimationManagerW;
import geogebra.web.kernel.KernelW;
import geogebra.web.kernel.UndoManagerW;
import geogebra.web.util.GeoGebraLogger;
import geogebra.web.util.ImageManager;
import geogebra.web.util.MyDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.Set;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AppW extends App {

	/**
	 * Constants related to internationalization
	 * 
	 */
	public final static String DEFAULT_LANGUAGE = "en";
	public final static String DEFAULT_LOCALE = "default";
	public final static String A_DOT = ".";
	public final static String AN_UNDERSCORE = "_";

	/*
	 * The representation of no_NO_NY (Norwegian Nynorsk) is illegal in a BCP47
	 * language tag: it should actually use "nn" (Norwegian Nynorsk) for the
	 * language field
	 * 
	 * @Ref:
	 * https://sites.google.com/site/openjdklocale/design-specification#TOC
	 * -Norwegian
	 */
	public final static String LANGUAGE_NORWEGIAN_NYNORSK = "no_NO_NY"; // Nynorsk
																		// Norwegian
																		// language
																		// Java
																		// Locale
	public final static String LANGUAGE_NORWEGIAN_NYNORSK_BCP47 = "nn"; // Nynorsk
																		// Norwegian
																		// language
																		// BCP47

	public final static String syntaxStr = "_Syntax";

	public static String geoIPCountryName;
	public static String geoIPLanguage;

	private FontManagerW fontManager;

	private boolean[] showAxes = { true, true };
	private boolean showGrid = false;

	protected ImageManager imageManager;

	private EuclidianDockPanelW euclidianViewPanel;
	private Canvas canvas;
	private geogebra.common.plugin.GgbAPI ggbapi;
	private HashMap<String, String> currentFile = null;
	private static LinkedList<Map<String, String>> fileList = new LinkedList<Map<String, String>>();

	private ArticleElement articleElement;
	private GeoGebraFrame frame;
	private GeoGebraAppFrame appFrame;

	private String ORIGINAL_BODY_CLASSNAME = "";

	private HashMap<String, String> englishCommands = null;

	private SpreadsheetTableModelW tableModel;

	// convenience method
	public AppW(ArticleElement ae, GeoGebraFrame gf) {
		this(ae, gf, true);
	}

	private static void startLogger() {
		logger = new GeoGebraLogger();
		logger.setLogDestination(LogDestination.CONSOLES);
		logger.setLogLevel(Window.Location.getParameter("logLevel"));
	}

	/**
	 * @param undoActive
	 *            if true you can undo by CTRL+Z and redo by CTRL+Y
	 */
	public AppW(ArticleElement ae, GeoGebraFrame gf, final boolean undoActive) {
		this.articleElement = ae;
		this.frame = gf;
		createSplash();
		this.useFullGui = ae.getDataParamGui();
		if (ae.getDataParamShowLogging())
			startLogger();
		infobar = new InfoBarW(this);
		
		info("GeoGebra " + GeoGebraConstants.VERSION_STRING + " "
		        + GeoGebraConstants.BUILD_DATE + " "
		        + Window.Navigator.getUserAgent());
		initCommonObjects();

		euclidianViewPanel = new EuclidianDockPanelW(false);
		this.canvas = euclidianViewPanel.getCanvas();
		canvas.setWidth("1px");
		canvas.setHeight("1px");
		canvas.setCoordinateSpaceHeight(1);
		canvas.setCoordinateSpaceWidth(1);
		initing = true;
		initCoreObjects(undoActive, this);
	}

	private void afterCoreObjectsInited() {
		if (appFrame != null) {
			initGuiManager();
			getGuiManager().getLayout().setPerspectives(tmpPerspectives);

			getSettings().getEuclidian(1).setPreferredSize(
			        geogebra.common.factories.AwtFactory.prototype.newDimension(
			                appCanvasWidth, appCanvasHeight));
			getEuclidianView1().setDisableRepaint(false);
			getEuclidianView1().synCanvasSize();
			getEuclidianView1().doRepaint2();
			appFrame.finishAsyncLoading(articleElement, appFrame, this);
		} else if (frame != null) {
			frame.finishAsyncLoading(articleElement, frame, this);
			initing = false;
		}
    }

	// This is called for the full GUI based GeoGebraWeb --- Zoltan
	public AppW(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame,
	        boolean undoActive) {
		this.articleElement = article;
		this.appFrame = geoGebraAppFrame;
		createAppSplash();
		this.useFullAppGui = true;
		appCanvasHeight = appFrame.getCanvasCountedHeight();
		appCanvasWidth = appFrame.getCanvasCountedWidth();

		setCurrentFileId();
		if (article.getDataParamShowLogging())
			startLogger();
		infobar = new InfoBarW(this);

		initCommonObjects();

		this.canvas = appFrame.getEuclidianView1Canvas();
		this.euclidianViewPanel = appFrame.getEuclidianView1Panel();

		initCoreObjects(undoActive, this);

		// initing = true;
	}

	public AppW(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame) {
		this(article, geoGebraAppFrame, true);
	}

	private static ArrayList<String> supportedLanguages = null;

	/**
	 * @return ArrayList of languages suitable for GWT, eg "en", "de_AT"
	 */
	public static ArrayList<String> getSupportedLanguages() {

		if (supportedLanguages != null) {
			return supportedLanguages;
		}

		supportedLanguages = new ArrayList<String>();

		Language[] languages = Language.values();

		for (int i = 0; i < languages.length; i++) {

			Language language = languages[i];

			if (language.fullyTranslated || GeoGebraConstants.IS_PRE_RELEASE) {
				supportedLanguages.add(language.localeGWT);
			}
		}

		return supportedLanguages;

	}


	/**
	 * This method was supposed to change the initial language depending on the
	 * GeoIP of the user-agent.
	 */
	public void initializeLanguage() {

		// App.debug("GeoIP Country: " + AppW.geoIPCountryName);
		// App.debug("GeoIP Language: " + AppW.geoIPLanguage);
		//
		// App.debug("Test closeset language: " +
		// Language.getClosestGWTSupportedLanguage(AppW.geoIPLanguage));

		// initially change the language to a one that comes from GeoIP.
		setDefaultLanguage();
	}

	/**
	 * 
	 */
	public void setDefaultLanguage() {
		// App.debug("Browser Language: " + AppW.geoIPLanguage);

		String[] localeNames = LocaleInfo.getAvailableLocaleNames();
		for (int i = 0; i < localeNames.length; i++) {
			App.debug("Locale Name: " + localeNames[i]);
		}

		String lCookieName = LocaleInfo.getLocaleCookieName();
		String lCookieValue = null;
		if (lCookieName != null) {
			lCookieValue = Cookies.getCookie(lCookieName);
		}
		String currentLanguage = LocaleInfo.getCurrentLocale().getLocaleName();
		String closestlangcodetoGeoIP = Language
		        .getClosestGWTSupportedLanguage(AppW.geoIPLanguage);

		App.debug("Cookie Value: " + lCookieValue + ", currentLanguage: "
		        + currentLanguage + ", Language from GeoIP: "
		        + AppW.geoIPLanguage + ", closest Language from GeoIP: "
		        + closestlangcodetoGeoIP);

		if (Language.isEnabledInGWT(closestlangcodetoGeoIP)) {

			App.debug("Language is enabeled!!!");

			if (lCookieValue == null
			        && currentLanguage != closestlangcodetoGeoIP
			        && !AppW.DEFAULT_LANGUAGE.equals(currentLanguage)) {

				App.debug("Changing Language depending on GeoIP!");

				// Window.Location.assign( // or replace()
				// Window.Location.createUrlBuilder()
				// .setParameter(LocaleInfo.getLocaleQueryParam(), "ar")
				// .buildString());

				UrlBuilder newUrl = Window.Location.createUrlBuilder();
				newUrl.setParameter(LanguageCommand.LOCALE_PARAMETER,
				        closestlangcodetoGeoIP);
				Window.Location.assign(newUrl.buildString());

				Cookies.removeCookie(lCookieName);
				Cookies.setCookie(lCookieName, closestlangcodetoGeoIP);

			}

		}

	}

	@Override
    public void setUndoActive(boolean flag) {
		// don't allow undo when running with restricted permissions
		/*
		 * if (flag && !hasFullPermissions) { flag = false; }
		 */

		if (kernel.isUndoActive() == flag) {
			return;
		}

		kernel.setUndoActive(flag);
		if (flag) {
			kernel.initUndoInfo();
		}

		if (guiManager != null) {
			getGuiManager().updateActions();
		}

		// isSaved = true;
	}

	public ArticleElement getArticleElement() {
		return articleElement;
	}

	public GeoGebraFrame getGeoGebraFrame() {
		return frame;
	}

	/**
	 * Register file drop handlers for the canvas of this application
	 */
	native void registerFileDropHandlers(CanvasElement ce) /*-{

		var appl = this;
		var canvas = ce;

		if (canvas) {
			canvas.addEventListener("dragover", function(e) {
				e.preventDefault();
				e.stopPropagation();
				canvas.style.borderColor = "#ff0000";
			}, false);
			canvas.addEventListener("dragenter", function(e) {
				e.preventDefault();
				e.stopPropagation();
			}, false);
			canvas
					.addEventListener(
							"drop",
							function(e) {
								e.preventDefault();
								e.stopPropagation();
								canvas.style.borderColor = "#000000";
								var dt = e.dataTransfer;
								if (dt.files.length) {
									var fileToHandle = dt.files[0];
									var imageRegEx = /\.(png|jpg|jpeg|gif)$/i;
									var ggbRegEx = /\.(ggb|ggt)$/i;
									if (fileToHandle.name.toLowerCase().match(
											imageRegEx)) {
										var reader = new FileReader();
										reader.onloadend = function(ev) {
											if (reader.readyState === reader.DONE) {
												var reader2 = new FileReader();
												var base64result = reader.result;
												reader2.onloadend = function(
														eev) {
													if (reader2.readyState === reader2.DONE) {
														var fileStr = base64result;
														var fileStr2 = reader2.result;
														var fileName = fileToHandle.name;
														appl.@geogebra.web.main.AppW::imageDropHappened(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lgeogebra/common/kernel/geos/GeoPoint;)(fileName, fileStr, fileStr2, null);
													}
												}
												reader2
														.readAsBinaryString(fileToHandle);
											}
										};
										reader.readAsDataURL(fileToHandle);
									} else if (fileToHandle.name.toLowerCase()
											.match(ggbRegEx)) {
										var reader = new FileReader();
										reader.onloadend = function(ev) {
											if (reader.readyState === reader.DONE) {
												var fileStr = reader.result;
												appl.@geogebra.web.main.AppW::loadGgbFileAgain(Ljava/lang/String;)(fileStr);
											}
										};
										reader.readAsDataURL(fileToHandle);
									}
									//console.log(fileToHandle.name);
								} else {
									// This would raise security exceptions later - see ticket #2301
									//var gdat = dt.getData("URL");
									//if (gdat && gdat != " ") {
									//	var coordx = e.offsetX ? e.offsetX : e.layerX;
									//	var coordy = e.offsetY ? e.offsetY : e.layerY;
									//	appl.@geogebra.web.main.AppW::urlDropHappened(Ljava/lang/String;II)(gdat, coordx, coordy);
									//}
								}
							}, false);
		}
		$doc.body.addEventListener("dragover", function(e) {
			e.preventDefault();
			e.stopPropagation();
			if (canvas)
				canvas.style.borderColor = "#000000";
		}, false);
		$doc.body.addEventListener("drop", function(e) {
			e.preventDefault();
			e.stopPropagation();
		}, false);
	}-*/;

	/**
	 * Loads an image and puts it on the canvas (this happens on webcam input)
	 * On drag&drop or insert from URL this would be called too, but that would
	 * set security exceptions
	 * 
	 * @param url
	 *            - the data url of the image
	 * @param clientx
	 *            - desired position on the canvas (x) - unused
	 * @param clienty
	 *            - desired position on the canvas (y) - unused
	 */
	public void urlDropHappened(String url, int clientx, int clienty) {

		// Filename is temporarily set until a better solution is found
		// TODO: image file name should be reset after the file data is
		// available

		MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
		String zip_directory = md5e.encrypt(url);

		// with dummy extension, maybe gif or jpg in real
		String imgFileName = zip_directory + ".png";

		String fn = imgFileName;
		int index = imgFileName.lastIndexOf('/');
		if (index != -1) {
			fn = fn.substring(index + 1, fn.length()); // filename without
		}
		// path
		fn = geogebra.common.util.Util.processFilename(fn);

		// filename will be of form
		// "a04c62e6a065b47476607ac815d022cc\liar.gif"
		imgFileName = zip_directory + '/' + fn;

		doDropHappened(imgFileName, url, null);
	}

	/**
	 * Loads an image and puts it on the canvas (this happens by drag & drop)
	 * 
	 * @param imgFileName
	 *            - the file name of the image
	 * @param fileStr
	 *            - the image data url
	 * @param fileStr2
	 *            - the image binary string
	 * @param clientx
	 *            - desired position on the canvas (x)
	 * @param clienty
	 *            - desired position on the canvas (y)
	 */
	public void imageDropHappened(String imgFileName, String fileStr,
	        String fileStr2, GeoPoint loc) {

		MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
		String zip_directory = md5e.encrypt(fileStr2);

		String fn = imgFileName;
		int index = imgFileName.lastIndexOf('/');
		if (index != -1) {
			fn = fn.substring(index + 1, fn.length()); // filename without
		}
		// path
		fn = geogebra.common.util.Util.processFilename(fn);

		// filename will be of form
		// "a04c62e6a065b47476607ac815d022cc\liar.gif"
		imgFileName = zip_directory + '/' + fn;

		doDropHappened(imgFileName, fileStr, loc);
	}

	private void doDropHappened(String imgFileName, String fileStr, GeoPoint loc) {

		Construction cons = getKernel().getConstruction();
		EuclidianViewInterfaceCommon ev = getActiveEuclidianView();
		((ImageManager) getImageManager()).addExternalImage(imgFileName,
		        fileStr);
		GeoImage geoImage = new GeoImage(cons);
		((ImageManager) getImageManager()).triggerSingleImageLoading(
		        imgFileName, geoImage);
		geoImage.setImageFileName(imgFileName);

		if (loc == null) {
			double cx = ev.getXmin() + (ev.getXmax() - ev.getXmin()) / 4;
			double cy = ev.getYmin() + (ev.getYmax() - ev.getYmin()) / 4;
			GeoPoint gsp = new GeoPoint(cons, cx, cy, 1);
			gsp.setLabel(null);
			gsp.setLabelVisible(false);
			gsp.update();
			geoImage.setCorner(gsp, 0);

			cx = ev.getXmax() - (ev.getXmax() - ev.getXmin()) / 4;
			GeoPoint gsp2 = new GeoPoint(cons, cx, cy, 1);
			gsp2.setLabel(null);
			gsp2.setLabelVisible(false);
			gsp2.update();
			geoImage.setCorner(gsp2, 1);
		} else {
			geoImage.setCorner(loc, 0);
		}

		geoImage.setLabel(null);
		GeoImage.updateInstances();

		// these things are done in Desktop GuiManager.loadImage too
		GeoElement[] geos = { geoImage };
		getActiveEuclidianView().getEuclidianController().clearSelections();
		getActiveEuclidianView().getEuclidianController()
		        .memorizeJustCreatedGeos(geos);
		setDefaultCursor();
	}

	@Override
	public String getCommand(String key) {

		if (key == null) {
			return "";
		}
		
		if (language == null) {
			// keys not loaded yet
			return key;
		}


		String ret = getPropertyNative(language, crossReferencingPropertiesKeys(key), "command");
		
		if (ret == null || "".equals(ret)) {
			App.debug("command key not found: "+key);
			return key;
		}
		
		return ret;

}

	/**
	 * This method checks if the command is stored in the command properties
	 * file as a key or a value.
	 * 
	 * @param command
	 *            : a value that should be in the command properties files (part
	 *            of Internationalization)
	 * @return the value "command" after verifying its existence.
	 */
	@Override
	final public String getReverseCommand(String command) {

		if (language == null) {
			// keys not loaded yet
			return command;
		}

		return super.getReverseCommand(command);
	}

	/**
	 * @author Rana This method should work for both if the getPlain and
	 *         getPlainTooltip. In the case of getPlainTooltip, if the
	 *         tooltipFlag is true, then getPlain is called.
	 */
	@Override
	public String getPlain(String key) {

		if (key == null) {
			return "";
		}
		
		if (language == null) {
			// keys not loaded yet
			return key;
		}

		if (language == null) {
			// keys not loaded yet
			return key;
		}

		String ret = getPropertyNative(language, crossReferencingPropertiesKeys(key), "plain");
		
		if (ret == null || "".equals(ret)) {
			App.debug("plain key not found: "+key+" "+ret);
			return key;
		}
		
		return ret;
	}

	/**
	 * @author Rana Cross-Referencing properties keys: from old system of
	 *         properties keys' naming convention to new GWt compatible system
	 *         The old naming convention used dots in the syntax of keys in the
	 *         properties files. Since dots are not allowed in syntaxes of
	 *         methods (refer to GWT Constants and ConstantsWithLookup
	 *         interfaces), the new naming convention uses underscore instead of
	 *         dots. And since we are still using the old naming convention in
	 *         passing the key, we need to cross-reference.
	 */
	public static String crossReferencingPropertiesKeys(String key) {

		if (key == null) {
			return "";
		}

		String aStr = null;
		if (key.equals("X->Y")) {
			aStr = "X_Y";
		} else if (key.equals("Y<-X")) {
			aStr = "Y_X";
		} else {
			aStr = key;
		}

		return aStr.replace(A_DOT, AN_UNDERSCORE);
	}
	
	boolean menuKeysLoaded = false;

	/**
	 * @author Rana This method should work for both menu and menu tooltips
	 *         items
	 */
	@Override
	public String getMenu(String key) {

		if (key == null) {
			return "";
		}
		
		if (language == null) {
			// keys not loaded yet
			return key;
		}

		String ret = getPropertyNative(language, crossReferencingPropertiesKeys(key), "menu");
		
		if (ret == null || "".equals(ret)) {
			App.debug("menu key not found: "+key);
			return key;
		}
		
		return ret;
	
	}
	
	//
	/*
	 * eg __GGB__keysVar.en.command.Ellipse
	 */
	public native String getPropertyNative(String language, String key, String section) /*-{
		
		//if (!$wnd["__GGB__keysVar"]) {
		//	return "languagenotloaded";
		//}
		
		if ($wnd["__GGB__keysVar"][language]) {
			// translated
			return $wnd["__GGB__keysVar"][language][section][key];
		} else {
			// English (always available)
			return $wnd["__GGB__keysVar"]["en"][section][key];
		}
		
	}-*/;


	@Override
	public String getError(String key) {

		if (key == null) {
			return "";
		}
		
		if (language == null) {
			// keys not loaded yet
			return key;
		}

		String ret = getPropertyNative(language, crossReferencingPropertiesKeys(key), "error");
		
		if (ret == null || "".equals(ret)) {
			App.debug("error key not found: "+key);
			return key;
		}
		
		return ret;
	}

	/**
	 * @author Rana Since we are not implementing at this stage a secondary
	 *         language for tooltips The default behavior of setTooltipFlag()
	 *         will be to set the member variable tooltipFlag to true
	 */
	@Override
	public void setTooltipFlag() {
		tooltipFlag = true;
	}

	@Override
	public boolean isApplet() {
		return false;
	}

	@Override
	public void storeUndoInfo() {
		if (isUndoActive()) {
			kernel.storeUndoInfo();
			// isSaved = false;
		}
	}

	public void restoreCurrentUndoInfo() {
		if (isUndoActive()) {
			kernel.restoreCurrentUndoInfo();
			// isSaved = false;
		}
	}

	@Override
	public boolean isUsingFullGui() {
		// return useFullGui;
		// TODO
		return guiManager != null;
	}

	@Override
	public boolean showView(int view) {
		App.debug("showView: implementation needed"); // TODO Auto-generated
		return false;
	}

	/**
	 * Following Java's convention, the return string should only include the
	 * language part of the locale. The assumption here that the "default"
	 * locale is English.
	 */
	@Override
	public String getLanguage() {		
		return language;
	}

	/**
	 * This method is used for debugging purposes:
	 */
	public static void displaySupportedLocales() {
		String[] localeNames = LocaleInfo.getAvailableLocaleNames();
		for (int i = 0; i < localeNames.length; i++) {
			App.debug("GWT Module Supported Locale no." + i + ", Locale Name: "
			        + localeNames[i]);
		}
	}

	/**
	 * This method is used for debugging purposes:
	 */
	public static void displayLocaleCookie() {
		App.debug("Locale Cookie Name: " + LocaleInfo.getLocaleCookieName()
		        + ", Cookie Value: "
		        + Cookies.getCookie(LocaleInfo.getLocaleCookieName()));
	}

	@Override
	public String getLocaleStr() {		
		String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
		App.trace("Current Locale: " + localeName);

		if (localeName.toLowerCase().equals(AppW.DEFAULT_LOCALE)) {
			return AppW.DEFAULT_LANGUAGE;
		}
		return localeName.substring(0,2);
	}
	
	public String getLanguageFromCookie(){
		return Cookies.getCookie("GGWlang");
	}
	
	@Override
	protected void directionForWeb(String lang){

		String oldLang = getLanguageFromCookie();
		
		//TODO: change "en" for the default language
		//if there is no cookie yet, it starts with the default language
		if (oldLang==null) oldLang="en";
		
		boolean oldRTLOrder = rightToLeftReadingOrder(oldLang);
		
		App.debug("RTL order: " + rightToLeftReadingOrder + "old RTL order: " + oldRTLOrder);
		Cookies.setCookie("GGWlang", lang);
		if (oldRTLOrder != rightToLeftReadingOrder){
			Location.reload();
		}
		
	}
	
	
	// eg "en_GB", "es"
	// remains null until we're sure keys are loaded
	String language = "en";

	public void setLanguage(final String lang) {
		
		if (lang.equals(language)) {
			setLabels(); 
			return;
		}
		
		if (lang == null || "".equals(lang)) {
			
			App.error("language being set to empty string");
			setLanguage("en");
			return;
		}
		
		App.debug("setting language to:" + lang);
		
		
		
		
		// load keys (into a JavaScript <script> tag)
		DynamicScriptElement script = (DynamicScriptElement) Document.get().createScriptElement();
		script.setSrc(GWT.getModuleBaseURL()+"js/properties_keys_"+lang+".js");
		script.addLoadHandler(new ScriptLoadCallback() {
			
			public void onLoad() {
				if (lang == null || "".equals(lang)) {
					language = "en";
				} else {
					language = lang;
				}
				
				// force reload
				commandDictionary = null;

				setCommandChanged(true);
				
				App.debug("keys loaded for language: "+lang);
				App.debug("TODO: reinitialize GUI on language change");
		
				
				updateLanguageFlags(lang);
				
				
				// make sure digits are updated in all numbers
				getKernel().updateConstruction();
				setUnsaved();

				// update display & Input Bar Dictionary etc
				setLabels(); 
				
				//inputField.setDictionary(getCommandDictionary());

				

			}
		});
		Document.get().getBody().appendChild(script);
	}
	

	public void setLanguage(String language, String country) {
				
		if (language == null || "".equals(language)) {
			App.warn("error calling setLanguage(), setting to English (US): "+language+"_"+country);
			setLanguage("en");
			return;
		}
		
		if (country == null || "".equals(country)) {
			setLanguage(language);
		}this.
		
		setLanguage(language+"_"+country);
	}
	
	void setLabels() {
		if (initing) {
			return;
		}

		if (guiManager != null) {
			getGuiManager().setLabels();
		}

		//if (rbplain != null) {
		kernel.updateLocalAxesNames();
		//}

		updateCommandDictionary();
	}



	@Override
	public boolean letRedefine() {
		// AbstractApplication.debug("implementation needed"); // TODO
		// Auto-generated
		return true;
	}

	
	MyDictionary commandDictionary = null;
	
	private MyDictionary getCommandDict() {
		if (commandDictionary == null) {
			try {
				//commandDictionary = Dictionary.getDictionary("__GGB__dictionary_"+language);
				commandDictionary = MyDictionary.getDictionary("command", language);
			} catch (MissingResourceException e) {
				//commandDictionary = Dictionary.getDictionary("__GGB__dictionary_en");
				commandDictionary = MyDictionary.getDictionary("command", "en");
				App.error("Missing Dictionary " + language);
			}
		}

			return commandDictionary;

	}

	@Override
	public String getInternalCommand(String cmd) {
		initTranslatedCommands();

		// The Dictionary class is used to get the whole set of command
		// properties keys dynamically (during runtime)
		// These command keys are defined in the HTML host page as a JavaScript
		// Object named "commandKeysVaren", "commandKeysVarfr" etc
		
		if (getCommandDict() != null) {
			Set<String> commandPropertyKeys = getCommandDict().keySet();
			Iterator<String> commandKeysIterator = commandPropertyKeys
			        .iterator();
			while (commandKeysIterator.hasNext()) {
				String s = crossReferencingPropertiesKeys(commandKeysIterator
				        .next());
				// AbstractApplication.debug("Testing: " + s);
				// Remove keys with .Syntax, .SyntaxCAS, .Syntax3D from the
				// investigated set of keys.
				if (s.indexOf(syntaxStr) == -1) {
					// insure that the lower/upper cases are taken into
					// consideration
					if (getCommand(s).toLowerCase().equals(cmd.toLowerCase())) {
						return s;
					}
				}
			}
		}
		return null;
	}

	public void showMessage(final String message) {
		App.printStacktrace("showMessage: " + message);
		GOptionPaneW.INSTANCE.showConfirmDialog(null, message,
		        getPlain("ApplicationName") + " - " + getMenu("Info"),
		        GOptionPane.DEFAULT_OPTION, 0);
	}

	@Override
    public void showErrorDialog(final String msg) {
		final PopupPanel dialog = new PopupPanel(false, true);
		// dialog.setText(getPlain("ApplicationName") + " - " +
		// getMenu("Info"));

		GOptionPaneW.INSTANCE.showConfirmDialog(null, msg,
		        getPlain("ApplicationName") + " - " + getError("Error"),
		        GOptionPane.DEFAULT_OPTION, 0);
	}

	@Override
	public void showError(String s) {
		showErrorDialog(s);
	}

	@Override
	public boolean freeMemoryIsCritical() {
		// can't access available memory etc from JavaScript
		return false;
	}

	@Override
	public long freeMemory() {
		return 0;
	}

	@Override
	public AlgebraView getAlgebraView() {
		return getGuiManager().getAlgebraView();
		// if (guiManager == null) {
		// return null;
		// }
		// return guiManager.getAlgebraView();
	}

	@Override
	public EuclidianViewW getEuclidianView1() {
		return (EuclidianViewW) euclidianView;
	}

	@Override
    public EuclidianViewW getEuclidianView2() {
		return (EuclidianViewW) null; // TODO: add euclidianview2 here later
	}

	@Override
	public EuclidianViewInterfaceCommon getActiveEuclidianView() {
		// if (getGuiManager() == null) {
		return getEuclidianView1();
		// }
		// return getGuiManager().getActiveEuclidianView();
	}

	@Override
	public boolean isShowingEuclidianView2() {
		// TODO
		return false;
	}

	@Override
	public void evalJavaScript(App app, String script, String arg) {

		// TODO: maybe use sandbox?

		String ggbApplet = getArticleElement().getDataParamId();
		script = "ggbApplet = document." + ggbApplet + ";" + script;

		// script = "ggbApplet = document.ggbApplet;"+script;

		// add eg arg="A"; to start
		if (arg != null) {
			script = "arg=\"" + arg + "\";" + script;
		}

		evalScriptNative(script);
	}

	public native void evalScriptNative(String script) /*-{
		$wnd.eval(script);
	}-*/;

	/**
	 * Initializes the application, seeds factory prototypes, creates Kernel and
	 * MyXMLIO
	 * 
	 * @param undoActive
	 */
	public void init(final boolean undoAct) {
		initCommonObjects();
	}

	private void initCommonObjects() {
		geogebra.common.factories.AwtFactory.prototype = new geogebra.web.factories.AwtFactoryW();
		geogebra.common.factories.FormatFactory.prototype = new geogebra.web.factories.FormatFactoryW();
		geogebra.common.factories.CASFactory.setPrototype(new geogebra.web.factories.CASFactoryW());
		geogebra.common.factories.SwingFactory.setPrototype(new geogebra.web.factories.SwingFactoryW());
		geogebra.common.factories.UtilFactory.prototype = new geogebra.web.factories.UtilFactoryW();
		geogebra.common.factories.Factory.setPrototype(new geogebra.web.factories.FactoryW());
		geogebra.common.util.StringUtil.prototype = new geogebra.common.util.StringUtil();

		geogebra.common.euclidian.HatchingHandler.prototype = new geogebra.web.euclidian.HatchingHandlerW();
		geogebra.common.euclidian.EuclidianStatic.prototype = new geogebra.web.euclidian.EuclidianStaticW();
		geogebra.common.euclidian.clipping.DoubleArrayFactory.prototype = new geogebra.common.euclidian.clipping.DoubleArrayFactoryImpl();

		// App.initializeSingularWS();

		// neded to not overwrite anything already exists
		ORIGINAL_BODY_CLASSNAME = RootPanel.getBodyElement().getClassName();
	}

	private void showSplashImageOnCanvas() {
		if (this.canvas != null) {
			String geogebra = "GeoGebra";

			canvas.setWidth("427px");
			canvas.setHeight("120px");
			canvas.setCoordinateSpaceWidth(427);
			canvas.setCoordinateSpaceHeight(120);
			Context2d ctx = canvas.getContext2d();
			ctx.clearRect(0, 0, canvas.getCoordinateSpaceWidth(),
			        canvas.getCoordinateSpaceHeight());
			ctx.setTextBaseline(TextBaseline.TOP);
			ctx.setTextAlign(TextAlign.START);
			ctx.setFont("50px Century Gothic, Helvetica, sans-serif");
			ctx.setFillStyle("#666666");
			ctx.fillText(geogebra, 33, 37);
			// TextMetrics txm = ctx.measureText(geogebra);
			// ctx.setFillStyle("#7e7eff");
			// ctx.setTextAlign(TextAlign.LEFT);
			// ctx.setFont("20px Century Gothic, Helvetica, sans-serif");
			// ctx.fillText("4",txm.getWidth(),37);
		}
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public EuclidianDockPanelW getEuclidianViewpanel() {
		return euclidianViewPanel;
	}

	public void loadGgbFile(HashMap<String, String> archiveContent)
	        throws Exception {
		loadFile(archiveContent);
	}

	public void loadGgbFileAgain(String dataUrl) {

		geogebra.web.main.DrawEquationWeb
		        .deleteLaTeXes((EuclidianViewW) getActiveEuclidianView());
		imageManager.reset();
		if (useFullAppGui)
			GeoGebraAppFrame.fileLoader.getView().processBase64String(dataUrl);
		else
			GeoGebraFrame.fileLoader.getView().processBase64String(dataUrl);
	}

	public void beforeLoadFile() {
		getEuclidianView1().setDisableRepaint(true);
		getEuclidianView1().setReIniting(true);
	}

	public void afterLoadFile() {
		kernel.initUndoInfo();
		getEuclidianView1().setDisableRepaint(false);
		getEuclidianView1().synCanvasSize();
		getEuclidianView1().doRepaint2();
		splash.canNowHide();
		getEuclidianView1().requestFocusInWindow();

		if (needsSpreadsheetTableModel()) 
			getSpreadsheetTableModel(); //ensure create one if not already done
	}

	/**
	 * Does some refining after file loaded in the App. Also note, that only one
	 * euclidianview is used now, later it must be retought. We save the
	 * original widht, height of the canvas, and restore it after file loading,
	 * as it needed to be fixed after all.
	 */
	public void afterLoadAppFile() {
		kernel.initUndoInfo();
		getEuclidianView1().setDisableRepaint(false);
		getEuclidianView1().synCanvasSize();
		splashDialog.canNowHide();
		getEuclidianView1().doRepaint2();

		// Well, it may cause freeze if we attach this too early
		attachViews();
	}

	public void appSplashCanNowHide() {
		if (splashDialog != null) {
			splashDialog.canNowHide();
			attachViews();
		}

		// Well, it may cause freeze if we attach this too early

		// allow eg ?command=A=(1,1);B=(2,2) in URL
		String cmd = com.google.gwt.user.client.Window.Location
		        .getParameter("command");

		if (cmd != null) {

			App.debug("exectuing commands: " + cmd);

			String[] cmds = cmd.split(";");
			for (int i = 0; i < cmds.length; i++) {
				getKernel().getAlgebraProcessor()
				        .processAlgebraCommandNoExceptionsOrErrors(cmds[i],
				                false);
			}
		}

	}

	private void attachViews() {
		if (!getGuiManager().getAlgebraView().isAttached())
			getGuiManager().attachView(VIEW_ALGEBRA);

		if (needsSpreadsheetTableModel())
			getSpreadsheetTableModel();// its constructor calls attachView as a
								   // side-effect
		// Attached only on first click
		// getGuiManager().attachView(VIEW_PROPERTIES);
	}

	private void loadFile(HashMap<String, String> archiveContent)
	        throws Exception {

		beforeLoadFile();

		HashMap<String, String> archive = (HashMap<String, String>) archiveContent
		        .clone();

		// Handling of construction and macro file
		String construction = archive.remove("geogebra.xml");
		String macros = archive.remove("geogebra_macro.xml");

		// Construction (required)
		if (construction == null) {
			throw new ConstructionException(
			        "File is corrupt: No GeoGebra data found");
		}

		// Macros (optional)
		if (macros != null) {
			// macros = DataUtil.utf8Decode(macros);
			// //DataUtil.utf8Decode(macros);
			myXMLio.processXMLString(macros, true, true);
		}

		if (archive.entrySet() != null) {
			for (Entry<String, String> entry : archive.entrySet()) {
				maybeProcessImage(entry.getKey(), entry.getValue());
			}
		}
		if (!imageManager.hasImages()) {
			// Process Construction
			// construction =
			// DataUtil.utf8Decode(construction);//DataUtil.utf8Decode(construction);
			myXMLio.processXMLString(construction, true, false);
			setCurrentFile(archiveContent);
			if (!useFullAppGui) {
				afterLoadFile();
			} else {
				afterLoadAppFile();
			}
		} else {
			// on images do nothing here: wait for callback when images loaded.
			imageManager.triggerImageLoading(
			        /* DataUtil.utf8Decode( */construction/*
														 * )/*DataUtil.utf8Decode
														 * (construction)
														 */, (MyXMLio) myXMLio,
			        this);
			setCurrentFile(archiveContent);
		}
	}

	private static final ArrayList<String> IMAGE_EXTENSIONS = new ArrayList<String>();
	static {
		IMAGE_EXTENSIONS.add("bmp");
		IMAGE_EXTENSIONS.add("gif");
		IMAGE_EXTENSIONS.add("jpg");
		IMAGE_EXTENSIONS.add("png");
	}

	private void maybeProcessImage(String filename, String binaryContent) {
		String fn = filename.toLowerCase();
		if (fn.equals("geogebra_thumbnail.png")) {
			return; // Ignore thumbnail
		}

		int index = fn.lastIndexOf('.');
		if (index == -1) {
			return; // Ignore files without extension
		}

		String ext = fn.substring(index + 1).toLowerCase();
		if (!IMAGE_EXTENSIONS.contains(ext)) {
			return; // Ignore non image files
		}

		// for file names e.g. /geogebra/main/nav_play.png in GeoButtons
		if (filename != null && filename.length() != 0
		        && filename.charAt(0) == '/')
			addExternalImage(filename.substring(1), binaryContent);
		else
			addExternalImage(filename, binaryContent);
	}

	private String createImageSrc(String ext, String base64) {
		String dataUrl = "data:image/" + ext + ";base64," + base64;
		return dataUrl;
	}

	@Override
	public EuclidianViewW createEuclidianView() {
		return (EuclidianViewW) this.euclidianView;
	}

	@Override
	public AbstractImageManager getImageManager() {
		return imageManager;
	}

	@Override
	public String reverseGetColor(String locColor) {
		String str = StringUtil.removeSpaces(StringUtil.toLowerCase(locColor));

		try {

			//Dictionary colorKeysDict = Dictionary.getDictionary("__GGB__colors_"+language);
			MyDictionary colorKeysDict = MyDictionary.getDictionary("colors", language);
			Iterator<String> colorKeysIterator = colorKeysDict.keySet()
			        .iterator();
			while (colorKeysIterator != null && colorKeysIterator.hasNext()) {
				String key = colorKeysIterator.next();
				if (key != null
				        && str.equals(StringUtil.removeSpaces(StringUtil
				                .toLowerCase(this.getColor(key))))) {
					return key;
				}
			}

			return str;
		} catch (MissingResourceException e) {
			return str;
		}
	}

	@Override
	public String getColor(String key) {

		if (key == null) {
			return "";
		}

		if ((key.length() == 5)
		        && StringUtil.toLowerCase(key).startsWith("gray")) {
			switch (key.charAt(4)) {
			case '0':
				return getColor("white");
			case '1':
				return getPlain("AGray", Unicode.fraction1_8);
			case '2':
				return getPlain("AGray", Unicode.fraction1_4); // silver
			case '3':
				return getPlain("AGray", Unicode.fraction3_8);
			case '4':
				return getPlain("AGray", Unicode.fraction1_2);
			case '5':
				return getPlain("AGray", Unicode.fraction5_8);
			case '6':
				return getPlain("AGray", Unicode.fraction3_4);
			case '7':
				return getPlain("AGray", Unicode.fraction7_8);
			default:
				return getColor("black");
			}
		}

		return key;

	}

	@Override
	protected String getSyntaxString() {
		return syntaxStr;
	}

	@Override
	public void showError(MyError e) {
		final String command = e.getcommandName();

		// TODO
		App.debug("TODO later: make sure splash screen not showing");

		if (command == null) {
			showErrorDialog(e.getLocalizedMessage());
			return;
		}

		final PopupPanel dialog = new PopupPanel(false, true);

		Button ok = new Button(getPlain("OK"));
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialog.removeFromParent();
			}
		});
		Button showHelp = new Button(getPlain("ShowOnlineHelp"));
		showHelp.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				getGuiManager().openCommandHelp(command);
				dialog.removeFromParent();
			}
		});

		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("DialogButtonPanel");
		buttonPanel.add(ok);
		buttonPanel.add(showHelp);

		VerticalPanel panel = new VerticalPanel();
		String[] lines = e.getLocalizedMessage().split("\n");
		for (String item : lines) {
			panel.add(new Label(item));
		}

		panel.add(buttonPanel);
		dialog.setWidget(panel);
		dialog.center();
		dialog.show();
		ok.getElement().focus();

	}

	@Override
	public void showError(String key, String error) {
		showErrorDialog(getError(key) + ":\n" + error);
	}

	DrawEquationWeb drawEquation;
	private GuiManagerW guiManager;
	private boolean commandChanged = true;

	@Override
	public DrawEquationInterface getDrawEquation() {
		if (drawEquation == null) {
			drawEquation = new DrawEquationWeb(this);
		}

		return drawEquation;
	}

	@Override
	public void setShowConstructionProtocolNavigation(boolean show,
	        boolean playButton, double playDelay, boolean showProtButton) {
		App.debug("setShowConstructionProtocolNavigation: implementation needed"); // TODO
																				   // Auto-generated

	}

	@Override
	public double getWidth() {
		if (canvas == null)
			return 0;
		return canvas.getCanvasElement().getWidth();
	}

	@Override
	public double getHeight() {
		if (canvas == null)
			return 0;
		return canvas.getCanvasElement().getHeight();
	}

	@Override
    public GFont getFontCanDisplay(String testString, boolean serif, int style,
	        int size) {
		return fontManager.getFontCanDisplay(testString, serif, style, size);
	}

	@Override
	public GuiManagerW getGuiManager() {
		if (guiManager == null) {
			// TODO: add getGuiManager(), see #1783
			if (getUseFullGui() || showToolBar) {
				guiManager = new GuiManagerW(this);
			}
		}

		return guiManager;
	}

	@Override
	public UndoManager getUndoManager(Construction cons) {
		return new UndoManagerW(cons);
	}

	@Override
	public AnimationManager newAnimationManager(Kernel kernel2) {
		return new AnimationManagerW(kernel2);
	}

	@Override
	public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
		return new geogebra.web.kernel.geos.GeoElementGraphicsAdapter(this);
	}

	@Override
	public void updateStyleBars() {

		if (!isUsingFullGui() || isIniting()) {
			return;
		}

		if (getEuclidianView1().hasStyleBar()) {
			getEuclidianView1().getStyleBar().updateStyleBar();
		}

		if (hasEuclidianView2()
		        && ((EuclidianViewW) getEuclidianView2()).hasStyleBar()) {
			getEuclidianView2().getStyleBar().updateStyleBar();
		}
	}

	@Override
	public void setXML(String xml, boolean clearAll) {
		if (clearAll) {
			setCurrentFile(null);
		}

		try {
			// make sure objects are displayed in the correct View
			setActiveView(App.VIEW_EUCLIDIAN);
			myXMLio.processXMLString(xml, clearAll, false);
		} catch (MyError err) {
			err.printStackTrace();
			showError(err);
		} catch (Exception e) {
			e.printStackTrace();
			showError("LoadFileFailed");
		}
	}

	@Override
	public geogebra.common.plugin.GgbAPI getGgbApi() {
		if (ggbapi == null) {
			ggbapi = new geogebra.web.main.GgbAPI(this);
		}

		return ggbapi;
	}

	@Override
	public SoundManager getSoundManager() {
		App.debug("getSoundManager: implementation needed for GUI"); // TODO
																	 // Auto-generated
		return null;
	}

	@Override
	protected boolean isCommandChanged() {
		return commandChanged;
	}

	@Override
	protected void setCommandChanged(boolean b) {
		commandChanged = b;
	}

	@Override
	protected boolean isCommandNull() {
		return false;
	}

	@Override
	public void initCommand() {
		//
	}
	
	private void initCommandConstants() {
		//
	}


	@Override
	public void initScriptingBundle() {
		App.debug("initScriptingBundle: implementation needed"); // TODO
																 // Auto-generated

	}

	@Override
	public String getScriptingCommand(String internal) {
		App.debug("getScriptingCommand: implementation needed really"); // TODO
																		// Auto-generated
		return null;
	}

	@Override
	protected EuclidianView newEuclidianView(boolean[] showAxes,
	        boolean showGrid) {
		return euclidianView = new EuclidianViewW(euclidianViewPanel,
		        euclidianController, showAxes, showGrid, getSettings()
		                .getEuclidian(1));
	}

	@Override
	protected EuclidianController newEuclidianController(Kernel kernel) {
		return new EuclidianControllerW(kernel);

	}

	@Override
	public boolean showAlgebraInput() {
		App.debug("showAlgebraInput: implementation needed"); // TODO
															  // Auto-generated
		return false;
	}

	private GlobalKeyDispatcherW globalKeyDispatcher;

	@Override
	final public GlobalKeyDispatcherW getGlobalKeyDispatcher() {
		if (globalKeyDispatcher == null) {
			globalKeyDispatcher = newGlobalKeyDispatcher();
		}
		return globalKeyDispatcher;
	}

	protected GlobalKeyDispatcherW newGlobalKeyDispatcher() {
		return new GlobalKeyDispatcherW(this);
	}

	@Override
	public SpreadsheetTableModel getSpreadsheetTableModel() {
		if (tableModel == null) {
			tableModel = new SpreadsheetTableModelW(this, SPREADSHEET_INI_ROWS,
			        SPREADSHEET_INI_COLS);
		}
		return tableModel;
	}

	@Override
	public void evalPythonScript(App app, String string, String arg) {
		debug("Python scripting not supported");

	}

	@Override
    public ScriptManager getScriptManager() {
		if (scriptManager == null) {
			scriptManager = new ScriptManagerW(this);
		}
		return scriptManager;
	}

	@Override
	public void callAppletJavaScript(String fun, Object[] args) {
		if (args == null || args.length == 0) {
			callNativeJavaScript(fun);
		} else if (args.length == 1) {
			App.debug("calling function: " + fun + "(" + args[0].toString()
			        + ")");
			callNativeJavaScript(fun, args[0].toString());
		} else {
			debug("callAppletJavaScript() not supported for more than 1 argument");
		}

	}

	public native void callNativeJavaScript(String funcname) /*-{
		if ($wnd[funcname]) {
			$wnd[funcname]();
		}
	}-*/;

	public native void callNativeJavaScript(String funcname, String arg) /*-{
		if ($wnd[funcname]) {
			$wnd[funcname](arg);
		}
	}-*/;

	public static native void ggbOnInit() /*-{
		if (typeof $wnd.ggbOnInit === 'function')
			$wnd.ggbOnInit();
	}-*/;

	public static native void ggbOnInit(String arg) /*-{
		if (typeof $wnd.ggbOnInit === 'function')
			$wnd.ggbOnInit(arg);
	}-*/;

	@Override
	public void updateMenubar() {
		// getGuiManager().updateMenubar();
		App.debug("AppW.updateMenubar() - implementation needed - just finishing"); // TODO
															 // Auto-generated
	}

	@Override
	public GFont getPlainFontCommon() {
		return new geogebra.web.awt.GFontW("normal");
	}

	@Override
	public FontManager getFontManager() {
		return fontManager;
	}

	@Override
	public void updateUI() {
		App.debug("updateUI: implementation needed for GUI"); // TODO
															  // Auto-generated

	}

	@Override
	public String getTooltipLanguageString() {

		String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
		if (localeName != null && !"".equals(localeName)) {
			if (localeName.equals(LANGUAGE_NORWEGIAN_NYNORSK_BCP47)) {
				return LANGUAGE_NORWEGIAN_NYNORSK;
			}
			return localeName;
		}
		return DEFAULT_LANGUAGE;

	}

	@Override
	protected void getWindowLayoutXML(StringBuilder sb, boolean asPreference) {
		// save the dimensions of the current window
		sb.append("\t<window width=\"");

		if (getEuclidianView1().getWidth() > 1) {
			//double ratio = 800.0 * (getEuclidianView1().getHeight() + 170) / 600.0;
			//sb.append((int)ratio);
			// so it seems GeoGebraTube doesn't add anything to the following:
			sb.append(getEuclidianView1().getWidth());
		} else {
			sb.append(800);
		}

		sb.append("\" height=\"");

		if (getEuclidianView1().getHeight() > 1) {
			// 170 is a GeoGebraTube hack
			sb.append(getEuclidianView1().getHeight() + 170);
		} else {
			sb.append(600);
		}

		sb.append("\" />\n");

		if (guiManager == null) {
			initGuiManager();
		}
		getGuiManager().getLayout().getXml(sb, asPreference);//TODO implementation needed

		// labeling style
		// default changed so we need to always save this now
		// if (labelingStyle != ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC) {
		sb.append("\t<labelingStyle ");
		sb.append(" val=\"");
		sb.append(getLabelingStyle());
		sb.append("\"/>\n");
		// }
	}

	public Map<String, String> getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(HashMap<String, String> file) {
		if (currentFile == file) {
			return;
		}

		currentFile = file;
		if (currentFile != null) {
			addToFileList(currentFile);
		}

		// if (!isIniting() && isUsingFullGui()) {
		// updateTitle();
		// getGuiManager().updateMenuWindow();
		// }
	}

	public static void addToFileList(Map<String, String> file) {
		if (file == null) {
			return;
		}
		// add or move fileName to front of list
		fileList.remove(file);
		fileList.addFirst(file);
	}

	public static Map<String, String> getFromFileList(int i) {
		if (fileList.size() > i) {
			return fileList.get(i);
		}
		return null;
	}

	public static int getFileListSize() {
		return fileList.size();
	}

	@Override
	public void reset() {
		if (currentFile != null) {
			try {
				loadGgbFile(currentFile);
			} catch (Exception e) {
				clearConstruction();
			}
		} else {
			clearConstruction();
		}
	}

	@Override
    public boolean clearConstruction() {
		// if (isSaved() || saveCurrentFile()) {
		kernel.clearConstruction();

		kernel.initUndoInfo();
		setCurrentFile(null);
		setMoveMode();

		geogebra.web.main.DrawEquationWeb
		        .deleteLaTeXes((EuclidianViewW) getActiveEuclidianView());
		 return true;
		
		// }
		//return false;
	}

	@Override
	public PythonBridge getPythonBridge() {
		// not available in web
		return null;
	}

	@Override
	public String getPlainTooltip(String key) {

		if (tooltipFlag) {
			return getPlain(key);
		}

		return null;
	}

	@Override
	final public String getSymbol(int key) {

		if (language == null) {
			// keys not loaded yet
			return null;
		}

		String ret = getPropertyNative(language, "S"+key, "symbol");
		
		if (ret == null || "".equals(ret)) {
			App.debug("menu key not found: "+key);
			return null;
		}
		
		return ret;
	}

	@Override
	final public String getSymbolTooltip(int key) {

		if (language == null) {
			// keys not loaded yet
			return null;
		}

		String ret = getPropertyNative(language, "T"+key, "symbol");
		
		if (ret == null || "".equals(ret)) {
			App.debug("menu key not found: "+key);
			return null;
		}
		
		return ret;
	}

	/**
	 * Clear selection
	 * 
	 * @param repaint
	 *            whether all views need repainting afterwards
	 */
	@Override
	public void clearSelectedGeos(boolean repaint) {
		// if (getUseFullGui()) ?
		if (useFullAppGui)
			((AlgebraViewW) getAlgebraView()).clearSelection();
		super.clearSelectedGeos(repaint);
	}

	@Override
	public GeoElementSelectionListener getCurrentSelectionListener() {
		// TODO Auto-generated method stub
		return null;
	}

	public void buildApplicationPanel() {
		if (showMenuBar){
			attachMenubar();
		}
		
		if (showToolBar) {
			attachToolbar();
		}
		
		//return euclidianViewPanel;
		frame.add(euclidianViewPanel);

		if (showAlgebraInput){
			attachAlgebraInput();
		}
	}
	
	public void attachAlgebraInput(){
		GGWCommandLine inputbar = new GGWCommandLine();
		inputbar.attachApp(this);
		frame.add(inputbar);
	}
	
	public void attachMenubar() {
		GGWMenuBar menubar = new GGWMenuBar();
		menubar.init(this);
		frame.add(menubar);
	}	
	
	public void attachToolbar() {
		GGWToolBar toolbar = new GGWToolBar();
		toolbar.init(this);
		frame.add(toolbar);
	}

	public void showLoadingAnimation(boolean go) {
		// showSplashImageOnCanvas();

	}

	@Override
	public boolean isHTML5Applet() {
		return true;
	}

	protected void initImageManager() {
		imageManager = new ImageManager();
	}

	public void addExternalImage(String filename, String src) {
		imageManager.addExternalImage(filename, src);
	}

	@Override
	public GBufferedImage getExternalImageAdapter(String fileName) {
		ImageElement im = ImageManager.getExternalImage(fileName);
		if (im == null)
			return null;
		return new geogebra.web.awt.GBufferedImageW(im);
	}

	// random id to identify ggb files
	// eg so that GeoGebraTube can notice it's a version of the same file
	private String uniqueId = null;// FIXME: generate new UUID: +
								   // UUID.randomUUID();
	private geogebra.web.gui.dialog.DialogManagerW dialogManager;
	public SplashDialog splash;

	@Override
    public String getUniqueId() {
		return uniqueId;
	}

	@Override
    public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	@Override
    public void resetUniqueId() {
		uniqueId = null;// FIXME: generate new UUID: + UUID.randomUUID();
	}

	public ImageElement getRefreshViewImage() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(GuiResources.INSTANCE
		        .viewRefresh());
	}

	public ImageElement getPlayImage() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(GuiResources.INSTANCE.navPlay());
	}

	public ImageElement getPauseImage() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(GuiResources.INSTANCE.navPause());
	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public StringType getFormulaRenderingType() {
		return StringType.LATEX;
	}

	public static native void console(JavaScriptObject dataAsJSO) /*-{
		@geogebra.common.main.App::debug(Ljava/lang/String;)(dataAsJSO);
	}-*/;

	@Override
	public geogebra.common.main.DialogManager getDialogManager() {
		if (dialogManager == null) {
			dialogManager = new DialogManagerW(this);
		}
		return dialogManager;
	}

	@Override
	public void showURLinBrowser(final String pageUrl) {
		// Window.open(pageUrl, "_blank", "");
		debug("opening: " + pageUrl);

		// assume showURLinBrowserWaiterFixedDelay is called before
		showURLinBrowserPageUrl = pageUrl;
	}

	public String showURLinBrowserPageUrl = null;

	public native void showURLinBrowserWaiterFixedDelay() /*-{
		this.@geogebra.web.main.AppW::showURLinBrowserPageUrl = null;

		var that = this;
		var timer = {};
		function intervalTask() {
			if (that.@geogebra.web.main.AppW::showURLinBrowserPageUrl != null) {
				$wnd.open(
						that.@geogebra.web.main.AppW::showURLinBrowserPageUrl,
						"_blank");
				if (timer.tout) {
					$wnd.clearInterval(timer.tout);
				}
			}
		}

		timer.tout = $wnd.setInterval(intervalTask, 700);
	}-*/;

	public void copyEVtoClipboard() {
		Window.open(getEuclidianView1().getExportImageDataUrl(3, false),
		        "_blank", null);
	}

	@Override
	public void uploadToGeoGebraTube() {
		showURLinBrowserWaiterFixedDelay();
		GeoGebraTubeExportWeb ggbtube = new GeoGebraTubeExportWeb(this);
		((GgbAPI) getGgbApi()).getBase64(true,
		        getUploadToGeoGebraTubeCallback(ggbtube));
	}

	public native JavaScriptObject getUploadToGeoGebraTubeCallback(
	        GeoGebraTubeExportWeb ggbtube) /*-{
		return function(base64string) {
			ggbtube.@geogebra.web.main.GeoGebraTubeExportWeb::uploadWorksheetSimple(Ljava/lang/String;)(base64string);
		}
	}-*/;

	@Override
	public void setWaitCursor() {
		RootPanel.get().setStyleName(ORIGINAL_BODY_CLASSNAME);
		RootPanel.get().addStyleName("cursor_wait");
	}

	@Override
	public void setDefaultCursor() {
		RootPanel.get().setStyleName(ORIGINAL_BODY_CLASSNAME);
	}

	public void resetCursor() {
		RootPanel.get().setStyleName(ORIGINAL_BODY_CLASSNAME);
	}

	@Override
	protected void initGuiManager() {
		setWaitCursor();
		guiManager = newGuiManager();
		guiManager.setLayout(new geogebra.web.gui.layout.LayoutW());
		guiManager.initialize();
		setDefaultCursor();
	}

	/**
	 * @return a GuiManager for GeoGebraWeb
	 */
	protected GuiManagerW newGuiManager() {
		return new GuiManagerW(AppW.this);
	}

	private void createSplash() {
		splash = new SplashDialog();
		int splashWidth = 427;
		int splashHeight = 120;
		int width = articleElement.getDataParamWidth();
		int height = articleElement.getDataParamHeight();
		if (width > 0 && height > 0) {
			frame.setWidth(width + "px");
			setDataParamWidth(width);
			setDataParamHeight(height);
			frame.setHeight(height + "px");
			splash.addStyleName("splash");
			splash.getElement().getStyle()
			        .setTop((height / 2) - (splashHeight / 2), Unit.PX);
			splash.getElement().getStyle()
			        .setLeft((width / 2) - (splashWidth / 2), Unit.PX);

		}
		frame.addStyleName("jsloaded");
		frame.add(splash);
	}

	private geogebra.web.gui.app.SplashDialog splashDialog = null;

	private void createAppSplash() {
		splashDialog = new geogebra.web.gui.app.SplashDialog();
	}

	/**
	 * @param undoActive
	 * @param this_app
	 * 
	 *            Initializes Kernel, EuclidianView, EuclidianSettings, etc..
	 */
	
	void initCoreObjects(final boolean undoActive, final App this_app) {
		
		kernel = new KernelW(this_app);
			
		
		// init settings
		settings = new Settings();
		
		myXMLio = new MyXMLio(kernel, kernel.getConstruction());
			
		fontManager = new FontManagerW();
		setFontSize(12);
		initEuclidianViews();

		initImageManager();

				

				
		setFontSize(12);
		// setLabelDragsEnabled(false);
		capturingThreshold = 20;

		// make sure undo allowed
		hasFullPermissions = true;

		getScriptManager();// .ggbOnInit();//this is not called here because we
						// have to delay it
				        // until the canvas is first drawn

		setUndoActive(undoActive);
		registerFileDropHandlers((CanvasElement) canvas.getElement().cast());
		afterCoreObjectsInited();
			
	}

	/**
	 * @param ggwGraphicsViewWidth
	 * 
	 *            Resets the width of the Canvas converning the Width of its
	 *            wrapper (splitlayoutpanel center)
	 */
	public void ggwGraphicsViewDimChanged(int width, int height) {
		getSettings().getEuclidian(1).setPreferredSize(
		        geogebra.common.factories.AwtFactory.prototype.newDimension(
		                width, height));

		// simple setting temp.
		appCanvasHeight = height;
		appCanvasWidth = width;

		getEuclidianView1().setDisableRepaint(false);
		getEuclidianView1().synCanvasSize();
		getEuclidianView1().doRepaint2();
		((EuclidianControllerW) getActiveEuclidianView()
		        .getEuclidianController()).updateOffsets();
	}

	public static native void console(String string) /*-{
		if ($wnd && $wnd.console) {
			@geogebra.common.main.App::debug(Ljava/lang/String;)(string);
		}
	}-*/;

	public void updateToolBar() {
		if (!showToolBar || isIniting()) {
			return;
		}

		getGuiManager().updateToolbar();

		setMoveMode();
	}

	public GeoGebraAppFrame getAppFrame() {
		return appFrame;
	}

	@Override
	public void updateApplicationLayout() {
		App.debug("updateApplicationLayout: Implementation needed...");
	}

	public void setShowInputHelpPanel(boolean b) {
		App.debug("setShowInputHelpPanel: Implementation needed...");
	}

	public String getCommandSyntaxCAS(String key) {
		String command = getCommand(key);
		String syntax = getCommand(key + syntaxCAS);

		syntax = syntax.replace("[", command + '[');

		return syntax;
	}

	@Override
	public void fileNew() {

		// clear all
		// triggers the "do you want to save" dialog
		// so must be called first
		if (!clearConstruction()) {			
			return;
		}

		// clear input bar
		if (isUsingFullGui() && showAlgebraInput()) {
			AlgebraInputW ai = (AlgebraInputW) (getGuiManager()
			        .getAlgebraInput());
			ai.clear();
		}

		// reset spreadsheet columns, reset trace columns
		if (isUsingFullGui()) {
			// getGuiManager().resetSpreadsheet();
		}

		getEuclidianView1().resetXYMinMaxObjects();
		if (hasEuclidianView2EitherShowingOrNot()) {
			getEuclidianView2().resetXYMinMaxObjects();
		}

		resetUniqueId();

		driveBase64FileName = null;
		driveBase64description = null;
		driveBase64Content = null;
		currentFileId = "";
		((DialogManagerW) getDialogManager())
		        .refreshAndShowCurrentFileDescriptors(driveBase64FileName,
		                driveBase64description);

	}

	private String driveBase64Content = null;
	private String driveBase64description = null;
	private String driveBase64FileName = null;
	/**
	 * static because it gets from server side, either "" or the set filename
	 */
	public static String currentFileId = null;

	public void refreshCurrentFileDescriptors(String fName, String desc,
	        String fileCont) {
		driveBase64Content = fileCont;
		driveBase64description = desc;
		driveBase64FileName = fName;
		((DialogManagerW) getDialogManager())
		        .refreshAndShowCurrentFileDescriptors(driveBase64FileName,
		                driveBase64description);

	}

	public String getFileName() {
		return driveBase64FileName;
	}

	public String getFileDescription() {
		return driveBase64description;
	}

	private static native void setCurrentFileId() /*-{
	                                              @geogebra.web.main.AppW::currentFileId = $wnd.GGW_appengine.FILE_IDS[0];
	                                              }-*/;

	@Override
	public String getCountryFromGeoIP() {
		// warn("unimplemented");

		AppW.debug("GeoIPCountry: " + AppW.geoIPCountryName);
		AppW.debug("GeoIPLanguage: " + AppW.geoIPLanguage);
		return AppW.geoIPCountryName;
	}

	@Override
    public boolean loadXML(String xml) throws Exception {
		myXMLio.processXMLString(xml, true, false);
		return true;
	}

	@Override
	public void exportToLMS(boolean b) {
		App.debug("unimplemented");
	}

	@Override
	public void copyGraphicsViewToClipboard() {
		App.debug("unimplemented");
	}

	@Override
	public void exitAll() {
		App.debug("unimplemented");
	}

	@Override
    public void addMenuItem(MenuInterface parentMenu, String filename,
	        String name, boolean asHtml, MenuInterface subMenu) {

		String funcName = filename.substring(0, filename.lastIndexOf('.'));
		ImageResource imgRes = (ImageResource) (AppResources.INSTANCE
		        .getResource(funcName));
		String iconString = imgRes.getSafeUri().asString();

		((MenuBar) parentMenu).addItem(
		        GeoGebraMenubarW.getMenuBarHtml(iconString, name), true,
		        (MenuBar) subMenu);

	}

	@Override
	public String getVersionString() {
		return super.getVersionString() + "-HTML5";
	}

	private NormalizerMinimal normalizerMinimal;
	private MyXMLio xmlio;

	@Override
	public NormalizerMinimal getNormalizer() {
		if (normalizerMinimal == null) {
			normalizerMinimal = new NormalizerMinimal();
		}

		return normalizerMinimal;
	}

	public void setShowAxesSelected(MenuItem mi) {
		GeoGebraMenubarW.setMenuSelected(mi, getGuiManager()
		        .getActiveEuclidianView().getShowXaxis()
		        && (getGuiManager().getActiveEuclidianView().getShowYaxis()));
	}

	public void setShowGridSelected(MenuItem mi) {
		GeoGebraMenubarW.setMenuSelected(mi, getGuiManager()
		        .getActiveEuclidianView().getShowGrid());
	}

	@Override
	public void runScripts(GeoElement geo1, String string) {
		geo1.runClickScripts(string);
	}

	public String getEnglishCommand(String pageName) {
		initCommand();
		//String ret = commandConstants
		//        .getString(crossReferencingPropertiesKeys(pageName));
		//if (ret != null)
		//	return ret;
		return pageName;
	}

	@Override
	protected Object getMainComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public CASFactory getCASFactory() {
	    return CASFactory.getPrototype();
    }

	@Override
    public SwingFactory getSwingFactory() {
	    return SwingFactory.getPrototype();
    }

	@Override
    public Factory getFactory() {
	    return Factory.getPrototype();
    }

	private static boolean controlDown = false;
	private static boolean shiftDown = false;

	public static boolean getControlDown() {
		return controlDown;
	}

	public static boolean getShiftDown() {
		return shiftDown;
	}

	public static void setDownKeys(KeyEvent ev) {
		controlDown = ev.isControlKeyDown();
		shiftDown = ev.isShiftKeyDown();
	}

	@Override 
	public MyXMLio getXMLio() { 
		if (xmlio == null) { 
			xmlio = createXMLio(kernel.getConstruction()); 
		} 
		return xmlio; 
	}

	@Override
	public MyXMLio createXMLio(Construction cons) {
		return new MyXMLio(cons.getKernel(), cons);
	}

	@Override
    public void setShowToolBar(boolean toolbar, boolean help) {
		if (toolbar) {
			JavaScriptInjector.inject(GuiResources.INSTANCE.propertiesKeysJS().getText());
		}
		super.setShowToolBar(toolbar, help);
	}

	/**
	 * This is used for LaTeXes in GeoGebraWeb (DrawText, DrawEquationWeb)
	 */
    @Override
    public void scheduleUpdateConstruction() {

    	// set up a scheduler in case 0.5 seconds would not be enough for the computer
    	Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
    		public void execute() {
    			
    			// 0.5 seconds is good for the user and maybe for the computer too
    	    	Timer timeruc = new Timer() {
    	    		@Override
                    public void run() {
    	    			boolean force = kernel.getForceUpdatingBoundingBox();
    	    			kernel.setForceUpdatingBoundingBox(true);
    	    			kernel.getConstruction().updateConstruction();
    	    			kernel.notifyRepaint();
    	    			kernel.setForceUpdatingBoundingBox(force);
    	    		}
    	    	};
    	    	timeruc.schedule(500);
    		}
    	});
    }
    
	public native String getNativeEmailSet() /*-{
		if($wnd.GGW_appengine){
			return $wnd.GGW_appengine.USER_EMAIL;
		}
		else return "";
	}-*/;
}
