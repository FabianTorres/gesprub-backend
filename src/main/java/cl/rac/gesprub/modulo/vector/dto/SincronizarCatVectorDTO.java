package cl.rac.gesprub.modulo.vector.dto;

public class SincronizarCatVectorDTO {
    private Integer vectorId;
    private String nombre;
    private String tipoTecnologia;
    private String versionIngreso;
    private Boolean estado;

    public Integer getVectorId() { return vectorId; }
    public void setVectorId(Integer vectorId) { this.vectorId = vectorId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipoTecnologia() { return tipoTecnologia; }
    public void setTipoTecnologia(String tipoTecnologia) { this.tipoTecnologia = tipoTecnologia; }

    public String getVersionIngreso() { return versionIngreso; }
    public void setVersionIngreso(String versionIngreso) { this.versionIngreso = versionIngreso; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}