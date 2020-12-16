package com.agnux.kemikal.controllers;


import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import com.agnux.kemikal.reportes.InvListaProductosXls;
import com.itextpdf.text.DocumentException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/invcarga/")
public class InvCargaController {
    private static final Logger log  = Logger.getLogger(InvCargaController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoInv")
    private InvInterfaceDao invDao;
    
    public InvInterfaceDao getInvDao() {
        return invDao;
    }
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvCargaController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        ModelAndView x = new ModelAndView("invcarga/startup", "title", "Carga de inventario f&iacute;sico");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
        x = x.addObject("url", resource.getUrl(request));
        x = x.addObject("username", user.getUserName());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        x = x.addObject("sucursal", user.getSucursal());
        
        String userId = String.valueOf(user.getUserId());
        
        String codificado = Base64Coder.encodeString(userId);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getCargar.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getCargarJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ){
        
        log.log(Level.INFO, "Ejecutando getCargarJson de {0}", InvCargaController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("Alms", this.getInvDao().getAlmacenesSucursal(id_empresa, id_sucursal));
        jsonretorno.put("Lineas", this.getInvDao().getProducto_Lineas(id_empresa));
        jsonretorno.put("Marcas", this.getInvDao().getProducto_Marcas(id_empresa));
        jsonretorno.put("Tipos", this.getInvDao().getProducto_TiposInventariable());
        
        return jsonretorno;
    }
    
    
    
    
    //obtiene los Familias del producto seleccionado
    @RequestMapping(method = RequestMethod.POST, value="/getSubFamiliasByFamProd.json")
    //public @ResponseBody HashMap<java.lang.String,java.lang.Object> getProveedorJson(
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getSubFamiliasByFamProdJson(
            @RequestParam(value="fam", required=true) String familia_id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getSubFamiliasByFamProdJson de {0}", InvCargaController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> subfamilias = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        subfamilias = this.getInvDao().getProducto_Subfamilias(id_empresa,familia_id );
        
        jsonretorno.put("SubFamilias", subfamilias);
        
        return jsonretorno;
    }
    
    
    //obtiene los Familias del producto seleccionado
    @RequestMapping(method = RequestMethod.POST, value="/getFamiliasByTipoProd.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getFamiliasByTipoProdJson(
            @RequestParam(value="tipo_prod", required=true) String tipo_prod,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getFamiliasByTipoProdJson de {0}", InvCargaController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> familias = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        familias = this.getInvDao().getInvProdSubFamiliasByTipoProd(id_empresa, tipo_prod);
        
        jsonretorno.put("Familias", familias);
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    //Genera Reporte de Dicas Promedio de entrega de OC
    @RequestMapping(value = "/getFormato/{tipo_reporte}/{almacen}/{linea}/{marca}/{tipo}/{familia}/{subfamilia}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getReporteJson(
                @PathVariable("tipo_reporte") String tipo_reporte,
                @PathVariable("almacen") String almacen,
                @PathVariable("linea") String linea,
                @PathVariable("marca") String marca,
                @PathVariable("tipo") String tipo,
                @PathVariable("familia") String familia,
                @PathVariable("subfamilia") String subfamilia,
                @PathVariable("iu") String id_user_cod,
                HttpServletRequest request,
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> lista_productos = new ArrayList<HashMap<String, String>>();
        int app_selected=178;
        Integer id_empresa = 0;
        String rfcEmpresa="";
        String dir_tmp = "";
        String file_name = "";
        String fileout = "";
        String data_string = "";
        
        String tituloReporte="Descargar fomato en excel";
        System.out.println(tituloReporte);
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        rfcEmpresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
       
        //obtener el directorio temporal
        //String dir_tmp = System.getProperty("java.io.tmpdir");
        dir_tmp = this.getGralDao().getTmpDir();
        
        
        //File file_dir_tmp = new File(dir_tmp);
        //System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        if(tipo_reporte.equals("1")){
            file_name = rfcEmpresa+"_Productos"+TimeHelper.getFechaActualYMDHMS()+".xls";
        }
        
        if(tipo_reporte.equals("2")){
            file_name = rfcEmpresa+"_Lotes"+TimeHelper.getFechaActualYMDHMS()+".xls";
        }
        
        //ruta de archivo de salida
        fileout = dir_tmp + file_name;
        
        data_string = app_selected+"___"+id_usuario+"___"+id_empresa+"___"+almacen+"___"+linea+"___"+marca+"___"+tipo+"___"+familia+"___"+subfamilia+"___"+tipo_reporte;
        
        lista_productos = this.getInvDao().selectFunctionForInvReporte(app_selected,data_string);
        
        InvListaProductosXls excel = new InvListaProductosXls(tipo_reporte, fileout,lista_productos);
        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/xls");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());  	
        response.flushBuffer();
        
        FileHelper.delete(fileout);
        
        return null;
    }
    
    
    
    
    
    
    //para subir el archivo a la carpeta temporal de java
    @RequestMapping(method = RequestMethod.POST, value="/fileUpload.json")
    public @ResponseBody String fileUploadJson(
            @RequestParam(value="file", required=true) MultipartFile upload,
            Model model, Exception exception
        ) {
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        String retorno="";
        if (!upload.isEmpty()) {
            try {
                byte[] bytes = upload.getBytes();
                String urlSave = FileHelper.saveByteFile(bytes, this.getGralDao().getTmpDir()+upload.getOriginalFilename());
                
                String ul_img = upload.getOriginalFilename();
                
                jsonretorno.put("url",ul_img);
                jsonretorno.put("success","true");
                retorno="true";
            } catch (IOException ex) {
                retorno="false";
            }

        } else {
            log.log(Level.INFO, "Test upload {0}", "uploadFailure");
            jsonretorno.put("url","no");
            jsonretorno.put("success","false");
            retorno="false";
        }
        return retorno;
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/deleteFile.json")
    public @ResponseBody HashMap<String, Object> getDeleteFileJson(
            @RequestParam(value="file", required=true) String file_name,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ){
        
        log.log(Level.INFO, "Ejecutando getDeleteFileJson de {0}", InvCargaController.class.getName());
        HashMap<String, Object> jsonretorno = new HashMap<String, Object>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        //Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //userDat = this.getHomeDao().getUserById(id_usuario);
        
        //Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));

        String dir_tmp = this.getGralDao().getTmpDir();
        
        //ruta de archivo de salida
        String fileout = dir_tmp + file_name;
        
        File file = new File(fileout);
        boolean delete = false;
        String msj="";
        
        if(file.exists()){
            try {
                delete = FileHelper.delete(fileout);
                msj = "El Archivo cargado fue eliminado";
            } catch (Exception ex) {
                msj = "No se ha podido eliminar el Archivo. Intente nuevamente";
            }
        }
        
        jsonretorno.put("success", delete);
        jsonretorno.put("msj", msj);
        
        return jsonretorno;
    }
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/process_file.json")
    public @ResponseBody HashMap<String,Object> getProcessFileJson(
            @RequestParam(value="select_tipo_reporte", required=true) String select_tipo_reporte,
            @RequestParam(value="nombre_archivo", required=true) String file_name,
            @ModelAttribute("user") UserSessionData user
        ) {
            //Cargar arkchivo
            ArrayList<HashMap<String, String>> errors = new ArrayList<HashMap<String, String>>();
            HashMap<String, Object> jsonretorno = new HashMap<String, Object>();
            HashMap<String, String> userDat = new HashMap<String, String>();
            
            Integer app_selected = 178;
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            String data_string="";
            String success="true";
            String msj="";
            boolean actualizar=true;
            boolean formato_corrcto=false;
            String patron_enteros = "[0-9]*";
            String patron_decimales = "^[0-9]{1,7}+.[0-9]{0,4}";
            
            userDat = this.getHomeDao().getUserById(id_usuario);
            Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
            Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
            
            String dir_tmp = this.getGralDao().getTmpDir();
            
            //ruta de archivo de salida
            String fileout = dir_tmp + file_name;
            
            //Este es para eliminar todos los registros de la tabla temporal
            int delete = this.getInvDao().getDeleteFromInvExiTmp(select_tipo_reporte, id_empresa);
            
            
            String id_prod = "";
            String codigo = "";
            String id_almacen = "";
            String existencia = "";
            
            String lote_int = "";
            String lote_prov = "";
            
            try {
                FileInputStream input = new FileInputStream(fileout);
                POIFSFileSystem fs = new POIFSFileSystem(input);
                HSSFWorkbook wb = new HSSFWorkbook(fs);
                HSSFSheet sheet = wb.getSheetAt(0);
                Row row;
                //Cell cell;
                int y=0;
                
                System.out.println("sheet.getSheetName(): "+sheet.getSheetName());
                
                
                if (sheet.getSheetName().indexOf("PRODUCT") > -1){
                    if (select_tipo_reporte.equals("1")){
                        formato_corrcto=true;
                    }
                }else{
                    if (sheet.getSheetName().indexOf("LOTE") > -1){
                        if (select_tipo_reporte.equals("2")){
                            formato_corrcto=true;
                        }
                    }
                }
                
                
                if(formato_corrcto){
                    System.out.println("::INICIA CARGA DE DATOS A LA TABLA TEMPORAL:::::::::::::::::::::::::::::");

                    for (int i=1; i<=sheet.getLastRowNum(); i++) {
                        row = sheet.getRow(i);

                        //Inventario
                        if(select_tipo_reporte.equals("1")){
                            id_prod = verificarTipoDatoCelda(row.getCell(0),"int", false);
                            codigo = verificarTipoDatoCelda(row.getCell(1),"string",false);
                            id_almacen = verificarTipoDatoCelda(row.getCell(4),"int", false);
                            existencia = verificarTipoDatoCelda(row.getCell(6),"double",true);
                            
                            existencia = StringHelper.removerComas(existencia);
                            //System.out.println("Data: "+id_empresa+"___"+id_prod+"___"+codigo+"___"+id_almacen+"___"+existencia);
                            if(!id_almacen.matches(patron_enteros)){
                                success="false";
                                msj = "Valor para NO_ALMACEN no valido. <br>Fila "+(i+1) +"  =>   no_prod: <b>"+id_prod+"</b>     codigo: <b>"+codigo+"</b>      no_almacen: <b>"+id_almacen+"</b>      existencia: <b>"+existencia+"</b>";
                                actualizar=false;
                                break;
                            }

                            if(!id_prod.matches(patron_enteros)){
                                success="false";
                                msj = "Valor para NO_PROD no valido. <br>Fila "+(i+1) +"  =>   no_prod: <b>"+id_prod+"</b>     codigo: <b>"+codigo+"</b>      no_almacen: <b>"+id_almacen+"</b>      existencia: <b>"+existencia+"</b>";
                                actualizar=false;
                                break;
                            }

                            if(!existencia.equals("")){
                                /*
                                if(!existencia.matches(patron_decimales)){
                                    success="false";
                                    msj = "Valor para Existencia no valido. <br>Fila "+(i+1) +"  =>   no_prod: <b>"+id_prod+"</b>     codigo: <b>"+codigo+"</b>      no_almacen: <b>"+id_almacen+"</b>      existencia: <b>"+existencia+"</b>";
                                    actualizar=false;
                                    break;
                                }
                                */

                                data_string = id_empresa+"___"+id_prod+"___"+codigo+"___"+id_almacen+"___"+existencia;

                                int affectedRow = this.getInvDao().getInsertInvExiTmp(select_tipo_reporte, data_string);

                                if(affectedRow<=0){
                                    //Aqui entra porque hubo errores al intentar insertar el registro en inv_exi_tmp
                                    success="false";
                                    msj = "Error al intentar actualizar datos: <br>Fila "+(i+1) +"  =>   no_prod: <b>"+id_prod+"</b>     codigo: <b>"+codigo+"</b>      no_almacen: <b>"+id_almacen+"</b>      existencia: <b>"+existencia+"</b>";
                                    System.out.println(msj);

                                    //Eliminar contenido de la tabla inv_exi_tmp
                                    delete = this.getInvDao().getDeleteFromInvExiTmp(select_tipo_reporte, id_empresa);

                                    actualizar=false;
                                    break;
                                }
                            }
                        }


                        //LOTES
                        if(select_tipo_reporte.equals("2")){
                            id_prod = verificarTipoDatoCelda(row.getCell(0),"int",false);
                            codigo = verificarTipoDatoCelda(row.getCell(1),"string",false);
                            id_almacen = verificarTipoDatoCelda(row.getCell(4),"int",false);
                            lote_int = verificarTipoDatoCelda(row.getCell(6),"string", false);
                            lote_prov = verificarTipoDatoCelda(row.getCell(7),"string",false);
                            existencia = verificarTipoDatoCelda(row.getCell(8), "double",true);
                            
                            existencia = StringHelper.removerComas(existencia);
                            
                            //System.out.println("Fila "+(i+1) +" => "+id_empresa+"___"+id_prod+"___"+codigo+"___"+id_almacen+"___"+lote_int+"___"+lote_prov+"___"+existencia);
                            
                            if(!id_almacen.matches(patron_enteros)){
                                success="false";
                                msj = "Valor para NO_ALMACEN no valido. <br>Fila "+(i+1) +"  =>   no_prod: <b>"+id_prod+"</b>     codigo: <b>"+codigo+"</b>      no_almacen: <b>"+id_almacen+"</b>      lote_int: <b>"+lote_int+"</b>      lote_prov: <b>"+lote_prov+"</b>      existencia: <b>"+existencia+"</b>";
                                actualizar=false;
                                break;
                            }

                            if(!id_prod.matches(patron_enteros)){
                                success="false";
                                msj = "Valor para NO_PROD no valido. <br>Fila "+(i+1) +"  =>   no_prod: <b>"+id_prod+"</b>     codigo: <b>"+codigo+"</b>      no_almacen: <b>"+id_almacen+"</b>      lote_int: <b>"+lote_int+"</b>      lote_prov: <b>"+lote_prov+"</b>      existencia: <b>"+existencia+"</b>";
                                actualizar=false;
                                break;
                            }

                            if(!existencia.equals("")){
                                /*
                                if(!existencia.matches(patron_decimales)){
                                    success="false";
                                    msj = "Valor para Existencia no valido. <br>Fila "+(i+1) +"  =>   no_prod: <b>"+id_prod+"</b>     codigo: <b>"+codigo+"</b>      no_almacen: <b>"+id_almacen+"</b>      lote_int: <b>"+lote_int+"</b>      lote_prov: <b>"+lote_prov+"</b>      existencia: <b>"+existencia+"</b>";
                                    actualizar=false;
                                    break;
                                }
                                */
                                data_string = id_empresa+"___"+id_prod+"___"+codigo+"___"+id_almacen+"___"+lote_int+"___"+lote_prov+"___"+existencia;

                                int affectedRow = this.getInvDao().getInsertInvExiTmp(select_tipo_reporte, data_string);

                                if(affectedRow<=0){
                                    //Aqui entra porque hubo errores al intentar insertar el registro en inv_lote_tmp
                                    success="false";
                                    msj = "Error al intentar actualizar datos: <br>Fila "+(i+1) +"  =>   no_prod: <b>"+id_prod+"</b>     codigo: <b>"+codigo+"</b>      no_almacen: <b>"+id_almacen+"</b>      lote_int: <b>"+lote_int+"</b>      lote_prov: <b>"+lote_prov+"</b>      existencia: <b>"+existencia+"</b>";
                                    System.out.println(msj);

                                    //Eliminar contenido de la tabla inv_exi_tmp
                                    delete = this.getInvDao().getDeleteFromInvExiTmp(select_tipo_reporte, id_empresa);

                                    actualizar=false;
                                    break;
                                }
                            }
                        }

                        y++;
                    }

                    System.out.println("::FINALIZA LA CARGA DE DATOS A LA TABLA TEMPORAL:::::::::::::::::::::::::::::");
                    
                }else{
                    success="false";
                    msj = "Error: No es posible realizar la carga, el formato no corresponde al Tipo de Reporte seleccionado.";
                    actualizar=false;
                }

                
                
                
                //Cerrar archivo
                input.close();


                if(actualizar){
                    
                    System.out.println("::INICIA EJECUCION DE PROCESO QUE ACTUALIZA EL INVENTARIO::::::::::::::::::::::::::::");
                    
                    //Ejecutar procedimiento de actualizacion de inventario(Actualizar registros en inv_exi)
                    success = this.getInvDao().getUpdateInvExi(id_usuario, id_empresa, id_sucursal, Integer.valueOf(select_tipo_reporte));
                    
                    if(success.equals("true")){
                        if (select_tipo_reporte.equals("1")){
                            msj="El Inventario se ha ctualizado con &eacute;xito.";
                        }else{
                            if (select_tipo_reporte.equals("2")){
                                msj="El Inventario de Lotes se ha ctualizado con &eacute;xito.";
                            }else{
                                msj="El Inventario se ha ctualizado con &eacute;xito.";
                            }
                        }
                    }else{
                        //System.out.println("success::: "+success);
                        
                        String msj2="";
                        
                        String arregloError[] = success.split("___");
                        int detail=0;
                        int error=0;
                        for(int i=0; i<arregloError.length; i++){
                            if (arregloError[i].indexOf("Detail") > -1){
                                if(detail==0){
                                    msj2 += arregloError[i]+"\n";
                                    detail++;
                                }
                            }
                            
                            if (arregloError[i].indexOf("ERROR") > -1){
                                if(error==0){
                                    msj2 += arregloError[i].substring(arregloError[i].indexOf("ERROR"))+"\n";
                                    error++;
                                }
                            }
                        }
                        
                        if(!msj2.equals("")){
                            success="false";
                            msj="No se ha podido actualizarl el inventario debido a errores en el proceso. Intente nuevamente.\n\n"+msj2;
                        }else{
                            success="false";
                            msj="No se ha podido actualizarl el inventario debido a errores en el proceso. Intente nuevamente.";   
                        }
                    }
                    
                    System.out.println("::TERMINA DE PROCESO QUE ACTUALIZA EL INVENTARIO => "+msj);
                }


                System.out.println("Total => "+ y);

            } catch (IOException ioe) {
                success="false";
                msj="No fue posible leer el archivo. Cargue nuevamente.";
            }
            
            jsonretorno.put("success", success);
            jsonretorno.put("msj", msj);
            
            System.out.println("success:"+jsonretorno.get("success")+"   msj:"+jsonretorno.get("msj"));
            
        return jsonretorno;
    }
    
    
    
    private String verificarTipoDatoCelda(Cell celda, String tipo_dato, boolean decimal){
        String dato="";
        try{
            if(celda.getCellType() == Cell.CELL_TYPE_STRING){
                dato = celda.getStringCellValue();
                //System.out.println("getCellType:"+celda.getCellType()+"       Value:"+String.valueOf(celda.getStringCellValue()));
            }

            if(celda.getCellType() == Cell.CELL_TYPE_NUMERIC){
                if(tipo_dato.equals("string")){
                    dato = String.valueOf(celda.getNumericCellValue());
                }

                if(tipo_dato.equals("int")){
                    dato = String.valueOf((int)celda.getNumericCellValue());
                }

                if(tipo_dato.equals("double")){
                    dato = StringHelper.roundDouble(celda.getNumericCellValue(), 4);
                }
                //System.out.println("getCellType:"+celda.getCellType()+"       Value:"+String.valueOf(celda.getNumericCellValue()));
            }
        }catch (NullPointerException ex) {
            System.out.println("Entro en NULL");
        }

        
        return dato;
    }
    
    
    
}
