package geogebra.mobile.gui.algebra;

import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.Kernel;
import geogebra.mobile.controller.MobileController;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.dom.client.recognizer.swipe.SwipeEndEvent;
import com.googlecode.mgwt.dom.client.recognizer.swipe.SwipeEndHandler;
import com.googlecode.mgwt.dom.client.recognizer.swipe.SwipeEvent.DIRECTION;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.touch.TouchDelegate;

/**
 * Extends from {@link LayoutPanel}. Holds the instances of the
 * {@link AlgebraView algebraView} and {@link ScrollPanel scrollPanel}.
 */

public class AlgebraViewPanel extends LayoutPanel
{
	protected ScrollPanel scrollPanel;
	protected AlgebraViewM algebraView;

	boolean small = false;

	/**
	 * Initializes the {@link TouchDelegate} and adds a {@link TapHandler} and a
	 * {@link SwipeEndHandler}.
	 */
	public AlgebraViewPanel()
	{
		this.addStyleName("algebraview");

		TouchDelegate touchDelegate = new TouchDelegate(this);

		touchDelegate.addTapHandler(new TapHandler()
		{

			/**
			 * If there is a tap on the {@link AlgebraViewPanel} and it is
			 * collapsed, the panel gets extended.
			 * 
			 * @param event
			 *            Event
			 */
			@Override
			public void onTap(TapEvent event)
			{
				if (AlgebraViewPanel.this.small)
				{
					extend();
				}
			}
		});

		touchDelegate.addSwipeEndHandler(new SwipeEndHandler()
		{

			/**
			 * The {@link AlgebraViewPanel} extends if there is a swipe from
			 * left to right and collapses if there is a swipe form right to
			 * left.
			 */
			@Override
			public void onSwipeEnd(SwipeEndEvent event)
			{
				if (event.getDirection() == DIRECTION.LEFT_TO_RIGHT)
				{
					extend();
				} else if (event.getDirection() == DIRECTION.RIGHT_TO_LEFT)
				{
					AlgebraViewPanel.this
							.addStyleName("algebraView-notExtended");
					AlgebraViewPanel.this.small = true;
					AlgebraViewPanel.this.scrollPanel.setVisible(false);
				}
			}
		});

	}

	/**
	 * Extends the {@link AlgebraViewPanel}.
	 */
	protected void extend()
	{
		AlgebraViewPanel.this.removeStyleName("algebraView-notExtended");
		this.scrollPanel.setVisible(true);
	}

	/**
	 * Creates a {@link ScrollPanel} and adds the {@link AlgebraViewM
	 * algebraView} to it. Attaches the {@link AlgebraViewM algebraView} to the
	 * {@link Kernel kernel}.
	 * 
	 * @param controller
	 *            MobileAlgebraController
	 * @param kernel
	 *            Kernel
	 */
	public void initAlgebraView(MobileController controller, Kernel kernel)
	{
		this.algebraView = new AlgebraViewM(controller);
		kernel.attach(this.algebraView);

		this.scrollPanel = new ScrollPanel(this.algebraView);
		this.scrollPanel.addStyleName("algebraScrollPanel");
		add(this.scrollPanel);
	}

}
