package geogebra.awt;

import geogebra.common.awt.GShape;

public class GAffineTransformD implements geogebra.common.awt.GAffineTransform {

	private java.awt.geom.AffineTransform at;

	public GAffineTransformD() {
		at = new java.awt.geom.AffineTransform();
	}

	public GAffineTransformD(java.awt.geom.AffineTransform a) {
		at = a;
	}

	java.awt.geom.AffineTransform getImpl() {
		return at;
	}

	public void setTransform(geogebra.common.awt.GAffineTransform a) {
		at.setTransform(((GAffineTransformD)a).getImpl());
	}

	public void setTransform(double m00, double m10, double m01, double m11, double m02, double m12) {
		at.setTransform(m00, m10, m01, m11, m02, m12);
	}

	public void concatenate(geogebra.common.awt.GAffineTransform a) {
		at.concatenate(((GAffineTransformD)a).getImpl());
	}

	public double getScaleX() {
		return at.getScaleX();
	}
	
	public double getScaleY() {
		return at.getScaleY();
	}
	
	public double getShearX() {
		return at.getShearX();
	}
	
	public double getShearY() {
		return at.getShearY();
	}
	
	public double getTranslateX(){
    	return at.getTranslateX();
    }
	
    public double getTranslateY(){
    	return at.getTranslateY();
    }

	public static java.awt.geom.AffineTransform getAwtAffineTransform(geogebra.common.awt.GAffineTransform a) {
		if (!(a instanceof GAffineTransformD))
			return null;
		return ((GAffineTransformD)a).getImpl();
	}

	public GShape createTransformedShape(geogebra.common.awt.GShape shape) {
		java.awt.Shape ret = null;
		ret = at.createTransformedShape(geogebra.awt.GGenericShapeD.getAwtShape(shape));
		return new geogebra.awt.GGenericShapeD(ret);
	}

	public void transform(geogebra.common.awt.GPoint2D p, geogebra.common.awt.GPoint2D p2) {
		java.awt.geom.Point2D point = geogebra.awt.GPoint2DD.getAwtPoint2D(p);
		java.awt.geom.Point2D point2 = geogebra.awt.GPoint2DD.getAwtPoint2D(p2); 
		at.transform(point, point2);
		p2.setX(point2.getX());
		p2.setY(point2.getY());
	}

	public void transform(double[] labelCoords, int i, double[] labelCoords2,
			int j, int k) {
		at.transform(labelCoords, i, labelCoords2, j, k);
		
	}

	public geogebra.common.awt.GAffineTransform createInverse() throws Exception {
		return new GAffineTransformD(at.createInverse());
	}

	public void scale(double xscale, double d) {
		at.scale(xscale, d);
		
	}

	public void translate(double ax, double ay) {
		at.translate(ax, ay);
		
	}
}
