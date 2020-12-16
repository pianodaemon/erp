/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxcInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author agnux
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/repcomparativoasventasanualesproducto/")
public class RepComparativoDeVentasAnualesProductoController {
     private static final Logger log  = Logger.getLogger(RepComparativoDeVentasAnualesProductoController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoCxc")
    private CxcInterfaceDao cxcDao;
        
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public CxcInterfaceDao getCxcDao() {
        return (CxcInterfaceDao) cxcDao;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", RepComparativoDeVentasAnualesProductoController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        
        ModelAndView x = new ModelAndView("repcomparativoasventasanualesproducto/startup", "title", "Reporte Comparativo de Ventas Anuales por Producto");
        
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

   
    //Cargar cargar datos iniciales para el buscador
   @RequestMapping(method = RequestMethod.POST, value="/getDatos.json")
        public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDatosJson(
        @RequestParam(value="iu", required=true) String id_user,
        Model model
    ){
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, Object> mes = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> mesActual = new ArrayList<HashMap<String, Object>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        mes.put("mesActual", TimeHelper.getMesActual());
        mes.put("anioActual", TimeHelper.getFechaActualY());
        mesActual.add(0, mes);
        
        jsonretorno.put("Anios", this.getCxcDao().getCxc_AnioReporteSaldoMensual());
        jsonretorno.put("ProdTipo", this.getCxcDao().getProductoTiposV2());
        jsonretorno.put("Dato", mesActual);
        return jsonretorno;
    }
   
   
      //obtiene los tipos de productos para el buscador de productos
    @RequestMapping(method = RequestMethod.POST, value = "/getProductoTipos.json")
    public @ResponseBody
    HashMap<String, ArrayList<HashMap<String, String>>> getProductoTiposJson(
            @RequestParam(value = "iu", required = true) String id_user_cod,
            Model model) {

        HashMap<String, ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String, ArrayList<HashMap<String, String>>>();

        ArrayList<HashMap<String, String>> arrayTiposProducto = new ArrayList<HashMap<String, String>>();
        arrayTiposProducto = this.getCxcDao().getProductoTipos();
        jsonretorno.put("prodTipos", arrayTiposProducto);

        return jsonretorno;
    }
    
   
   //Buscador de productos
    @RequestMapping(method = RequestMethod.POST, value = "/get_buscador_productos.json")
    public @ResponseBody
    HashMap<String, ArrayList<HashMap<String, String>>> get_buscador_productosJson(
            @RequestParam(value = "sku", required = true) String sku,
            @RequestParam(value = "tipo", required = true) String tipo,
            @RequestParam(value = "descripcion", required = true) String descripcion,
            @RequestParam(value = "iu", required = true) String id_user,
            Model model) {

        HashMap<String, ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String, ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));

        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

        jsonretorno.put("productos", this.getCxcDao().getBuscadorProductos(sku, tipo, descripcion, id_empresa));
        return jsonretorno;
    }
    //fin del buscador de productos
   
    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_clientes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> get_buscador_clientesJson(
            @RequestParam(value="cadena", required=true) String cadena,
            @RequestParam(value="filtro", required=true) Integer filtro,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("Clientes", this.getCxcDao().getBuscadorClientes(cadena,filtro,id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
   
    //obtiene las familias de acuerdo al tipo de producto
   @RequestMapping(method = RequestMethod.POST, value="/getFamilias.json")
        public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getFamiliasJson(
            @RequestParam(value="tipo", required=true) Integer tipo_producto,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ){
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
    
        jsonretorno.put("Familias", this.getCxcDao().getFamilias(tipo_producto, id_empresa));
        return jsonretorno;
    }
   
   
   
    //obtiene las subfamilias de acuerdo a la Familia
   @RequestMapping(method = RequestMethod.POST, value="/getSubFamilias.json")
        public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getSubFamiliasJson(
            @RequestParam(value="familia_id", required=true) Integer familia_id,
            Model model
        ){
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
    
        jsonretorno.put("SubFamilias", this.getCxcDao().getSubFamilias(familia_id));
        return jsonretorno;
    }
    
    //obtiene la estadistica de ventas en un periodo espesifico
    @RequestMapping(method = RequestMethod.POST, value="/getComparativo.json")
    public  @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> RepComparativoAnualesVentasProductoControllerJson(
            @RequestParam("familia_id")Integer familia,
            @RequestParam("subfamilia_id")Integer subfamilia,
            @RequestParam("tipo")Integer tipo_producto,
            @RequestParam("anio_in") Integer anio_inicial,
            @RequestParam("anio_fin") Integer anio_final,
            //@RequestParam(value="razon_cli") String cliente,
            @RequestParam(value = "producto", required = true) String producto,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getComparativoVentasProducto de {0}", RepComparativoDeVentasAnualesProductoController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> Comparativo = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        Comparativo = this.getCxcDao().getComparativoVentasProducto(producto,anio_inicial,anio_final,tipo_producto,familia,subfamilia,id_empresa);
        
        jsonretorno.put("Comparativo", Comparativo);
        
        return jsonretorno;
    }
    
}
