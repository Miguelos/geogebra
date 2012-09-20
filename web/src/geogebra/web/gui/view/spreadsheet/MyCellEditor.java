package geogebra.web.gui.view.spreadsheet;

import geogebra.common.gui.view.spreadsheet.RelativeCopy;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.main.App;
import geogebra.web.gui.inputfield.AutoCompleteTextFieldW;
//import geogebra.web.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;

/**
 * Default cell editor for the spreadsheet, extends
 * DefaultCellEditor(JTextField)
 * 
 */
public class MyCellEditor /*extends DefaultCellEditor implements FocusListener*/ {

	private static final long serialVersionUID = 1L;

	protected Kernel kernel;
	protected AppW app;
	protected GeoElement value;
	protected MyTableW table;
	private AutoCompleteTextFieldW textField;

	protected int column;
	protected int row;
	private boolean editing = false;
	private boolean errorOnStopEditing = false;

	private boolean allowProcessGeo = false;

	public boolean allowProcessGeo() {
		return allowProcessGeo;
	}

	public void setAllowProcessGeo(boolean allowProcessGeo) {
		this.allowProcessGeo = allowProcessGeo;
	}

	private boolean enableAutoComplete = false;

	public boolean isEnableAutoComplete() {
		return enableAutoComplete;
	}

	public void setEnableAutoComplete(boolean enableAutoComplete) {
		this.enableAutoComplete = enableAutoComplete;
		textField.setAutoComplete(enableAutoComplete);
	}

	public MyCellEditor(Kernel kernel) {

		//TODO//super(new AutoCompleteTextFieldW(0, (AppW) kernel.getApplication(), false));

		this.kernel = kernel;
		app = (AppW) kernel.getApplication();
		textField = new AutoCompleteTextFieldW(0, (AppW) kernel.getApplication(), false);
		textField.setAutoComplete(enableAutoComplete);

		textField.addDomHandler(
			new SpreadsheetCellEditorKeyListener(false),
			KeyDownEvent.getType());
		//?//textField.addFocusListener(this);

		/*DocumentListener documentListener = new DocumentListener() {
			public void changedUpdate(DocumentEvent documentEvent) {
				// do nothing
			}

			public void insertUpdate(DocumentEvent documentEvent) {
				updateFormulaBar(documentEvent);
			}

			public void removeUpdate(DocumentEvent documentEvent) {
				updateFormulaBar(documentEvent);
			}

			private void updateFormulaBar(DocumentEvent documentEvent) {
				if (table.view.getShowFormulaBar()
						&& (textField.hasFocus() || table.isDragging2))
					table.view.getFormulaBar().setEditorText(
							textField.getText());
			}
		};
		textField.getDocument().addDocumentListener(documentListener);*/

	}

	public void setText(String text) {
		if (!textField.hasFocus() && !table.isDragging2)
			textField.setText(text);

	}

	/*@Override
	public Component getTableCellEditorComponent(JTable table0, Object value0,
			boolean isSelected, int row0, int column0) {

		table = (MyTableW) table0;

		if (value0 instanceof String) { // clicked to type
			value = null;
		} else {
			value = (GeoElement) value0;
		}

		column = column0;
		row = row0;
		String text = "";

		if (value != null) {
			text = getEditorInitString(value);
			int index = text.indexOf("=");
			if ((!value.isGeoText())) {
				if (index == -1) {
					text = "=" + text;
				}
			}
		}
		delegate.setValue(text);

		Component component = getComponent();
		component.setFont(app.getFontCanDisplayAwt(text));

		editing = true;

		return component;
	}*/

	/**
	 * set flag to require text start with "=" to activate autocomplete
	 */
	/*public void setEqualsRequired(boolean equalsRequired) {
		textField.setEqualsRequired(equalsRequired);
	}*/

	/**
	 * returns flag that requires text start with "=" to activate autocomplete
	 */
	/*public boolean isEqualsRequired() {
		return textField.isEqualsRequired();
	}*/

	public void setLabels() {
		textField.setDictionary(app.getCommandDictionary());
	}

