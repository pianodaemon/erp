package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import java.io.IOException;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/invetiquetasentrada/")
public class InvEtiquetasEntradaController {
    private static final Logger log  = Logger.getLogger(InvEtiquetasEntradaController.class.getName());
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvEtiquetasEntradaController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("folio", "Folio:90");
        infoConstruccionTabla.put("fecha_entrada","Fecha Entrada:90");
        infoConstruccionTabla.put("origen", "Origen:300");
        infoConstruccionTabla.put("orden_compra", "Orden Compra:110");
        infoConstruccionTabla.put("folio_doc", "Folio Doc.:110");
        infoConstruccionTabla.put("fecha_doc","Fecha Doc.:110");
        infoConstruccionTabla.put("estado","Estado:90");
        
        ModelAndView x = new ModelAndView("invetiquetasentrada/startup", "title", "Etiquetas de Entradas");
        
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
    
    
    
    
    //Metodo para el grid y el Paginado
    @RequestMapping(value="/getAllOrdenesEntrada.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllOrdenesEntradaJson(
           @RequestParam(value="orderby", required=true) String orderby,
           @RequestParam(value="desc", required=true) String desc,
           @RequestParam(value="items_por_pag", required=true) int items_por_pag,
           @RequestParam(value="pag_start", required=true) int pag_start,
           @RequestParam(value="display_pag", required=true) String display_pag,
           @RequestParam(value="input_json", required=true) String input_json,
           @RequestParam(value="cadena_busqueda", required=true) String cadena_busqueda,
           @RequestParam(value="iu", required=true) String id_user_cod,
           Model modcel
    ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String,String> has_busqueda = StringHelper.convert2hash(StringHelper.ascii2string(cadena_busqueda));
        
        //Aplicativo Etiquetas de Entrada(INV)
        Integer app_selected = 202;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //Variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String orden_compra = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("orden_compra")))+"%";
        String factura = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("factura")))+"%";
        String proveedor = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("proveedor")))+"%";
        String tipo_doc = StringHelper.isNullString(String.valueOf(has_busqueda.get("tipo_doc")));
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"%";
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+orden_compra+"___"+factura+"___"+proveedor+"___"+ tipo_doc +"___"+ codigo;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getInvDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getInvDao().getInvOrdenEntrada_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getEntrada.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getEntradaJson(
            @RequestParam(value="identificador", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getOrdenEntradaJson de {0}", InvEtiquetasEntradaController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        ArrayList<HashMap<String, String>> monedas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> impuestos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosOrdenEntrada = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosGrid = new ArrayList<HashMap<String, String>>();
        //ArrayList<HashMap<String, String>> datosGridLotes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> almacenes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tiposMovimiento = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( id != 0 ){
            datosOrdenEntrada = this.getInvDao().getInvOrdenEntrada_Datos(id);
            datosGrid = this.getInvDao().getInvEtiquetasEntrada_DatosGrid(id);
            //datosGridLotes = this.getInvDao().getInvOrdenEntrada_DatosGridLotes(id);
        }
        
        monedas = this.getInvDao().getMonedas();
        impuestos = this.getInvDao().getEntradas_Impuestos();
        almacenes = this.getInvDao().getAlmacenes(id_empresa);
        tiposMovimiento = this.getInvDao().getAllTiposMovimientoInventario(id_empresa);
        
        jsonretorno.put("Datos", datosOrdenEntrada);
        jsonretorno.put("datosGrid", datosGrid);
        //jsonretorno.put("Lotes", datosGridLotes);
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("Impuestos", impuestos);
        jsonretorno.put("Almacenes", almacenes);
        jsonretorno.put("TMovInv", tiposMovimiento);
        
        return jsonretorno;
    }
    
    
    
}
