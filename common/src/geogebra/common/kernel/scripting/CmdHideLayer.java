package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;

import java.util.Iterator;

/**
 *HideLayer
 */
public class CmdHideLayer extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdHideLayer(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected
	final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isNumberValue()) {
				GeoNumeric layerGeo = (GeoNumeric) arg[0];
				int layer = (int) layerGeo.getDouble();

				Iterator<GeoElement> it = kernelA.getConstruction()
						.getGeoSetLabelOrder().iterator();
				while (it.hasNext()) {
					GeoElement geo = it.next();
					if (geo.getLayer() == layer) {
						geo.setEuclidianVisible(false);
						geo.updateRepaint();
					}
				}

				
				return;

			}
			throw argErr(app, c.getName(), null);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
