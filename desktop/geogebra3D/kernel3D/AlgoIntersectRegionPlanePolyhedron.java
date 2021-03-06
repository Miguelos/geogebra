/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoIntersectLines.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoElementND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Algo for intersection of a plane with a polyhedron, outputs polygons
 * 
 * @author matthieu
 */
public class AlgoIntersectRegionPlanePolyhedron extends AlgoIntersectPathPlanePolygon3D {
	
	private GeoPolyhedron polyhedron;
	

	private OutputHandler<GeoPolygon3D> outputPolygons;
	protected OutputHandler<GeoSegment3D> outputSegments; // output
	private OutputHandler<GeoPoint3D> outputPoints;
	

	
	/**
	 * class extending Coords with reference to parent geo
	 *
	 */
	private class CoordsWithParent extends Coords implements Comparable<CoordsWithParent> {

		protected GeoElementND parent;
		
		private Double parameter;
		
		public CoordsWithParent(Double parameter, Coords v, GeoElementND parent) {
			super(v);
			this.parent = parent;
			this.parameter =  parameter;
		}

		/**
		 * 
		 * @return polygons on which this point belongs
		 */
		public TreeSet<GeoPolygon> getPolygons() {
			
			//parent is segment
			if (parent instanceof GeoSegmentND)
				return ((GeoSegmentND) parent).getEdgeOf();
			
			//parent is point
			return ((GeoPointND) parent).getVertexOf();
			
		}

		public int compareTo(CoordsWithParent o) {
			//first compare parameters
			if (Kernel.isGreater(parameter, o.parameter))
				return 1;
			if (Kernel.isGreater(o.parameter, parameter))
				return -1;
			
			//if same parameter, compare parents
			return compareParentTo(o);
			
		}
		
		/**
		 * compare parent to o
		 * @param o other coords
		 * @return comparison result
		 */
		public int compareParentTo(CoordsWithParent o){
			return parent.toGeoElement().compareTo(o.parent.toGeoElement());
		}
		
	}
	
	/**
	 * coords for each face
	 */
	protected TreeSet<CoordsWithParent> newCoords;

	
	/**
	 * bi-point for each intersection segment
	 *
	 */
	private class Segment {//implements Comparable<Segment>{
		protected CoordsWithParent p1, p2;
		protected boolean used = false;
		
		public Segment(CoordsWithParent p1, CoordsWithParent p2) {
			//if (p1.compareParentTo(p2)<0){
				this.p1 = p1;
				this.p2 = p2;
			/*}
			else{
				this.p2 = p1;
				this.p1 = p2;
			}*/
		}

		/*
		public int compareTo(Segment o) {
			//compare first point parents
			int ret = p1.compareParentTo(o.p1);
			if (ret==0)
				//compare second point parents
				return p2.compareParentTo(o.p2);
			return ret;
		}
		*/
		
	}
	

	/**
	 * common constructor
	 * 
	 * @param c
	 * @param labels
	 * @param plane plane
	 * @param p polyhedron
	 */
	public AlgoIntersectRegionPlanePolyhedron(Construction c, String[] labels,
			GeoPlane3D plane, GeoPolyhedron p) {



			super(c);
			

			setFirstInput(plane);
			setSecondInput(p);

			createOutput();

			
			setInputOutput(); // for AlgoElement

			// set labels
			/*
			if (labelsLength > 1) {
				compute((labelsLength + 1) / 2);// create maybe undefined outputs
				poly.setLabel(labels[0]);
				int d = 1;
				for (int i = 0; i < outputSegments.size(); i++)
					outputSegments.getElement(i).setLabel(labels[d + i]);
				d += outputSegments.size();
				for (int i = 0; i < outputPoints.size(); i++)
					outputPoints.getElement(i).setLabel(labels[d + i]);
			} else if (labelsLength == 1) {
				poly.setLabel(labels[0]);
			} else {
				poly.setLabel(null);
			}
			*/
			
			update();

		

	}

	@Override
	protected void setSecondInput(GeoElement geo){
		this.polyhedron = (GeoPolyhedron) geo;
	}

	@Override
	protected GeoElement getSecondInput(){
		return polyhedron;
	}

	


	
	
	
	
	@Override
	protected void addCoords(double parameter, Coords coords, GeoElementND geo){
		newCoords.add(new CoordsWithParent(parameter, coords, geo));
	}
	
	private TreeMap<GeoPolygon, ArrayList<Segment>> newCoordsList;
	
