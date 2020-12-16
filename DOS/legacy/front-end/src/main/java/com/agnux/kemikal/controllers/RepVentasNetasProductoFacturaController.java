/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxcInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.PdfRepVentasNetasProductoFactura;
import com.agnux.kemikal.reportes.PdfReporteVentasNetasProductoFacturados;
import com.agnux.kemikal.reportes.PdfReporteVentasNetasSumatoriaxClientes;
import com.agnux.kemikal.reportes.PdfReporteVentasNetasSumatoriaxProducto;
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

@Controller
@SessionAttributes({"user"})
@RequestMapping("/repventasnetasproductofactura/")
public class RepVentasNetasProductoFacturaController {
    private static final Logger log = Logger.getLogger(RepVentasNetasProductoFacturaController.class.getName());
    ResourceProject resource = new ResourceProject();
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    @Autowired
    @Qualifier("daoCxc")
    private CxcInterfaceDao cxcDao;
    
    public CxcInterfaceDao getCxcDao() {
        return cxcDao;
    }
    
    public void setCxcDao(CxcInterfaceDao cxcDao) {
        this.cxcDao = cxcDao;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    @RequestMapping(value = "/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        log.log(Level.INFO, "Ejecutando starUp de {0}", RepVentasNetasProductoFacturaController.class.getName());

        LinkedHashMap<String, String> infoConstruccionTabla = new LinkedHashMap<String, String>();
        
        ModelAndView x = new ModelAndView("repventasnetasproductofactura/startup", "title", "Ventas Netas por producto desglosado por factura");
        
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

    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value = "/get_buscador_clientes.json")
    public @ResponseBody
    HashMap<String, ArrayList<HashMap<String, Object>>> get_buscador_clientesJson(
            @RequestParam(value = "cadena", required = true) String cadena,
            @RequestParam(value = "filtro", required = true) Integer filtro,
            @RequestParam(value = "iu", required = true) String id_user,
            Model model) {

        HashMap<String, ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String, ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        //System.out.println("id_usuario: "+id_usuario);

        userDat = this.getHomeDao().getUserById(id_usuario);

        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        Integer id_sucursal = 0;

        jsonretorno.put("Clientes", this.getCxcDao().getBuscadorClientes(cadena, filtro, id_empresa, id_sucursal));

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

    //Cargando filtros
    @RequestMapping(method = RequestMethod.POST, value = "/get_cargando_filtros.json")
    public @ResponseBody
    HashMap<String,Object> get_cargando_filtrosJson(
            @RequestParam(value = "linea", required = true) Integer id_linea,
            @RequestParam(value = "marca", required = true) Integer id_marca,
            @RequestParam(value = "familia", required = true) Integer id__familia,
            @RequestParam(value = "subfamilia", required = true) Integer id_subfamilia,
            @RequestParam(value = "id_agente", required = true) Integer id_agente,
            @RequestParam(value = "iu", required = true) String id_user_cod,
            Model model
        ) {

        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();

        ArrayList<HashMap<String, String>> arraySubfamilia = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayAgentes = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        if (id__familia != 0) {
            arraySubfamilia = this.getCxcDao().getSubfamilias(id__familia);
        }
        
        arrayAgentes =  this.getCxcDao().getAgentes(id_empresa);
        
        jsonretorno.put("lineas", this.getCxcDao().getLineas(id_empresa));
        jsonretorno.put("marcas", this.getCxcDao().getMarcas(id_empresa));
        jsonretorno.put("familias", this.getCxcDao().getFamilias(id_empresa));
        jsonretorno.put("subfamilias", arraySubfamilia);
        jsonretorno.put("agentes", arrayAgentes);
        jsonretorno.put("Segmentos", this.getCxcDao().getCliente_Clasificacion1());
        jsonretorno.put("Mercados", this.getCxcDao().getCliente_Clasificacion2());
        
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
    
    
    //Obtiene datos para el Reporte
    @RequestMapping(value = "/getVentasNetasProductoFactura/out.json", method = RequestMethod.POST)
    public @ResponseBody
    HashMap<String, ArrayList<HashMap<String, String>>> getVentasNetasProductoFactura(
            @RequestParam(value = "tipo_reporte", required = true) Integer tipo_reporte,
            @RequestParam(value = "tipo_costo", required = true) Integer tipo_costo,
            @RequestParam(value = "cliente", required = true) String cliente,
            @RequestParam(value = "producto", required = true) String producto,
            @RequestParam(value = "fecha_inicial", required = true) String fecha_inicial,
            @RequestParam(value = "fecha_final", required = true) String fecha_final,
            @RequestParam(value = "linea", required = true) Integer id_linea,
            @RequestParam(value = "marca", required = true) Integer id_marca,
            @RequestParam(value = "familia", required = true) Integer id_familia,
            @RequestParam(value = "subfamilia", required = true) Integer id_subfamilia,
            @RequestParam(value = "id_agente", required = true) Integer id_agente,
            @RequestParam(value = "segmento", required = true) Integer segmento,
            @RequestParam(value = "mercado", required = true) Integer mercado,
            @RequestParam(value = "iu", required = true) String id_user_cod,
            Model model) {
        log.log(Level.INFO, "Ejecutando getVentasNetasProductoFactura de {0}", RepVentasNetasProductoFacturaController.class.getName());
        HashMap<String, ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String, ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: " + id_usuario);

        userDat = this.getHomeDao().getUserById(id_usuario);

        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        cliente = "%"+cliente.trim()+"%";
        producto = "%"+producto.trim()+"%";
                
        ArrayList<HashMap<String, String>> z = this.getCxcDao().getVentasNetasProductoFactura(tipo_reporte, cliente, producto, fecha_inicial, fecha_final, id_empresa, id_linea, id_marca, id_familia, id_subfamilia, tipo_costo,id_agente,segmento,mercado);
        
        String razon_social = "";
        String nombre_producto = "";


        Double sumatoria_cantidad = 0.0;
        Double sumatoria_costo = 0.0;
        Double sumatoria_venta = 0.0;
        Double  costo =0.0;;


        ArrayList<HashMap<String, String>> arraysumas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arraytotales = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> sumatorias;
        // 1 .- Reporte Por Cliente  2.- Reporte por producto 3.- sumatoria por producto  4 .- sumatoria por cliente
        if (tipo_reporte == 1 || tipo_reporte == 4) {
            if (z.size() > 0) {
                razon_social = z.get(0).get("razon_social");

                HashMap<String, String> registros_normales = null;
                for (int x = 0; x <= z.size() - 1; x++) {
                    HashMap<String, String> registro = z.get(x);
                    if (registro.get("razon_social").equals(razon_social)) {

                        registros_normales = new HashMap<String, String>();

                        registros_normales.put("tipo", "REG");
                        registros_normales.put("numero_control", registro.get("numero_control"));
                        registros_normales.put("razon_social", registro.get("razon_social"));
                        registros_normales.put("codigo", registro.get("codigo"));
                        registros_normales.put("producto", registro.get("producto"));
                        registros_normales.put("factura", registro.get("factura"));
                        registros_normales.put("unidad", registro.get("unidad"));
                        registros_normales.put("cantidad", registro.get("cantidad"));
                        registros_normales.put("precio_unitario", registro.get("precio_unitario"));
                        registros_normales.put("moneda", registro.get("moneda"));
                        registros_normales.put("tipo_cambio", registro.get("tipo_cambio"));
                        registros_normales.put("venta_pesos", registro.get("venta_pesos"));
                        registros_normales.put("costo", registro.get("costo"));
                        registros_normales.put("fecha_factura", registro.get("fecha_factura"));
                        registros_normales.put("id_presentacion", registro.get("id_presentacion"));
                        registros_normales.put("presentacion", registro.get("presentacion"));
                        registros_normales.put("total_cantidad", String.valueOf(0.0));
                        registros_normales.put("total_costo", String.valueOf(0.0));
                        registros_normales.put("total_venta", String.valueOf(0.0));

                        sumatoria_cantidad = sumatoria_cantidad + Double.parseDouble(registro.get("cantidad"));
                        sumatoria_costo = sumatoria_costo + Double.parseDouble(registro.get("costo"));
                        sumatoria_venta = sumatoria_venta + Double.parseDouble(registro.get("venta_pesos"));
                        arraysumas.add(registros_normales);

                    } else {
                        sumatorias = new HashMap<String, String>();
                        registros_normales = new HashMap<String, String>();

                        registros_normales.put("tipo", "TOTAL");
                        registros_normales.put("numero_control", "");
                        registros_normales.put("razon_social", razon_social);
                        registros_normales.put("codigo", "");
                        registros_normales.put("producto", "");
                        registros_normales.put("factura", "");
                        registros_normales.put("unidad", "");
                        registros_normales.put("cantidad", String.valueOf(0.0));
                        registros_normales.put("precio_unitario", String.valueOf(0.0));
                        registros_normales.put("moneda", "");
                        registros_normales.put("tipo_cambio", String.valueOf(0.0));
                        registros_normales.put("venta_pesos", String.valueOf(0.0));
                        registros_normales.put("costo", String.valueOf(0.0));
                        registros_normales.put("fecha_factura", "");
                        registros_normales.put("id_presentacion", String.valueOf(0.0));
                        registros_normales.put("presentacion", "");
                        registros_normales.put("total_cantidad", String.valueOf(sumatoria_cantidad));
                        registros_normales.put("total_costo", String.valueOf(sumatoria_costo));
                        registros_normales.put("total_venta", String.valueOf(sumatoria_venta));

                        //agregar al array
                        arraysumas.add(registros_normales);


                        sumatorias.put("cliente", razon_social);
                        sumatorias.put("cantidad", String.valueOf(sumatoria_cantidad));
                        sumatorias.put("costo", String.valueOf(sumatoria_costo));
                        sumatorias.put("venta", String.valueOf(sumatoria_venta));

                        arraytotales.add(sumatorias);
                        //inicializar variables de sumatorias
                        sumatoria_cantidad = 0.0;
                        sumatoria_costo = 0.0;
                        sumatoria_venta = 0.0;

                        //razon_social="";

                        registros_normales = new HashMap<String, String>();

                        //tomar el nombre del nuevo cliente
                        razon_social = registro.get("razon_social");
                        registros_normales.put("tipo", "REG");
                        registros_normales.put("numero_control", registro.get("numero_control"));
                        registros_normales.put("razon_social", registro.get("razon_social"));
                        registros_normales.put("codigo", registro.get("codigo"));
                        registros_normales.put("producto", registro.get("producto"));
                        registros_normales.put("factura", registro.get("factura"));
                        registros_normales.put("unidad", registro.get("unidad"));
                        registros_normales.put("cantidad", registro.get("cantidad"));
                        registros_normales.put("precio_unitario", registro.get("precio_unitario"));
                        registros_normales.put("moneda", registro.get("moneda"));
                        registros_normales.put("tipo_cambio", registro.get("tipo_cambio"));
                        registros_normales.put("venta_pesos", registro.get("venta_pesos"));
                        registros_normales.put("costo", registro.get("costo"));
                        registros_normales.put("fecha_factura", registro.get("fecha_factura"));
                        registros_normales.put("id_presentacion", registro.get("id_presentacion"));
                        registros_normales.put("presentacion", registro.get("presentacion"));
                        registros_normales.put("total_cantidad", String.valueOf(0.0));
                        registros_normales.put("total_costo", String.valueOf(0.0));
                        registros_normales.put("total_venta", String.valueOf(0.0));

                        //sumar primera cantidad del nuevo cliente
                        sumatoria_cantidad = sumatoria_cantidad + Double.parseDouble(registro.get("cantidad"));
                        if (Double.parseDouble(registro.get("costo"))== 0){
                             costo=0.0;
                        }else{
                            costo=Double.parseDouble(registro.get("costo"));
                        }
                        sumatoria_costo = sumatoria_costo + costo;
                        sumatoria_venta = sumatoria_venta + Double.parseDouble(registro.get("venta_pesos"));

                        arraysumas.add(registros_normales);
                    }


                }//fin del for
                sumatorias = new HashMap<String, String>();
                registros_normales = new HashMap<String, String>();

                registros_normales.put("tipo", "TOTAL");
                registros_normales.put("numero_control", "");
                registros_normales.put("razon_social", razon_social);
                registros_normales.put("codigo", "");
                registros_normales.put("producto", "");
                registros_normales.put("factura", "");
                registros_normales.put("unidad", "");
                registros_normales.put("cantidad", String.valueOf(0.0));
                registros_normales.put("precio_unitario", String.valueOf(0.0));
                registros_normales.put("moneda", "");
                registros_normales.put("tipo_cambio", String.valueOf(0.0));
                registros_normales.put("venta_pesos", String.valueOf(0.0));
                registros_normales.put("costo", String.valueOf(0.0));
                registros_normales.put("fecha_factura", "");
                registros_normales.put("id_presentacion", String.valueOf(0.0));
                registros_normales.put("presentacion", "");
                registros_normales.put("total_cantidad", String.valueOf(sumatoria_cantidad));
                registros_normales.put("total_costo", String.valueOf(sumatoria_costo));
                registros_normales.put("total_venta", String.valueOf(sumatoria_venta));

                //agregar al array
                sumatorias.put("cliente", razon_social);
                sumatorias.put("cantidad", String.valueOf(sumatoria_cantidad));
                sumatorias.put("costo", String.valueOf(sumatoria_costo));
                sumatorias.put("venta", String.valueOf(sumatoria_venta));
                arraytotales.add(sumatorias);

                arraysumas.add(registros_normales);
            }
            //jsonretorno.put("datos", z);
            jsonretorno.put("totales", arraytotales);
            jsonretorno.put("datos_normales", arraysumas);


        }



        // 1 .- Reporte Por Producto
        if (tipo_reporte == 2 || tipo_reporte == 3) {
            if (z.size() > 0) {
                nombre_producto = z.get(0).get("producto");

                HashMap<String, String> registros_normales = null;
                for (int x = 0; x <= z.size() - 1; x++) {
                    HashMap<String, String> registro = z.get(x);
                    if (registro.get("producto").equals(nombre_producto)) {

                        registros_normales = new HashMap<String, String>();

                        registros_normales.put("tipo", "REG");
                        registros_normales.put("numero_control", registro.get("numero_control"));
                        registros_normales.put("razon_social", registro.get("razon_social"));
                        registros_normales.put("codigo", registro.get("codigo"));
                        registros_normales.put("producto", registro.get("producto"));
                        registros_normales.put("factura", registro.get("factura"));
                        registros_normales.put("unidad", registro.get("unidad"));
                        registros_normales.put("cantidad", registro.get("cantidad"));
                        registros_normales.put("precio_unitario", registro.get("precio_unitario"));
                        registros_normales.put("moneda", registro.get("moneda"));
                        registros_normales.put("tipo_cambio", registro.get("tipo_cambio"));
                        registros_normales.put("venta_pesos", registro.get("venta_pesos"));
                        registros_normales.put("costo", registro.get("costo"));
                        registros_normales.put("fecha_factura", registro.get("fecha_factura"));
                        registros_normales.put("id_presentacion", registro.get("id_presentacion"));
                        registros_normales.put("presentacion", registro.get("presentacion"));
                        registros_normales.put("total_cantidad", String.valueOf(0.0));
                        registros_normales.put("total_costo", String.valueOf(0.0));
                        registros_normales.put("total_venta", String.valueOf(0.0));

                        sumatoria_cantidad = sumatoria_cantidad + Double.parseDouble(registro.get("cantidad"));
                        if (Double.parseDouble(registro.get("costo"))== 0){
                             costo=0.0;
                        }else{
                        costo=Double.parseDouble(registro.get("costo"));
                        }
                        
                        sumatoria_costo = sumatoria_costo + costo;
                        
                        sumatoria_venta = sumatoria_venta + Double.parseDouble(registro.get("venta_pesos"));
                        arraysumas.add(registros_normales);

                    } else {
                        sumatorias = new HashMap<String, String>();
                        registros_normales = new HashMap<String, String>();

                        registros_normales.put("tipo", "TOTAL");
                        registros_normales.put("numero_control", "");
                        registros_normales.put("razon_social", "");
                        registros_normales.put("codigo", "");
                        registros_normales.put("producto", nombre_producto);
                        registros_normales.put("factura", "");
                        registros_normales.put("unidad", "");
                        registros_normales.put("cantidad", String.valueOf(0.0));
                        registros_normales.put("precio_unitario", String.valueOf(0.0));
                        registros_normales.put("moneda", "");
                        registros_normales.put("tipo_cambio", String.valueOf(0.0));
                        registros_normales.put("venta_pesos", String.valueOf(0.0));
                        registros_normales.put("costo", String.valueOf(0.0));
                        registros_normales.put("fecha_factura", "");
                        registros_normales.put("id_presentacion", String.valueOf(0.0));
                        registros_normales.put("presentacion", "");
                        registros_normales.put("total_cantidad", String.valueOf(sumatoria_cantidad));
                        registros_normales.put("total_costo", String.valueOf(sumatoria_costo));
                        registros_normales.put("total_venta", String.valueOf(sumatoria_venta));

                        //agregar al array
                        arraysumas.add(registros_normales);


                        sumatorias.put("producto", nombre_producto);
                        sumatorias.put("cantidad", String.valueOf(sumatoria_cantidad));
                        sumatorias.put("costo", String.valueOf(sumatoria_costo));
                        sumatorias.put("venta", String.valueOf(sumatoria_venta));

                        arraytotales.add(sumatorias);
                        //inicializar variables de sumatorias
                        sumatoria_cantidad = 0.0;
                        sumatoria_costo = 0.0;
                        sumatoria_venta = 0.0;

                        //razon_social="";

                        registros_normales = new HashMap<String, String>();

                        //tomar el nombre del nuevo producto
                        nombre_producto = registro.get("producto");

                        registros_normales.put("tipo", "REG");
                        registros_normales.put("numero_control", registro.get("numero_control"));
                        registros_normales.put("razon_social", registro.get("razon_social"));
                        registros_normales.put("codigo", registro.get("codigo"));
                        registros_normales.put("producto", registro.get("producto"));
                        registros_normales.put("factura", registro.get("factura"));
                        registros_normales.put("unidad", registro.get("unidad"));
                        registros_normales.put("cantidad", registro.get("cantidad"));
                        registros_normales.put("precio_unitario", registro.get("precio_unitario"));
                        registros_normales.put("moneda", registro.get("moneda"));
                        registros_normales.put("tipo_cambio", registro.get("tipo_cambio"));
                        registros_normales.put("venta_pesos", registro.get("venta_pesos"));
                        registros_normales.put("costo", registro.get("costo"));
                        registros_normales.put("fecha_factura", registro.get("fecha_factura"));
                        registros_normales.put("id_presentacion", registro.get("id_presentacion"));
                        registros_normales.put("presentacion", registro.get("presentacion"));
                        registros_normales.put("total_cantidad", String.valueOf(0.0));
                        registros_normales.put("total_costo", String.valueOf(0.0));
                        registros_normales.put("total_venta", String.valueOf(0.0));

                        //sumar primera cantidad del nuevo cliente
                        sumatoria_cantidad = sumatoria_cantidad + Double.parseDouble(registro.get("cantidad"));
                        if (Double.parseDouble(registro.get("costo"))== 0){
                             costo=0.0;
                        }else{
                        costo=Double.parseDouble(registro.get("costo"));
                        }
                        sumatoria_costo = sumatoria_costo + costo;
                        sumatoria_venta = sumatoria_venta +Double.parseDouble(registro.get("venta_pesos"));

                        arraysumas.add(registros_normales);
                    }


                }//fin del for

                sumatorias = new HashMap<String, String>();
                registros_normales = new HashMap<String, String>();
                //agregar al array
                sumatorias.put("producto", nombre_producto);
                sumatorias.put("cantidad", String.valueOf(sumatoria_cantidad));
                sumatorias.put("costo", String.valueOf(sumatoria_costo));
                sumatorias.put("venta", String.valueOf(sumatoria_venta));
                arraytotales.add(sumatorias);


                registros_normales.put("tipo", "TOTAL");
                registros_normales.put("numero_control", "");
                registros_normales.put("razon_social", "");
                registros_normales.put("codigo", "");
                registros_normales.put("producto", nombre_producto);
                registros_normales.put("factura", "");
                registros_normales.put("unidad", "");
                registros_normales.put("cantidad", String.valueOf(0.0));
                registros_normales.put("precio_unitario", String.valueOf(0.0));
                registros_normales.put("moneda", "");
                registros_normales.put("tipo_cambio", String.valueOf(0.0));
                registros_normales.put("venta_pesos", String.valueOf(0.0));
                registros_normales.put("costo", String.valueOf(0.0));
                registros_normales.put("fecha_factura", "");
                registros_normales.put("id_presentacion", String.valueOf(0.0));
                registros_normales.put("presentacion", "");
                registros_normales.put("total_cantidad", String.valueOf(sumatoria_cantidad));
                registros_normales.put("total_costo", String.valueOf(sumatoria_costo));
                registros_normales.put("total_venta", String.valueOf(sumatoria_venta));

                arraysumas.add(registros_normales);
            }// fin de si z > 0
            //jsonretorno.put("datos", z);
            jsonretorno.put("totales", arraytotales);
            jsonretorno.put("datos_normales", arraysumas);
        }








        return jsonretorno;
    }

    //reporte de ventas netas por Cliente/producto desglosado por factura
    @RequestMapping(value = "/getrepventasnetasproductofactura/{cadena}/out.json", method = RequestMethod.GET)
    public ModelAndView PdfVentasNetasProductoFactura(
            @PathVariable("cadena") String cadena,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException {
        //                 0               1            2                3                  4               5             6             7                8                   9               10               11             12            13
        //cadena = tipo_reporte+"___"+cliente+"___"+producto+"___"+fecha_inicial+"___"+fecha_final+"___"+usuario+"___"+id_linea+"___"+id_marca+"___"+id_familia+"___"+id_subfamilia+"___"+tipo_costo+"___" id_agente+"___"+segmento+"___" mercado

        String arreglo[];
        arreglo = cadena.split("___");
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> lista_ventasporproducto;
        ArrayList<HashMap<String, String>> arraysumatorias = new ArrayList<HashMap<String, String>>();
        // HashMap<String, String> sumatorias;

        System.out.println("Generando reporte de Ventas Netas por producto factura");

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(arreglo[5]));
        //System.out.println("id_usuario: " + id_usuario);

        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);

        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();


        String[] array_company = razon_social_empresa.split(" ");
        String company_name = array_company[0].toLowerCase();
        //String ruta_imagen = this.getPgdao().getImagesDir() +"logo_"+ company_name +".png";
        /*
        System.out.println("pocicion 0:" + arreglo[0] + "pocicion 1:" + arreglo[1] + "pocicion 2:" + arreglo[2]
                + "pocicion 3:" + arreglo[3] + "pocicion 4:" + arreglo[4]
                + "pocicion 5:" + arreglo[5] + "pocicion 6:" + Integer.parseInt(arreglo[6])
                + "pocicion 7:" + Integer.parseInt(arreglo[7]) + "pocicion 8:" + Integer.parseInt(arreglo[8])
                + "pocicion 9:" + Integer.parseInt(arreglo[9]) + "pocicion 10:" + Integer.parseInt(arreglo[10])
                + "pocicion 11:" + Integer.parseInt(arreglo[11]) + "Segmento[12]:" + Integer.parseInt(arreglo[12])
                + "Mercado[13]:" + Integer.parseInt(arreglo[13])
                );
        */
        File file_dir_tmp = new File(dir_tmp);

        //System.out.println("Directorio temporal: " + file_dir_tmp.getCanonicalPath());

        String file_name = "";
        if (Integer.parseInt(arreglo[0]) == 1) {
            file_name = "VentasNetas_x_cliente.pdf";
        }
        if (Integer.parseInt(arreglo[0]) == 4) {
            file_name = "VentasNetas_sumarizado_x_cliente.pdf";
        }
        if (Integer.parseInt(arreglo[0]) == 2) {
            file_name = "VentasNetas_x_producto.pdf";
        }

        if (Integer.parseInt(arreglo[0]) == 3) {
            file_name = "VentasNetas_sumarizado_x_producto.pdf";
        }
        //ruta de archivo de salida
        String fileout = file_dir_tmp + "/" + file_name;
        //String fileout = "C:\\Users\\micompu\\Desktop\\mi reporte de clientes sumarizados.pdf";

        String razon_social = "";
        String nombre_producto = "";
        Double sumatoria_cantidad = 0.0;
        Double sumatoria_costo = 0.0;
        Double sumatoria_venta = 0.0;
        
        String producto = "";
        
        lista_ventasporproducto = this.getCxcDao().getVentasNetasProductoFactura(Integer.parseInt(arreglo[0]), "%"+arreglo[1]+"%", "%"+arreglo[2]+"%", arreglo[3], arreglo[4], id_empresa, Integer.parseInt(arreglo[6]), Integer.parseInt(arreglo[7]), Integer.parseInt(arreglo[8]), Integer.parseInt(arreglo[9]), Integer.parseInt(arreglo[10]),Integer.parseInt(arreglo[11]),Integer.parseInt(arreglo[12]),Integer.parseInt(arreglo[13]));
        
        // 1 .- Reporte Por Cliente  2.- Reporte por producto 3.- sumatoria por producto  4 .- sumatoria por cliente
        if (Integer.parseInt(arreglo[0]) == 1 || Integer.parseInt(arreglo[0]) == 4) {
            if (lista_ventasporproducto.size() > 0) {
                razon_social = lista_ventasporproducto.get(0).get("razon_social");
                for (int x = 0; x <= lista_ventasporproducto.size() - 1; x++) {
                    HashMap<String, String> registro = lista_ventasporproducto.get(x);
                    if (registro.get("razon_social").equals(razon_social)) {

                        sumatoria_cantidad = sumatoria_cantidad + Double.parseDouble(registro.get("cantidad"));
                        sumatoria_costo = sumatoria_costo + Double.parseDouble(registro.get("costo"));
                        sumatoria_venta = sumatoria_venta + Double.parseDouble(registro.get("venta_pesos"));
                    } else {
                        HashMap<String, String> sumatorias = new HashMap<String, String>();

                        sumatorias.put("cliente", razon_social);
                        sumatorias.put("cantidad", String.valueOf(sumatoria_cantidad));
                        sumatorias.put("costo", String.valueOf(sumatoria_costo));
                        sumatorias.put("venta", String.valueOf(sumatoria_venta));

                        arraysumatorias.add(sumatorias);
                        //inicializar variables de sumatorias
                        sumatoria_cantidad = 0.0;
                        sumatoria_costo = 0.0;
                        sumatoria_venta = 0.0;
                        razon_social = registro.get("razon_social");

                        //sumar primera cantidad del nuevo cliente
                        sumatoria_cantidad = sumatoria_cantidad + Double.parseDouble(registro.get("cantidad"));
                        sumatoria_costo = sumatoria_costo + Double.parseDouble(registro.get("costo"));
                        sumatoria_venta = sumatoria_venta + Double.parseDouble(registro.get("venta_pesos"));
                    }
                }//fin del for
                HashMap<String, String> sumatorias = new HashMap<String, String>();
                //agregar al array
                sumatorias.put("cliente", razon_social);
                sumatorias.put("cantidad", String.valueOf(sumatoria_cantidad));
                sumatorias.put("costo", String.valueOf(sumatoria_costo));
                sumatorias.put("venta", String.valueOf(sumatoria_venta));
                arraysumatorias.add(sumatorias);
            }
        }

        // 1 .- Reporte Por Cliente  2.- Reporte por producto 3.- sumatoria por producto  4 .- sumatoria por cliente
        if (Integer.parseInt(arreglo[0]) == 2 || Integer.parseInt(arreglo[0]) == 3) {
            if (lista_ventasporproducto.size() > 0) {
                producto = lista_ventasporproducto.get(0).get("producto");
                for (int x = 0; x <= lista_ventasporproducto.size() - 1; x++) {
                    HashMap<String, String> registro = lista_ventasporproducto.get(x);
                    if (registro.get("producto").equals(producto)) {

                        sumatoria_cantidad = sumatoria_cantidad + Double.parseDouble(registro.get("cantidad"));
                        sumatoria_costo = sumatoria_costo + Double.parseDouble(registro.get("costo"));
                        sumatoria_venta = sumatoria_venta + Double.parseDouble(registro.get("venta_pesos"));
                    } else {
                        HashMap<String, String> sumatorias = new HashMap<String, String>();

                        sumatorias.put("producto", producto);
                        sumatorias.put("cantidad", String.valueOf(sumatoria_cantidad));
                        sumatorias.put("costo", String.valueOf(sumatoria_costo));
                        sumatorias.put("venta", String.valueOf(sumatoria_venta));

                        arraysumatorias.add(sumatorias);
                        //inicializar variables de sumatorias
                        sumatoria_cantidad = 0.0;
                        sumatoria_costo = 0.0;
                        sumatoria_venta = 0.0;
                        producto = registro.get("producto");

                        //sumar primera cantidad del nuevo cliente
                        sumatoria_cantidad = sumatoria_cantidad + Double.parseDouble(registro.get("cantidad"));
                        sumatoria_costo = sumatoria_costo + Double.parseDouble(registro.get("costo"));
                        sumatoria_venta = sumatoria_venta + Double.parseDouble(registro.get("venta_pesos"));
                    }
                }//fin del for
                HashMap<String, String> sumatorias = new HashMap<String, String>();
                //agregar al array
                sumatorias.put("producto", producto);
                sumatorias.put("cantidad", String.valueOf(sumatoria_cantidad));
                sumatorias.put("costo", String.valueOf(sumatoria_costo));
                sumatorias.put("venta", String.valueOf(sumatoria_venta));
                arraysumatorias.add(sumatorias);
            }
        }
        
        //Tipo=1 Reporte de Ventas netas por Producto
        //Tipo=2 Reporte comercial para agentes de Ventas
        Integer tipo = 1; 
        
        //obtiene los informacion de ventas  del periodo indicado
        //[0]tipo_reporte+"___"+[1]cliente+"___"+[2]producto+"___"+[3]fecha_inicial+"___"+[4]fecha_final+"___"+[5]usuario
        if (Integer.parseInt(arreglo[0]) == 1) {
            //instancia a la clase que construye el pdf  del reporte ventas netas por cliente
            PdfRepVentasNetasProductoFactura x = new PdfRepVentasNetasProductoFactura(tipo, Integer.parseInt(arreglo[0]), arraysumatorias, lista_ventasporproducto, arreglo[3], arreglo[4], razon_social_empresa, fileout);
        }

        if (Integer.parseInt(arreglo[0]) == 4) {
            //instancia a la clase que construye el pdf ventas netas Sumarizado por Cliente
            PdfReporteVentasNetasSumatoriaxClientes x = new PdfReporteVentasNetasSumatoriaxClientes(tipo, Integer.parseInt(arreglo[0]), arraysumatorias,lista_ventasporproducto, arreglo[2], arreglo[3], arreglo[4], razon_social_empresa, fileout);
        }

        if (Integer.parseInt(arreglo[0]) == 2) {
            //instancia a la clase que construye el pdf ventas netas por Producto
            PdfReporteVentasNetasProductoFacturados x = new PdfReporteVentasNetasProductoFacturados(tipo, Integer.parseInt(arreglo[0]), arraysumatorias,lista_ventasporproducto, arreglo[3], arreglo[4], razon_social_empresa, fileout);
        }
        if (Integer.parseInt(arreglo[0]) == 3) {
            //instancia a la clase que construye el pdf ventas netas Sumarizado por Producto
            PdfReporteVentasNetasSumatoriaxProducto x = new PdfReporteVentasNetasSumatoriaxProducto(tipo, Integer.parseInt(arreglo[0]), arraysumatorias,lista_ventasporproducto, arreglo[1], arreglo[3], arreglo[4], razon_social_empresa, fileout);
        }

        //System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        FileCopyUtils.copy(bis, response.getOutputStream());
        response.flushBuffer();
        try {
            FileHelper.delete(fileout);
        } catch (Exception ex) {
            Logger.getLogger(RepVentasNetasProductoFacturaController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
