package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/invplazassucursales/")
public class InvPlazasSucursalesController {
  ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(InvPlazasSucursalesController.class.getName());
    
    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("daoInv")   //utilizo todos los metodos de invinterfacedao
    private InvInterfaceDao invDao;
    
    public InvInterfaceDao getInvDao() {
        return invDao;
    }
    
    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("daoHome")   //permite controlar usuarios que entren
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
    @ModelAttribute("user") UserSessionData user)
    throws ServletException, IOException {        
       
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvPlazasSucursalesController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();        
        
        ModelAndView x = new ModelAndView("invplazassucursales/startup", "title", "Asignacion de Plazas a sucursales");
        
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
    
   
    //para obtener plazas y sucursales para al cargar la pagina.
    @RequestMapping(value="/getSucursales.json", method = RequestMethod.POST)
    public @ResponseBody  HashMap<String,ArrayList<HashMap<String, String>>> getSucursales(           
            @RequestParam(value="iu", required=true) String id_user_cod,
            org.springframework.ui.Model model) 
            {        
                log.log(Level.INFO, "Ejecutando getSucursal de {0}", InvPlazasSucursalesController.class.getName());
                HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
                ArrayList<HashMap<String, String>> sucursal = new ArrayList<HashMap<String, String>>();
                ArrayList<HashMap<String, String>> plaza = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> userDat = new HashMap<String, String>();

                //decodificar id de usuario
                Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));                
                System.out.println("id_usuario: "+id_usuario);
                userDat = this.getHomeDao().getUserById(id_usuario);

                Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
                
                sucursal =this.getInvDao().getSucursales(id_empresa);                
                plaza =this.getInvDao().getPlazas(id_empresa);
                
                jsonretorno.put("Sucursal", sucursal);
                jsonretorno.put("Plaza", plaza);
                
                return jsonretorno;                 
            }
    
    //para traer las plazas asignadas y no asignadas filtradas por sucursal
    @RequestMapping(value="/getFiltroPlazas.json", method = RequestMethod.POST)
    public @ResponseBody  HashMap<String,ArrayList<HashMap<String, String>>> getFiltroPlazas(           
            @RequestParam(value="iu", required=true) String id_user_cod, 
            @RequestParam(value="id_sucursal", required=true) Integer id_sucursal,
                    
            org.springframework.ui.Model model) 
            {                   
                log.log(Level.INFO, "Ejecutando getSucursal de {0}", InvPlazasSucursalesController.class.getName());
                
                HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
                
                ArrayList<HashMap<String, String>> asignadas = new ArrayList<HashMap<String, String>>();
                ArrayList<HashMap<String, String>> noAsignadas = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> userDat = new HashMap<String, String>();

                //decodificar id de usuario
                Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
               
                System.out.println("id_usuario: "+id_usuario);

                userDat = this.getHomeDao().getUserById(id_usuario);

                Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

                asignadas =this.getInvDao().getPlazasAsignadas(id_empresa,id_sucursal);
                noAsignadas =this.getInvDao().getPlazasNoAsignadas(id_empresa,id_sucursal);
                
                jsonretorno.put("Asignadas", asignadas);
                jsonretorno.put("NoAsignadas", noAsignadas);
                
                return jsonretorno;                 
            }    
    
    //para actualizar la base de datos en la tabla gral_suc_pza
    @RequestMapping(value="/getActualizar.json", method = RequestMethod.POST)
    public @ResponseBody  HashMap<String,ArrayList<HashMap<String, String>>> getActualizar(
            @RequestParam(value="id_sucursal", required=true) Integer id_sucursal,
            @RequestParam(value="Agregadas", required=true) String plazasAgregadas){   
                
        String extra_data_array = "'sindatos'";
        Integer app_selected = 54;
        String command_selected = "new";  

        String data_string = app_selected+"___"+command_selected+"___"+id_sucursal+"___"+plazasAgregadas;                   

        this.getInvDao().selectFunctionForThisApp(data_string,extra_data_array);

        log.log(Level.INFO, "Ejecutando getSucursal de {0}", InvPlazasSucursalesController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();

        return jsonretorno;                 
    }   
}