	@Override
	protected void setNewCoords(){
		
		if (newCoordsList==null)
			newCoordsList = new TreeMap<GeoPolygon, ArrayList<Segment>>();
		else
			newCoordsList.clear();
		
		/*
		if (originalEdges==null)
			originalEdges = new TreeMap<GeoElement, TreeMap<GeoElement,Segment>>();
		else
			originalEdges.clear();
			*/
		
		for (GeoPolygon polygon: polyhedron.getPolygons()){
			p = polygon;
			setNewCoordsList();
		}
		
		for (GeoPolygon polygon: polyhedron.getPolygonsLinked()){
			p = polygon;
			setNewCoordsList();
		}


	}
	
	private void setNewCoordsList(){
		//line origin and direction
		setIntersectionLine();
		
		
		//check if polygon is included in the plane		
		if (d1.isZero() && !(Kernel.isZero(o1.getW()))){//then include all edges of the polygon
			GeoPointND[] points = p.getPointsND();
			segmentCoords = new ArrayList<Segment>();
			GeoPointND p2 = points[0];
			for (int i = 0; i<points.length; i++){
				GeoPointND p1 = p2;
				p2 = points[(i+1)%(points.length)];
				
				segmentCoords.add(new Segment(
						new CoordsWithParent((double) i, p1.getInhomCoordsInD(3), p1), 
						new CoordsWithParent((double) i+1, p2.getInhomCoordsInD(3), p2)));
				
				newCoordsList.put(p, segmentCoords);
				//App.debug("\npoly (included):"+p+"\nsegmentCoords.size():"+segmentCoords.size());
			}

		}else{//regular case: polygon not included in plane

			// fill a new points map
			if (newCoords==null)
				newCoords = new TreeSet<CoordsWithParent>();
			else
				newCoords.clear();

			//add intersection coords
			intersectionsCoords(p);

			//add polygon points
			addPolygonPoints();

			if (newCoords.size()>1){ //save it only if at least two points
				segmentCoords = getSegmentsCoords();
				//add (polygon,segments) to newCoordsList
				if (segmentCoords.size()>0){
					newCoordsList.put(p, segmentCoords);
					//App.debug("\npoly:"+p+"\nnewCoords.size():"+newCoords.size()+"\nsegmentCoords.size():"+segmentCoords.size());
				}
			}
		}
		

	}
	
	/*
	 * segments equal to original edges
	 */
	//private TreeMap<GeoElement,TreeMap<GeoElement,Segment>> originalEdges;
	
	private ArrayList<Segment> getSegmentsCoords(){
		ArrayList<Segment> ret = new ArrayList<Segment>();
		
		Iterator<CoordsWithParent> it = newCoords.iterator();
		CoordsWithParent b = it.next();
		//use start/end of segment to merge following segments
		CoordsWithParent startSegment = null;
		CoordsWithParent endSegment = null;
		while (it.hasNext()) {
			CoordsWithParent a = b;
			b = it.next();
			//check if the segment is included in the polygon: check the midpoint
			if (checkMidpoint(p, a, b)){
				if (startSegment==null)
					startSegment = a; //new start segment
				endSegment = b; //extend segment to b
			}else{
				if (startSegment!=null){//add last correct segment
					ret.add(new Segment(startSegment,endSegment));
					startSegment=null;
				}
			}
		}
		
		if (startSegment!=null)//add last correct segment
			ret.add(new Segment(startSegment,endSegment));
		
		
		return ret;
	}
	
	
	
	@SuppressWarnings("serial")
	private class VerticesList extends ArrayList<ArrayList<Coords>>{
		
		protected int cumulateSize = 0;
		
		public VerticesList() {
			super();
		}

		@Override
		public boolean add(ArrayList<Coords> vertices){
			cumulateSize+=vertices.size();
			return super.add(vertices);
		}
		
		@Override
		public void clear(){
			cumulateSize = 0;
			super.clear();
		}
		
	}
	
	private VerticesList verticesList;
	
	
	
	private ArrayList<Segment> segmentCoords;
	
	/**
	 * find next vertex linking the start point of the polygon with new intersection segment
	 * @param p2
	 * @param startPoint
	 * @param oldPoint vertex before startPoint
	 * @return next vertex
	 */
	private CoordsWithParent nextVertex(GeoPolygon p2, CoordsWithParent startPoint, GeoElementND oldPoint){
		
		//get intersection segments coords for this polygon
		segmentCoords = newCoordsList.get(p2);
				
		CoordsWithParent a;
		CoordsWithParent b = null;
		
		//App.debug("\nstart parent:"+startPoint.parent+"\nold parent:"+oldPoint.parent);
		
		//check if for a segment, one of the vertex as same parent as starting vertex
		//then take the second point as next vertex
		boolean notFound = true;
		int i;
		for (i=0; i<segmentCoords.size() && notFound; i++){
			Segment segment = segmentCoords.get(i);
			a = segment.p1;
			if (a.parent==startPoint.parent){
				b = segment.p2;
				//App.debug("\ni:"+i+"\na:"+a.parent+"\nb:"+b.parent);
				if (b.parent!=oldPoint){ //prevent immediate return
					notFound = false;
				}
			}else{
				b = a;
				a = segment.p2;
				if (a.parent==startPoint.parent){
					//App.debug("\ni:"+i+"\na:"+a.parent+"\nb:"+b.parent);
					if (b.parent!=oldPoint){ //prevent immediate return
						notFound = false;
					}
				}
			}
		}


		if (notFound)
			b=null;
		else
			//remove the segment found: not usable anymore
			removeSegmentCoords(i-1,p2);

		return b;
	}
	
