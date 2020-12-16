/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.common.obj;
    
/**
 *
 * @author pianodaemon
 */
public final class UserSessionData {
    private int userId;
    private String userName;
    private int empresaId;
    private String razonSocialEmpresa;
    private int sucursalId;
    private String Sucursal;
    private String incluyeCrm;


    public UserSessionData(String login_name, int userId , int empresaId, String razonSocEmp, int sucursalid, String nombreSucursal,String incluyeCrm) {
        this.setUserId(userId);
        this.setUserName(login_name);
        this.setEmpresaId(empresaId);
        this.setRazonSocialEmpresa(razonSocEmp);
        this.setSucursalId(sucursalid);
        this.setSucursal(nombreSucursal);
        this.setIncluyeCrm(incluyeCrm);
    }
    
    
    public int getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(int empresaId) {
        this.empresaId = empresaId;
    }
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getRazonSocialEmpresa() {
        return razonSocialEmpresa;
    }
    
    public void setRazonSocialEmpresa(String razonSocialEmpresa) {
        this.razonSocialEmpresa = razonSocialEmpresa;
    }
    
    public String getSucursal() {
        return Sucursal;
    }
    
    public void setSucursal(String Sucursal) {
        this.Sucursal = Sucursal;
    }
    
    public int getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(int sucursalId) {
        this.sucursalId = sucursalId;
    }
    
    public String getIncluyeCrm() {
        return incluyeCrm;
    }

    public void setIncluyeCrm(String incluyeCrm) {
        this.incluyeCrm = incluyeCrm;
    }

}