	/**
	 * 
	 * @return true if the completion popup is open
	 */
	public boolean completionsPopupOpen() {
		return textField.getCompletions() != null;
	}

	// =======================================================
	// In-cell Editing Methods
	// =======================================================

	/**
	 * Returns the definition of geo used to init the editor when editing is
	 * started.
	 * 
	 * @param geo
	 */
	public String getEditorInitString(GeoElement geo) {
		return geo.getRedefineString(true, false);
	}

	public boolean isEditing() {
		return editing;
	}

	public int getCaretPosition() {
		return textField.getCaretPosition();
	}

	/** Insert a geo label into current editor string. */
	public void addLabel(String label) {
		if (!editing)
			return;
		// String text = (String) delegate.getCellEditorValue();
		// delegate.setValue(text + label);
		textField.insertString(" " + label + " ");
	}

	public void setLabel(String text) {
		if (!editing)
			return;
		//TODO! delegate.setValue(text);
	}

	public String getEditingValue() {
		return null;//TODO! (String) delegate.getCellEditorValue();
	}

	public Object getCellEditorValue() {
		return value;
	}

	// =======================================================
	// Stop/Cancel Editing
	// =======================================================

	public void cancelCellEditing() {
		editing = false;
		errorOnStopEditing = false;

		//?//super.cancelCellEditing();

		// give the table the focus in case the formula bar is the editor
		//?//if (table.getView().getFormulaBar().editorHasFocus()) {
			// Application.debug("give focus to table");
		//?//	table.requestFocus();
		//?//}
	}

	public boolean stopCellEditing() {

		errorOnStopEditing = true; // flag to handle column resizing during
									// editing (see focusLost method)

		// try to redefine or create the cell geo with the current editing
		// string
		if (!processGeo())
			return false;

		errorOnStopEditing = false;
		editing = false;
		boolean success = true;//TODO super.stopCellEditing();

		// give the table the focus in case the formula bar is the editor
		//?//if (table.getView().getFormulaBar().editorHasFocus()) {
			// Application.debug("give focus to table");
		//?//	table.requestFocus();
		//?//}
		return success;
	}

	boolean stopCellEditing(int colOff, int rowOff) {
		allowProcessGeo = true;
		boolean success = stopCellEditing();
		moveSelectedCell(colOff, rowOff);
		allowProcessGeo = false;
		return success;
	}

	private void moveSelectedCell(int colOff, int rowOff) {
		int nextRow = Math.min(row + rowOff, table.getRowCount() - 1);
		int nextColumn = Math.min(column + colOff, table.getColumnCount() - 1);
		table.setSelection(nextColumn, nextRow);
	}

	/**
	 * Attempts to create or redefine the cell geo using the current editing
	 * string
	 * 
	 * @return
	 */
	private boolean processGeo() {

		try {

			if (allowProcessGeo) {
				String text = "";//TODO! (String) delegate.getCellEditorValue();
				// get GeoElement of current cell
				value = kernel.lookupLabel(GeoElementSpreadsheet
						.getSpreadsheetCellName(column, row), false);

				if (text.equals("")) {
					if (value != null) {
						value.removeOrSetUndefinedIfHasFixedDescendent();
						value = null;
					}

				} else {
					GeoElement newVal = RelativeCopy
							.prepareAddingValueToTableNoStoringUndoInfo(kernel,
									app, text, value, column, row);
					if (newVal == null) {
						return false;
					}
					value = newVal;
				}

				if (value != null)
					app.storeUndoInfo();
			}

		} catch (Exception ex) {
			// show GeoGebra error dialog
			// kernel.getApplication().showError(ex.getMessage());
			ex.printStackTrace();
			//TODO super.stopCellEditing();
			editing = false;
			return false;
		}
		return true;
	}

	// =======================================================
	// Key and Focus Listeners
	// =======================================================

	// keep track of when <tab> was first pressed
	// so we can return to that column when <enter> pressed
	public static int tabReturnCol = -1;

	public class SpreadsheetCellEditorKeyListener implements KeyDownHandler {

		// boolean escape = false;
		boolean isFormulaBarListener;

		public SpreadsheetCellEditorKeyListener(boolean isFormulaBarListener) {
			this.isFormulaBarListener = isFormulaBarListener;
		}

