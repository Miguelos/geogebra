package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoTranslate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoVectorND;


public class AlgoTranslate3D extends AlgoTranslate {

	public AlgoTranslate3D(Construction cons, GeoElement in, GeoElement v) {
		super(cons, in, v);
	}
	
    @Override
	protected Coords getVectorCoords(){
    	GeoVectorND vec = (GeoVectorND) v;
    	return vec.getCoordsInD(3);
    }
    
    @Override
	protected GeoElement copy(GeoElement geo){
    	if (v.isGeoElement3D())
    		return ((Kernel3D) kernel).copy3D(geo);
    	else
    		return super.copy(geo);
    }
    
    @Override
	protected GeoElement copyInternal(Construction cons, GeoElement geo){
    	if (v.isGeoElement3D())
    		return ((Kernel3D) kernel).copyInternal3D(cons,geo);
    	else
    		return super.copyInternal(cons,geo);
    }

}
