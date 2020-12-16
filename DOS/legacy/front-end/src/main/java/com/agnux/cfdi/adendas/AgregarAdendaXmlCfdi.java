package com.agnux.cfdi.adendas;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


public final class AgregarAdendaXmlCfdi {
    private String path_xml_cfdi;
    private Document doc_adenda;
    private Document DocCFDI;
    
    public AgregarAdendaXmlCfdi(String pathXmlCfdi, Document docAdenda) {
        this.setPath_xml_cfdi(pathXmlCfdi);
        this.setDoc_adenda(docAdenda);
    }
    
    public void agregarAdenda() throws ParserConfigurationException, SAXException, IOException, Exception{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        
        Document docCfdi = null;
        docCfdi = db.parse(new FileInputStream(new File(this.getPath_xml_cfdi())));
        Element element = docCfdi.getDocumentElement();
        
        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        //Tomamos el documento xml de la adenda
        Document docAdenda =  this.getDoc_adenda();
        Element etiquetaRaizACopiar = docAdenda.getDocumentElement(); 
        
        // Luego la copiamos bajo nuestra etiqueta hijaRaiz
        // El segundo atributo indica si queremos copiar los hijos
        Node etiquetaRaizCopiada = docCfdi.importNode(etiquetaRaizACopiar, true); 
        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        
        // Ya tenemos una copia de la etiqueta en nuestro document. Ahora la situamos bajo etiquetaHija
        element.appendChild(etiquetaRaizCopiada);
        
        this.setDocCFDI(docCfdi);
    }
    
    
    public String getXmlOutString() throws Exception {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer out = new StringWriter();
        tf.transform(new DOMSource(this.getDocCFDI()), new StreamResult(out));
 
        return out.toString();
    }
    
    public Document getDoc_adenda() {
        return doc_adenda;
    }

    public void setDoc_adenda(Document doc_adenda) {
        this.doc_adenda = doc_adenda;
    }

    public String getPath_xml_cfdi() {
        return path_xml_cfdi;
    }

    public void setPath_xml_cfdi(String path_xml_cfdi) {
        this.path_xml_cfdi = path_xml_cfdi;
    }
    
    public Document getDocCFDI() {
        return DocCFDI;
    }

    public void setDocCFDI(Document DocCFDI) {
        this.DocCFDI = DocCFDI;
    }
}
