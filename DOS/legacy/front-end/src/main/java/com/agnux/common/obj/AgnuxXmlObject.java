/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.common.obj;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 *
 * @author pianodaemon
 */
public class AgnuxXmlObject {

    public AgnuxXmlObject(){
        this.setDbf(DocumentBuilderFactory.newInstance());
        try {
            this.setDb(this.getDbf().newDocumentBuilder());
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(AgnuxXmlObject.class.getName()).log(Level.SEVERE, null, ex);
        }
	this.setDomImpl(this.getDb().getDOMImplementation());
    }

    public DocumentBuilder getDb() {
        return db;
    }

    public void setDb(DocumentBuilder db) {
        this.db = db;
    }

    public DocumentBuilderFactory getDbf() {
        return dbf;
    }

    public void setDbf(DocumentBuilderFactory dbf) {
        this.dbf = dbf;
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public DOMImplementation getDomImpl() {
        return domImpl;
    }

    public void setDomImpl(DOMImplementation domImpl) {
        this.domImpl = domImpl;
    }
    private Document doc;
    private DocumentBuilder db;
    private DOMImplementation domImpl;
    private DocumentBuilderFactory dbf;

}