		public void onKeyDown(KeyDownEvent e) {
			checkCursorKeys(e);
			int keyCode = e.getNativeKeyCode();

			switch (keyCode) {
			case KeyCodes.KEY_ESCAPE:
				GeoElement oldGeo = kernel.getGeoAt(column, row);
				cancelCellEditing();

				// restore old text in spreadsheet
				table.getModel().setValueAt(oldGeo, row, column);

				// stopCellEditing(0,0);
				// force nice redraw
				table.setSelection(column, row);

				// update the formula bar after escape
				//?//table.getView().updateFormulaBar();

				break;

			}
		}

		public void checkCursorKeys(KeyDownEvent e) {

			String text = "";//TODO! (String) delegate.getCellEditorValue();

			int keyCode = e.getNativeKeyCode();
			// Application.debug(e+"");
			switch (keyCode) {
			case KeyCodes.KEY_UP:
				if (isFormulaBarListener)
					return;

				// Application.debug("UP");
				stopCellEditing(0, -1);
				editing = false;
				//?//e.consume();
				tabReturnCol = -1;
				break;

			case KeyCodes.KEY_TAB:
				if (isFormulaBarListener)
					return;
				App.debug(" tab");
				// Application.debug("RIGHT");
				// shift-tab moves left
				// tab moves right
				if (tabReturnCol == -1)
					tabReturnCol = column;
				stopCellEditing(e.isShiftKeyDown() ? -1 : 1, 0);
				editing = false;

				break;

			case KeyCodes.KEY_ENTER:
				// if incomplete command entered, want to move the cursor to
				// between []
				int bracketsIndex = text.indexOf("[]");
				if (bracketsIndex == -1) {

					if (tabReturnCol != -1) {
						int colOffset = tabReturnCol - column;
						stopCellEditing(colOffset, 1);
						editing = false;
					} else {
						stopCellEditing(0, 1);
					}
				} else {
					textField.setCaretPosition(bracketsIndex + 1);
					//?//e.consume();
				}

				tabReturnCol = -1;
				break;

			case KeyCodes.KEY_DOWN:
				if (isFormulaBarListener) {
					//?//e.consume();
					return;
				}
				// Application.debug("DOWN");
				stopCellEditing(0, 1);
				editing = false;
				tabReturnCol = -1;
				break;

			case KeyCodes.KEY_LEFT:
				if (isFormulaBarListener)
					return;
				// Application.debug("LEFT");
				// Allow left/right keys to exit cell for easier data entry
				if (getCaretPosition() == 0) {
					stopCellEditing(-1, 0);
					editing = false;
				}
				editing = false;
				tabReturnCol = -1;
				break;

			case KeyCodes.KEY_RIGHT:
				if (isFormulaBarListener)
					return;
				// Application.debug("RIGHT");
				// Allow left/right keys to exit cell for easier data entry
				if (getCaretPosition() == text.length()) {
					stopCellEditing(1, 0);
					editing = false;
				}

				editing = false;
				tabReturnCol = -1;
				break;

			case KeyCodes.KEY_PAGEDOWN:
			case KeyCodes.KEY_PAGEUP:
				//?//e.consume();
				tabReturnCol = -1;
				break;

			// An F1 keypress causes the focus to be lost, so we
			// need to set 'editing' to false to prevent the focusLost()
			// method from calling stopCellEditing()
			//?//case KeyEvent.VK_F1:
			//?//	editing = false;
			//?//	break;

			}

		}

	}

	/*
	public void focusGained(FocusEvent arg0) {
		editing = true;
	}

	public void focusLost(FocusEvent arg0) {

		// VirtualKeyboard gets the focus very briefly when opened
		// so ignore this!
		if (arg0.getOppositeComponent() instanceof VirtualKeyboard)
			return;

		// only needed if eg columns resized
		if (editing == true) {
			if (!errorOnStopEditing) {
				// this stops editing but does not process geos ... needed for
				// formula bar sync
				stopCellEditing();
			} else if (!app.isErrorDialogShowing()) {
				cancelCellEditing();
			}
		}
	}*/

}