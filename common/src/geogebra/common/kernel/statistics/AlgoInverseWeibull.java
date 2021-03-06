/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;

import org.apache.commons.math.distribution.WeibullDistribution;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoInverseWeibull extends AlgoDistribution {

	
	/**
     * @param cons construction
     * @param label label for output
     * @param a shape
     * @param b scale
     * @param c variable value
     */
    public AlgoInverseWeibull(Construction cons, String label, NumberValue a,NumberValue b, NumberValue c) {
        super(cons, label, a, b, c, null);
    }
    /**
     * @param cons construction
     * @param a shape
     * @param b scale
     * @param c variable value
     */
    public AlgoInverseWeibull(Construction cons, NumberValue a,
			NumberValue b, NumberValue c) {
        super(cons, a, b, c, null);
	}

    @Override
	public Commands getClassName() {
		return Commands.InverseWeibull;
	}
    
	@Override
	public final void compute() {
    	
    	
    	if (input[0].isDefined() && input[1].isDefined() && input[2].isDefined()) {
		    double param = a.getDouble();
		    double param2 = b.getDouble();
    		    double val = c.getDouble();
        		try {
        			WeibullDistribution dist = getWeibullDistribution(param, param2);
        			num.setValue(dist.inverseCumulativeProbability(val));    
        			
        		}
        		catch (Exception e) {
        			num.setUndefined();        			
        		}
    	} else
    		num.setUndefined();
    }       
        
    
}



