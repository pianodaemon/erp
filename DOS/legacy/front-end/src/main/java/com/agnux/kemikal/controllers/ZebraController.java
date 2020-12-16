/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author vale
 */

@Controller
@RequestMapping("/zebra/")
public class ZebraController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(ZebraController.class.getName());
    
    private static enum Action { BUSCA_TRABAJO ,CONFIRMAR };
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    @RequestMapping(value="/master.agnux")
    public ModelAndView master(HttpServletRequest request, HttpServletResponse response,
     @RequestParam(value="command", required=true) String command,
     @RequestParam(value="name_file", required=true) String name_file
     )throws ServletException, IOException, Exception {
        
        
        //log.log(Level.INFO , "-------------------------------------------------------------------------------------------");
        //log.log(Level.INFO, "Ejecutando master de {0}", ZebraController.class.getName());
        //log.log(Level.INFO , "-------------------------------------------------------------------------------------------");
        
        ModelAndView x = new ModelAndView("zebra/master");
        String salida = "";
        String ip_cliente = request.getRemoteAddr();
        ip_cliente = ip_cliente.replace(".", "");
        
        switch ( ZebraController.Action.valueOf(command.toUpperCase()) ) {
            case BUSCA_TRABAJO:
                    
                    //log.info("Entrando en el Comando --> " + command.toUpperCase());
                    
                    String name = this.getFileName(ip_cliente);
                    if(!name.equals("")){
                        //lee el xml 
                        x = x.addObject("response", FileHelper.stringFromFile(this.getGralDao().getZebraInDir()+"/"+name));
                        
                        //pasa e archivo de in a processing
                        FileHelper.move(this.getGralDao().getZebraInDir()+"/"+name, this.getGralDao().getZebraProcessingDir()+"/"+name);
                    }else{
                        x = x.addObject("response", generateXmlResponse("false"));
                    }
                    
                break;
            
            case CONFIRMAR:
                    //pasa de la carpeta processing a out
                    FileHelper.move(this.getGralDao().getZebraProcessingDir()+"/"+name_file, this.getGralDao().getZebraOutDir()+"/"+name_file);
                    //log.info("Nombre archivo --> " + name_file.toUpperCase());
                    //log.info("Entrando en el Comando --> " + command.toUpperCase());
                    x = x.addObject("response", generateXmlResponse(salida));
                    
                break;
                
            default:
                //log.log(Level.SEVERE, "El comando" + command +" no existe");
                break;

        }
        //String command = null;
        
        

        return x;
    }
    
    private String getFileName(String ip){
        String file_retorno = "";
        
        String file_dir = this.getGralDao().getZebraInDir()+"/";
        List<File> archivos = FileHelper.listRegularFilesOfDirectory(file_dir);
        //log.log(Level.INFO, "Ipe {0}",ip+"   dir"+file_dir);
        for (File archivo : archivos){
            String nombre_archivo = archivo.getName();
            String arr_file[] = nombre_archivo.split("\\_");
            //log.log(Level.INFO, "Nombre archivo {0}",nombre_archivo);
            if(ip.equals(arr_file[0]) ){
                file_retorno = archivo.getName();
                break;
            }
        }
        
        return file_retorno;
    }
    
    
    private String generateXmlResponse(String salida) {
        return salida;
    }
}
