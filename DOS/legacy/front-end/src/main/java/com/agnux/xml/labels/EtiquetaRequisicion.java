/*package com.agnux.xml.labels;

import com.agnux.common.obj.AgnuxXmlObject;

public class EtiquetaRequisicion  extends AgnuxXmlObject{
    public EtiquetaRequisicion(){
        super();
        
    }
}*/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.xml.labels;

import com.agnux.common.obj.AgnuxXmlObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
/**
 *
 * @author mi_compu
 */
public final class EtiquetaRequisicion extends AgnuxXmlObject{
    public EtiquetaRequisicion(LinkedHashMap<String, Object> datos_cuerpo){
        super();
        this.construyeNodoEtiqueta();
        this.construyeNodoCuerpo(datos_cuerpo);
    }
    private void construyeNodoEtiqueta(){
    	Document tmp = this.getDb().newDocument();
        Element element = tmp.createElement("etiquetaRequisicion");
        
        
        tmp.appendChild(element);
        this.setDoc(tmp);
    }
    
    public void construyeNodoCuerpo(LinkedHashMap<String, Object> datos_cuerpo){
        Document tmp = this.getDoc();
        Element element = tmp.createElement("cuerpo");
        element.setAttribute("titulo", datos_cuerpo.get("cuerpo_titulo").toString());
        element.setAttribute("archivo", datos_cuerpo.get("nombre_archivo").toString());
        
        Element tamaño_etiqueta = tmp.createElement("medida_etiqueta");
        tamaño_etiqueta.setAttribute("alto", datos_cuerpo.get("etiqueta_alto").toString());
        tamaño_etiqueta.setAttribute("largo", datos_cuerpo.get("etiqueta_largo").toString());
        element.appendChild(tamaño_etiqueta);
        
        
         Element impresora = tmp.createElement("impresora");
        Text modelo_impresora = tmp.createTextNode(datos_cuerpo.get("modelo_impresora").toString());
        impresora.appendChild(modelo_impresora);
        element.appendChild(impresora);
        
        Element lote = tmp.createElement("lote");
        lote.setAttribute("produccion", datos_cuerpo.get("lote_produccion").toString());
        lote.setAttribute("interno", datos_cuerpo.get("lote_interno").toString());
        element.appendChild(lote);
        

        Element producto = tmp.createElement("producto");
        producto.setAttribute("codigo",datos_cuerpo.get("producto_codigo").toString());
        producto.setAttribute("nombre", datos_cuerpo.get("producto_nombre").toString());
        producto.setAttribute("cantidad", datos_cuerpo.get("producto_cantidad").toString());
        producto.setAttribute("unidad", datos_cuerpo.get("producto_unidad").toString());
        lote.appendChild(producto);
        
        Element caducidad = tmp.createElement("caducidad");
        caducidad.setAttribute("caducidad",datos_cuerpo.get("caducidad_fecha").toString());
        //Text fecha_caducidad = tmp.createTextNode(datos_cuerpo.get("caducidad_fecha").toString());
        //caducidad.appendChild(fecha_caducidad);
        lote.appendChild(caducidad);
        
        tmp.getDocumentElement().appendChild(element);
        
        this.setDoc(tmp);
    }
    
    
    public Document obtieneDoc(){
        return this.getDoc();
    }
}
