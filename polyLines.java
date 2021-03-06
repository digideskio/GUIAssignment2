/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package guiassignment2;

import java.util.*;
import org.jdom2.*;

/**
 * This class is used to obtain all the information available about a bone
 * record. The function getBoneRecs is used to initiate this process. This
 * process is not initiated in the class constructor. XMLParse class is
 * implemented to retrieve the information from the bone record XML files.
 * <br>
 * When getBoneRecs is called, it reads the information from bones.xml. Then
 * the individual records uniqueID is extracted to open the bone records
 * XML file that contains all the information necessary to draw the bone on
 * the mammoth site map.
 * <br>
 * The points for a bone record are stored in a two dimensional ArrayList where
 * each row corresponds to a different  bone record. Each column in a row
 * corresponds to a differnt poly line that makes up the bone in that row.
 * This data is retrieved from the class and used in the MapPainter to draw
 * the bones on the mammoth site map.
 * 
 * @author Benjamin Sherman, Derek Stotz, & Erik Hattervig
 */
public class polyLines
{
    
    // stores a bone record data elements
        public ArrayList<String> xymin;
        public ArrayList<String> xymax;
        public ArrayList<String> uniqueID;
        public ArrayList<Integer> objectnum;
        public ArrayList<String> taxon;
        public ArrayList<Integer> element;
        public ArrayList<String> subElement;
        public ArrayList<String> side;
        public ArrayList<String> completeness;
        public ArrayList<String> expside;
        public ArrayList<String> articulate;
        public ArrayList<String> gender;
        public ArrayList<String> datefound;
        public ArrayList<Double> elevation;
        public ArrayList<Integer> objectid;
        public ArrayList<Double> shapelength;   
        
        public ArrayList<ArrayList<Double[]>> allPolyPoints;

    // stores all bone record poly lines
    private  List<Element> bonerecs;
    // constructor gets working tree of bones.xml data,
    // then it calls getBoneRecs to get all bone record poly lines
    public  polyLines()
    {
        // initialize class variables
        xymin = new ArrayList<>();
        xymax = new ArrayList<>();
        uniqueID = new ArrayList<>();
        objectnum = new ArrayList<>();
        taxon = new ArrayList<>();
        element = new ArrayList<>();
        subElement = new ArrayList<>();
        side = new ArrayList<>();
        completeness = new ArrayList<>();
        expside = new ArrayList<>();
        articulate = new ArrayList<>();
        gender = new ArrayList<>();
        datefound = new ArrayList<>();
        elevation = new ArrayList<>();
        objectid = new ArrayList<>();
        shapelength = new ArrayList<>();
        allPolyPoints = new ArrayList<>(); 

    }
   /**
    * This function is iterates through all the bone records listed in bones.xml.
    * It then opens up the corresponding bone record xml file. There are 352 valid
    * bone record files. <br>
    * It iterates through each record id in bones.xml, finds the records
    * files, and stores the polyline elements as well as all other record info.
    *
    */
    public void getBoneRecs()
    {
        polyLines line = new polyLines();
        XMLParse BoneRecs = new XMLParse("bonexml/bones.xml");
        Element root = BoneRecs.getRoot();

       bonerecs = root.getChildren("bonerec");
       //polyLines rec = new polyLines();
       Element record;
       Element boneRec;
       for(int i = 0; i < bonerecs.size(); i++)
       {
           record = bonerecs.get(i).clone();
           uniqueID.add(record.getChildTextTrim("uniqueid"));
           datefound.add(record.getChildTextTrim("datefound"));
           objectnum.add(Integer.parseInt(record.getChildTextTrim("objectnum")));
           taxon.add(record.getChildTextTrim("taxon"));
           element.add(Integer.parseInt(record.getChildTextTrim("element")));
           subElement.add(record.getChildTextTrim("subelement"));
           side.add(record.getChildTextTrim("side"));
           completeness.add(record.getChildTextTrim("completeness"));
           expside.add(record.getChildTextTrim("expside"));
           articulate.add(record.getChildTextTrim("articulate"));
           gender.add(record.getChildTextTrim("gender"));
           elevation.add(Double.parseDouble(record.getChildTextTrim("elevation")));
           objectid.add(Integer.parseInt(record.getChildTextTrim("objectid")));
           shapelength.add(Double.parseDouble(record.getChildTextTrim("shapelength")));
           XMLParse bone  = new XMLParse("bonexml/" + uniqueID.get(i) + ".xml");
           boneRec = bone.getRoot();
           // the reference to a bone record xml file from  bones.xml  has just
           // tried to open. If the bone record that we tried to open doesn't 
           // exist, this if statement prevents the program from crashing as 
           // getting a null child is illegal.
           if(boneRec != null)
           {
                xymin.add(boneRec.getChildTextTrim("xymin"));
                xymax.add(boneRec.getChildTextTrim("xymax"));
                allPolyPoints.add(getPolyLines(bone.getRoot()));
           }
       }
    }
    
    /**
     * This function is given all the children of a bone record. Then the poly
     * line child which contains all the points that must be drawn to make a 
     * bone is extracted. From there, there are mulitple polyline children
     * that are looped through, getting all the points for each polyline and
     * storing it in an ArrayList which contains arrays of Doubles. <br>
     * The polypoints are extracted as one long line of strings. From there
     * the long string has all uncessary characters removed and then is split on
     * spaces. Then the Array of strings are parsed into doubles.
     * 
     * @param current contains all the children of a bone record.
     * 
     * @return xyPoints ArrayList of Double arrays. Each Double array in the ArrayList
     * contains one polyline. Each element in the ArrayList contains all of one
     * bones polylines.
     * 
     */
    private ArrayList<Double[]> getPolyLines( Element current )
    {
        String[] numbers;
        String numStr;
        
        // get list of children
        List<Element> children;
        children = current.getChildren();
        // get child that has poly line points
        children = children.get(3).getChildren("polyline");
        ArrayList<Double[]> xyPoints = new ArrayList<>();
        
        // this for loop gets the values of a poly line in a 
        // bone record as a long string. From here these strings are split and
        // all non numerical characters are removed. After that, the string
        // is split int individual strings that each contain either an x or y
        // point. All x points are in the even indices and all y points are
        // in the odd indices. 
        for(int i = 0; i < children.size(); i++)
        {
            numStr = children.get(i).getValue();
            numStr = numStr.replace("\n", "");
            numStr = numStr.replace("  ", " ");
            numStr = numStr.trim();
            numbers = numStr.split(" ");
            // getPolyPoints is a function that given an array of strings,
            // it parses those strings into doubles which are then stored
            xyPoints.add(getPolyPoints(numbers));
        }
        return xyPoints;
    }
    

    /**
    * This function given an array of strings, parses those strings into
    * integers and stores them in an array of doubles which are returned
    * from the function.
    */
    private Double[] getPolyPoints(String[] x_y)
    {
        Double[] points = new Double[x_y.length];
        Double temp;
        for(int i = 0; i < x_y.length; i++)
        {
            temp = Double.parseDouble(x_y[i]);
            points[i] = temp;
        }   
        return points;
    }
}