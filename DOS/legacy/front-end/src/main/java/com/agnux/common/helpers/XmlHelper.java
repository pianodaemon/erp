package com.agnux.common.helpers;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

public class XmlHelper
{
    
    
    public static String transformar(String xmlOrigen,String xslOrigen) throws Exception{
          //System.out.println("xmlOrigen:"+xmlOrigen);
          
          InputStream is = new ByteArrayInputStream(xmlOrigen.getBytes("UTF-8"));
          
          Source xmlSource = new StreamSource(is);
          Source xsltSource = new StreamSource(new File(xslOrigen));
          
          StringWriter cadenaSalida = new StringWriter();
          
          Result bufferResultado = new StreamResult(cadenaSalida);
          
          TransformerFactory factoriaTrans = TransformerFactory.newInstance();
          Transformer transformador = factoriaTrans.newTransformer(xsltSource);
          
          transformador.transform(xmlSource, bufferResultado);
          
          return cadenaSalida.toString();
    }
    
    
    
    
} 