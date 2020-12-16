package com.agnux.kemikal.interfacedaos;

import java.util.ArrayList;
import java.util.HashMap;


public interface TesInterfaceDao {
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array);
    public String selectFunctionForThisApp(String campos_data, String extra_data_array);
    public int countAll(String data_string);
    
    /*metodos para el catalogo tesmovtipos*/
    public ArrayList<HashMap<String, Object>> getTesMovTiposGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getTesMovTipos_Datos(Integer id);
    
    
    /*metodos para el catalogo tesban*/
    public ArrayList<HashMap<String, Object>> getTesBanGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getTesBan_Datos(Integer id);
    
    
    /*metodos para el catalogo tesban*/
    public ArrayList<HashMap<String, Object>> getTesConGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getTesCon_Datos(Integer id);
    
    
    //catalogo de chequera
    public ArrayList<HashMap<String, Object>> getTesChequeraGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getTesChequera_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getMonedas();
    public ArrayList<HashMap<String, String>> getBancos(Integer idEmpresa);
    public ArrayList<HashMap<String, String>> getTesChequera_DatosContabilidad(Integer id);
    public ArrayList<HashMap<String, String>> getTesChequera_CuentasMayor(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getTesChequera_CuentasContables(Integer cta_mayor, Integer detalle, String clasifica, String cta, String scta, String sscta, String ssscta, String sssscta, String descripcion, Integer id_empresa);
    
    public ArrayList<HashMap<String, String>> getPaises();
    public ArrayList<HashMap<String, String>> getEntidadesForThisPais(String id_pais);
    public ArrayList<HashMap<String, String>> getMunicipiosForThisEntidad(String id_pais,String id_entidad);
    
    
    
}
