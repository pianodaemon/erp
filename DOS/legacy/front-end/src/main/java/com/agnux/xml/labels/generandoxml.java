/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.xml.labels;


import com.agnux.common.obj.AgnuxXmlObject;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;




/**
 *
 * @author mi_compu
 */
public class generandoxml  {
    
    public generandoxml(Document doc) throws TransformerException {
        //super();
        this.createxml(doc);
    }
   
 
    public String createxml(Document doc) throws TransformerException {
        /////////////////
            //Output the XML

            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            //create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            String xmlString = sw.toString();

            //print xml
            //System.out.println("IMPRIMIENDO EL  XML de la clase generando XML:\n\n" + xmlString);
            
            //helpers.FileHelper.createFileWithText("/home/mi_compu/NetBeansProjects/Ejemploxml", "xmletiquetaproduccion_b", xmlString);
        
        return xmlString;
    }
    
}