	private void removeSegmentCoords(int index, GeoPolygon p2){
		
		segmentCoords.remove(index);
		//App.debug("\np2:"+p2+"\nsize="+segmentCoords.size());
		if (segmentCoords.size()==0)
			newCoordsList.remove(p2);
			
	}
	
	
	/**
	 * find next vertex linking a vertex of the polyhedron to next segment
	 * @param startPoint start vertex
	 * @param oldPoint vertex before startPoint
	 * @return next vertex
	 */
	private CoordsWithParent nextVertex(CoordsWithParent startPoint, GeoElementND oldPoint){
		
		CoordsWithParent b;
		
		// 1) try keep same poly (interior point)
		if (newCoordsList.containsKey(p)){
			b=nextVertex(p, startPoint, oldPoint);
			if (b!=null)
				return b;
		}
		
		

		
		// 2) try other polygons
		TreeSet<GeoPolygon> polySet = startPoint.getPolygons();
		Iterator<GeoPolygon> it = polySet.iterator();
		GeoPolygon p2 = null;
		while (it.hasNext()){
			p2 = it.next();
			//App.debug("\np2:"+p2+"\np2==p:"+(p2==p)+"\nkey:"+newCoordsList.containsKey(p2));
			//find other polygon, contained as a key
			if (p2 != p && newCoordsList.containsKey(p2)){					
				//App.debug("\npoly2:"+p2);
				//try to find next vertex
				b=nextVertex(p2, startPoint, oldPoint);
				if (b!=null){ //if found
					p=p2;
					//App.debug("\nb.parent:"+b.parent);
					return b;
				}
			}
		}
		
		//3) return null: no next vertex
		return null;
		
	}
	
	/**
	 * Add vertices from one to the next
	 * @return vertices list
	 */
	private ArrayList<Coords> addVertices(){
		
		ArrayList<Coords> vertices = new ArrayList<Coords>();

		//take first segment for the face p
		segmentCoords = newCoordsList.get(p);
		
		//start with first point of the segment
		CoordsWithParent firstPoint = segmentCoords.get(0).p1;
		CoordsWithParent startPoint = segmentCoords.get(0).p2;
		removeSegmentCoords(0, p);
		vertices.add(firstPoint);
		//App.debug("\na.parent:"+firstPoint.parent);//App.debug("\n\n\n\n\n");
		//App.debug("\nb.parent:"+startPoint.parent+"\npoly:"+p);//App.debug("\n\n\n\n\n");
		//at first oldParent is null, so polygons A-B-A are possible
		GeoElementND oldParent = null;
		while (startPoint.parent!=firstPoint.parent){
			//if (!startPoint.isEqual(oldPoint))
			vertices.add(startPoint);
			CoordsWithParent c = nextVertex(startPoint,oldParent);
			if (c==null) //no next point
				return null;
			oldParent = startPoint.parent;
			startPoint = c;
			//App.debug("\nb.parent:"+startPoint.parent+"\npoly:"+p);//App.debug("\n\n\n\n\n");
		}
		
		return vertices;

	}
	
