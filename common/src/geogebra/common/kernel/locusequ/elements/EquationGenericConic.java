/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationList;
import geogebra.common.kernel.locusequ.EquationPoint;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.arith.EquationExpression;
import geogebra.common.kernel.locusequ.arith.EquationNumericValue;

import java.util.HashMap;
import java.util.Map;

import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.*;

/**
 * @author sergio
 * Common base class for EquationElements for conics.
 * All constructor subclasses must call this.computeMatrix() at the end.
 */
public abstract class EquationGenericConic extends EquationElement {

	/* 
     *               ( A[0]  A[3]    A[4] )
     *      matrix = ( A[3]  A[1]    A[5] )
     *               ( A[4]  A[5]    A[2] )
     */

    private EquationExpression[] matrix;
    private Map<EquationPoint, EquationPolarLine> polarLines;
    

    /**
     * General constructor.
     * @param conic {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationGenericConic(final GeoElement conic, final EquationScope scope) {
        super(conic, scope);
        this.polarLines = new HashMap<EquationPoint, EquationPolarLine>();
    }
    
    /**
     * Must compute a matrix such this:
     * 
     *               ( A[0]  A[3]    A[4] )
     *      matrix = ( A[3]  A[1]    A[5] )
     *               ( A[4]  A[5]    A[2] )
     */
    protected abstract void computeMatrix();
    

    /**
     * Setter for matrix. For use in computeMatrix impl.
     * @param matrix computed.
     */
    protected void setMatrix(final EquationExpression[] matrix) {
        this.matrix = matrix;
    }
    
    /**
     * @return matrix.
     */
    public EquationExpression[] getMatrix() { return this.matrix; }
    
    @Override
    protected EquationList forPointImpl(EquationPoint p) {
        return equation(sum(times( this.getMatrix()[0], pow(p.getXExpression(), EquationNumericValue.from(2))),
                            times( this.getMatrix()[1], pow(p.getYExpression(), EquationNumericValue.from(2))),
                            this.getMatrix()[2],
                            times( dbl(this.getMatrix()[3]), p.getXExpression(), p.getYExpression()),
                            times( dbl(this.getMatrix()[4]), p.getXExpression()),
                            times( dbl(this.getMatrix()[5]), p.getYExpression()))).toList();
    }


    @Override
    public boolean isAlgebraic() {
        return true;
    }
    

    /**
     * @param a one vector (length 3)
     * @param b another vector (length 3)
     * @return b * matrix * a
     */
    protected EquationExpression applyVectorsToMatrix(EquationExpression[] a, EquationExpression[] b) {
        return sum( times(b[0], sum( times(a[0], this.getMatrix()[0]),
                                     times(a[1], this.getMatrix()[3]),
                                     times(a[2], this.getMatrix()[4]))),
                    times(b[1], sum( times(a[0], this.getMatrix()[3]),
                                     times(a[1], this.getMatrix()[1]),
                                     times(a[2], this.getMatrix()[5]))),
                    times(b[2], sum( times(a[0], this.getMatrix()[4]),
                                     times(a[1], this.getMatrix()[5]),
                                     times(a[2], this.getMatrix()[2]))));
    }
    
    /**
     * Calculates the coefficients for the polar line determined by p.
     * @param p an {@link EquationPoint}
     * @return a 3-long vector containing expressions for the polar line.
     */
    public EquationExpression[] getPolarLineCoefficientsFor(final EquationPoint p) {
        return new EquationExpression[] {
                sum(times(p.getXExpression(), getMatrix()[0]), times(p.getYExpression(), getMatrix()[3]), getMatrix()[4]),
                sum(times(p.getXExpression(), getMatrix()[3]), times(p.getYExpression(), getMatrix()[1]), getMatrix()[5]),
                sum(times(p.getXExpression(), getMatrix()[4]), times(p.getYExpression(), getMatrix()[5]), getMatrix()[2])
        };
    }
    

    /**
     * @param p {@link EquationPoint}
     * @return a polar line of this conic going through p.
     */
    public EquationPolarLine getPolarLine(final EquationPoint p) {
        EquationPolarLine res = this.polarLines.get(p);
        
        if(res == null) {
            res = new EquationPolarLine(this, p, this.getScope());
            this.polarLines.put(p, res);
        }
        
        return res;
    }
    

    /**
     * Calculates a matrix for a circle given its center and a radius squared.
     * @param center of the circle.
     * @param radius2 radius squared.
     * @return a 6-long vector.
     */
    protected EquationExpression[] matrixForCircle(final EquationPoint center, final EquationExpression radius2) {
        EquationExpression[] circleMatrix = new EquationExpression[6];
        
        circleMatrix[0] = circleMatrix[1] = EquationNumericValue.from(1);
        circleMatrix[2] = diff(sum(sqr(center.getXExpression()),
                             sqr(center.getYExpression())),radius2);
        circleMatrix[3] = EquationNumericValue.from(0);
        circleMatrix[4] = center.getX().getOpposite();
        circleMatrix[5] = center.getY().getOpposite();
        
        return circleMatrix;
    }
    
    
    /**
     * Calculates a matrix for a circle given three points.
     * @param a first point.
     * @param b second point.
     * @param c third point.
     * @return a 6-long vector.
     */
    protected EquationExpression[] matrixForCircle(final EquationPoint a, final EquationPoint b, final EquationPoint c) {
        EquationExpression[] circleMatrix = new EquationExpression[6];
        
        circleMatrix[0] = det3(a.getXExpression(),a.getYExpression(), EquationNumericValue.from(1),
                               b.getXExpression(),b.getYExpression(), EquationNumericValue.from(1),
                               c.getXExpression(),c.getYExpression(), EquationNumericValue.from(1));
        circleMatrix[1] =  circleMatrix[0];
        circleMatrix[2] = det3(a.getXExpression(),a.getYExpression(), ssqr(a),
                               b.getXExpression(),b.getYExpression(), ssqr(b),
                               c.getXExpression(),c.getYExpression(), ssqr(c)).getOpposite();
        circleMatrix[3] = EquationNumericValue.from(0);
        circleMatrix[4] = div(det3(a.getYExpression(), ssqr(a), EquationNumericValue.from(1),
                                   b.getYExpression(), ssqr(b), EquationNumericValue.from(1),
                                   c.getYExpression(), ssqr(c), EquationNumericValue.from(1)),
                              EquationNumericValue.from(2));
        circleMatrix[5] = div(det3(a.getXExpression(), ssqr(a), EquationNumericValue.from(1),
                                   b.getXExpression(), ssqr(b), EquationNumericValue.from(1),
                                   c.getXExpression(), ssqr(c), EquationNumericValue.from(1)),
                              EquationNumericValue.from(2));
        
        return circleMatrix;
    }

    /**
     * @param s a point.
     * @return s_{x}^{2} + s_{y}^{2}
     */
    private EquationExpression ssqr(final EquationPoint s) {
        return sum(times(s.getXExpression(), s.getXExpression()),
                   times(s.getYExpression(), s.getYExpression()));
    }
}