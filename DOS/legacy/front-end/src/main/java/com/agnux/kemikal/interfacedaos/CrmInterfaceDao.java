package com.agnux.kemikal.interfacedaos;

import java.util.ArrayList;
import java.util.HashMap;


public interface CrmInterfaceDao {

    //metodos  de uso general
    public int countAll(String data_string);
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array);
    public String selectFunctionForThisApp(String campos_data, String extra_data_array);
    public String selectFunctionForCrmAdmProcesos(String campos_data, String extra_data_array);

    /*Buscador de contactos*/
    public ArrayList<HashMap<String, String>> getBuscadorContactos(String nombre, String apellidop, String apellidom, String tipo_contacto, Integer id_empresa);
    
    //Buscador de proveedores
    public ArrayList<HashMap<String, Object>> getBuscadorProveedores(String rfc, String noProveedor, String razon_social, Integer id_empresa);
    
    //metodos para aplicativo de Motivos de Visita
    public ArrayList<HashMap<String, String>> getMotivoVisita_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getMotivosVisita_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc,Integer id_empresa);

    //metodos para aplicativo de Formas de Contacto
    public ArrayList<HashMap<String, Object>> getFormasContacto_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc,Integer id_empresa);
    public ArrayList<HashMap<String, String>> getFormasContacto_Datos(Integer id);

    //metodos para aplicativo de Motivos de LLamadas
    public ArrayList<HashMap<String, String>> getMotivosLlamada_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getMotivosLlamada_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc,Integer id_empresa);

   //Metodos para Aplicativo de Registro de Llamadas
    public ArrayList<HashMap<String, String>> getMotivos_Llamadas(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getCalificacion_Llamadas(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getRegistroLlamadas_Seguimiento(Integer id_empresa);
    public ArrayList<HashMap<String,String>>getCrmRegistroLlamadas_Datos(Integer id);
    public ArrayList<HashMap<String,Object>>getRegistroLlamadas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);



    //Métodos para ASplicativo de Registlro de Visitas
    public ArrayList<HashMap<String, Object>> getCrmRegistroVisitas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> getCrmRegistroVisitas_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getAgentes(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getEmpleadosPorDepartamento(Integer id_empresa, Integer id_departamento);
    public HashMap<String, String> getUserRol(Integer id_empleado);
    public ArrayList<HashMap<String, String>> getCrmRegistroVisitas_Motivos(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getCrmRegistroVisitas_Calificaciones(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getCrmRegistroVisitas_Seguimientos(Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getClientesProspectos(Integer tipo, String cadena, Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getAutocompletadoContactos(Integer tipo, String cadena, Integer id_empresa);

    //metodos para aplicativo de Oportunidades
    public ArrayList<HashMap<String, Object>> getOportunidades_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc,Integer id_empresa);
    public ArrayList<HashMap<String, String>> getOportunidad_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getTiposOportunidad();
    public ArrayList<HashMap<String, String>> getEtapasVenta();

    //metodos para Catalogo de Registro de prospectos
    public ArrayList<HashMap<String, Object>> getPaises();
    public ArrayList<HashMap<String, Object>> getProspecto_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getEntidadesForThisPais(String id_pais);
    public ArrayList<HashMap<String, Object>> getLocalidadesForThisEntidad(String id_pais,String id_entidad);
    public ArrayList<HashMap<String, Object>> getProspectos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, Object>> gettipo_prospecto(String id_prospecto);
    public ArrayList<HashMap<String, Object>> getTipo_Prospecto();
    //public ArrayList<HashMap<String, Object>> getEtapas_venta();
    public ArrayList<HashMap<String, Object>> getClasificacion_prospecto();
    public ArrayList<HashMap<String, Object>> getTipo_industria();

    public ArrayList<HashMap<String, Object>> getEtapas_prospecto();



    //metodos para el aplicativo de metas
    public ArrayList<HashMap<String, String>> getCrmRegistoMetas_Datos(Integer id);

    public ArrayList<HashMap<String, Object>> getRegistroMetas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);

    //Métodos para ASplicativo de Registlro de Casos
    public ArrayList<HashMap<String, Object>> getCrmRegistroCasos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> getCrmRegistroCasos_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getBuscadorCliente_Prospecto(String Razon_social, String rfc, Integer identificador_cliente_prospecto, Integer id_empresa);

    //Metodos para el buscador de Llamadas

    public ArrayList<HashMap<String, String>> getBuscadorRegistros(Integer id, Integer agente, Integer tipo_seleccion, Integer status, Integer etapa, String fecha_inicial, String fecha_final, Integer id_empresa);

    //Metodos para el buscador de Visitas
    public ArrayList<HashMap<String, String>> getBuscadorVisitas(Integer id, Integer agente, Integer tipo_seleccion, Integer status, Integer etapa, String fecha_inicial, String fecha_final, Integer id_empresa);

    public ArrayList<HashMap<String, String>> getBuscadorVarios(Integer id, Integer agente, Integer tipo_seleccion, Integer status, Integer etapa, String fecha_inicial, String fecha_final, Integer id_empresa);

    public ArrayList<HashMap<String, String>> getBuscadorCasos(Integer id, Integer agente, Integer tipo_seleccion, Integer status, Integer etapa, String fecha_inicial, String fecha_final, Integer id_empresa);

    public ArrayList<HashMap<String, String>> getBuscadorOportunidades(Integer id, Integer agente, Integer tipo_seleccion, Integer status, Integer etapa, String fecha_inicial, String fecha_final, Integer id_empresa);


    public ArrayList<HashMap<String, Object>> getContactos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getContacto_Datos(Integer id);
    
    //CRM  Reportes
    
    //CRM  Reporte de visitas
    public ArrayList<HashMap<String, String>> getVisitas(String fecha_inicial, String fecha_final,Integer id_empresa, Integer idAgente);
    
    //CRM  Reporte de Proyectos
    public ArrayList<HashMap<String, String>> getProyectos(String fecha_inicial, String fecha_final,Integer id_empresa, Integer idAgente);
    
    //fin de reportes de CRM
    
    //CRM  Configuracion de consultas
    public ArrayList<HashMap<String, String>> getDatosConfigConsulta(Integer empresa, Integer empleado_id);
    public ArrayList<HashMap<String, String>> getResultadosBigPicture(Integer id_usuario,Integer  id_empresa,String agente,String fecha_inicio,String fecha_fin);
    //FIN CRM  Configuracion de consultas
    
    //Datos para el Paginado de Registro de Proyectos
    public ArrayList<HashMap<String, Object>> getCrmRegistroProyectos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    //Obtiene datos del proyecto
    public ArrayList<HashMap<String, Object>> getCrmRegistroProyectos_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getCrmRegistroProyectos_Competidores(Integer id);
   
    //Obtener los diferentes estatus para registro de proyectos
    public ArrayList<HashMap<String, String>> getCrmRegistroProyectos_Estatus(Integer id_empresa);
    
    public ArrayList<HashMap<String, Object>> getMonedas();
    
    public ArrayList<HashMap<String, Object>> getSeguimientoVisitas_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getSeguimientoVisitas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc, Integer id_empresa);
    
    public HashMap<String, String> getCrm_Parametros(Integer id_emp, Integer id_suc);
}