	@Override
	public void compute() {

		// set the point map
		setNewCoords();
		
		//App.debug("\noriginalEdges:"+originalEdges);
		
		// set segments
		if (newCoordsList.size()==0) { //no segment
			//outputSegments.adjustOutputSize(1);
			//outputSegments.getElement(0).setUndefined();
		} else {		
			

			if (verticesList == null)
				verticesList = new VerticesList();
			else
				verticesList.clear();

			//start with one face, set a polygon, then get a new face, etc.
			while (newCoordsList.size()!=0){
				//App.debug(newCoordsList.keySet());
				p = newCoordsList.firstKey();
				ArrayList<Coords> vertices = addVertices();
				if (vertices!=null) //prevent not matching search
					verticesList.add(vertices);
			}
			//App.debug(newCoordsList.keySet());
			
			
			//set output points
			outputPoints.adjustOutputSize(verticesList.cumulateSize,false);
			outputPoints.updateLabels();
			int index = 0;
			for (ArrayList<Coords> vertices : verticesList){
				int length = vertices.size();
				for (int i = 0; i<length; i++){
					GeoPoint3D point = outputPoints.getElement(index);
					point.setCoords(vertices.get(i));
					index++;
				}
			}
			
			//adjust output polygons size
			outputPolygons.adjustOutputSize(verticesList.size(), false);
			outputPolygons.updateLabels();
			
			//get points list
			GeoPoint3D[] points = new GeoPoint3D[verticesList.cumulateSize];
			points = outputPoints.getOutput(points);
			
			//set output segments and polygons
			outputSegments.adjustOutputSize(verticesList.cumulateSize,false);
			outputSegments.updateLabels();
			int polygonOffset = 0;
			int polygonIndex = 0;
			index = 0;
			for (ArrayList<Coords> vertices : verticesList){
				int length = vertices.size();
				//App.debug("polygonIndex: "+polygonIndex);
				GeoPolygon outputPoly = outputPolygons.getElement(polygonIndex);
				GeoPoint3D[] polyPoints = new GeoPoint3D[length];
				GeoSegment3D[] polySegments = new GeoSegment3D[length];
				for (int i = 0; i<length; i++){
					//App.debug(points[polygonOffset + i]);
					outputSegments.getElement(index).modifyInputPolyAndPoints(
							outputPoly,
							points[polygonOffset + i],
							points[polygonOffset + (i + 1) % length]);
					polyPoints[i] = points[polygonOffset + i];
					polySegments[i] = outputSegments.getElement(index);
					index++;
				}
				
				// update polygon
				outputPoly.setPoints(polyPoints, null, false); // don't create segments
				outputPoly.setSegments(polySegments);
				outputPoly.calcArea();
				
				polygonOffset+=length;
				polygonIndex++;
			}
			
			/*
			index = 0;
			for (ArrayList<Coords> vertices : verticesList){
				int length = vertices.size();
				for (int i = 0; i<length; i++){
					GeoSegmentND segment = (GeoSegmentND) outputSegments
							.getElement(index);
					setSegment(segment, vertices.get(i), vertices.get((i+1) % length));
					//((GeoElement) segment).update(); // TODO optimize it
					//App.debug(((GeoElement) segment).isDefined());
					index++;
				}
			}
			*/

			
		}
	}
	

    @Override
	protected boolean checkParameter(double t1){
    	return true; //nothing to check here
    }

    
	@Override
	public final Commands getClassName() {
        return Commands.IntersectRegion;
    }
	
	
	
	
	
	
	
	
	
	private final void createOutput(){
		
		outputPolygons = new OutputHandler<GeoPolygon3D>(
				new elementFactory<GeoPolygon3D>() {
					public GeoPolygon3D newElement() {
						GeoPolygon3D p = new GeoPolygon3D(cons);
						p.setParentAlgorithm(AlgoIntersectRegionPlanePolyhedron.this);
						p.setViewFlags(getFirstInput().getViewSet());
						return p;
					}
				});
		
		outputPolygons.adjustOutputSize(1);
		
		outputSegments = //createOutputSegments();
				new OutputHandler<GeoSegment3D>(
						new elementFactory<GeoSegment3D>() {
							public GeoSegment3D newElement() {
								GeoSegment3D segment = (GeoSegment3D) outputPolygons
										.getElement(0).createSegment(outputPoints.getElement(0), outputPoints.getElement(1), true);
								segment.setAuxiliaryObject(true);
								//segment.setLabelVisible(showNewSegmentsLabels);
								segment.setViewFlags(getFirstInput().getViewSet());
								return segment;
							}
						});
		
		outputPoints = new OutputHandler<GeoPoint3D>(
				new elementFactory<GeoPoint3D>() {
					public GeoPoint3D newElement() {
						GeoPoint3D newPoint = new GeoPoint3D(cons);
						newPoint.setCoords(0, 0, 0, 1);
						newPoint.setParentAlgorithm(AlgoIntersectRegionPlanePolyhedron.this);
						newPoint.setAuxiliaryObject(true);
						newPoint.setViewFlags(getFirstInput().getViewSet());
						/*
						newPoint.setPointSize(A.getPointSize());
						newPoint.setEuclidianVisible(A.isEuclidianVisible()
								|| B.isEuclidianVisible());
						newPoint.setAuxiliaryObject(true);
						newPoint.setViewFlags(A.getViewSet());
						GeoBoolean conditionToShow = A.getShowObjectCondition();
						if (conditionToShow == null)
							conditionToShow = B.getShowObjectCondition();
						if (conditionToShow != null) {
							try {
								((GeoElement) newPoint)
										.setShowObjectCondition(conditionToShow);
							} catch (Exception e) {
								// circular exception -- do nothing
							}
						}
						*/
						return newPoint;
					}
				});
	}
	
	
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = getFirstInput();
		input[1] = getSecondInput();

		// set dependencies
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}
		cons.addToAlgorithmList(this);
	}
}
